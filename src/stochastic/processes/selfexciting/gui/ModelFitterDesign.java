package stochastic.processes.selfexciting.gui;

import static java.lang.System.err;
import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import fastmath.arb.Real;
import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;
import stochastic.processes.selfexciting.ExponentialSelfExcitingProcess;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;

public class ModelFitterDesign
{

  private JFrame frame;
  private JComboBox<SelfExcitingProcessFactory.Type> processTypeComboBox;
  private ParameterPanel parameterPanel;
  private ExponentialSelfExcitingProcess process;
  private Container contentPane;
  private KernelPanel kernelPanel;
  private DefaultTableModel coeffecientModel;
  private JTable coeffecientTable;

  /**
   * Launch the application.
   */
  public static void
         main(String[] args)
  {
    EventQueue.invokeLater(() -> {
      try
      {
        ModelFitterDesign window = new ModelFitterDesign();
        window.frame.setVisible(true);
      }
      catch (Exception e)
      {
        e.printStackTrace(err);
      }
    });
  }

  /**
   * Create the application.
   */
  public ModelFitterDesign()
  {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   * 
   * @wbp.parser.entryPoint
   */
  private void
          initialize()
  {
    frame = new JFrame();
    frame.setBounds(100, 100, 2000, 700);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());

    JPanel topPanel = getTopPanel();
    topPanel.setMaximumSize(new Dimension(3000,500));
    contentPane.add(topPanel, BorderLayout.PAGE_START);
    refreshProcess();

    updateParameterPanel();

    refreshTypeComboBox(null);

    contentPane.add(kernelPanel = new KernelPanel(process), BorderLayout.PAGE_END);

    doLayout();
  }

  public JPanel
         getTopPanel()
  {
    JPanel topPanel = new JPanel(new BorderLayout());

    Type[] processTypes = SelfExcitingProcessFactory.Type.values();
    processTypeComboBox = new JComboBox<>(processTypes);
    processTypeComboBox.addActionListener(this::refreshTypeComboBox);

    JPanel topLeftPanel = new JPanel(new BorderLayout());

    topLeftPanel.add(processTypeComboBox, BorderLayout.PAGE_START);
    topLeftPanel.add(new JButton("Load points"), BorderLayout.PAGE_END);
    topPanel.add(topLeftPanel, BorderLayout.WEST);

    process = (ExponentialSelfExcitingProcess) Type.values()[0].instantiate(1);

    coeffecientModel = new DefaultTableModel(process != null ? process.order() : 0, tableColumnNames.length);
    coeffecientModel.setColumnIdentifiers(tableColumnNames);
    coeffecientTable = new JTable(coeffecientModel);
    setAmplitudeDecayValues();

    JPanel topRightPanel = new JPanel(new BorderLayout());

    tableScroller = new JScrollPane(coeffecientTable);
    topRightPanel.add(tableScroller, BorderLayout.PAGE_START);
    topPanel.add(topRightPanel, BorderLayout.CENTER);
    topRightPanel.revalidate();
    // ModelViewer.getLogPriceChart(process);
    topRightBottomPanel = new JPanel();
    topRightPanel.add(topRightBottomPanel, BorderLayout.PAGE_END);
    return topPanel;
  }
  
  JPanel topRightBottomPanel;
  
  public void
         updateParameterPanel()
  {
    if (parameterPanel != null)
    {
      topRightBottomPanel.remove(parameterPanel);
    }
    topRightBottomPanel.add(parameterPanel = new ParameterPanel(process, this::onParameterUpdated), BorderLayout.PAGE_END);
    doLayout();
  }

  public void
         onParameterUpdated()
  {
    if (kernelPanel != null)
    {
      kernelPanel.refreshGraphs();
    }
    setAmplitudeDecayValues();
  }

  public void
         doLayout()
  {
    frame.validate();
    frame.pack();
  }

  public void
         refreshTypeComboBox(ActionEvent event)
  {
    refreshProcess();
    setAmplitudeDecayValues();

    /**
     * TODO: add m and M here
     */
    // JPanel mrowPanel = getParameterRow("m", 0.1, 5, 1000);
    // JPanel MrowPanel = getParameterRowPanel("M", 1, 15, 15);
    // JSpinner Mspinner = new JSpinner(new SpinnerNumberModel(15, 1, 15, 1));

    // parameterPanel.add(Mspinner);
    // JPanel MowPanel = new JPanel(new GridLayout(1, 5));

  }

  public AbstractSelfExcitingProcess
         refreshProcess()
  {
    Type type = getSelectedType();
    if (process == null || !process.getType().equals(type))
    {
      out.println("Switched kernel to " + type);
      process = (ExponentialSelfExcitingProcess) type.instantiate(1);
      coeffecientModel.setRowCount(process.order());
      updateParameterPanel();
      if (kernelPanel != null)
      {
        kernelPanel.setProcess(process);
      }

    }
    setAmplitudeDecayValues();
    tableScroller.revalidate();

    return process;
  }

  public void
         setAmplitudeDecayValues()
  {
    for (int i = 0; i < process.order(); i++)
    {
      double amplitude = process.α(i);
      double decayRate = process.β(i);
      Real amplifiedJointDecayRate = process.γ(i);
      double halfLife = process.getHalfLife(i);
      coeffecientModel.setValueAt(i, i, 0);
      coeffecientModel.setValueAt(amplitude, i, 1);
      coeffecientModel.setValueAt(decayRate, i, 2);
      coeffecientModel.setValueAt(amplifiedJointDecayRate.toString(), i, 3);
      coeffecientModel.setValueAt(halfLife, i, 4);
    }
    coeffecientTable.repaint();
  }

  public Type
         getSelectedType()
  {
    return SelfExcitingProcessFactory.Type.values()[processTypeComboBox.getSelectedIndex()];
  }

  String[] tableColumnNames = new String[]
  { "order", "α", "β", "γ", "half-life" };

  private JScrollPane tableScroller;

}
