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
import fastmath.Vector;
import fastmath.Vector.Condition;
import fastmath.matfile.MatFile;
import stochastic.processes.selfexciting.ExponentialSelfExcitingProcess;
import stochastic.processes.selfexciting.ExtendedApproximatePowerlawSelfExcitingProcess;
import stochastic.processes.selfexciting.SelfExcitingProcessEstimator;
import stochastic.processes.selfexciting.gui.ModelViewer;
import util.DateUtils;

public class NasdaqTradingStrategy
{
  public static void main(String args[]) throws FileNotFoundException, IOException
  {
    final String matFile = args.length > 0 ? args[0] : "/home/stephen/fm/SPY.mat";
    final String symbol = args.length > 1 ? args[1] : "SPY";

    TradingFiltration tradingProcess = new TradingFiltration(MatFile.loadMatrix(matFile, symbol));

    // out.println( "buyTimes=" + buyTimes );
    // out.println( "sellTimes=" + sellTimes );

    ArrayList<ExponentialSelfExcitingProcess> processes = getCalibratedProcesses(matFile, tradingProcess);

    launchModelViewer(processes).frame.setTitle(ModelViewer.class.getSimpleName() + ": " + matFile);

    // TODO: 1. finish implementing the multivariate exponential self-exciting
    // process code
    // that was written before and adapt the exponential approximations of powerlaws
    // so
    // they work in a multivariate manner. In this case, bivariate process of
    // buys/sells
    // were the superposition of the two inferred processes follows a known process.
    // 2. Price direction expectation could be incorporated by introducing an
    // asymmetry in the trigger for long/short position triggering. 3. determine
    // some
    // threshold to trigger trades and see if its profitable

  }

  public static ModelViewer launchModelViewer(ArrayList<ExponentialSelfExcitingProcess> processes)
  {
    ModelViewer viewer = new ModelViewer( processes);
    viewer.show();
    return viewer;
  }


  public static ArrayList<ExponentialSelfExcitingProcess> getCalibratedProcesses(final String matFile, TradingFiltration tradingFiltration)
  {
    int n = tradingFiltration.tradeIndexes.length;
    ArrayList<ExponentialSelfExcitingProcess> processes = new ArrayList<>();
    for (int i = 0; i < n; i++)
    {
      DoubleMatrix markedPointSlice = tradingFiltration.markedPoints.sliceRows(i == 0 ? 0 : tradingFiltration.tradeIndexes[i - 1],
                                                                               tradingFiltration.tradeIndexes[i]);
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
    out.println("Dividing the interval containing " + times.size() + " points into " + n + " pieces");
    int indexes[] = new int[n];
    for (int i = 0; i < n; i++)
    {
      double startPoint = NasdaqTradingProcess.openTime + ((i) * SelfExcitingProcessEstimator.W);
      double endPoint = NasdaqTradingProcess.openTime + ((i + 1) * SelfExcitingProcessEstimator.W);

      double t = DateUtils.convertTimeUnits(endPoint, TimeUnit.HOURS, TimeUnit.MILLISECONDS);
      int idx = times.find(t, Condition.GTE, i == 0 ? 0 : indexes[i - 1]);
      if (idx == -1)
      {
        idx = times.size() - 1;
      }
      indexes[i] = idx;
    }
    return indexes;
  }
}
