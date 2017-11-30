package stochastic.processes.selfexciting;

import static fastmath.Functions.grid;
import static fastmath.Functions.sign;
import static fastmath.Functions.sum;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.System.out;
import static util.Console.println;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.knowm.xchart.SwingWrapper;

import fastmath.Vector;
import jdk.net.NetworkPermission;
import junit.framework.TestCase;
import util.Plotter;

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
    process.T = new Vector(2);
    process.T.set(0, 0);
    process.T.set(1, 19);

    double p = process.Λphase(0.6, 1.2);
    out.println("Λphase=" + p);
    p = process.Λphase(-14.2, 1.2);
    out.println("Λphase=" + p);

    
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
