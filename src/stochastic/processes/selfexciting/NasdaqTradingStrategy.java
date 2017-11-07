package stochastic.processes.selfexciting;

import static java.lang.System.out;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import fastmath.DoubleColMatrix;
import fastmath.DoubleMatrix;
import fastmath.IntVector;
import fastmath.Vector;
import fastmath.Vector.Condition;
import fastmath.matfile.MatFile;
import stochastic.processes.point.MarkedPointProcess;
import stochastic.processes.pointprocesses.finance.Side;
import stochastic.processes.selfexciting.gui.ModelViewer;
import util.DateUtils;
import util.TradeClassifier;

public class NasdaqTradingStrategy
{
  public static void main(String args[]) throws FileNotFoundException, IOException
  {
    final String matFile = args.length > 0 ? args[0] : "/home/stephen/fm/SPY.mat";
    final String symbol = args.length > 1 ? args[1] : "SPY";

    DoubleColMatrix markedPoints = MatFile.loadMatrix(matFile, symbol);
    Vector times = markedPoints.col(0).setName("times");
    Vector prices = markedPoints.col(1).setName("prices");
    IntVector types = new IntVector( times.size() );
    TradeClassifier classifier = new TradeClassifier();
    
    Vector buyTimes = new Vector(times.size());
    Vector sellTimes = new Vector(times.size());
    int buyCount = 0;
    int sellCount = 0;
    for (int i = 0; i < times.size(); i++)
    {
      double price = prices.get(i);
      Side side = classifier.classify(price);
      types.set(i, side.ordinal() );
      classifier.record(price);
      double t = times.get(i);
      if (side == Side.Buy)
      {
        buyTimes = buyTimes.append(t);
        buyCount++;
      }
      else if (side == Side.Sell)
      {
        sellTimes = sellTimes.append(t);
        sellCount++;
      }
    }
    buyTimes = buyTimes.slice(0, buyCount );
    sellTimes = sellTimes.slice(0, sellCount );
    
    int[] indexes = getIndices(times);
    int[] buyIndexes = getIndices(buyTimes);
    int[] sellIndexes = getIndices(sellTimes);

    // out.println( "buyTimes=" + buyTimes );
    // out.println( "sellTimes=" + sellTimes );

    ArrayList<ExponentialSelfExcitingProcess> processes = getCalibratedProcesses(matFile, times, indexes, markedPoints);

    launchModelViewer(processes).frame.setTitle(ModelViewer.class.getSimpleName() + ": " + matFile);

    out.println("indexes=" + Arrays.toString(indexes));
    out.println("buyIndexes=" + Arrays.toString(buyIndexes));
    out.println("sellIndexes=" + Arrays.toString(sellIndexes));

    // TODO: 1. implement the multivariate exponential self-exciting process code
    // that
    // was written before and adapt the exponential approximations of powerlaws so
    // they
    // work in a multivariate manner. In this case, bivariate process of buys/sells
    // were the superposition of the two inferred processes follows a known process.

    // 2. Price direction expectation could be incorporated by introducing an
    // asymmetry in the trigger for long/short position triggering.

    // 3. determine some threshold to trigger trades and see if its profitable

  }

  public static ModelViewer launchModelViewer(ArrayList<ExponentialSelfExcitingProcess> processes)
  {
    List<Object[]> processStats = processes.stream().map(process -> process.evaluateParameterStatistics(process.getParameters().toArray())).collect(toList());
    int M = processStats.size();
    String[] columnHeaders = processes.get(0).getColumnHeaders();
    int N = columnHeaders.length;
    Object[][] stats = new Object[M][N];
    for (int i = 0; i < M; i++)
    {
      for (int j = 0; j < N; j++)
      {
        stats[i][j] = processStats.get(i)[j];
      }
    }
    ModelViewer viewer = new ModelViewer(columnHeaders, stats, processes);
    viewer.show();
    return viewer;
  }

  public static ArrayList<ExponentialSelfExcitingProcess> getCalibratedProcesses(final String matFile, Vector times, int[] indexes,
      DoubleColMatrix markedPoints)
  {
    int n = indexes.length;
    ArrayList<ExponentialSelfExcitingProcess> processes = new ArrayList<>();
    for (int i = 0; i < n; i++)
    {
      DoubleMatrix pointSlice = markedPoints.sliceRows(i == 0 ? 0 : indexes[i - 1], indexes[i]);
      Vector timeSlice = pointSlice.col(0);

      ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();
      process.X = pointSlice;
      process.T = timeSlice;
      process.loadParameters(new File(matFile + ".eapl." + i + ".model"));
      processes.add(process);

    }
    return processes;
  }

  /**
   * 
   * @param times
   *          a {@link Vector} of timestamps covering the span
   *          {@link MarkedPointProcess#openTime} to
   *          {@link MarkedPointProcess#closeTime}
   * @return an array of offsets indicating the timestamp indexes which divides
   *         the data into n windows of length
   *         {@link SelfExcitingProcessEstimator#W} hours over the interval
   *         {@link MarkedPointProcess#openTime} to
   *         {@link MarkedPointProcess#closeTime}
   */
  public static int[] getIndices(Vector times)
  {
    int n = (int) (MarkedPointProcess.tradingDuration / SelfExcitingProcessEstimator.W);
    out.println("Dividing the interval into " + n + " pieces");
    int indexes[] = new int[n];
    for (int i = 0; i < n; i++)
    {
      double startPoint = MarkedPointProcess.openTime + ((i) * SelfExcitingProcessEstimator.W);
      double endPoint = MarkedPointProcess.openTime + ((i + 1) * SelfExcitingProcessEstimator.W);

      double t = DateUtils.convertTimeUnits(endPoint, TimeUnit.HOURS, TimeUnit.MILLISECONDS);
      int idx = times.find(t, Condition.GTE, 0);
      if (idx == -1)
      {
        idx = times.size() - 1;
      }
      indexes[i] = idx;
    }
    return indexes;
  }
}
