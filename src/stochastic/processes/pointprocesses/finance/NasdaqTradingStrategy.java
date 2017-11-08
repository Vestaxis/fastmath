package stochastic.processes.pointprocesses.finance;

import static java.lang.System.out;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fastmath.DoubleMatrix;
import fastmath.IntVector;
import fastmath.Vector;
import fastmath.Vector.Condition;
import fastmath.matfile.MatFile;
import stochastic.processes.selfexciting.ExponentialSelfExcitingProcess;
import stochastic.processes.selfexciting.ExtendedApproximatePowerlawSelfExcitingProcess;
import stochastic.processes.selfexciting.SelfExcitingProcessEstimator;
import stochastic.processes.selfexciting.gui.ModelViewer;
import util.DateUtils;
import util.TradeClassifier;

public class NasdaqTradingStrategy
{
  public static class TradingProcess
  {
    public TradingProcess(DoubleMatrix markedPoints)
    {
      super();
      this.markedPoints = markedPoints;
      times = markedPoints.col(0).setName("times");
      prices = markedPoints.col(1).setName("prices");
      types = new IntVector(times.size());
      buyTimes = new Vector(times.size());
      sellTimes = new Vector(times.size());

      classifyTradeSequences();
    }

    public void classifyTradeSequences()
    {
      TradeClassifier classifier = new TradeClassifier();

      int buyCount = 0;
      int sellCount = 0;
      for (int i = 0; i < times.size(); i++)
      {
        double price = prices.get(i);
        Side side = classifier.classify(price);
        types.set(i, side.ordinal());
        classifier.record(price);
        double t = times.get(i);
        if (side == Side.Buy)
        {
          buyTimes.set(buyCount++, t);
        }
        else if (side == Side.Sell)
        {
          sellTimes.set(sellCount++, t);
        }
      }
      buyTimes = buyTimes.slice(0, buyCount);
      sellTimes = sellTimes.slice(0, sellCount);

      tradeIndexes = getIndices(times);
      buyIndexes = getIndices(buyTimes);
      sellIndexes = getIndices(sellTimes);

    }

    public Vector times;
    public Vector prices;
    public DoubleMatrix markedPoints;
    public IntVector types;
    public Vector buyTimes;
    public Vector sellTimes;
    public int[] tradeIndexes;
    public int[] buyIndexes;
    public int[] sellIndexes;
  }

  public static void main(String args[]) throws FileNotFoundException, IOException
  {
    final String matFile = args.length > 0 ? args[0] : "/home/stephen/fm/SPY.mat";
    final String symbol = args.length > 1 ? args[1] : "SPY";

    TradingProcess tradingProcess = new TradingProcess(MatFile.loadMatrix(matFile, symbol));

    // out.println( "buyTimes=" + buyTimes );
    // out.println( "sellTimes=" + sellTimes );

    ArrayList<ExponentialSelfExcitingProcess> processes = getCalibratedProcesses(matFile, tradingProcess);

    launchModelViewer(processes).frame.setTitle(ModelViewer.class.getSimpleName() + ": " + matFile);

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

  public static ArrayList<ExponentialSelfExcitingProcess> getCalibratedProcesses(final String matFile, TradingProcess tradingProcess)
  {
    int n = tradingProcess.tradeIndexes.length;
    ArrayList<ExponentialSelfExcitingProcess> processes = new ArrayList<>();
    for (int i = 0; i < n; i++)
    {
      DoubleMatrix markedPointSlice = tradingProcess.markedPoints.sliceRows(i == 0 ? 0 : tradingProcess.tradeIndexes[i - 1], tradingProcess.tradeIndexes[i]);
      Vector timeSlice = markedPointSlice.col(0);

      ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();
      process.X = markedPointSlice;
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
   *          {@link NasdaqTradingProcess#openTime} to
   *          {@link NasdaqTradingProcess#closeTime}
   * @return an array of offsets indicating the timestamp indexes which divides
   *         the data into n windows of length
   *         {@link SelfExcitingProcessEstimator#W} hours over the interval
   *         {@link NasdaqTradingProcess#openTime} to
   *         {@link NasdaqTradingProcess#closeTime}
   */
  public static int[] getIndices(Vector times)
  {
    int n = (int) (NasdaqTradingProcess.tradingDuration / SelfExcitingProcessEstimator.W);
    out.println("Dividing the interval into " + n + " pieces");
    int indexes[] = new int[n];
    for (int i = 0; i < n; i++)
    {
      double startPoint = NasdaqTradingProcess.openTime + ((i) * SelfExcitingProcessEstimator.W);
      double endPoint = NasdaqTradingProcess.openTime + ((i + 1) * SelfExcitingProcessEstimator.W);

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
