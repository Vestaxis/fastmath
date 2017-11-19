package stochastic.processes.selfexciting.gui;

import static java.lang.System.out;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.print.attribute.standard.PrinterLocation;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;
import stochastic.processes.selfexciting.BoundedParameter;
import util.SpringLayoutUtils;

public class ParameterPanel extends JPanel
{
  private AbstractSelfExcitingProcess process;
  private Runnable callback;

  public ParameterPanel(AbstractSelfExcitingProcess process, Runnable callback)
  {
    super(new SpringLayout());
    this.callback = callback;
    this.process = process;

    for (BoundedParameter param : process.getBoundedParameters())
    {
      double minValue = param.getMin();
      double maxValue = param.getMax();
      String paramName = param.getName();
      add(getParameterRowPanel(paramName, minValue, maxValue));
    }
    SpringLayoutUtils.makeGrid(this, process.getParamCount(), 1, 5, 5, 5, 5);
  }

  HashMap<String, JSlider> sliders = new HashMap<>();

  HashMap<String, JTextField> textFields = new HashMap<>();

  public static final int sliderResolution = 1000;

  boolean sliderEnabled = true;

  public JPanel
         getParameterRowPanel(String paramName,
                              double minValue,
                              double maxValue)
  {

    JPanel rowPanel = new JPanel(new GridLayout(1, 5));
    rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
    JLabel paramNameLabel = new JLabel(paramName);
    paramNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    rowPanel.add(paramNameLabel);

    JLabel minValueLabel = new JLabel(Double.toString(minValue));
    rowPanel.add(minValueLabel);
    minValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    JSlider slider = new JSlider((int) (minValue * sliderResolution), (int) (maxValue * sliderResolution));
    slider.setName(paramName);
    slider.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    rowPanel.add(slider);
    sliders.put(paramName, slider);

    JLabel maxValueLabel = new JLabel(Double.toString(maxValue));
    rowPanel.add(maxValueLabel);
    maxValueLabel.setHorizontalAlignment(SwingConstants.LEFT);
    JTextField textField = new JTextField();
    textFields.put(paramName, textField);

    ChangeListener sliderUpdated = sliderEvent -> {
      double value = (double) slider.getValue() / (double) sliderResolution;
      if (sliderEnabled)
      {
        Field field = process.getField(paramName);
        try
        {
          if (value < minValue)
          {
            value = minValue;
          }
          else if (value > maxValue)
          {
            value = maxValue;
          }
          if (field.getType().equals(double.class))
          {
            field.setDouble(process, value);
          }
          else if (field.getType().equals(int.class))
          {
            field.setInt(process, (int) value);
          }
          else
          {
            throw new UnsupportedOperationException("unhandled field type " + field.getType() + " in " + this.getClass().getSimpleName());
          }
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
          throw new UnsupportedOperationException(e.getMessage(), e);
        }
      }
      textField.setText(Double.toString(value));
      if (callback != null)
      {
        callback.run();
      }
    };
    slider.addChangeListener(sliderUpdated);
    sliderUpdated.stateChanged(null);

    rowPanel.add(textField);

    return rowPanel;
  }

  public void
         setParameterValue(String name,
                           double fieldValue)
  {
    BoundedParameter boundedParameter = process.getBoundedParameter(name);
    double parameterRange = boundedParameter.getMax() - boundedParameter.getMin();
    JSlider slider = sliders.get(name);
    JTextField textField = textFields.get(name);
    int sliderRange = slider.getMaximum() - slider.getMinimum();
    double paramδ = parameterRange / sliderRange;

    slider.setValue(slider.getMinimum() + (int) ((process.getFieldValue(name) - slider.getMinimum()) / paramδ));
    textField.setText(Double.toString(fieldValue));

  }
}