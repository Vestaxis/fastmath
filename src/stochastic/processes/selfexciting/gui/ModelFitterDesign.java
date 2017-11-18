package stochastic.processes.selfexciting.gui;

import static java.lang.System.err;
import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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
  private DefaultTableModel amplitudeDecayModel;
  private JTable amplitudeDecayTable;

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
   */
  private void
          initialize()
  {
    frame = new JFrame();
    frame.setBounds(100, 100, 2000, 700);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());

    JPanel topPanel = new JPanel(new BorderLayout());

    processTypeComboBox = new JComboBox<>(SelfExcitingProcessFactory.Type.values());
    processTypeComboBox.addActionListener(this::refreshTypeComboBox);

    JPanel topLeftPanel = new JPanel(new BorderLayout());

    topPanel.add(processTypeComboBox, BorderLayout.WEST);
    topLeftPanel.add(processTypeComboBox, BorderLayout.PAGE_START);
    topLeftPanel.add(new JButton("Load points"), BorderLayout.PAGE_END);
    topPanel.add(topLeftPanel, BorderLayout.WEST);

    process = (ExponentialSelfExcitingProcess) Type.values()[0].instantiate(1);

    amplitudeDecayModel = new DefaultTableModel(process != null ? process.order() : 0, tableColumnNames.length);
    amplitudeDecayModel.setColumnIdentifiers(tableColumnNames);
    amplitudeDecayTable = new JTable(amplitudeDecayModel);
    setAmplitudeDecayValues();

    tableScroller = new JScrollPane(amplitudeDecayTable);
    topPanel.add(tableScroller, BorderLayout.CENTER);

    contentPane.add(topPanel, BorderLayout.PAGE_START);
    refreshProcess();

    updateParameterPanel();

    refreshTypeComboBox(null);

    contentPane.add(kernelPanel = new KernelPanel(process), BorderLayout.PAGE_END);

    doLayout();
  }

  public void
         updateParameterPanel()
  {
    if (parameterPanel != null)
    {
      contentPane.remove(parameterPanel);
    }
    contentPane.add(parameterPanel = new ParameterPanel(process, this::onParameterUpdated), BorderLayout.CENTER);
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
      amplitudeDecayModel.setRowCount(process.order());
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
      amplitudeDecayModel.setValueAt(i, i, 0);
      amplitudeDecayModel.setValueAt(amplitude, i, 1);
      amplitudeDecayModel.setValueAt(decayRate, i, 2);
      amplitudeDecayModel.setValueAt(amplifiedJointDecayRate.toString(), i, 3);
      amplitudeDecayModel.setValueAt(halfLife, i, 4);
      out.format("i=%d α=%f β=%f γ=%s halfLife=%f\n", i, amplitude, decayRate, amplifiedJointDecayRate, halfLife);
    }
    amplitudeDecayTable.repaint();
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
