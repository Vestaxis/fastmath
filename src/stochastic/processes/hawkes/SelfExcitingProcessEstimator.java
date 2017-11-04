package stochastic.processes.hawkes;

import static fastmath.Console.println;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.math3.optim.PointValuePair;

import dnl.utils.text.table.TextTable;
import fastmath.DoubleColMatrix;
import fastmath.Vector;
import fastmath.Vector.Condition;
import fastmath.matfile.MatFile;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import stochastic.processes.hawkes.ExponentialSelfExcitingProcessFactory.Type;
import stochastic.processes.point.MarkedPointProcess;
import stochastics.annotations.Units;
import util.DateUtils;
import util.TerseThreadFactory;

public class SelfExcitingProcessEstimator
{
  @Units(time = TimeUnit.HOURS)
  private static final double W = 0.5; // one tenth of a half hour

  static
  {
    System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", TerseThreadFactory.class.getName());
  }

  private ExponentialSelfExcitingProcess process;
  private int trajectoryCount = Runtime.getRuntime().availableProcessors();

  public SelfExcitingProcessEstimator(ExponentialSelfExcitingProcess process)
  {
    this.process = process;
  }

  public static void main(String[] args) throws IOException, CloneNotSupportedException
  {

    ExponentialSelfExcitingProcessFactory.Type type = Type.ExtendedApproximatePowerlaw;
    String filename = args.length > 0 ? args[0] : "/home/stephen/git/fastmath/SPY.mat";

    int trajectoryCount = Runtime.getRuntime().availableProcessors() * 1;
    if (args.length > 1)
    {
      trajectoryCount = Integer.valueOf(args[1]);
    }
    String symbol = "SPY";

    out.println("Estimating parameters for " + filename);
    ArrayList<ExponentialSelfExcitingProcess> processes = estimateHawkesProcess(type, filename, trajectoryCount, symbol);
    for (int i = 0; i < processes.size(); i++)
    {
      File modelFile = new File(filename + "." + type.getFilenameExtension() + "." + i + ".model");
      ExponentialSelfExcitingProcess process = processes.get(i);
      double firstTimestampInInterval = DateUtils.convertTimeUnits(process.T.getLeftmostValue(), TimeUnit.MILLISECONDS, TimeUnit.HOURS);
      double lastTimestampInInterval = DateUtils.convertTimeUnits(process.T.getRightmostValue(), TimeUnit.MILLISECONDS, TimeUnit.HOURS);

      out.println("Storing estimated parameters in " + modelFile
                  + " covering the range "
                  + firstTimestampInInterval
                  + " to "
                  + lastTimestampInInterval
                  + " hours");

      process.storeParameters(modelFile);
    }

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
  public static ArrayList<ExponentialSelfExcitingProcess> estimateHawkesProcess(ExponentialSelfExcitingProcessFactory.Type type, String filename, String symbol)
      throws IOException

  {
    return estimateHawkesProcess(type, filename, Runtime.getRuntime().availableProcessors(), symbol);
  }

  public static ArrayList<ExponentialSelfExcitingProcess> estimateHawkesProcess(ExponentialSelfExcitingProcessFactory.Type type, String filename, int trajectoryCount,
      String symbol) throws IOException
  {
    Vector data = loadData(filename, symbol);

    return estimateHawkesProcesses(type, trajectoryCount, data);
  }

  public static ArrayList<ExponentialSelfExcitingProcess> estimateHawkesProcess(ExponentialSelfExcitingProcessFactory.Type type, Vector data) throws IOException
  {
    return estimateHawkesProcesses(type, Runtime.getRuntime().availableProcessors(), data);
  }

  /**
   * Return an array of calibrated Hawkes processes by splitting the trading
   * session up into windows of length this{@link #W}
   * 
   * @param type
   *          {@link Type} of self-exciting process to use
   * 
   * @param trajectoryCount
   *          number of random starts for the multistart optimizer to use to
   *          determine optimal parameters
   * @param data
   * @return
   * @throws IOException
   */
  public static ArrayList<ExponentialSelfExcitingProcess> estimateHawkesProcesses(ExponentialSelfExcitingProcessFactory.Type type, int trajectoryCount, Vector data)
      throws IOException
  {

    double Edt = data.diff().mean();

    out.println("E[dt]=" + Edt);

    ArrayList<ExponentialSelfExcitingProcess> processes = new ArrayList<>();
    int n = (int) (MarkedPointProcess.tradingDuration / W);
    int indexes[] = new int[n];
    out.println("Estimaing " + n + " pieces");
    for (int i = 0; i < n; i++)
    {
      double startPoint = MarkedPointProcess.openTime + ((i) * W);
      double endPoint = MarkedPointProcess.openTime + ((i + 1) * W);

      double t = DateUtils.convertTimeUnits(endPoint, TimeUnit.HOURS, TimeUnit.MILLISECONDS);
      int idx = data.find(t, Condition.GTE, 0);
      if (i == n && idx == -1)
      {
        idx = data.size() - 1;
      }
      indexes[i] = idx;
    }

    for (int i = 0; i < n; i++)
    {
      Vector slice = data.slice(i == 0 ? 0 : indexes[i - 1], indexes[i]);
      ExponentialSelfExcitingProcess process = ExponentialSelfExcitingProcessFactory.spawnNewProcess(type);
      SelfExcitingProcessEstimator estimator = new SelfExcitingProcessEstimator(process);
      estimator.setTrajectoryCount(trajectoryCount);
      estimator.estimate(slice);
      processes.add(process);

      File testFile = new File("test" + i + ".mat");
      storeParameterEstimationResults(testFile, slice, process);

    }

    return processes;
  }

  public static void storeParameterEstimationResults(File testFile, Vector data, ExponentialSelfExcitingProcess process) throws IOException
  {
    Vector compensator = process.Λ().setName("comp");
    Vector intensity = process.λvector().setName("intensity");
    out.println("writing timestamp data, compensator and intensity to " + testFile.getAbsolutePath()
                + " E[data.dt]="
                + data.diff().mean()
                + " 1/k="
                + (1 / process.κ));
    MatFile.write(testFile, data.createMiMatrix(), compensator.createMiMatrix(), intensity.createMiMatrix());
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

  public ExponentialSelfExcitingProcess estimate(Vector data) throws IOException
  {
    if (verbose)
    {
      println("spawning " + getTrajectoryCount()
              + " "
              + process.getClass().getSimpleName()
              + "es to estimate the model parameters ["
              + asList(process.getParameterFields()).stream().map(field -> field.getName()).collect(joining(","))
              + "] most likely to have generated the observed sequence of "
              + data.size()
              + " timestamps");
    }

    process.T = data;
    ParallelMultistartMultivariateOptimizer optimizer = process.estimateParameters(getTrajectoryCount());
    printResults(optimizer);

    return process;

  }

  public TextTable printResults(ParallelMultistartMultivariateOptimizer multiopt)
  {

    BoundedParameter[] params = process.getBoundedParameters();

    println("estimated parameters for " + process.getClass().getSimpleName() + "[" + stream(params).map(param -> param.getName()).collect(joining(",")) + "]");

    PointValuePair[] optima = multiopt.getOptima().toArray(new PointValuePair[0]);

    String[] columnHeaders = concat(stream(params).map(param -> param.getName()), asList(ExponentialSelfExcitingProcess.statisticNames).stream())
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

    DoubleColMatrix matrix = MatFile.loadMatrix(filename, symbol);
    Vector data = matrix.col(0).setName("data");

    return data;
  }

}
