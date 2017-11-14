package stochastic.processes.selfexciting.gui;

import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;
import stochastic.processes.selfexciting.BoundedParameter;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;
import util.SpringLayoutUtils;

public class ModelFitterDesign
{

  private JFrame frame;
  private JComboBox<SelfExcitingProcessFactory.Type> processTypeComboBox;
  private int resolution = 1000;
  private JPanel parameterPanel;

  /**
   * Launch the application.
   */
  public static void
         main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void
             run()
      {
        try
        {
          ModelFitterDesign window = new ModelFitterDesign();
          window.frame.setVisible(true);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
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
    frame.setBounds(100, 100, 450, 300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());

    processTypeComboBox = new JComboBox<>(SelfExcitingProcessFactory.Type.values());
    contentPane.add(processTypeComboBox, BorderLayout.PAGE_START);
    parameterPanel = new JPanel();
    contentPane.add(parameterPanel, BorderLayout.CENTER);

    processTypeComboBox.addActionListener(this::refreshTypeComboBox);
    refreshTypeComboBox(null);
  }

  public void
         refreshTypeComboBox(ActionEvent event)
  {
    Type type = SelfExcitingProcessFactory.Type.values()[processTypeComboBox.getSelectedIndex()];
    out.println(type + " ...");
    AbstractSelfExcitingProcess process = type.instantiate(1);
    parameterPanel.setLayout(new SpringLayout());
    // ArrayList<JTextField> parameterTextFields = new ArrayList<JTextField>( );
    parameterPanel.removeAll();
    for (BoundedParameter param : process.getBoundedParameters())
    {
      parameterPanel.add(getParameterRow(param));
    }
    SpringLayoutUtils.makeGrid(parameterPanel, process.getBoundedParameters().length, 1, 5, 5, 5, 5);
    frame.validate();
    frame.pack();
  }

  public JPanel
         getParameterRow(BoundedParameter param)
  {
    JPanel rowPanel = new JPanel(new GridLayout(1, 5));
    rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
    rowPanel.add(new JLabel(param.getName()));

    JLabel minValueLabel = new JLabel(Double.toString(param.getMin()));
    rowPanel.add(minValueLabel);
    minValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    JSlider slider = new JSlider((int) (param.getMin() * resolution), (int) (param.getMax() * resolution));
    slider.setName(param.getName());
    slider.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    rowPanel.add(slider);

    JLabel maxValueLabel = new JLabel(Double.toString(param.getMax()));
    rowPanel.add(maxValueLabel);
    maxValueLabel.setHorizontalAlignment(SwingConstants.LEFT);
    JTextField textField = new JTextField();

    ChangeListener sliderUpdated = sliderEvent -> textField.setText(Double.toString((double) slider.getValue() / (double) resolution));
    slider.addChangeListener(sliderUpdated);
    sliderUpdated.stateChanged(null);

    rowPanel.add(textField);

    return rowPanel;
  }

}
