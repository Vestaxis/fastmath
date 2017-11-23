package stochastic.processes.selfexciting;

import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static org.fusesource.jansi.Ansi.ansi;
import static util.Console.println;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.optim.PointValuePair;
import org.fusesource.jansi.Ansi.Color;

import dnl.utils.text.table.TextTable;
import fastmath.DoubleColMatrix;
import fastmath.Vector;
import fastmath.matfile.MatFile;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import stochastic.annotations.Units;
import stochastic.processes.pointprocesses.finance.NasdaqTradingProcess;
import stochastic.processes.pointprocesses.finance.NasdaqTradingStrategy;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;
import util.TerseThreadFactory;

public class SelfExcitingProcessEstimator
{
  @Units(time = TimeUnit.HOURS)
  public static final double W = 0.5; // half hour

  static
  {
    System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", TerseThreadFactory.class.getName());
  }

  private AbstractSelfExcitingProcess process;

  private int trajectoryCount = Runtime.getRuntime().availableProcessors();

  public SelfExcitingProcessEstimator(SelfExcitingProcess process)
  {
    this.process = (AbstractSelfExcitingProcess) process;
  }

  public static void
         main(String[] args) throws IOException,
                             CloneNotSupportedException
  {

    SelfExcitingProcessFactory.Type type = Type.ExtendedApproximatePowerlaw;
    String filename = args.length > 0 ? args[0] : "/home/stephen/git/fastmath/SPY.mat";
    int cpuMultiplier = 1;

    cpuMultiplier = 4;
    int trajectoryCount = Runtime.getRuntime().availableProcessors() * cpuMultiplier;
    if (args.length > 1)
    {
      trajectoryCount = Integer.valueOf(args[1]);
    }
    String symbol = "SPY";

    out.println("Estimating parameters for " + filename);
    ArrayList<AbstractSelfExcitingProcess> processes = estimateSelfExcitingProcess(type, filename, trajectoryCount, symbol);

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
  public static ArrayList<AbstractSelfExcitingProcess>
         estimateSelfExcitingProcess(SelfExcitingProcessFactory.Type type,
                                     String filename,
                                     String symbol) throws IOException

  {
    return estimateSelfExcitingProcess(type, filename, Runtime.getRuntime().availableProcessors(), symbol);
  }

  public static ArrayList<AbstractSelfExcitingProcess>
         estimateSelfExcitingProcess(SelfExcitingProcessFactory.Type type,
                                     String filename,
                                     int trajectoryCount,
                                     String symbol) throws IOException
  {
    Vector data = loadTimes(filename, symbol);

    return estimateSelfExcitingProcesses(type, trajectoryCount, data);
  }

  /**
   * Return an array of calibrated self-exciting processes by splitting the
   * trading session up into windows of length this{@link #W}
   * 
   * @param type
   *          {@link Type} of self-exciting process to use
   * 
   * @param trajectoryCount
   *          number of random starts for the multistart optimizer to use to
   *          determine optimal parameters
   * @param times
   * @return
   * @throws IOException
   */
  public static ArrayList<AbstractSelfExcitingProcess>
         estimateSelfExcitingProcesses(SelfExcitingProcessFactory.Type type,
                                       int trajectoryCount,
                                       Vector times) throws IOException
  {

    double Edt = times.diff().mean();

    out.println("E[dt]=" + Edt);

    ArrayList<AbstractSelfExcitingProcess> processes = new ArrayList<>();
    int n = (int) (NasdaqTradingProcess.tradingDuration / W);
    int indexes[] = NasdaqTradingStrategy.getIndices(times);

    range(0, n).forEachOrdered(i -> {
      Vector slice = times.slice(i == 0 ? 0 : indexes[i - 1], indexes[i]);
      AbstractSelfExcitingProcess process = SelfExcitingProcessFactory.spawnNewProcess(type, 1);
      SelfExcitingProcessEstimator estimator = new SelfExcitingProcessEstimator(process);
      estimator.setTrajectoryCount(trajectoryCount);
      estimator.estimate(slice);
      processes.add(process);

      File testFile = new File("test" + i + ".mat");
      File modelFile = new File(testFile.getAbsolutePath() + "." + type.getFilenameExtension() + ".model");

      storeParameterEstimationResults(testFile, slice, process, modelFile);

    });

    return processes;
  }

  public static void
         storeParameterEstimationResults(File testFile,
                                         Vector data,
                                         AbstractSelfExcitingProcess process,
                                         File modelFile)
  {
    Vector compensator = process.Λ().setName("comp");
    Vector intensity = process.λvector().setName("intensity");
    out.println("writing timestamp data, compensator and intensity to " + testFile.getAbsolutePath() + " and parameters to " + modelFile);

    try
    {
      process.storeParameters(modelFile);
      MatFile.write(testFile, data.createMiMatrix(), compensator.createMiMatrix(), intensity.createMiMatrix());
    }
    catch (IOException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public void
         setTrajectoryCount(int trajectoryCount)
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
  public int
         getTrajectoryCount()
  {
    return trajectoryCount;
  }

  public AbstractSelfExcitingProcess
         estimate(Vector data)
  {
    if (verbose)
    {
      println(ansi().fgRed()
                    .bgBright(Color.WHITE)
                    .a("spawning ")
                    .fgBrightMagenta()
                    .a(getTrajectoryCount())
                    .fgGreen()
                    .a(" " + process.getClass().getSimpleName())
                    .fgRed()
                    .bgBright(Color.WHITE)
                    .a("es to estimate the model parameters ")
                    .fgBrightBlue()
                    .a("[" + asList(process.getParameterFields()).stream().map(field -> field.getName()).collect(joining(",")))
                    .fgRed()
                    .bgBright(Color.WHITE)
                    .a("] most likely to have generated the observed sequence of ")
                    .fgBrightMagenta()
                    .a(data.size())
                    .fgRed()
                    .bgBright(Color.WHITE)
                    .a(" timestamps")
                    .reset());
    }

    process.T = data;
    ParallelMultistartMultivariateOptimizer optimizer = process.estimateParameters(getTrajectoryCount(), j -> out.println("j=" + j));
    printResults(optimizer);

    return process;

  }

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
    Vector times = matrix.col(0).copy().setName("times");

    return times;
  }

}
