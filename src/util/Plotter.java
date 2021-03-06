package util;

import static java.util.stream.IntStream.rangeClosed;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.XYStyler;

import fastmath.Pair;
import fastmath.Vector;
import fastmath.Vector.Condition;

public class Plotter
{

  public static XChartPanel<XYChart>
         plot(String xAxisTitle,
              String seriesName,
              UnivariateFunction func,
              double left,
              double right,
              Consumer<XYChart> chartInitializer)
  {
    return plot(xAxisTitle, seriesName, func, left, right, 1000, chartInitializer);
  }

  public static XChartPanel<XYChart>
         plot(String xAxisTitle,
              String seriesName,
              UnivariateFunction func,
              double left,
              double right)
  {
    return plot(xAxisTitle, seriesName, func, left, right, 1000);
  }

  public static XChartPanel<XYChart>
         plot(XYChart chart,
              String seriesName,
              UnivariateFunction func,
              double left,
              double right)
  {
    return plot(chart, seriesName, func, left, right, 1000);
  }

  public static XChartPanel<XYChart>
         plot(XYChart chart,
              String seriesName,
              UnivariateFunction func,
              double left,
              double right,
              int n)
  {
    return new XChartPanel<XYChart>(chart(chart, seriesName, func, left, right, n, t -> t));
  }

  public static XChartPanel<XYChart>
         plot(String xAxisTitle,
              String seriesName,
              UnivariateFunction func,
              double left,
              double right,
              int n)
  {
    XYChart chart = chart(xAxisTitle, seriesName, func, left, right, n, t -> t);
    return new XChartPanel<XYChart>(chart);
  }

  public static XChartPanel<XYChart>
         plot(String xAxisTitle,
              String seriesName,
              UnivariateFunction func,
              double left,
              double right,
              int n,
              Consumer<XYChart> chartInitializer)
  {
    XYChart chart = chart(xAxisTitle, seriesName, func, left, right, n, t -> t, chartInitializer);
    return new XChartPanel<XYChart>(chart);
  }

  public static XChartPanel<XYChart>
         plot(String xAxisTitle,
              String yAxisTitle,
              UnivariateFunction func,
              double left,
              double right,
              int n,
              ToDoubleFunction<Double> xAxisTransformer)
  {
    XYChart chart = chart(xAxisTitle, yAxisTitle, func, left, right, n, xAxisTransformer);
    return new XChartPanel<XYChart>(chart);
  }

  public static XChartPanel<XYChart>
         plot(String xAxisTitle,
              String yAxisTitle,
              UnivariateFunction func,
              double left,
              double right,
              int n,
              ToDoubleFunction<Double> xAxisTransformer,
              Consumer<XYChart> chartInitializer)
  {
    XYChart chart = chart(xAxisTitle, yAxisTitle, func, left, right, n, xAxisTransformer);
    chartInitializer.accept(chart);
    return new XChartPanel<XYChart>(chart);
  }

  public static XYChart
         chart(String xAxisTitle,
               String seriesName,
               UnivariateFunction func,
               double left,
               double right,
               ToDoubleFunction<Double> timeAxisTransformer)
  {
    return chart(xAxisTitle, seriesName, func, left, right, 10000, timeAxisTransformer);
  }

  public static XYChart
         chart(XYChart chart,
               String seriesName,
               UnivariateFunction func,
               double left,
               double right,
               int n,
               ToDoubleFunction<Double> timeAxisTransformer)
  {

    XYStyler styler = chart.getStyler();
    styler.setXAxisMin(timeAxisTransformer.applyAsDouble(left));
    styler.setXAxisMax(timeAxisTransformer.applyAsDouble(right));
    styler.setMarkerSize(0);
    styler.setToolTipsEnabled(true);
    double W = right - left;
    double dt = W / n;
    double t = left;
    double x[] = new double[n];
    double y[] = new double[n];

    for (int i = 0; i < n; i++, t = left + dt * i)
    {
      x[i] = timeAxisTransformer.applyAsDouble(t);
      y[i] = func.value(t);
    }
    chart.addSeries(seriesName, x, y);
    return chart;
  }

  public static XYChart
         chart(String xAxisTitle,
               String seriesName,
               UnivariateFunction func,
               double left,
               double right,
               int n,
               ToDoubleFunction<Double> timeAxisTransformer)
  {
    XYChart chart = new XYChart(600, 400);
    chart.setXAxisTitle(xAxisTitle);

    XYStyler styler = chart.getStyler();
    styler.setXAxisMin(timeAxisTransformer.applyAsDouble(left));
    styler.setXAxisMax(timeAxisTransformer.applyAsDouble(right));
    configureStyler(styler);

    Pair<double[], double[]> sample = sampleFunction(func, n, left, right, timeAxisTransformer);
    chart.addSeries(seriesName, sample.left, sample.right);
    return chart;
  }

