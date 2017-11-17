package stochastic.processes.selfexciting.gui;

import static java.lang.System.err;
import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;

/**
 * TODO: add emperical autocorrelation and histogram
 * 
 */
public class ModelFitterDesign
{

  private JFrame frame;
  private JComboBox<SelfExcitingProcessFactory.Type> processTypeComboBox;
  private ParameterPanel parameterPanel;
  private AbstractSelfExcitingProcess process;
  private Container contentPane;

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

    processTypeComboBox = new JComboBox<>(SelfExcitingProcessFactory.Type.values());
    processTypeComboBox.addActionListener(this::refreshTypeComboBox);
    contentPane.add(processTypeComboBox, BorderLayout.PAGE_START);
    refreshProcess();
    
    updateParameterPanel();

    refreshTypeComboBox(null);

    contentPane.add(new KernelPanel(process), BorderLayout.PAGE_END);

    doLayout();
  }

  public void
         updateParameterPanel()
  {
    if (parameterPanel != null)
    {
      contentPane.remove(parameterPanel);
    }
    contentPane.add(parameterPanel = new ParameterPanel(process), BorderLayout.CENTER);
    doLayout();
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
      out.println("Switched kernel to " + type );
      process = type.instantiate(1);
      updateParameterPanel();
    }
    return process;
  }

  public Type
         getSelectedType()
  {
    return SelfExcitingProcessFactory.Type.values()[processTypeComboBox.getSelectedIndex()];
  }

  private void
          refreshPlots()
  {
    // TODO Auto-generated method stub

  }

}
