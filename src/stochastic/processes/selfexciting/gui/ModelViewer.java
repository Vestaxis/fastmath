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
import stochastic.processes.selfexciting.NasdaqTradingStrategy;
import util.DateUtils;

public class ModelViewer
{

  public JFrame frame;
  private JTable table;

  public static void main(String args[]) throws IOException
  {
    final String matFile = args.length > 0 ? args[0] : "/home/stephen/fm/SPY.mat";
    final String symbol = args.length > 1 ? args[1] : "SPY";

    Vector times = SelfExcitingProcessEstimator.loadTimes(matFile, symbol);

    int indexes[] = NasdaqTradingStrategy.getIndices(times);

    ArrayList<ExponentialSelfExcitingProcess> processes = NasdaqTradingStrategy.getCalibratedProcesses(matFile, times, indexes);

    NasdaqTradingStrategy.launchModelViewer(processes);

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