  public static Pair<double[], double[]>
         sampleFunction(UnivariateFunction func,
                        int n,
                        double left,
                        double right,
                        ToDoubleFunction<Double> timeAxisTransformer)
  {
    double x[] = new double[n];
    double y[] = new double[n];
    double t = left;
    double W = right - left;
    double dt = W / n;

    for (int i = 0; i < n; i++, t = left + dt * i)
    {
      x[i] = timeAxisTransformer.applyAsDouble(t);
      double v = func.value(t);

      y[i] = v;
    }
    return new Pair<double[], double[]>(x, y);
  }

  public static void
         configureStyler(XYStyler styler)
  {

    styler.setMarkerSize(0);
    styler.setAntiAlias(true);
    styler.setPlotGridLinesStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
    styler.setPlotGridLinesColor(Color.DARK_GRAY);
    styler.setSeriesColors(new Color[]
    { Color.YELLOW, Color.RED, Color.GREEN, Color.ORANGE, Color.BLUE });
    styler.setSeriesLines(new BasicStroke[]
    { new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER),
      new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]
      { 3.0f, 1.0f }, 1.0f),
      new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]
      { 3.0f, 3.0f }, 0.0f),
      new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]
      { 2.0f }, 0.0f) });
    styler.setToolTipsEnabled(true);
    styler.setPlotBackgroundColor(Color.BLACK);
  }

  public static XYChart
         chart(String xAxisTitle,
               String seriesName,
               UnivariateFunction func,
               double left,
               double right,
               int n,
               ToDoubleFunction<Double> timeAxisTransformer,
               Consumer<XYChart> chartInitializer)
  {
    XYChart chart = chart(xAxisTitle, seriesName, func, left, right, n, timeAxisTransformer);
    if (chartInitializer != null)
    {
      chartInitializer.accept(chart);
    }
    return chart;
  }

  /**
   * Plot a function by sampling n points in a specified interval, and also
   * including provided values of the function at specified points
   * 
   * @param xAxisTitle
   * @param yAxisTitle
   * @param func
   * @param left
   * @param right
   * @param n
   * @param xAxisTransformer
   * @param times
   * @param values
   * @return
   */
  public static XYChart
         chart(String xAxisTitle,
               String yAxisTitle,
               UnivariateFunction func,
               double left,
               double right,
               int n,
               ToDoubleFunction<Double> xAxisTransformer,
               Vector times,
               Vector values)
  {
    assert times.size() == values.size();
    XYChart chart = new XYChart(600, 400);
    chart.setXAxisTitle(xAxisTitle);
    chart.setYAxisTitle(yAxisTitle);
    XYStyler styler = chart.getStyler();
    styler.setXAxisMin(xAxisTransformer.applyAsDouble(left));
    styler.setXAxisMax(xAxisTransformer.applyAsDouble(right));
    styler.setMarkerSize(0);
    styler.setToolTipsEnabled(true);
    double W = right - left;
    double dt = W / n;

    int firstTimeIndex = times.find(left, Condition.GTE, 0);
    int lastTimeIndex = times.findLast(right, Condition.LTE);

    times = times.slice(firstTimeIndex, lastTimeIndex);
    values = values.slice(firstTimeIndex, lastTimeIndex);

    TreeSet<DoublePair> expandedTimes = new TreeSet<>(DoublePair.compareLeft);
    for (int i = 0; i < n; i++)
    {
      double t = left + dt * i;
      expandedTimes.add(new DoublePair(t, func.value(t)));
    }
    for (int i = 0; i < times.size(); i++)
    {
      expandedTimes.add(new DoublePair(times.get(i), values.get(i)));
    }

    n = expandedTimes.size();
    double x[] = new double[n];
    double y[] = new double[n];
    int i = 0;
    for (DoublePair pair : expandedTimes)
    {
      x[i] = xAxisTransformer.applyAsDouble(pair.left);
      y[i] = pair.right;
      i++;
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
    XYChart chart = new XYChart(600, 400);
    double left = x.fmin();
    XYStyler styler = chart.getStyler();
    styler.setXAxisMin(left);
    double right = x.fmax();
    styler.setXAxisMax(right);
    styler.setMarkerSize(0);
    styler.setToolTipsEnabled(true);
    double W = right - left;
    double t = left;
    int n = x.size();
    assert n == y.size();
    chart.addSeries(seriesName, x.toDoubleArray(), y.toDoubleArray());
    return chart;
  }

  public static XChartPanel<XYChart>
         plot(String xAxisName,
              String yAxisName,
              UnivariateFunction func,
              double fmin,
              double fmax,
              int n,
              ToDoubleFunction<Double> timeAxisTransformer,
              Vector times,
              Vector values)
  {
    return new XChartPanel<XYChart>(chart(xAxisName, yAxisName, func, fmin, fmax, n, timeAxisTransformer, times, values));
  }

  public static void
         addSeriesToChart(XYChart chart,
                          String name,
                          Vector X,
                          Vector Y)
  {
    chart.addSeries(name, X.toDoubleArray(), Y.toDoubleArray());
  }

}
