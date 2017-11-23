package stochastic.processes.selfexciting;

import static java.lang.System.out;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static util.Console.println;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.optim.PointValuePair;

import dnl.utils.text.table.TextTable;
import fastmath.DoubleColMatrix;
import fastmath.Vector;
import fastmath.matfile.MatFile;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;
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
                             CloneNotSupportedException
  {
    SelfExcitingProcessFactory.Type type = Type.ConstrainedApproximatePowerlaw; // Type.ExtendedApproximatePowerlaw;
    int cpuMultiplier = 1;
    if (type == Type.ConstrainedApproximatePowerlaw)
    {
      cpuMultiplier = 2;
    }

    String symbol = "SPY";

    ExponentialSelfExcitingProcess process = (ExponentialSelfExcitingProcess) SelfExcitingProcessFactory.spawnNewProcess(type, 1);
    process.loadParameters(new File(args[0]));
    ExponentialDistribution expDist = new ExponentialDistribution(1);

    double t = 0;
    int n = 1000;
    Vector T = new Vector(n);
    Vector Λ = new Vector(n);
    for (int i = 0; i < n; i++)
    {
      double h = expDist.sample();
      double dt = process.invH(h);
      Λ.set(i, h);
      T.set(i, t += dt);
    }
    out.println( "Λ=" + Λ );
    out.println( "T=" + T );
    
    Plotter.plot(T, Λ);
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
