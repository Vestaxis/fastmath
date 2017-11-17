package stochastic.processes.selfexciting.gui;

import static util.Plotter.plot;

import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.XYStyler;

import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;

public class KernelPanel extends JPanel
{
  public KernelPanel(AbstractSelfExcitingProcess process)
  {
    super(new GridLayout(1, 3));

    XChartPanel<XYChart> inverseIntegratedHazardChartPanel = plot("h", "anti(∫h)", process == null ? t -> 0 : process::ih, 0, 20, chart -> {
      chart.setYAxisTitle("t (ms)");
    });

    XChartPanel<XYChart> impulseResponseChartPanel = plot("t (ms)", "ν(t)", process == null ? t -> 0 : process::ν, 0, 100);
    plot(impulseResponseChartPanel.getChart(), "h(t)", process == null ? t -> 0 : process::h, 0, 100);

    XChartPanel<XYChart> integratedImpulseResponseChartPanel = plot("t (ms)", "∫ν(t)dt", process == null ? t -> 0 : process::iν, 0, 100, chart -> {
      XYStyler styler = chart.getStyler();
      styler.setYAxisMin(0.0);
      styler.setYAxisMax(1.0);
    });
    plot(integratedImpulseResponseChartPanel.getChart(), "∫h(t)", process == null ? t -> 0 : process::ih, 0, 100);

    add(impulseResponseChartPanel);
    add(integratedImpulseResponseChartPanel);
    add(inverseIntegratedHazardChartPanel);
  }

}
