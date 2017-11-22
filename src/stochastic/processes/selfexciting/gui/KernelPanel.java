package stochastic.processes.selfexciting.gui;

import static java.lang.System.out;
import static util.Plotter.plot;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.XYStyler;

import fastmath.Pair;
import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;
import util.Plotter;
import util.RelativeLayout;

public class KernelPanel extends JPanel
{
  private static final String INT_HAZARD = "∫h(t)";
  private static final String INT_KERNEL = "∫ν(t)dt";
  private static final String HAZARD = "h(t)";
  private static final String KERNEL = "ν(t)";
  private static final int ν_MAXRANGE = 100;
  private static final int SAMPLES = 1000;
  private static final int IH_MAXRANGE = 20;
  private static final String ANTI_H = "anti(∫h)";
  private AbstractSelfExcitingProcess process;
  private XChartPanel<XYChart> inverseIntegratedHazardChartPanel;
  private XChartPanel<XYChart> impulseResponseChartPanel;
  private XChartPanel<XYChart> integratedImpulseResponseChartPanel;
  private XYChart inverseIntegratedHazardChart;
  private XYChart impulseResponseChart;
  private XYChart integratedImpulseResponseChart;
  private XChartPanel<XYChart> HphasePanel;
  double H = 0;

  public KernelPanel(AbstractSelfExcitingProcess process)
  {
    super(new RelativeLayout());
    this.process = process;
    assert process != null;

//    inverseIntegratedHazardChartPanel = plot("h", ANTI_H, process == null ? t -> 0 : process::invH, 0, IH_MAXRANGE, chart -> {
//      chart.setYAxisTitle("t (ms)");
//    });
    //inverseIntegratedHazardChart = inverseIntegratedHazardChartPanel.getChart();

    impulseResponseChartPanel = plot("t (ms)", KERNEL, process == null ? t -> 0 : process::f, 0, ν_MAXRANGE);
    impulseResponseChart = impulseResponseChartPanel.getChart();

    plot(impulseResponseChart, HAZARD, process == null ? t -> 0 : process::h, 0, 100);

    integratedImpulseResponseChartPanel = plot("t (ms)", INT_KERNEL, process == null ? t -> 0 : process::F, 0, ν_MAXRANGE, chart -> {
      XYStyler styler = chart.getStyler();
      styler.setYAxisMin(0.0);
      styler.setYAxisMax(1.0);
    });
    integratedImpulseResponseChart = integratedImpulseResponseChartPanel.getChart();
    plot(integratedImpulseResponseChart, INT_HAZARD, process == null ? t -> 0 : process::H, 0, ν_MAXRANGE);

    add(impulseResponseChartPanel);
    add(integratedImpulseResponseChartPanel);
    //add(inverseIntegratedHazardChartPanel);
//    add( HphasePanel = plot("t (ms)", "Hphase(H,t)", process == null ? t -> 0 : t -> process.Hphase(H, t), 0, 3) );
//    JSlider Hslider = new JSlider(SwingConstants.VERTICAL, 0, 1000, 500);
//    Hslider.addChangeListener(event -> {
//      H = Hslider.getValue() / 1000;
//    });
//    add(Hslider);
//    add(new JLabel("H"));

  }

  public void
         setProcess(AbstractSelfExcitingProcess process)
  {
    this.process = process;
    // chart.updateXYSeries("sine", data[0], data[1], null);
  }

  public void
         refreshGraphs()
  {
    out.println("refresh graphs " + process.getParamString());
    Pair<double[], double[]> invhSample = Plotter.sampleFunction(process::invH, SAMPLES, 0, IH_MAXRANGE, t -> t);
//    inverseIntegratedHazardChart.updateXYSeries(ANTI_H, invhSample.left, invhSample.right, null);
//    inverseIntegratedHazardChartPanel.revalidate();
//    inverseIntegratedHazardChartPanel.repaint();

    Pair<double[], double[]> νSample = Plotter.sampleFunction(process::f, SAMPLES, 0, ν_MAXRANGE, t -> t);
    impulseResponseChart.updateXYSeries(KERNEL, νSample.left, νSample.right, null);

    Pair<double[], double[]> hSample = Plotter.sampleFunction(process::h, SAMPLES, 0, ν_MAXRANGE, t -> t);
    impulseResponseChart.updateXYSeries(HAZARD, hSample.left, hSample.right, null);
    impulseResponseChartPanel.revalidate();
    impulseResponseChartPanel.repaint();

    Pair<double[], double[]> ihSample = Plotter.sampleFunction(process::H, SAMPLES, 0, ν_MAXRANGE, t -> t);
    Pair<double[], double[]> iνSample = Plotter.sampleFunction(process::F, SAMPLES, 0, ν_MAXRANGE, t -> t);

    integratedImpulseResponseChart.updateXYSeries(INT_HAZARD, ihSample.left, ihSample.right, null);
    integratedImpulseResponseChart.updateXYSeries(INT_KERNEL, iνSample.left, iνSample.right, null);

    integratedImpulseResponseChartPanel.revalidate();
    integratedImpulseResponseChartPanel.repaint();
  }

}
