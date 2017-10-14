package stochastic.processes.hawkes;

import static fastmath.Console.println;
import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.math3.optim.PointValuePair;

import dnl.utils.text.table.TextTable;
import fastmath.Vector;
import fastmath.matfile.MatFile;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import stochastic.processes.hawkes.ExponentialHawkesProcessFactory.Type;
import util.TerseThreadFactory;

public class HawkesProcessEstimator
{
  static
  {
    System.setProperty( "java.util.concurrent.ForkJoinPool.common.threadFactory", TerseThreadFactory.class.getName());
  }

  private ExponentialHawkesProcess process;

  public HawkesProcessEstimator(ExponentialHawkesProcess process)
  {
    this.process = process;
  }

  public static void main(String[] args) throws IOException, CloneNotSupportedException
  {
    
    ExponentialHawkesProcessFactory.Type type = Type.ApproximatePowerlaw;

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
    Vector autocor = data.diff().autocor(50);

    double lb = autocor.getLjungBoxStatistic(10);
    
    out.println("LjungBox(dT,10)=" + lb);
    HawkesProcessEstimator estimator = new HawkesProcessEstimator(process);
    estimator.estimate(data);
  }

  private boolean verbose = true;

  /**
   * 
   * @return number of trajectories do generate during search for optimal
   *         parameters
   * 
   */
  public int getTrajectoryCount()
  {
    return Runtime.getRuntime().availableProcessors() * 4;
  }

  public void estimate(Vector data) throws IOException
  {
    if (verbose)
    {
      println("spawning " + getTrajectoryCount() + " " + process.getClass().getSimpleName() + "es");
    }

    process.T = data;
    ParallelMultistartMultivariateOptimizer optimizer = process.estimateParameters(getTrajectoryCount());
    printResults(optimizer);

    File testFile = new File("test.mat");
    Vector compensator = process.Λ().setName("comp");
    out.println("writing timestamp data and compensator to " + testFile.getAbsolutePath());
    MatFile.write(testFile, data.createMiMatrix(), compensator.createMiMatrix());

  }

  public TextTable printResults(ParallelMultistartMultivariateOptimizer multiopt)
  {

    BoundedParameter[] params = process.getBoundedParameters();

    println("estimated parameters for " + process.getClass().getSimpleName()
            + "["
            + Arrays.stream(params).map(param -> param.getName()).collect(Collectors.joining(","))
            + "]");

    PointValuePair[] optima = multiopt.getOptima().toArray(new PointValuePair[0]);

    String[] columnHeaders = Stream
                                   .concat(Arrays.stream(params).map(param -> param.getName()),
                                           Arrays.asList(ExponentialHawkesProcess.statisticNames).stream())
                                   .collect(Collectors.toList())
                                   .toArray(new String[0]);

    Object[][] data = evaluateStatisticsForEachLocalOptima(optima, columnHeaders);

    TextTable tt = new TextTable(columnHeaders, data);

    tt.setAddRowNumbering(true);
    tt.printTable();

    return tt;
  }

  public Object[][] evaluateStatisticsForEachLocalOptima(PointValuePair[] optima, String[] columnHeaders)
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

  public static Vector loadData(String filename, String symbol) throws IOException
  {
    return loadData( filename, symbol, 5000 );
  }
  
  public static Vector loadData(String filename, String symbol, int n) throws IOException
  {
    Vector data = MatFile.loadMatrix(filename, symbol).col(0).setName("data");
    int midpoint = data.size() / 2;
    data = data.slice(midpoint - n, midpoint + n);
    return data;
  }

}
