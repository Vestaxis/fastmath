package stochastic.processes.selfexciting;

import static java.lang.System.out;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static util.Console.println;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math3.optim.PointValuePair;
import org.arblib.Real;
import org.fusesource.jansi.Ansi;
import org.knowm.xchart.SwingWrapper;

import dnl.utils.text.table.TextTable;
import fastmath.DoubleColMatrix;
import fastmath.Vector;
import fastmath.matfile.MatFile;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import util.Plotter;
import util.TerseThreadFactory;

public class ProcessSimulator
{

  static
  {
    System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", TerseThreadFactory.class.getName());
  }

  private AbstractSelfExcitingProcess process;

  public ProcessSimulator(SelfExcitingProcess process)
  {
    this.process = (AbstractSelfExcitingProcess) process;
  }

  public static void
         main(String[] args) throws IOException,
                             CloneNotSupportedException,
                             InterruptedException
  {

    ExtendedApproximatePowerlawSelfExcitingProcess process = ExtendedExponentialPowerlawSelfExcitingProcessTest.constructProcess();

    process.T = MatFile.loadMatrix("test0.mat", "times").asVector().copy().slice(0, 1000);
    final double t0 = process.T.get(0);
    for (int i = 0; i < process.T.size(); i++)
    {
      process.T.set(i, (int) (process.T.get(i) - t0));
    }
    // process.T = process.T.subtract(process.T.get(0));
    process.trace = false;

    // process.estimateParameters(25);
    out.println("estimated " + Ansi.ansi().fgBrightYellow() + process + Ansi.ansi().fgDefault() + " from " + process.T.size() + " points");

    process.trace = false;
    double Λmean = process.Λ().mean();
    process.trace = false;
    double Λvar = process.Λ().variance();
    out.println("Λmean=" + Ansi.ansi().fgBrightRed() + Λmean + Ansi.ansi().fgDefault() + " Λvar=" + Ansi.ansi().fgBrightRed() + Λvar + Ansi.ansi().fgDefault());

    int n = 10;
    out.println("in-sample forecasting starting at n=" + n);
    process.T = process.T.slice(0, n);

    process.trace = true;
    process.Λ();
    process.trace = false;

    out.println(Ansi.ansi().fgBrightGreen() + process.T.slice(1, process.T.size()).toString() + Ansi.ansi().fgDefault());
    out.println(Ansi.ansi().fgBrightGreen() + process.Λ().toString() + Ansi.ansi().fgDefault());
    process.trace = false;

    double y = 0.9;
    double nextdt = process.invΛ(y, n - 2);
    process.trace = true;
    Real nextdtReal = process.invΛReal(y, n - 2);
    process.trace = false;
    double shouldbe0 = process.Λphase(nextdt, y, n - 2);
    Real shouldbe0Real = process.ΛphaseReal(nextdtReal, y, n - 2);
    out.println("shouldbe0=" + shouldbe0 + " shouldbe0Real=" + shouldbe0Real + "\nβproduct=" + process.βproduct() + " βproductReal=" + process.βproductReal());
    double shouldbey = process.Λ(n - 2, nextdtReal.fpValue());
    out.println("shouldbey=" + shouldbey + " should be y=" + y);
    process.T = process.T.append(process.T.fmax() + nextdtReal.fpValue());

    new SwingWrapper<>(Plotter.chart("x", "y", t -> process.ΛphaseNormalized(-t, y, n - 2), -25, 40, t -> t)).displayChart();
    out.println("nextdt=" + nextdt + " nextdtreal=" + nextdtReal);

    out.println("T=" + process.T);
    process.dT = null;
    out.println("comp " + process.Λ());

    // ExponentialDistribution expDist = new ExponentialDistribution(1);
    //
    // int n = 1;
    // Vector N = new Vector(n);
    // Vector Λ = new Vector(n);
    // Vector T = new Vector(n);
    // Vector dT = new Vector(n);
    // int i = 0;
    // out.println("generating " + n + " samples of " + process);
    // for (; i < n; i++)
    // {
    // double ε = expDist.sample();
    // out.println("ε=" + ε);
    // double dt = process.invΛ(ε);
    //
    // out.println("invΛ(ε=" + ε + ")=" + dt);
    // process.dT = process.dT().append(dt);
    // process.T = process.T.append(process.T.fmax() + dt);
    // process.refreshCompensator();
    //
    // out.println("T=" + process.T);
    // out.println("dT=" + process.dT);
    // Vector compensated = process.Λ();
    // out.println("compensated=" + compensated);
    // double t = 0;
    // N.set(i, i);
    // Λ.set(i, ε);
    // T.set(i, t += dt);
    // dT.set(i, dt);
    // }
    // N = N.slice(0, i);
    // Λ = Λ.slice(0, i);
    // T = T.slice(0, i);
    // dT = dT.slice(0, i);
    //
    // process.estimateParameters(Runtime.getRuntime().availableProcessors());
    //
    // out.println("generated point set spans " +
    // DateUtils.convertTimeUnits(T.fmax(), TimeUnit.MILLISECONDS, TimeUnit.HOURS) +
    // " hours");
    //
    // out.println("mean(dT)=" + dT.mean());
    // out.println("process mean=" + process.mean());
    //
    // out.println("variance(dT)=" + dT.variance());
    // out.println("process variance=" + process.variance());
    //
    // Vector ac = dT.autocor(50);
    // out.println("ac=" + ac);
    // // new SwingWrapper<>(Plotter.plot(ac, "autoocorrelation")).displayChart();
    // // new SwingWrapper<>(Plotter.plot(T, N)).displayChart();
    // //
    // // AbstractSelfExcitingProcess estimatedProcess =
    // //
    // ProcessEstimator.estimateSelfExcitingProcess(Type.ExtendedApproximatePowerlaw,
    // // Runtime.getRuntime().availableProcessors(),
    // // T);
    // // out.println("estimated " + process);
    // //
    // // // while(true)
    // // // {
    // // // Thread.sleep(1000);
    // // // }
  }

