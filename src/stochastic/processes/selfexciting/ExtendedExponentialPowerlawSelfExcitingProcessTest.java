package stochastic.processes.selfexciting;

import static fastmath.Functions.grid;
import static java.lang.System.out;
import static org.fusesource.jansi.Ansi.ansi;
import static util.Console.println;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.arblib.Real;
import org.knowm.xchart.SwingWrapper;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import junit.framework.TestCase;
import util.Plotter;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawSelfExcitingProcessTest extends TestCase
{

  private double phasedt;

  public void
         testInvLambda()
  {
    ExtendedApproximatePowerlawSelfExcitingProcess process = ExtendedExponentialPowerlawSelfExcitingProcessTest.constructProcess();
    // process.ε = 0.05;

    process.T = new Vector(new double[]
    { 65, 67, 86, 140, 141, 149, 151, 163, 201, 205 }).setName("T");

    // process.T = process.T.subtract(process.T.get(0));
    process.trace = false;

    // process.printResults( process.estimateParameters(25) );

    // out.println( " Λ=" + process.Λ() );
    // for (int tk = 0; tk < 10; tk++)
    // {
    // out.println("A[" + tk + "]=" + Arrays.toString(process.A[tk]));
    // }
    out.println("estimated " + ansi().fgBrightYellow() + process + ansi().fgDefault() + " from " + process.T.size() + " points");
    out.println(process.getαβString());
    process.trace = false;
    double Λmean = process.Λ().mean();
    process.trace = false;
    double Λvar = process.Λ().variance();
    out.println("Λmean=" + ansi().fgBrightRed() + Λmean + ansi().fgDefault() + " Λvar=" + ansi().fgBrightRed() + Λvar + ansi().fgDefault());

    int n = 10;
    out.println("in-sample forecasting starting at n=" + n);
    process.T = process.T.slice(0, n);
    out.println(ansi().fgBrightGreen() + process.T.toString() + ansi().fgDefault());
    process.dT = null;
    out.println(ansi().fgBrightGreen() + process.dT().toString() + ansi().fgDefault());

    process.trace = true;
    process.recursive = true;
    out.println(" Λ=" + process.Λ());
    process.trace = false;

    process.trace = false;

    double hmm = process.Φδ(process.T.fmax() + 40, 1, n - 1);
    out.println("hmm " + hmm);
    TestCase.assertEquals(-4.8963233710073894061, hmm, 1E-15);
    new SwingWrapper<>(Plotter.chart("x", "y", t -> process.Φδ(t, 1, n - 1), -25, 60, t -> t)).displayChart();

    process.trace = true;
    for (double y = 0; y < 9; y += 0.25)
    {
      double dt = process.invΛ(y, n - 1);
      Real dtReal = process.invΛReal(y, n - 1);

      double q = process.Λ(n - 1, dt);

      out.println("y=" + y + " dt=" + dt + " dtReal=" + dtReal + " q=" + q);
      assertEquals(q, y, 1E-14);
    }
  }

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
    double y = 0.75;
    out.println("T=" + process.T);
    out.println("y=" + y);
    out.println("dΛ=" + process.Λ());
    int n = process.T.size() - 1;

    Vector compensated = process.Λ();
    process.trace = true;
    double nextdt = process.invΛ(y, n - 2);
    process.trace = false;
    double phase = process.Φ(22, y, 2);
    process.trace = true;
    Real nextdtReal = process.invΛReal(y, n - 2);
    process.trace = false;

    Real phaseReal = process.ΦReal(new Real(22), y, 2);
    process.trace = false;

    out.println("phase=" + phase);
    out.println("phaseδReal=" + phaseReal);
    assertEquals(phase, phaseReal.fpValue(), 1E-15);
    double phaseDiff = process.Φdt(22, 2);
    Real phaseDiffReal = process.ΦdtReal(new Real(22), 2);
    assertEquals(phaseDiff, phaseDiffReal.fpValue(), 1E-15);

    out.println("invΛ(y=" + y + ")=" + nextdt);
    // out.println("invΛReal(y=" + y + ")=" + nextdtReal);

    process.T = process.T.append(process.T.fmax() + nextdt);
    process.dT = null;

    out.println("T=" + process.T);
    compensated = process.Λ();
    out.println("compensated=" + compensated);

  }

  public void
         testTotalΛ() throws InterruptedException
  {
    ExtendedApproximatePowerlawSelfExcitingProcess process = constructProcess();
    process.T = new Vector(4);
    process.T.set(0, 0);
    process.T.set(1, 19);
    process.T.set(2, 24);
    process.T.set(3, 27);

    double a = process.Λ().sum();
    double b = process.totalΛ();
    assertEquals(a, b, 1E-15);

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
        double d = process.B(i, j);
        // out.println("a=" + a + " b=" + b + " c=" + c + " d=" + d);
        assertEquals(a, b, 1E-14);
        assertEquals(a, 1 + d, 1E-14);
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
