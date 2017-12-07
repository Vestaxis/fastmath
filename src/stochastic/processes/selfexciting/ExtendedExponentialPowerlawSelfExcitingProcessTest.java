package stochastic.processes.selfexciting;

import static fastmath.Functions.grid;
import static java.lang.System.out;
import static util.Console.println;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.arblib.Real;

import fastmath.Vector;
import junit.framework.TestCase;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawSelfExcitingProcessTest extends TestCase
{

  private double phasedt;

  public void
         testHphase()
  {
    final ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();

    double phase = process.Hphase(0.7, 0.2);
    assertEquals(32.22004331952762, phase, 1E-13);
    out.println("Hphase(0.7, 0.2)=" + phase);
    double otherPhase = phase;
    phase = process.Hphase(0.7, 9.3);
    assertEquals(phase, otherPhase);
    out.println("βproduct=" + process.βproduct());

  }

  final public static double tolerance = 1E-12;

  public void
         testFInverseConvergence()
  {
    final ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();
    out.println("Fphase: constructed " + process);
    out.println("params=" + process.getParamString());
    out.println("αβ=" + process.getαβString());

    grid(0, 1, 10).forEachOrdered(u -> {

      double t = process.invF(u);
      double shouldbeu = process.F(t);
      out.format("u=%f shouldbeu=%f\n", u, shouldbeu);
      assertEquals(u, shouldbeu, tolerance);
      out.println();
    });

  }

  public void
         testNormalization()
  {
    final ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();

    double z = process.Z();
    assertEquals(20.34, z, 1E-5);

  }

  public void
         testSaveLoad() throws IOException
  {
    ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();
    process.b = 1;
    process.η = 1;
    process.ε = 0.25;
    process.τ = 1;
    File tempFile = File.createTempFile("test", "params");
    out.println("Wrote params to " + tempFile.getAbsolutePath());
    process.storeParameters(tempFile);
    ExtendedApproximatePowerlawSelfExcitingProcess loaded = new ExtendedApproximatePowerlawSelfExcitingProcess();
    loaded.loadParameters(tempFile);
    assertEquals(process.getParameters(), loaded.getParameters());
  }

  // public void
  // testIntegralOfKernel()
  // {
  // ExtendedApproximatePowerlawSelfExcitingProcess process =
  // constructProcess();
  //
  //
  // RombergIntegrator integrator = new RombergIntegrator();
  // double integral = integrator.integrate(5000000, process::ν, 0, 30000 *
  // 10);
  // out.println("integral=" + integral);
  // assertEquals(1, integral, 1E-1);
  // }
  //
  public void
         testfandF()
  {

    final ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();

    println(process.getParamString());
    assertEquals(0.07971548, process.f(1.9), 1E-7);
    assertEquals(0.230423469, process.F(1.9), 1E-7);

  }

  public void
         testΛPhase() throws InterruptedException
  {
    ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();
    process.T = new Vector(3);
    process.T.set(0, 0);
    process.T.set(1, 19);
    process.T.set(2, 27);

    process.trace = false;
    ExponentialDistribution expDist = new ExponentialDistribution(1);
    double y = 1.1;
    out.println("T=" + process.T);
    out.println("y=" + y);
    out.println("dΛ=" + process.Λ());
    int n = process.T.size() - 1;

    Vector compensated = process.Λ();
    double nextdt = process.invΛ(y, n - 1);
    double phase = process.Λphase(22, 0.9, 2);
    Real nextdtReal = process.invΛReal(y, n - 1);
    Real phaseReal = process.ΛphaseReal(new Real( 22 ), 0.9, 2);
    out.println( "phase=" + phase );
    out.println( "phaseReal=" + phaseReal );
    
    out.println("invΛ(y=" + y + ")=" + nextdt);
    out.println("invΛReal(y=" + y + ")=" + nextdtReal);

    process.dT = process.dT.append(nextdt);
    process.T = process.T.append(process.T.fmax() + nextdt);

    out.println("T=" + process.T);
    compensated = process.Λ();
    out.println("compensated=" + compensated);

  }

  public void
         testA()
  {
    ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();
    process.T = new Vector(3);
    process.T.set(0, 0);
    process.T.set(1, 19);
    process.T.set(2, 27);
    for (int j = 0; j < process.order(); j++)
    {
      for (int i = 0; i < process.T.size(); i++)
      {
        double a = process.Asum(i, j);
        double b = process.A(i, j);
        Real c = process.AReal(i, j);
        out.println( "a=" + a + " b=" + b + " c=" + c );
        assertEquals(a, b, 1E-14);
      }
    }

  }

  public static ExtendedApproximatePowerlawSelfExcitingProcess
         constructProcess()
  {
    final ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();

    process.assignParameters(new double[]
    { 1, 0, 3, 1.78 });
    return process;
  }

}
