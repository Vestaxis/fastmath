package util;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class Plotter
{

  public static void plot(String name, UnivariateFunction func, double left, double right)
  {
    plot(name, func, left, right, 1000);
  }

  public static void plot(String name, UnivariateFunction func, double left, double right, int n)
  {
    XYChart chart = new XYChart(800, 600);
    chart.getStyler().setXAxisMin(left);
    chart.getStyler().setXAxisMax(right);
    double W = right - left;
    double dt = W / n;
    double t = left;
    double x[] = new double[n];
    double y[] = new double[n];

    for (int i = 0; i < n; i++, t = left + dt * i)
    {
      x[i] = t;
      y[i] = func.value(t);
      // out.println( x[i] + "=" + y[i] );
    }
    chart.addSeries(name, x, y);
    new SwingWrapper<XYChart>(chart).displayChart();
  }

}
