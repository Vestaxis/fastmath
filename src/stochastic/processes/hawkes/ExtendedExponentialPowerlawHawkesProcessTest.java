package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.Math.random;
import static java.lang.System.out;

import java.io.File;
import java.io.IOException;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import junit.framework.TestCase;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawHawkesProcessTest extends TestCase
{
  public void testKernel()
  {
    ExponentialPowerlawHawkesProcess eplhp = new ExponentialPowerlawHawkesProcess(1.6, 0.15);

    ExtendedExponentialPowerlawHawkesProcess exthp = new ExtendedExponentialPowerlawHawkesProcess(
        eplhp.getη(), eplhp.getε(), eplhp.αS(), eplhp.βS());

    for (int i = 0; i < eplhp.order(); i++)
    {
      assertEquals(String.format("α[%d]", i), eplhp.α(i), exthp.α(i));
      assertEquals(String.format("β[%d]", i), eplhp.β(i), exthp.β(i));
    }

    assertEquals("Z", eplhp.Z(), exthp.Z());

    out.println("αS=" + exthp.αS());
    out.println("βS=" + exthp.βS());

    double r = eplhp.ψ(1.3);
    double s = exthp.ψ(1.3);

    out.println("r=" + r);
    out.println("s=" + s);
    assertEquals(r, s);
  }

  // public void testIntensity()
  // {
  // double ε = 0.2;
  // double τ = 0.9;
  // double η = 0.35;
  // double b = 0.1;
  // ExtendedExponentialPowerlawHawkesProcess process = new
  // ExtendedExponentialPowerlawHawkesProcess( η, τ, ε, b );
  // process.T = new Vector( new double[] { 0.3, 0.5, 0.9, 1.2, 1.4, 2.0 } );
  // double intensity = process.λ(1.8);
  // out.println( "intensity=" + intensity );
  // assertEquals( .9073429721, intensity, pow(10,-9));
  // }
  //
  // public void testLL()
  // {
  // double ρ = 0.75;
  // double ε = 0.2;
  // double τ = 0.9;
  // double η = 0.35;
  // double b = 0.01;
  // ExtendedExponentialPowerlawHawkesProcess process = new
  // ExtendedExponentialPowerlawHawkesProcess( η, τ, ε, b );
  // process.T = new Vector( new double[] { 0.3, 0.5, 0.9, 1.2, 1.4, 2.0, 2.04,
  // 2.06, 2.08 } );
  // out.println( "log-likelihood=" + process.logLik() );
  // process.setη(1.0);
  // out.println( "log-likelihood=" + process.logLik() );
  // process.setη(0.7);
  // out.println( "log-likelihood=" + process.logLik() );
  // }
  //
  // public void testiΨ()
  // {
  // double ρ = 0.75;
  // double ε = 0.2;
  // double τ = 0.9;
  // double η = 0.35;
  // double b = 0.1;
  // ExtendedExponentialPowerlawHawkesProcess process = new
  // ExtendedExponentialPowerlawHawkesProcess( η, τ, ε, b );
  // double phi = process.iψ( 0.4 );
  // assertEquals( .1994241620, phi, pow(10,-9));
  // }
  //
  //
  public void testEstimateParmeters() throws IOException, CloneNotSupportedException
  {
    double b = 0;
    double τ = 1.0;
    double ε = 0.25;
    double τ0 = 1;
    ExtendedExponentialPowerlawHawkesProcess process = new ExtendedExponentialPowerlawHawkesProcess(τ0, ε, b, τ);
    Vector data = MatFile.loadMatrix("SPY.mat", "SPY").col(0);

    int midpoint = data.size() / 2;
    data = data.slice(midpoint - 5000, midpoint + 5000);
    data = data.copy().subtract( data.get(0 ) );
    
    process.normalize = true;
    process.T = data;

    int evals = process.estimateParameters(15);
    out.println(evals + " iterations");

    Vector compensator = process.Λ();
    out.println("mean(Λ)=" + compensator.mean());
    out.println("var(Λ)=" + compensator.variance());
    File outputFile = new File( "comp.mat" );
    out.println( "storing compensator to " + outputFile.getAbsolutePath() );
    MatFile.write(outputFile, compensator.setName("compensator").createMiMatrix());
  }
  //
  // public void testEstimateParmeters2() throws IOException
  // {
  // double ρ = 1;
  // double ε = 0.25;
  // double τ = 0.4;
  // double η = 0.8;
  // double b = 0.1;
  // ExtendedExponentialPowerlawHawkesProcess process = new
  // ExtendedExponentialPowerlawHawkesProcess( η, τ, ε, b );
  // Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
  // process.T = data;
  // int midpoint = data.size() / 2;
  // data = data.slice( midpoint - 500, midpoint + 500 );
  //
  //// while ( data.diff().find(0.0, Condition.EQUAL, 0) != -1 )
  //// {
  //// data = randomizeContemperaneousPoints( data );
  //// }
  //
  // //process.eventTimes = new Vector( new double[] { 0.3, 0.5, 0.9, 1.2, 1.4,
  // 2.0, 2.04, 2.06, 2.08 } );
  // process.T = data;
  // int evals = process.estimateParameters(15);
  // MatFile.write("/data/test.mat", data.setName("d").createMiMatrix(),
  // process.Λ().setName("C").createMiMatrix() );
  //
  // out.println( evals + " iterations");
  //
  // }

  public void testZeroEps()
  {
    double b = 1;
    double τ = 0.34;
    double ε = 0;
    double τ0 = 1;
    ExtendedExponentialPowerlawHawkesProcess process = new ExtendedExponentialPowerlawHawkesProcess(τ0, ε, b, τ);
    assertEquals( 20.1, process.Z(), pow(10,-13) );

  }
}
