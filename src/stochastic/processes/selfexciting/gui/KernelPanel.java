package stochastic.processes.selfexciting.gui;

import static java.lang.System.out;
import static util.Plotter.plot;

import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.XYStyler;

import fastmath.Pair;
import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;
import util.Plotter;

public class KernelPanel extends JPanel
{
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

  public KernelPanel(AbstractSelfExcitingProcess process)
  {
    super(new GridLayout(1, 3));
    this.process = process;
    assert process != null;

    inverseIntegratedHazardChartPanel = plot("h", ANTI_H, process == null ? t -> 0 : process::ih, 0, IH_MAXRANGE, chart -> {
      chart.setYAxisTitle("t (ms)");
    });
    inverseIntegratedHazardChart = inverseIntegratedHazardChartPanel.getChart();

    impulseResponseChartPanel = plot("t (ms)", KERNEL, process == null ? t -> 0 : process::ν, 0, ν_MAXRANGE);
    impulseResponseChart = impulseResponseChartPanel.getChart();

    plot(impulseResponseChart, HAZARD, process == null ? t -> 0 : process::h, 0, 100);

    integratedImpulseResponseChartPanel = plot("t (ms)", "∫ν(t)dt", process == null ? t -> 0 : process::iν, 0, 100, chart -> {
      XYStyler styler = chart.getStyler();
      styler.setYAxisMin(0.0);
      styler.setYAxisMax(1.0);
    });
    plot(integratedImpulseResponseChartPanel.getChart(), "∫h(t)", process == null ? t -> 0 : process::ih, 0, 100);

    add(impulseResponseChartPanel);
    add(integratedImpulseResponseChartPanel);
    add(inverseIntegratedHazardChartPanel);
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
    Pair<double[], double[]> ihSample = Plotter.sampleFunction(process::ih, SAMPLES, 0, IH_MAXRANGE, t -> t);
    inverseIntegratedHazardChart.updateXYSeries(ANTI_H, ihSample.left, ihSample.right, null);
    inverseIntegratedHazardChartPanel.revalidate();
    inverseIntegratedHazardChartPanel.repaint();

    Pair<double[], double[]> νSample = Plotter.sampleFunction(process::ν, SAMPLES, 0, ν_MAXRANGE, t -> t);
    impulseResponseChart.updateXYSeries(KERNEL, νSample.left, νSample.right, null);

    Pair<double[], double[]> hSample = Plotter.sampleFunction(process::h, SAMPLES, 0, ν_MAXRANGE, t -> t);
    impulseResponseChart.updateXYSeries(HAZARD, hSample.left, hSample.right, null);
    impulseResponseChartPanel.revalidate();
    impulseResponseChartPanel.repaint();

  }

}
