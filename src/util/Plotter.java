package util;

import static util.Plotter.plot;

import java.io.IOException;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import fastmath.Vector;
import stochastic.processes.hawkes.ExtendedApproximatePowerlawHawkesProcess;
import stochastic.processes.hawkes.HawkesProcessEstimator;

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
      // out.println( x[i] + "=" + y[i] );
    }
    chart.addSeries(name, x, y);
    new SwingWrapper<XYChart>(chart).displayChart();
  }

  public static void main(String args[]) throws IOException
  {
    double b = 1.3621739959112;
    double τ = 0.35043405476410616;
    double ε = 0.016225473443095387;
    double τ0 = 3.116820765602559;
    ExtendedApproximatePowerlawHawkesProcess process = new ExtendedApproximatePowerlawHawkesProcess(τ0, ε, b, τ);
    process.m = 5.2671868072744745;
    Vector T = HawkesProcessEstimator.loadData("/home/stephen/git/fastmath/SPY.mat", "SPY", 25 );
    T = T.copy().subtract(T.get(0));
    process.T = T;
    // double nextPoint = process.predict();

    // Plotter.plot("ψ(t)", t -> process.ψ(t), 0, 50);
    plot("λ(t)", t -> process.λ(t), 0, T.fmax(), 5000 );

  }
}
