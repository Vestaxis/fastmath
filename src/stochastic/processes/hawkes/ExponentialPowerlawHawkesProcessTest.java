package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;

import java.io.IOException;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import junit.framework.TestCase;

public class ExponentialPowerlawHawkesProcessTest extends TestCase
{
  public void testΨ()
  {
    ExponentialHawkesProcess process = new ConstrainedApproximatePowerlawHawkesProcess(1.4, 0.25);
    double x = process.ψ(1.3);
    // assertEquals(0.11591305818947, x, pow(10,-9));
    // out.println( "x=" + x );
  }

  public void testiΨ()
  {
    ExponentialHawkesProcess process = new ConstrainedApproximatePowerlawHawkesProcess(1.4, 0.25);
    double x = process.iψ(1.3);
    assertEquals(.18284483319013261698230044979325998875927092907043, x, pow(10, -9));
    // out.println( "x=" + x );
  }

  // public void testLogLik() throws IOException
  // {
  //
  // double ε = 0.16710;
  // double η = 1.58128;
  // ExponentialHawkesProcess process = new ExponentialPowerlawHawkesProcess(η,
  // ε);
  // Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
  // int midpoint = data.size() / 2;
  // data = data.slice(midpoint - 250, midpoint + 250);
  // process.T = data;
  // double llNonRecursive = process.logLik();
  // process.recursive = true;
  // double llRecursive = process.logLik();
  // assertEquals( llNonRecursive, llRecursive, pow( 10, -10 ) );
  // }

  public void testΛ() throws IOException
  {
    double ε = 0.15;
    double η = 1.6;
    ExponentialHawkesProcess process = new ConstrainedApproximatePowerlawHawkesProcess(η, ε);
    Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
    StandardExponentialHawkesProcessTest.doTest(process, data);

  }

  public void testMean()
  {
    double ε = 0.5;
    double τ0 = 8.915;
    ExponentialHawkesProcess process = new ApproximatePowerlawHawkesProcess(ε, τ0);
    out.println("mean is " + process.mean());
    out.println("variance is " + process.variance());
    Vector moments = new Vector(rangeClosed(0, 4).mapToDouble(n -> process.nthMoment(n))).setName("moments");
    Vector normalizedMoments = new Vector(rangeClosed(0, 4).mapToDouble(n -> process.nthNormalizedMoment(n))).setName("moments");

    out.println("first " + moments.size() + " moments are " + moments);
    out.println("first " + moments.size() + " normalized moments are " + normalizedMoments);
  }

}
