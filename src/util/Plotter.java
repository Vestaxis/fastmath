package util;

import static java.util.stream.IntStream.rangeClosed;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import fastmath.Vector;

public class Plotter
{

  public static XChartPanel<XYChart> plot(UnivariateFunction func, double left, double right)
  {
    return plot("f", func, left, right, 1000);
  }

  public static XChartPanel<XYChart> plot(String name, UnivariateFunction func, double left, double right)
  {
    return plot(name, func, left, right, 1000);
  }

  public static XChartPanel<XYChart> plot(String name, UnivariateFunction func, double left, double right, int n)
  {
    XYChart chart = chart(name, func, left, right, n);
    return new XChartPanel<XYChart>(chart);
  }

  public static XYChart chart(String name, UnivariateFunction func, double left, double right, int n)
  {
    XYChart chart = new XYChart(800, 600);
    chart.getStyler().setXAxisMin(left);
    chart.getStyler().setXAxisMax(right);
    chart.getStyler().setMarkerSize(0);
    double W = right - left;
    double dt = W / n;
    double t = left;
    double x[] = new double[n];
    double y[] = new double[n];

    for (int i = 0; i < n; i++, t = left + dt * i)
    {
      x[i] = t;
      y[i] = func.value(t);
    }
    chart.addSeries(name, x, y);
    return chart;
  }

  public static SwingWrapper<XYChart> display(XYChart chart)
  {
    SwingWrapper<XYChart> wrapper = new SwingWrapper<>(chart);
    wrapper.displayChart();
    return wrapper;
  }

  public static XYChart plot(Vector y)
  {
    return plot("A", y);
  }

  public static XYChart plot(String name, Vector y)
  {
    return plot(new Vector(rangeClosed(1, y.size()).mapToDouble(i -> i)), y, name);
  }

  public static XYChart plot(Vector x, Vector y)
  {
    return plot(x, y, "f");
  }

  public static XYChart plot(Vector x, Vector y, String name)
  {
    XYChart chart = new XYChart(800, 600);
    double left = x.fmin();
    chart.getStyler().setXAxisMin(left);
    double right = x.fmax();
    chart.getStyler().setXAxisMax(right);
    chart.getStyler().setMarkerSize(0);
    double W = right - left;
    double t = left;
    int n = x.size();
    assert n == y.size();

    chart.addSeries(name, x.toArray(), y.toArray());
    new SwingWrapper<XYChart>(chart).displayChart();
    return chart;

  }

}
