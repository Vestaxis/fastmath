package stochastic.processes.selfexciting;

import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;

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
import fastmath.DoubleRowMatrix;
import fastmath.Pair;
import fastmath.Vector;
import fastmath.Vector.Condition;
import fastmath.matfile.MatFile;
import stochastic.processes.point.MarkedPointProcess;
import stochastic.processes.pointprocesses.finance.Side;
import stochastic.processes.selfexciting.gui.ModelViewer;
import util.DateUtils;
import util.TradeClassifier;

public class TradingStrategy
{
  public static void main(String args[]) throws FileNotFoundException, IOException
  {
    final String matFile = args.length > 0 ? args[0] : "/home/stephen/fm/SPY.mat";
    final String symbol = args.length > 1 ? args[1] : "SPY";

    ArrayList<ExponentialSelfExcitingProcess> processes = new ArrayList<>();
    int n = (int) (MarkedPointProcess.tradingDuration / SelfExcitingProcessEstimator.W);

    DoubleColMatrix matrix = MatFile.loadMatrix(matFile, symbol);
    Vector times = matrix.col(0).setName("times");
    Vector prices = matrix.col(1).setName("prices");
    TradeClassifier classifier = new TradeClassifier();
    Vector buyTimes = new Vector();
    Vector sellTimes = new Vector();
    for (int i = 0; i < times.size(); i++)
    {
      double price = prices.get(i);
      Side side = classifier.classify(price);
      if (side == Side.Buy)
      {
        buyTimes.add(times.get(i));
      }
      else if ( side == Side.Sell )
      {
        sellTimes.add(times.get(i));
      }
    }

    out.println("Loading " + n + " pieces");
    int[] indexes = getIndices(times, n);
    int[] buyIndexes = getIndices(buyTimes, n);
    int[] sellIndexes = getIndices(sellTimes, n);

    for (int i = 0; i < n; i++)
    {
      Vector buyTimesSlice = times.slice(i == 0 ? 0 : indexes[i - 1], indexes[i]);

      ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();
      process.T = buyTimesSlice;
      process.loadParameters(new File(matFile + ".eapl." + i + ".model"));
      processes.add(process);

    }

    List<Object[]> processStats = processes.stream()
                                           .map(process -> process.evaluateParameterStatistics(process.getParameters().toArray()))
                                           .collect(Collectors.toList());
    int M = processStats.size();
    int N = processes.get(0).getColumnHeaders().length;
    Object[][] stats = new Object[M][N];
    for (int i = 0; i < M; i++)
    {
      for (int j = 0; j < N; j++)
      {
        stats[i][j] = processStats.get(i)[j];
      }
    }
    ModelViewer viewer = new ModelViewer(processes.get(0).getColumnHeaders(), stats);
    viewer.show();

    out.println("indexes=" + Arrays.toString(indexes));

    // TODO: estimate model parameters for each 30 minute interval, where it is
    // intended that the point processes
    // in question are quasi-stationary over the length of the interval

    // TODO: 1. use the same model calibrated on the trade process
    // to calculate the buy and sell intensities.. that is, we do NOT need to
    // calibrate on
    // the buy and sell processes separately since these set memberships are
    // inferred anyway.
    // The technical term for this is "smoothing", when you revise past estimates
    // based on new data.
    // When you revise current estimates based on current information, its
    // filtering, when you project future estimates based on current information,
    // thats prediction

    // Note: whatever the trade intensity is, we assume the buy intensity is half of
    // that, and likewise the sell intensity is half of that
    // This fact will be used to do do smoothing, filtering, and prediction of the
    // buy and sell point processes using the Hawkes process
    // calibrated on the trade intensity. This assumption is the random walk
    // assumption that there is no prior supposition that the
    // price would move up or down apriori.

    // 2. Price direction expectation could be incorporated by introducing an
    // asymmetry in the
    // trigger for long/short position triggering.

    // 3. determine some threshold to trigger trades and see if its profitable

  }

  public static int[] getIndices(Vector times, int n)
  {
    int indexes[] = new int[n];
    for (int i = 0; i < n; i++)
    {
      double startPoint = MarkedPointProcess.openTime + ((i) * SelfExcitingProcessEstimator.W);
      double endPoint = MarkedPointProcess.openTime + ((i + 1) * SelfExcitingProcessEstimator.W);

      double t = DateUtils.convertTimeUnits(endPoint, TimeUnit.HOURS, TimeUnit.MILLISECONDS);
      int idx = times.find(t, Condition.GTE, 0);
      if (i == n && idx == -1)
      {
        idx = times.size() - 1;
      }
      indexes[i] = idx;
    }
    return indexes;
  }
}
