package stochastic.processes.selfexciting;

import static java.lang.System.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import fastmath.DoubleColMatrix;
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
      classifier.record(price);
      double t = times.get(i);
      if (side == Side.Buy)
      {
        buyTimes = buyTimes.append(t);
      }
      else if ( side == Side.Sell )
      {
        sellTimes = sellTimes.append(t);
      }
    }

    out.println("Loading " + n + " pieces");
    int[] indexes = getIndices(times, n);
    int[] buyIndexes = getIndices(buyTimes, n);
    int[] sellIndexes = getIndices(sellTimes, n);

//    out.println( "buyTimes=" + buyTimes );
//    out.println( "sellTimes=" + sellTimes );
    
    //ArrayList<ExponentialSelfExcitingProcess> processes = getCalibratedProcesses(matFile, n, times, indexes);
    ArrayList<ExponentialSelfExcitingProcess> processes = getCalibratedProcesses(matFile, n, times, indexes);

    showProcessStats(processes);

    out.println("indexes=" + Arrays.toString(indexes));
    out.println("buyIndexes=" + Arrays.toString(buyIndexes));
    out.println("sellIndexes=" + Arrays.toString(sellIndexes));

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

  public static void showProcessStats(ArrayList<ExponentialSelfExcitingProcess> processes)
  {
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
  }

  public static ArrayList<ExponentialSelfExcitingProcess> getCalibratedProcesses(final String matFile, int n, Vector times, int[] indexes)
  {
    ArrayList<ExponentialSelfExcitingProcess> processes = new ArrayList<>();
   for (int i = 0; i < n; i++)
    {
      Vector timeSlice = times.slice(i == 0 ? 0 : indexes[i - 1], indexes[i]);

      ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();
      process.T = timeSlice;
      process.loadParameters(new File(matFile + ".eapl." + i + ".model"));
      processes.add(process);

    }
    return processes;
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
      if ( idx == -1)
      {
        idx = times.size() - 1;
      }
      indexes[i] = idx;
    }
    return indexes;
  }
}