  public static void
         storeParameterEstimationResults(File testFile,
                                         Vector data,
                                         AbstractSelfExcitingProcess process)
  {
    Vector compensator = process.Λ().setName("comp");
    Vector intensity = process.λvector().setName("intensity");
    out.println("writing timestamp data, compensator and intensity to " + testFile.getAbsolutePath()
                + " E[data.dt]="
                + data.diff().mean()
                + " 1/k="
                + (1 / process.κ));
    try
    {
      MatFile.write(testFile, data.createMiMatrix(), compensator.createMiMatrix(), intensity.createMiMatrix());
    }
    catch (IOException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private boolean verbose = true;

  public TextTable
         printResults(ParallelMultistartMultivariateOptimizer multiopt)
  {

    BoundedParameter[] params = process.getBoundedParameters();

    println("estimated parameters for " + process.getClass().getSimpleName() + "[" + stream(params).map(param -> param.getName()).collect(joining(",")) + "]");

    PointValuePair[] optima = multiopt.getOptima().toArray(new PointValuePair[0]);

    String[] columnHeaders = process.getColumnHeaders();

    Object[][] data = evaluateStatisticsForEachLocalOptima(optima, columnHeaders);

    TextTable tt = new TextTable(columnHeaders, data);

    tt.setAddRowNumbering(true);
    tt.printTable();

    return tt;
  }

  public Object[][]
         evaluateStatisticsForEachLocalOptima(PointValuePair[] optima,
                                              String[] columnHeaders)
  {
    Object[][] data = new Object[optima.length][columnHeaders.length];

    for (int i = 0; i < optima.length; i++)
    {
      Object[] row = process.evaluateParameterStatistics(optima[i].getPoint());

      for (int j = 0; j < columnHeaders.length; j++)
      {
        data[i][j] = row[j];
      }
    }
    return data;
  }

  public static Vector
         loadTimes(String filename,
                   String symbol) throws IOException
  {

    DoubleColMatrix matrix = MatFile.loadMatrix(filename, symbol);
    Vector data = matrix.col(0).setName("data");

    return data;
  }

}
