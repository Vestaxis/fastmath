package stochastic.processes.selfexciting;

import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import fastmath.DoubleMatrix;
import fastmath.DoubleRowMatrix;
import fastmath.Pair;
import fastmath.Vector;
import fastmath.Vector.Condition;
import stochastic.processes.point.MarkedPointProcess;
import util.DateUtils;

public class TradingStrategy
{
  public static void main(String args[]) throws FileNotFoundException, IOException
  {
    MarkedPointProcess mpp = MarkedPointProcess.loadMppFile(args.length > 0 ? args[0] : "/data/2016-05-20/SPY-2016-05-20.mpp");
    Pair<DoubleRowMatrix, DoubleRowMatrix> buySellMatrix = mpp.getBuySellMatrix(TimeUnit.MILLISECONDS);
    DoubleRowMatrix trades = mpp.getTradeMatrix(TimeUnit.MILLISECONDS);
    Vector times = trades.col(0);
    double firstTime = DateUtils.convertTimeUnits(times.get(0), TimeUnit.MILLISECONDS, TimeUnit.HOURS);
    double otherFuckingWay = DateUtils.convertTimeUnits(firstTime, TimeUnit.HOURS, TimeUnit.MILLISECONDS);
    
    // TODO: fix the damn bug here by modifying MarkedPointProcess so that the timestamp is in units of milliseconds past midnight, rather
    // than midnights since the UNIX epoch
    out.println("time " + firstTime + " " + times.get(0) + " fuck " + otherFuckingWay);
    // System.exit(1);
    out.println("total number of times " + times.size());

    int indexes[] = new int[13];

    for (int i = 1; i <= 13; i++)
    {
      double halfHour = 9.5 + (i * 0.5);
      double t = DateUtils.convertTimeUnits(halfHour, TimeUnit.HOURS, TimeUnit.MILLISECONDS);
      int idx = times.find(t, Condition.GTE, 0);
      if (i == 13 && idx == -1)
      {
        idx = trades.getRowCount() - 1;
      }
      indexes[i - 1] = idx;
    }
    for ( int i = 1; i <= 13; i++ )
    {
      DoubleMatrix slice = trades.sliceRows(indexes[i-1], indexes[i]);
      
    }
    out.println("indexes=" + Arrays.toString(indexes));

    // DoubleRowMatrix tradeSlice = trades.sliceRows(start, end)

    ExtendedApproximatePowerlawSelfExcitingProcess model = new ExtendedApproximatePowerlawSelfExcitingProcess();
    model.T = times;
    model.κ = 0.17171745708245348;
    model.η = 6.2285292213796675;
    model.b = 1.7066834288042811;
    model.ε = 0.09553988705003424;
    model.τ0 = 0.0014248824597174227;
    out.println("Using " + model);
    double modelCompMean = model.Λ().mean();
    double modelCompVar = model.Λ().variance();
    out.println("comp mean and var=" + modelCompMean + " " + modelCompVar);

    // TODO: estimate model parameters for each 30 minute interval, where it is intended that the point processes
    // in question are quasi-stationary over the length of the interval
    
    // TODO: 1. use the same model calibrated on the trade process  
    // to calculate the buy and sell intensities.. that is, we do NOT need to calibrate on
    // the buy and sell processes separately since these set memberships are inferred anyway.
    // The technical term for this is "smoothing", when you revise past estimates based on new data.
    // When you revise current estimates based on current information, its filtering, when you project future estimates based on current information, 
    // thats prediction
    
  
    // Note: whatever the trade intensity is, we assume the buy intensity is half of that, and likewise the sell intensity is half of that
    // This fact will be used to do do smoothing, filtering, and prediction of the buy and sell point processes using the Hawkes process
    // calibrated on the trade intensity. This assumption is the random walk assumption that there is no prior supposition that the 
    // price would move up or down apriori. 
    
    // 2. Price direction expectation could be incorporated by introducing an asymmetry in the
    // trigger for long/short position triggering.
    
    
    // 3. determine some threshold to trigger trades and see if its profitable

  }
}
