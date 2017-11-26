package stochastic.processes.selfexciting;

import static java.lang.Math.pow;
import static java.lang.System.out;
import static util.Console.println;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;

import jdk.net.NetworkPermission;
import junit.framework.TestCase;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawSelfExcitingProcessTest extends TestCase
{

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
    out.println( "βproduct=" + process.βproduct() );

    assertEquals(32.22004331952762, phase, 1E-13);
    out.println("Hphase(0.7, 9.3)=" + phase);
    phase = process.HphaseUnscaled(0.7, 9.3);
    out.println("HphaseLim(0.7, 9.3)=" + phase);
    assertEquals(58.6699560, phase, 1E-7);

    phase = process.HphaseDtUnscaled(0.7, 9.3);
    out.println("HphaseDt=" + phase);
    assertEquals(-26.42269791, phase, 1E-7);
    phase = process.FphaseDtUnscaled(0.7, 9.3);
    out.println("HphaseDtlim=" + phase);
    assertEquals(-26.422697918, phase, 1E-7);
    double Hphase = process.Hphase(0.7, 0.2);
    double HphaseDt = process.HphaseDtUnscaled(0.7, 0.2);
    double HphaseHphaseDtRatio = Hphase / HphaseDt;
    out.println("HphaseHphaseDtRatio=" + HphaseHphaseDtRatio);

    double HphaseLim = process.HphaseUnscaled(0.7, 0.2);
    double HphaseLimDt = process.HphaseDtUnscaled(0.7, 0.2);
    double HphaseLimHphaseLimDtRatio = Hphase / HphaseDt;
    double HphaseLimHphaseDtRatio = HphaseLim / HphaseLimDt;
    out.println("HphaseLimHphaseLimDtRatio=" + HphaseLimHphaseLimDtRatio);
    assertEquals(HphaseHphaseDtRatio, HphaseLimHphaseLimDtRatio);
  }
  
  public void testHphaseRoot() throws MaxIterationsExceededException, FunctionEvaluationException
  {
    final ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();

    double u = 0.7;
    BrentSolver solver = new BrentSolver( t-> process.Hphase(u,t));
    double root = solver.solve(0, 1000);
    double phaseAtRoot = process.Hphase(u, root);
    out.println("root=" + root + " phaseAtRoot=" + phaseAtRoot );
  }

  public void
         testFphase()
  {
    final ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();
    out.println("Fphase: constructed " + process);
    out.println("params=" + process.getParamString());
    out.println("αβ=" + process.getαβString());

    double phase = process.Fphase(0.7, 0.2);
    assertEquals(1.1523143729276232E-33, phase, 1E-13);

    out.println("Fphase(0.7, 0.2)=" + phase);
    double otherPhase = phase;
    phase = process.Fphase(0.7, 9.3);
    // assertEquals(phase, otherPhase);

    assertEquals(1.1523143729276232E-33, phase, 1E-13);
    out.println("Fphase(0.7, 9.3)=" + phase);
    phase = process.FphaseUnscaled(0.7, 9.3);
    out.println("FphaseLim(0.7, 9.3)=" + phase);
    assertEquals(5.918034329, phase, 1E-7);
//
//    phase = process.FphaseDtUnscaled(0.7, 9.3);
//    out.println("FphaseDt=" + phase);
//    assertEquals(-25.17023354150973, phase, 1E-13);
//    phase = process.FphaseDtUnscaled(0.7, 9.3);
//    out.println("FphaseDtlim=" + phase);
//    assertEquals(-25.17023354150973, phase, 1E-13);
//    double Fphase = process.Fphase(0.7, 0.2);
//    double FphaseDt = process.FphaseDtUnscaled(0.7, 0.2);
//    double FphaseFphaseDtRatio = Fphase / FphaseDt;
//    out.println("FphaseFphaseDtRatio=" + FphaseFphaseDtRatio);
//
//    double FphaseLim = process.FphaseUnscaled(0.7, 0.2);
//    double FphaseLimDt = process.FphaseDtUnscaled(0.7, 0.2);
//    double FphaseLimFphaseLimDtRatio = Fphase / FphaseDt;
//    double FphaseLimFphaseDtRatio = FphaseLim / FphaseLimDt;
//    out.println("FphaseLimFphaseLimDtRatio=" + FphaseLimFphaseLimDtRatio);
//    assertEquals(FphaseFphaseDtRatio, FphaseLimFphaseLimDtRatio);
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
  // ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();
  //
  //
  // RombergIntegrator integrator = new RombergIntegrator();
  // double integral = integrator.integrate(5000000, process::ν, 0, 30000 * 10);
  // out.println("integral=" + integral);
  // assertEquals(1, integral, 1E-1);
  // }

  public void
         testν()
  {

    final ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();

    println(process.getParamString());
    assertEquals(0.07971548, process.f(1.9), 1E-7);
    assertEquals(0.230423469, process.F(1.9), 1E-7);

  }

  public void
         testInverseIntegratedHazard()
  {

    final ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();

    println(process.getParamString());

    double h = 0.824;
    double t = process.invH(h);
    out.println("invih(" + h + ")=" + t);
    double r = process.H(t);

    assertEquals(t, r, 0.000000001);

  }

  public ExtendedApproximatePowerlawSelfExcitingProcess
         constructProcess()
  {
    final ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();

    process.assignParameters(new double[]
    { 1, 0, 3, 1.78 });
    return process;
  }

}
