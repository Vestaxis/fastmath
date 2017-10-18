package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;

import java.io.IOException;

import org.apache.commons.math3.analysis.integration.RombergIntegrator;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import junit.framework.TestCase;

public class ApproximatePowerlawHawkesProcessTest extends TestCase
{
  public void testBranchingRatio()
  {
    ApproximatePowerlawHawkesProcess process = new ApproximatePowerlawHawkesProcess(1.4, 0.25);
    assertEquals( 1.0, process.getBranchingRatio() );
    process.ρ = 0.5;
    assertEquals( 0.5, process.getBranchingRatio() );
    
  }
  
  public void testΨ()
  {
    ExponentialHawkesProcess process = new ConstrainedApproximatePowerlawHawkesProcess(1.4, 0.25);
    double x = process.ψ(1.3);
    // assertEquals(0.11591305818947, x, pow(10,-9));
    // out.println( "x=" + x );
  }

//  public void testiΨ()
//  {
//    ExponentialHawkesProcess process = new ConstrainedApproximatePowerlawHawkesProcess(1.4, 0.25);
//    double x = process.iψ(1.3);
//    assertEquals(.18284483319013261698230044979325998875927092907043, x, pow(10, -9));
//    // out.println( "x=" + x );
//  }
  
  public void testIntegralOfKernel()
  {

    double ε = 0.25;
    double τ0 = 1;
    ApproximatePowerlawHawkesProcess process = new ApproximatePowerlawHawkesProcess(τ0, ε);
    testHawkesProcess(process); 
    process.κ = 0.1;
    process.ρ = 0.5;
    testHawkesProcess(process);     
  }

  private void testHawkesProcess(ExponentialHawkesProcess process)
  {
    RombergIntegrator integrator = new RombergIntegrator();
    double integral = integrator.integrate(500000, process::ψ, 0, 5000);
    out.println( "integral=" + integral );
    assertEquals( process.ρ, integral, pow(10,-4));
    process.ρ = 0.5;
    integral = integrator.integrate(500000, process::ψ, 0, 5000);
    assertEquals( process.ρ, integral, pow(10,-4));
    out.println( "integral=" + integral );
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

//  public void testΛ() throws IOException
//  {
//    double ε = 0.15;
//    double η = 1.6;
//    ExponentialHawkesProcess process = new ConstrainedApproximatePowerlawHawkesProcess(η, ε);
//    Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
//    StandardExponentialHawkesProcessTest.doTest(process, data);
//
//  }

  public void testMean()
  {
    double ε = .4674039567;
    double τ0 = 57.02008734;
    ApproximatePowerlawHawkesProcess process = new ApproximatePowerlawHawkesProcess(ε, τ0);
    process.M = 5;
    out.println("mean is " + process.mean());
    out.println("variance is " + process.variance());
    Vector moments = new Vector(rangeClosed(0, 4).mapToDouble(n -> process.nthMoment(n))).setName("moments");
    Vector normalizedMoments = new Vector(rangeClosed(0, 4).mapToDouble(n -> process.nthNormalizedMoment(n))).setName("moments");

    out.println("first " + moments.size() + " moments are " + moments);
    out.println("first " + moments.size() + " normalized moments are " + normalizedMoments);
  }

}
