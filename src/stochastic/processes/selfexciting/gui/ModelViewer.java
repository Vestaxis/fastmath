package stochastic.processes.selfexciting.gui;

import static java.lang.Math.exp;
import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.stream.Collectors.toList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleFunction;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.XYStyler;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import stochastic.processes.pointprocesses.finance.NasdaqTradingStrategy;
import stochastic.processes.pointprocesses.finance.TradingFiltration;
import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;
import util.DateUtils;
import util.Plotter;

public class ModelViewer
{

  public JFrame frame;
  private JTable table;
  private XYChart chart;
  private List<AbstractSelfExcitingProcess> processes;
  private XChartPanel<XYChart> chartPanel;
  private JPanel bottomPanel;

  public static void
         main(String args[]) throws IOException
  {
    final String matFile = args.length > 0 ? args[0] : "/home/stephen/fm/SPY.mat";
    final String symbol = args.length > 1 ? args[1] : "SPY";

    TradingFiltration filtration = new TradingFiltration(MatFile.loadMatrix(matFile, symbol));
    ArrayList<AbstractSelfExcitingProcess> processes = NasdaqTradingStrategy.getCalibratedProcesses(matFile, filtration, Type.ConstrainedApproximatePowerlaw);

    NasdaqTradingStrategy.launchModelViewer(processes);

  }

  public ModelViewer(List<AbstractSelfExcitingProcess> processes)
  {
    Object[][] data = getProcessParametersAndStatisticsMatrix(processes);

    String[] columnHeaders = processes.get(0).getColumnHeaders();

    initialize();
    DefaultTableModel tableModel = new DefaultTableModel()
    {
      @Override
      public boolean
             isCellEditable(int arg0,
                            int arg1)
      {
        return false;
      }
    };
    tableModel.setDataVector(data, columnHeaders);
    table.setModel(tableModel);
    this.processes = processes;
  }

  public static Object[][]
         getProcessParametersAndStatisticsMatrix(List<AbstractSelfExcitingProcess> processes)
  {
    List<Object[]> processStats = processes.stream().map(process -> process.evaluateParameterStatistics(process.getParameters().toArray())).collect(toList());
    int N = processStats.get(0).length;
    Object[][] stats = new Object[processStats.size()][N];
    for (int i = 0; i < processStats.size(); i++)
    {
      Object[] row = processStats.get(i);
      for (int j = 0; j < N; j++)
      {
        stats[i][j] = row[j];
      }
    }
    return stats;
  }

  /**
   * Initialize the contents of the frame.
   */
  private void
          initialize()
  {
    frame = new JFrame();
    frame.setBounds(100, 100, 2200, 1057);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    table = new JTable();
    table.setCellSelectionEnabled(true);
    table.setColumnSelectionAllowed(true);

    JScrollPane scrollPane = new JScrollPane(table);

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setTopComponent(scrollPane);
    splitPane.setDividerLocation(250);
    bottomPanel = new JPanel();

    splitPane.setBottomComponent(bottomPanel);
    chart = new XYChart(2000, 500);
    chartPanel = new XChartPanel<XYChart>(chart);
    bottomPanel.setLayout(new GridLayout(3, 1));
    bottomPanel.add(chartPanel);

    frame.getContentPane().add(splitPane, BorderLayout.CENTER);
  }

  public void
         show()
  {

    plotData();

    EventQueue.invokeLater(new Runnable()
    {
      public void
             run()
      {
        try
        {
          frame.setVisible(true);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });

  }

  public void
         plotData()
  {
    AbstractSelfExcitingProcess firstProcess = processes.get(0);
    double ν0 = firstProcess.ν(0);
    double z = firstProcess.Z();
    out.println("ν0=" + ν0 + " params=" + firstProcess.getParamString() + " Z=" + z);
    XChartPanel<XYChart> impulseResponseChart = Plotter.plot("t (ms)", "ν(t)", firstProcess::ν, 0, 100);

    impulseResponseChart.getChart().setTitle("impulse response kernel (ms)");

    double millisecondsToHours = DateUtils.convertTimeUnits(1, TimeUnit.MILLISECONDS, TimeUnit.HOURS);
    double millisecondsToSeconds = DateUtils.convertTimeUnits(1, TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
    Vector timesInHours = firstProcess.T.copy().multiply(millisecondsToHours);
    Vector timesInSeconds = firstProcess.T.copy().multiply(millisecondsToSeconds);
    Vector timesInMilliseconds = firstProcess.T.copy().multiply(millisecondsToHours);
    assert timesInHours.equals(firstProcess.X.col(0));
    Vector logPrices = firstProcess.X.col(1).copy().add(1).log();
    double logReferencePrice = logPrices.get(0);
    logPrices = logPrices.subtract(logReferencePrice).multiply(100);
    double referencePrice = exp(logReferencePrice);
    String logPriceName = format("(ln(price)-ln(%4.2f)*100", referencePrice);
    chart.addSeries(logPriceName, timesInHours.toArray(), logPrices.toArray());
    chart.setTitle("price Δ%");
    chart.setXAxisTitle("t (hours)");
    chart.setYAxisTitle(logPriceName);
    XYStyler styler = chart.getStyler();
    styler.setMarkerSize(0);
    styler.setYAxisTicksVisible(true);

    double firstTime = firstProcess.T.fmin();
    XChartPanel<XYChart> intensityChart = Plotter.plot("t (seconds)",
                                                       "λ(t)",
                                                       t -> firstProcess.λ(t) * 1000,
                                                       firstTime,
                                                       firstTime + 1000 * 5,
                                                       2000,
                                                       t -> (t - firstTime) / 1000);

    intensityChart.getChart().setTitle("conditional intensity (events per second)");
    intensityChart.getChart().getStyler().setSeriesColors(new Color[]
    { Color.RED });

    bottomPanel.add(intensityChart);
    bottomPanel.add(impulseResponseChart);

  }

}
