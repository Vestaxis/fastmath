package stochastic.processes.selfexciting.gui;

import static java.lang.System.out;

import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;
import stochastic.processes.selfexciting.BoundedParameter;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;

public class ModelFitterDesign
{

  private JFrame frame;
  private JComboBox<SelfExcitingProcessFactory.Type> processTypeComboBox;
  private int resolution = 1000;

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

    processTypeComboBox = new JComboBox<>(SelfExcitingProcessFactory.Type.values());
    refreshTypeComboBox(null);
    processTypeComboBox.addActionListener(this::refreshTypeComboBox);

  }

  public void
         refreshTypeComboBox(ActionEvent event)
  {
    Container contentPane = frame.getContentPane();
    Type type = SelfExcitingProcessFactory.Type.values()[processTypeComboBox.getSelectedIndex()];
    out.println(type + " ...");
    AbstractSelfExcitingProcess process = type.instantiate(1);
    contentPane.setLayout(new GridLayout(1 + process.getBoundedParameters().length, 3, 0, 0));
    contentPane.removeAll();
    contentPane.add(new JLabel("Kernel"));
    contentPane.add(processTypeComboBox);
    contentPane.add(new JLabel(""));
    // ArrayList<JTextField> parameterTextFields = new ArrayList<JTextField>( );
    for (BoundedParameter param : process.getBoundedParameters())
    {
      contentPane.add(new JLabel(param.getName()));
      JSlider slider = new JSlider((int) (param.getMin() * resolution), (int) (param.getMax() * resolution));
      slider.setName(param.getName());
      contentPane.add(slider);
      slider.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
      JTextField textField = new JTextField();
      slider.addChangeListener(sliderEvent -> textField.setText(Double.toString(slider.getValue() / resolution)));
      contentPane.add(textField);
      // slider.set
    }

  }

}
