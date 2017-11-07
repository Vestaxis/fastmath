package stochastic.processes.selfexciting.gui;

import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import fastmath.DoubleColMatrix;
import fastmath.Vector;
import fastmath.Vector.Condition;
import fastmath.matfile.MatFile;
import stochastic.processes.point.MarkedPointProcess;
import stochastic.processes.selfexciting.ExponentialSelfExcitingProcess;
import stochastic.processes.selfexciting.ExtendedApproximatePowerlawSelfExcitingProcess;
import stochastic.processes.selfexciting.SelfExcitingProcessEstimator;
import stochastic.processes.selfexciting.NasdaqTradingStrategy;
import util.DateUtils;

public class ModelViewer
{

  public JFrame frame;
  private JTable table;
  private XYChart chart;
  private ArrayList<ExponentialSelfExcitingProcess> processes;

  public static void main(String args[]) throws IOException
  {
    final String matFile = args.length > 0 ? args[0] : "/home/stephen/fm/SPY.mat";
    final String symbol = args.length > 1 ? args[1] : "SPY";

    DoubleColMatrix markedPoints = MatFile.loadMatrix(matFile, symbol);

    Vector times = markedPoints.col(0);

    int indexes[] = NasdaqTradingStrategy.getIndices(times);

    ArrayList<ExponentialSelfExcitingProcess> processes = NasdaqTradingStrategy.getCalibratedProcesses(matFile, times, indexes, markedPoints);

    NasdaqTradingStrategy.launchModelViewer(processes);

  }

  public ModelViewer(String[] columnHeaders, Object[][] data, ArrayList<ExponentialSelfExcitingProcess> processes)
  {
    initialize();
    DefaultTableModel tableModel = new DefaultTableModel()
    {
      @Override
      public boolean isCellEditable(int arg0, int arg1)
      {
        return false;
      }
    };
    tableModel.setDataVector(data, columnHeaders);
    table.setModel(tableModel);
    this.processes = processes;
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize()
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
    JPanel bottomPanel = new JPanel();

    splitPane.setBottomComponent(bottomPanel);
    chart = new XYChart(2000, 500);
    bottomPanel.add(new XChartPanel<XYChart>(chart));

    frame.getContentPane().add(splitPane, BorderLayout.CENTER);
  }

  public void show()
  {
    ExponentialSelfExcitingProcess firstProcess = processes.get(0);
    double factor = DateUtils.convertTimeUnits(1, TimeUnit.MILLISECONDS, TimeUnit.HOURS);
    Vector times = firstProcess.T.copy().multiply(factor);
    assert times.equals(firstProcess.X.col(0));
    Vector prices = firstProcess.X.col(1);
    chart.addSeries("price", times.toArray(), prices.toArray());
    chart.setTitle( "time units=minutes");
    chart.getStyler().setMarkerSize(0);

    EventQueue.invokeLater(new Runnable()
    {
      public void run()
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

}
