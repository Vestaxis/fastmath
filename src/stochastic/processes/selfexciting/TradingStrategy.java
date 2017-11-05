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

import fastmath.DoubleMatrix;
import fastmath.DoubleRowMatrix;
import fastmath.Pair;
import fastmath.Vector;
import fastmath.Vector.Condition;
import stochastic.processes.point.MarkedPointProcess;
import stochastic.processes.selfexciting.gui.ModelViewer;
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

    
    final String matFile = args.length > 0 ? args[0] : "/home/stephen/fm/SPY.mat";
    final String symbol = args.length > 1 ? args[1] : "SPY";

    
    ArrayList<ExponentialSelfExcitingProcess> processes = new ArrayList<>();
    int n = (int) (MarkedPointProcess.tradingDuration / SelfExcitingProcessEstimator.W);
    int indexes[] = new int[n];
    
    Vector data = SelfExcitingProcessEstimator.loadData(matFile, symbol);
    out.println("Loading " + n + " pieces");
    for (int i = 0; i < n; i++)
    {
      double startPoint = MarkedPointProcess.openTime + ((i) * SelfExcitingProcessEstimator.W);
      double endPoint = MarkedPointProcess.openTime + ((i + 1) * SelfExcitingProcessEstimator.W);

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
      // TODO: cool also load from the test%d.mat files but they need to be renamed to something like symbol-piece-%d.mat first
      Vector slice = data.slice(i == 0 ? 0 : indexes[i - 1], indexes[i]);
      ExtendedApproximatePowerlawSelfExcitingProcess process = new ExtendedApproximatePowerlawSelfExcitingProcess();
      process.T = slice;
      try
      {
        process.loadParameters(new File(matFile + ".eapl." + i + ".model"));
      }
      catch (IOException e)
      {
        throw new RuntimeException( e.getMessage(), e );
      }
      processes.add(process);

    }
    
    List<Object[]> processStats = processes.stream().map( process -> process.evaluateParameterStatistics(process.getParameters().toArray())).collect(Collectors.toList());
    int M = processStats.size();
    int N = processes.get(0).getColumnHeaders().length;
    Object[][] stats = new Object[M][N];
    for ( int i = 0; i < M; i++ )
    {
      for ( int j = 0; j < N; j++ )
      {
        stats[i][j] = processStats.get(i)[j];
      }
    }
    ModelViewer viewer = new ModelViewer(processes.get(0).getColumnHeaders(), stats );
    viewer.show();
   
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
