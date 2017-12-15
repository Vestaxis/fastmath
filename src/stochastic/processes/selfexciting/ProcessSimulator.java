package stochastic.processes.selfexciting;

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

    process.T = MatFile.loadMatrix("test0.mat", "times").asVector().copy().slice(0, 1000).setName("T");
    final double t0 = process.T.get(0);
    for (int i = 0; i < process.T.size(); i++)
    {
      process.T.set(i, (int) (process.T.get(i) - t0));
    }
    process.T = process.T.slice(1, process.T.size());
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

    double hmm = process.Φδ(40, 1, n - 1);
    out.println("hmm " + hmm);
    TestCase.assertEquals(-4.8963233710073894061, hmm, 1E-15);
    new SwingWrapper<>(Plotter.chart("x", "y", t -> process.Φδ(t, 1, n - 1), -25, 60, t -> t)).displayChart();

    for (double y = 0; y < 1; y += 0.05)
    {
      double dt = process.invΛ(y);
      double q = process.Λ(n - 1, dt);
      out.println("y=" + y + " dt=" + dt + " q=" + q);
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
