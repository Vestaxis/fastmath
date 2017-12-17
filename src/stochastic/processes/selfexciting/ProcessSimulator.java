package stochastic.processes.selfexciting;

import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.lang.Math.random;
import static java.lang.System.out;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.fusesource.jansi.Ansi.ansi;
import static util.Console.println;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math3.optim.PointValuePair;
import org.knowm.xchart.SwingWrapper;

import dnl.utils.text.table.TextTable;
import fastmath.DoubleColMatrix;
import fastmath.Vector;
import fastmath.matfile.MatFile;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import junit.framework.TestCase;
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
    // process.ε = 0.05;
    process.T = new Vector(new double[]
    { 1 });
    process.dT = new Vector(new double[] {});

    out.println("simulating " + ansi().fgBrightYellow() + process + ansi().fgDefault() + " from " + process.T.size() + " points");
    int n = process.T.size();
    for (int i = 0; i < 20; i++)
    {
      double y = -log(1 - random());
      process.trace = true;
      double dt = process.invΛ(y);
      process.trace = false;

      double q = process.Λ(n - 1, dt);
      double nextTime = process.T.getRightmostValue() + dt;
      out.println("y=" + y + " dt=" + dt + " q=" + q + " nextTime=" + nextTime);
      TestCase.assertEquals("y=" + y + " dt=" + dt + " q=" + q + " nextTime=" + nextTime, y, q, 1E-12);
      process.T = process.T.copyAndAppend(nextTime); // round up all remainders since the source data has this property as well.
                                                     // TODO: compare results with and without fractional part.
      process.dT = process.dT.copyAndAppend(dt);
      n++;
      process.A = null; // TODO: expand A here as well
      process.AReal = null; // TODO: expand A here as well

      out.println("T=" + process.T);
      out.println("Λ=" + process.Λ());
      out.println("Λmean=" + process.Λ().mean());
      out.println("Λvar=" + process.Λ().variance());
    }

    //
    // double y = 0.9;
    // double nextdt = process.invΛ(y, n - 2);
    // process.trace = true;
    // // Real nextdtReal = process.invΛReal(y, n - 2);
    // process.trace = false;
    // double shouldbe0 = process.Φ(nextdt, y, n - 2);
    // // Real shouldbe0Real = process.ΛphaseReal(nextdtReal, y, n - 2);
    // out.println("shouldbe0=" + shouldbe0 + "\nβproduct=" + process.βproduct() + "
    // βproductReal=" + process.βproductReal());
    // double shouldbey = process.Λ(n - 1, nextdt );
    // out.println("shouldbey=" + shouldbey + " should be y=" + y);
    // process.T = process.T.append(process.T.fmax() + nextdt);
    //
    // out.println("nextdt=" + nextdt );
    //
    // process.dT = null;
    // out.println("comp (rec) " + ansi().fgBrightMagenta() + process.Λ() +
    // ansi().fgDefault());
    // process.recursive = false;
    // out.println("comp (full) " + ansi().fgBrightMagenta() + process.Λ() +
    // ansi().fgDefault());
    //
    // out.println("∫comp " + process.iΛ());

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
