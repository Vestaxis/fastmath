package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;
import static util.Plotter.plot;

import java.io.IOException;
import java.util.function.IntToDoubleFunction;

import org.apache.commons.math3.analysis.function.Min;

import fastmath.Vector;
import junit.framework.TestCase;
import util.Plotter;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawHawkesProcessTest extends TestCase
{
  public void testKernel()
  {
    ConstrainedApproximatePowerlawHawkesProcess eplhp = new ConstrainedApproximatePowerlawHawkesProcess(1.6, 0.15);

    ExtendedApproximatePowerlawHawkesProcess exthp = new ExtendedApproximatePowerlawHawkesProcess(eplhp.τ0, eplhp.ε, eplhp.αS(), eplhp.βS());

    for (int i = 0; i < eplhp.order(); i++)
    {
      assertEquals(String.format("α[%d]", i), eplhp.α(i), exthp.α(i));
      assertEquals(String.format("β[%d]", i), eplhp.β(i), exthp.β(i));
    }

    assertEquals("Z", eplhp.Z(), exthp.Z());

    double r = eplhp.ψ(1.3);
    double s = exthp.ψ(1.3);

    assertEquals(r, s);
  }

  public void testZeroEps()
  {
    double b = 1;
    double τ = 0.34;
    double ε = 0;
    double τ0 = 1;
    ExtendedApproximatePowerlawHawkesProcess process = new ExtendedApproximatePowerlawHawkesProcess(τ0, ε, b, τ);
    assertEquals(20.1, process.Z(), pow(10, -13));

  }

  public void testPrediction() throws IOException
  {

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
