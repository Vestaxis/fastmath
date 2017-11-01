package stochastic.processes.hawkes;

import static java.lang.System.out;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fastmath.DoubleRowMatrix;
import fastmath.Pair;
import fastmath.Vector;
import stochastic.processes.point.MarkedPointProcess;
import util.DateUtils;

public class TradingStrategy
{
  public static void main( String args[] ) throws FileNotFoundException, IOException
  {
    MarkedPointProcess mpp = MarkedPointProcess.loadMppFile(args.length > 0 ? args[0] : "/data/2016-05-20/SPY-2016-05-20.mpp");
    Pair<DoubleRowMatrix, DoubleRowMatrix> buySellMatrix = mpp.getBuySellMatrix(TimeUnit.MILLISECONDS);
    DoubleRowMatrix trades = mpp.getTradeMatrix(TimeUnit.MILLISECONDS);
    Vector times = trades.col(0);
    double firstTime = DateUtils.convertTimeUnits( times.get(0), TimeUnit.MILLISECONDS, TimeUnit.HOURS );
    out.println( "time " + firstTime + " " + times.get(0));
   // System.exit(1);
//    for ( int i = 0; i < times; i++ )
//    {
//      
//    }
    ExtendedApproximatePowerlawHawkesProcess model = new ExtendedApproximatePowerlawHawkesProcess();
    model.T = times;
    model.κ = 0.17171745708245348;
    model.η = 6.2285292213796675;
    model.b = 1.7066834288042811;
    model.ε = 0.09553988705003424;
    model.τ0 = 0.0014248824597174227;
    out.println( "Using " + model );
    double modelCompMean = model.Λ().mean();
    double modelCompVar = model.Λ().variance();
    out.println( "comp mean and var=" + modelCompMean + " " + modelCompVar );
    
    // TODO: 1. use the same model to calculate the buy and sell intensities.
    // 2. do some smoothing of the intensities since the raw intensity is still pretty noisy.
    // 3. determine some threshold to trigger trades and see if its profitable
    
  }
}
