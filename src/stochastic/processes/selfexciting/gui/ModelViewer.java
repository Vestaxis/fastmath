package stochastic.processes.selfexciting.gui;

import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import fastmath.Vector;
import fastmath.Vector.Condition;
import stochastic.processes.point.MarkedPointProcess;
import stochastic.processes.selfexciting.ExponentialSelfExcitingProcess;
import stochastic.processes.selfexciting.ExtendedApproximatePowerlawSelfExcitingProcess;
import stochastic.processes.selfexciting.SelfExcitingProcessEstimator;
import util.DateUtils;

public class ModelViewer
{

  public JFrame frame;
  private JTable table;

  public static void main(String args[]) throws IOException
  {
    final String matFile = args.length > 0 ? args[0] : "/home/stephen/fm/SPY.mat";
    final String symbol = args.length > 1 ? args[1] : "SPY";

    ArrayList<ExponentialSelfExcitingProcess> processes = new ArrayList<>();
    int n = (int) (MarkedPointProcess.tradingDuration / SelfExcitingProcessEstimator.W);
    int indexes[] = new int[n];

    Vector data = SelfExcitingProcessEstimator.loadData(matFile, symbol);
    out.println("Loading " + n + " pieces");
    for (int i = 0; i < n; i++)
    {
      double startPoint = MarkedPointProcess.openTime + ((i) * SelfExcitingProcessEstimator.W);
      double endPoint = MarkedPointProcess.openTime + ((i + 1) * SelfExcitingProcessEstimator.W);

      double t = DateUtils.convertTimeUnits(endPoint, TimeUnit.HOURS, TimeUnit.MILLISECONDS);
      int idx = data.find(t, Condition.GTE, 0);
      if (i == n && idx == -1)
      {
        idx = data.size() - 1;
      }
      indexes[i] = idx;
    }

    for (int i = 0; i < n; i++)
    {
      // TODO: cool also load from the test%d.mat files but they need to be renamed to
      // something like symbol-piece-%d.mat first
      Vector slice = data.slice(i == 0 ? 0 : indexes[i - 1], indexes[i]);
      ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();
      process.T = slice;

      process.loadParameters(new File(matFile + ".eapl." + i + ".model"));
      processes.add(process);

    }

    List<Object[]> processStats = processes.stream()
                                           .map(process -> process.evaluateParameterStatistics(process.getParameters().toArray()))
                                           .collect(Collectors.toList());
    int M = processStats.size();
    int N = processes.get(0).getColumnHeaders().length;
    Object[][] stats = new Object[M][N];
    for (int i = 0; i < M; i++)
    {
      for (int j = 0; j < N; j++)
      {
        stats[i][j] = processStats.get(i)[j];
      }
    }
    ModelViewer viewer = new ModelViewer(processes.get(0).getColumnHeaders(), stats);
    viewer.show();

  }

  public ModelViewer(String[] columnHeaders, Object[][] data)
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
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize()
  {
    frame = new JFrame();
    frame.setBounds(100, 100, 983, 557);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    table = new JTable();
    table.setCellSelectionEnabled(true);
    table.setColumnSelectionAllowed(true);

    JScrollPane scrollPane = new JScrollPane(table);
    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
  }

  public void show()
  {

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
