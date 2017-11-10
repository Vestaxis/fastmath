package util;

import static java.util.stream.IntStream.rangeClosed;

import java.util.function.DoubleFunction;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;

import fastmath.Vector;

public class Plotter
{

  public static XChartPanel<XYChart>
         plot(String xAxisTitle,
              String yAxisTitle,
              UnivariateFunction func,
              double left,
              double right)
  {
    return plot(xAxisTitle, yAxisTitle, func, left, right, 1000);
  }

  public static XChartPanel<XYChart>
         plot(String xAxisTitle,
              String yAxisTitle,
              UnivariateFunction func,
              double left,
              double right,
              int n)
  {
    XYChart chart = chart(xAxisTitle, yAxisTitle, func, left, right, n, t -> t);
    return new XChartPanel<XYChart>(chart);
  }

  public static XYChart
         chart(String xAxisTitle,
               String yAxisTitle,
               UnivariateFunction func,
               double left,
               double right,
               DoubleFunction<Double> timeAxisTransformer)
  {
    return chart(xAxisTitle, yAxisTitle, func, left, right, 1000, timeAxisTransformer);
  }

  public static XYChart
         chart(String xAxisTitle,
               String yAxisTitle,
               UnivariateFunction func,
               double left,
               double right,
               int n,
               DoubleFunction<Double> timeAxisTransformer)
  {
    XYChart chart = new XYChart(800, 600);
    chart.setXAxisTitle(xAxisTitle);
    chart.setYAxisTitle(yAxisTitle);
    chart.getStyler().setXAxisMin(timeAxisTransformer.apply(left));
    chart.getStyler().setXAxisMax(timeAxisTransformer.apply(right));
    chart.getStyler().setMarkerSize(0);
    double W = right - left;
    double dt = W / n;
    double t = left;
    double x[] = new double[n];
    double y[] = new double[n];

    for (int i = 0; i < n; i++, t = left + dt * i)
    {
      x[i] = timeAxisTransformer.apply(t);
      y[i] = func.value(t);
    }
    chart.addSeries(yAxisTitle, x, y);
    return chart;
  }

  public static SwingWrapper<XYChart>
         display(XYChart chart)
  {
    SwingWrapper<XYChart> wrapper = new SwingWrapper<>(chart);
    wrapper.displayChart();
    return wrapper;
  }

  public static XYChart
         plot(Vector y)
  {
    return plot(y, y.getName() != null ? y.getName() : "A");
  }

  public static XYChart
         plot(Vector y,
              String seriesName)
  {
    return plot(new Vector(rangeClosed(1, y.size()).mapToDouble(i -> i)), y, seriesName);
  }

  public static XYChart
         plot(Vector x,
              Vector y)
  {
    XYChart chart = plot(x, y, "f");
    if (x.getName() != null)
    {
      chart.setXAxisTitle(x.getName());
    }
    if (y.getName() != null)
    {
      chart.setYAxisTitle(y.getName());
    }
    return chart;
  }

  /**
   * Constructs an {@link XYChart} containing an {@link XYSeries}
   * 
   * @param x
   * @param y
   * @param seriesName
   *          passed to {@link XYChart#addSeries(String, double[], double[])}
   * @return
   */
  public static XYChart
         plot(Vector x,
              Vector y,
              String seriesName)
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
    chart.addSeries(seriesName, x.toArray(), y.toArray());
    return chart;
  }

  public static XChartPanel<XYChart>
         plot(String string,
              String string2,
              UnivariateFunction univariateFunction,
              double fmin,
              double fmax,
              int i,
              DoubleFunction<Double> timeAxisTransformer)
  {
    return new XChartPanel<XYChart>(chart(string, string2, univariateFunction, fmin, fmax, i, timeAxisTransformer));
  }

}
