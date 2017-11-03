package stochastic.processes.hawkes;

import static fastmath.Console.println;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.math3.optim.PointValuePair;

import dnl.utils.text.table.TextTable;
import fastmath.DoubleColMatrix;
import fastmath.Vector;
import fastmath.matfile.MatFile;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import stochastic.processes.hawkes.ExponentialHawkesProcessFactory.Type;
import util.TerseThreadFactory;

public class HawkesProcessEstimator
{
  static
  {
    System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", TerseThreadFactory.class.getName());
  }

  private ExponentialHawkesProcess process;
  private int trajectoryCount = Runtime.getRuntime().availableProcessors();

  public HawkesProcessEstimator(ExponentialHawkesProcess process)
  {
    this.process = process;
  }

  /**
   * TODO: split the data into 30 minute chunks and estimate parameters on each
   * one
   * 
   * @param args
   * @throws IOException
   * @throws CloneNotSupportedException
   */
  public static void main(String[] args) throws IOException, CloneNotSupportedException
  {

    ExponentialHawkesProcessFactory.Type type = Type.ExtendedApproximatePowerlaw;
    String filename = args.length > 0 ? args[0] : "/home/stephen/git/fastmath/SPY.mat";

    int trajectoryCount = Runtime.getRuntime().availableProcessors() * 1;
    if (args.length > 1)
    {
      trajectoryCount = Integer.valueOf(args[1]);
    }
    String symbol = "SPY";

    out.println("Estimating parameters for " + filename);
    ExponentialHawkesProcess process = estimateHawkesProcess(type, filename, trajectoryCount, symbol);
    File modelFile = new File(filename + type.getFilenameExtension() + ".model");
    out.println("Storing estimated parameters in " + modelFile);
    process.storeParameters(modelFile);
    

  }

  /**
   * estimate the parameters of a Hawkes process model
   * 
   * @param type
   * @param filename
   * @param symbol
   * @return
   * @throws IOException
   */
  public static ExponentialHawkesProcess estimateHawkesProcess(ExponentialHawkesProcessFactory.Type type, String filename, String symbol) throws IOException

  {
    return estimateHawkesProcess(type, filename, Runtime.getRuntime().availableProcessors(), symbol);
  }

  public static ExponentialHawkesProcess estimateHawkesProcess(ExponentialHawkesProcessFactory.Type type, String filename, int trajectoryCount, String symbol)
      throws IOException
  {
    Vector data = loadData(filename, symbol);

    return estimateHawkesProcess(type, trajectoryCount, data);
  }

  public static ExponentialHawkesProcess estimateHawkesProcess(ExponentialHawkesProcessFactory.Type type, Vector data) throws IOException
  {
    return estimateHawkesProcess(type, Runtime.getRuntime().availableProcessors(), data);
  }

  public static ExponentialHawkesProcess estimateHawkesProcess(ExponentialHawkesProcessFactory.Type type, int trajectoryCount, Vector data) throws IOException
  {
    ExponentialHawkesProcess process = ExponentialHawkesProcessFactory.spawnNewProcess(type);

    double Edt = data.diff().mean();

    out.println("E[dt]=" + Edt);

    HawkesProcessEstimator estimator = new HawkesProcessEstimator(process);
    estimator.setTrajectoryCount(trajectoryCount);
    estimator.estimate(data);

    return process;
  }

  public void setTrajectoryCount(int trajectoryCount)
  {
    this.trajectoryCount = trajectoryCount;
  }

  private boolean verbose = true;

  /*
   * 
   * @return number of trajectories do generate during search for optimal
   * parameters
   * 
   */
  public int getTrajectoryCount()
  {
    return trajectoryCount;
  }

  public void estimate(Vector data) throws IOException
  {
    if (verbose)
    {
      println("spawning " + getTrajectoryCount()
              + " "
              + process.getClass().getSimpleName()
              + "es having parameters ["
              + asList(process.getParameterFields()).stream().map(field -> field.getName()).collect(joining(","))
              + "]");
    }

    process.T = data;
    ParallelMultistartMultivariateOptimizer optimizer = process.estimateParameters(getTrajectoryCount());
    printResults(optimizer);

    File testFile = new File("test.mat");
    Vector compensator = process.Λ().setName("comp");
    Vector intensity = process.λvector().setName("intensity");
    out.println("writing timestamp data, compensator and intensity to " + testFile.getAbsolutePath());
    MatFile.write(testFile, data.createMiMatrix(), compensator.createMiMatrix(), intensity.createMiMatrix());

  }

  public TextTable printResults(ParallelMultistartMultivariateOptimizer multiopt)
  {

    BoundedParameter[] params = process.getBoundedParameters();

    println("estimated parameters for " + process.getClass().getSimpleName()
            + "["
            + Arrays.stream(params).map(param -> param.getName()).collect(Collectors.joining(","))
            + "]");

    PointValuePair[] optima = multiopt.getOptima().toArray(new PointValuePair[0]);

    String[] columnHeaders = Stream.concat(Arrays.stream(params).map(param -> param.getName()), Arrays.asList(ExponentialHawkesProcess.statisticNames).stream())
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
    return loadData(filename, symbol, 5000);
  }

  public static Vector loadData(String filename, String symbol, int n) throws IOException
  {
    DoubleColMatrix matrix = MatFile.loadMatrix(filename, symbol);
    Vector data = matrix.col(0).setName("data");
    int midpoint = data.size() / 2;
    data = data.slice(midpoint - n, midpoint + n);
    return data;
  }

}
