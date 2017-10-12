package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;

import junit.framework.TestCase;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawHawkesProcessTest extends TestCase
{
  public void testKernel()
  {
    ConstrainedApproximatePowerlawHawkesProcess eplhp = new ConstrainedApproximatePowerlawHawkesProcess(1.6, 0.15);

    ExtendedApproximatePowerlawHawkesProcess exthp = new ExtendedApproximatePowerlawHawkesProcess(eplhp.τ0,
                                                                                                  eplhp.ε,
                                                                                                  eplhp.αS(),
                                                                                                  eplhp.βS());

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
  
  public void testMeanVariance()
  {
    double b = 1.5;
    double τ = 0.34;
    double ε = 0.25;
    double τ0 = 1.3;
    ExtendedApproximatePowerlawHawkesProcess process = new ExtendedApproximatePowerlawHawkesProcess(τ0, ε, b, τ);
    double mean = process.nthNormalizedMoment(1);
    double stdev = process.nthNormalizedMoment(2);
    out.println( "m=" + mean + " sd=" + stdev );
  }
}
