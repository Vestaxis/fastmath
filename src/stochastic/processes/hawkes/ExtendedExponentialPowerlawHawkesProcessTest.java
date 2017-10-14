package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;
import static util.Plotter.plot;

import java.io.IOException;
import java.util.function.IntToDoubleFunction;

import org.apache.commons.math3.analysis.function.Min;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;

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

  public void testIntegralOfKernel()
  {
    double b = 1;
    double τ = 1;
    double ε = 0.25;
    double τ0 = 1;
    ExtendedApproximatePowerlawHawkesProcess process = new ExtendedApproximatePowerlawHawkesProcess(τ0, ε, b, τ);
    RombergIntegrator integrator = new RombergIntegrator();
    double integral = integrator.integrate(500000, process::ψ, 0, 5000);
    out.println( "integral=" + integral );
    assertEquals( process.ρ, integral, pow(10,-4));
    process.ρ = 0.5;
    integral = integrator.integrate(500000, process::ψ, 0, 50000);
    assertEquals( process.ρ, integral, pow(10,-4));
    out.println( "integral=" + integral );    
  }


}
