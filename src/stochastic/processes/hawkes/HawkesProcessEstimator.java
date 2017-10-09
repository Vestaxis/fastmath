package stochastic.processes.hawkes;

import static fastmath.Console.println;
import static java.lang.System.out;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.optim.PointValuePair;

import dnl.utils.text.table.TextTable;
import fastmath.Vector;
import fastmath.matfile.MatFile;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import stochastic.processes.hawkes.ExponentialHawkesProcessFactory.Type;

public class HawkesProcessEstimator
{
  private ExponentialHawkesProcess process;

  public HawkesProcessEstimator(ExponentialHawkesProcess process)
  {
    this.process = process;
  }

  public static void main(String[] args) throws IOException, CloneNotSupportedException
  {
    ExponentialHawkesProcessFactory.Type type = Type.ExtendedExponentialPowerlawApproximation;

    String filename = args.length > 0 ? args[0] : "/home/stephen/git/fastmath/SPY.mat";
    if (args.length > 1)
    {
      int typeIndex = -1;
      try
      {
        typeIndex = Integer.valueOf(args[1]);
        if (typeIndex == -1)
        {
          typeIndex = Type.valueOf(args[1]).ordinal();
        }
      }
      catch (Exception e)
      {
      }
      if (typeIndex != -1)
      {
        type = Type.values()[typeIndex];
      }
    }
    ExponentialHawkesProcess process = ExponentialHawkesProcessFactory.spawnNewProcess(type);

    Vector data = loadData(filename, "SPY");
    HawkesProcessEstimator estimator = new HawkesProcessEstimator(process);
    estimator.estimate(data);
  }

  private boolean verbose = true;

  public void estimate(Vector data) throws IOException
  {
    if (verbose)
    {
      println("spawning " + process.getClass().getSimpleName());
    }

    process.T = data;
    ParallelMultistartMultivariateOptimizer optimizer = process.estimateParameters();
    printResults(optimizer);

    File testFile = new File("test.mat");
    Vector compensator = process.Λ().setName("comp");
    out.println("writing timestamp data and compensator to " + testFile.getAbsolutePath());
    MatFile.write(testFile, data.createMiMatrix(), compensator.createMiMatrix());

    Vector comp = process.Λ();
    out.println("comp mean=" + comp.mean());
    out.println("comp var=" + comp.variance());

    out.println(optimizer.getIterations() + " iterations");
  }

  public TextTable printResults(ParallelMultistartMultivariateOptimizer multiopt)
  {

    String[] statisticNames =
    { "Log-Lik", "1-KS", "mean(Λ)", "var(Λ)", "σ" };

    BoundedParameter[] params = process.getBoundedParameters();

    println("estimating parameters for " + process.getClass().getSimpleName()
            + "["
            + Arrays.stream(params).map(param -> param.getName()).collect(Collectors.joining(","))
            + "]");

    PointValuePair[] optima = multiopt.getOptima().toArray(new PointValuePair[0]);

    String[] columnHeaders = Stream.concat(Arrays.stream(params).map(param -> param.getName()), Arrays.asList(statisticNames).stream())
                                   .collect(Collectors.toList())
                                   .toArray(new String[0]);

    Object[][] data = new Object[optima.length][columnHeaders.length];

    for (int i = 0; i < optima.length; i++)
    {
      Object[] row = process.evaluateParameterStatistics(optima[i].getPoint());

      for (int j = 0; j < columnHeaders.length; j++)
      {
        data[i][j] = row[j];
      }
    }

    TextTable tt = new TextTable(columnHeaders, data);

    tt.setAddRowNumbering(true);
    tt.printTable();
    return tt;
  }

  public static Vector loadData(String filename, String symbol) throws IOException
  {
    Vector data = MatFile.loadMatrix(filename, symbol).col(0).setName("data");
    int midpoint = data.size() / 2;
    data = data.slice(midpoint - 5000, midpoint + 5000);
    return data;
  }
}
