package stochastic.processes.selfexciting.multivariate;

import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
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
import fastmath.DoubleMatrix;
import fastmath.IntVector;
import fastmath.Vector;
import fastmath.matfile.MatFile;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import stochastic.annotations.Units;
import stochastic.processes.pointprocesses.finance.NasdaqTradingProcess;
import stochastic.processes.pointprocesses.finance.NasdaqTradingStrategy;
import stochastic.processes.pointprocesses.finance.TradingFiltration;
import stochastic.processes.selfexciting.BoundedParameter;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;
import util.DateUtils;
import util.TerseThreadFactory;

public class MultivariateSelfExcitingProcessEstimator
{
  @Units(time = TimeUnit.HOURS)
  public static final double W = 0.5; // half hour

  static
  {
    System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", TerseThreadFactory.class.getName());
  }

  private MultivariateExponentialSelfExcitingProcess process;
  private int trajectoryCount = Runtime.getRuntime().availableProcessors();

  public MultivariateSelfExcitingProcessEstimator(MultivariateExponentialSelfExcitingProcess process)
  {
    this.process = process;
  }

  public static void
         main(String[] args) throws IOException,
                             CloneNotSupportedException
  {
    Type type = null; // Type.MultivariateExtendedApproximatePowerlaw;
    String filename = args.length > 0 ? args[0] : "/home/stephen/git/fastmath/SPY.mat";

    int trajectoryCount = Runtime.getRuntime().availableProcessors() * 1;
    if (args.length > 1)
    {
      trajectoryCount = Integer.valueOf(args[1]);
    }
    String symbol = "SPY";

    out.println("Estimating parameters for " + filename);
    ArrayList<MultivariateExponentialSelfExcitingProcess> processes = estimateSelfExcitingTradingProcess(type, filename, trajectoryCount, symbol);
    for (int i = 0; i < processes.size(); i++)
    {
      File modelFile = new File(filename + "." + type.getFilenameExtension() + "." + i + ".model");
      MultivariateExponentialSelfExcitingProcess process = processes.get(i);
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
  public static ArrayList<MultivariateExponentialSelfExcitingProcess>
         estimateHawkesProcess(Type type,
                               String filename,
                               String symbol) throws IOException

  {
    return estimateSelfExcitingTradingProcess(type, filename, Runtime.getRuntime().availableProcessors(), symbol);
  }

  public static ArrayList<MultivariateExponentialSelfExcitingProcess>
         estimateSelfExcitingTradingProcess(Type type,
                                            String filename,
                                            int trajectoryCount,
                                            String symbol) throws IOException
  {
    return estimateSelfExcitingTradingProcesses(type, trajectoryCount, new TradingFiltration(MatFile.loadMatrix(filename, symbol)));
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
   * @param times
   * @return
   * @throws IOException
   */
  public static ArrayList<MultivariateExponentialSelfExcitingProcess>
         estimateSelfExcitingTradingProcesses(Type type,
                                              int trajectoryCount,
                                              TradingFiltration tradingProcess) throws IOException
  {
    Vector times = tradingProcess.times;
    double Edt = times.diff().mean();

    out.println("E[dt]=" + Edt);

    ArrayList<MultivariateExponentialSelfExcitingProcess> processes = new ArrayList<>();
    int n = (int) (NasdaqTradingProcess.tradingDuration / W);
    int indexes[] = NasdaqTradingStrategy.getIndices(times);

    for (int i = 0; i < n; i++)
    {
      Vector timeSlice = times.slice(i == 0 ? 0 : indexes[i - 1], indexes[i]);
      IntVector typeSlice = tradingProcess.types.slice(i == 0 ? 0 : indexes[i - 1], indexes[i]);
      DoubleMatrix markedPointSlice = tradingProcess.markedPoints.sliceRows(i == 0 ? 0 : tradingProcess.tradeIndexes[i - 1], tradingProcess.tradeIndexes[i]);

      MultivariateExponentialSelfExcitingProcess process = MultivariateExponentialSelfExcitingProcess.spawnNewProcess(type, tradingProcess);

      MultivariateSelfExcitingProcessEstimator estimator = new MultivariateSelfExcitingProcessEstimator(process);
      estimator.setTrajectoryCount(trajectoryCount);
      estimator.estimate(markedPointSlice, typeSlice);
      processes.add(process);

      File testFile = new File("test" + i + ".mat");
      storeParameterEstimationResults(testFile, timeSlice, process);

    }

    return processes;
  }

  public static void
         storeParameterEstimationResults(File testFile,
                                         Vector data,
                                         MultivariateExponentialSelfExcitingProcess process) throws IOException
  {
    Vector compensator = process.dΛ().setName("comp");
    DoubleMatrix intensity = process.conditionalλ().setName("intensity");
    out.println("writing timestamp data, compensator and intensity to " + testFile.getAbsolutePath()
                + " E[data.dt]="
                + data.diff().mean()
                + " 1/k="
                + process.κ.pow(-1));
    MatFile.write(testFile, data.createMiMatrix(), compensator.createMiMatrix(), intensity.createMiMatrix());
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

  public MultivariateSelfExcitingProcess
         estimate(DoubleMatrix markedPoints,
                  IntVector types) throws IOException
  {
    process.T = markedPoints.col(0);
    process.X = markedPoints;
    process.K = types;

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
                    .a(process.T.size())
                    .fgRed()
                    .bgBright(Color.WHITE)
                    .a(" timestamps")
                    .reset());
    }

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
    Vector data = matrix.col(0).setName("data");

    return data;
  }

}
