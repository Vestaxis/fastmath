package stochastic.processes.hawkes;

import java.io.IOException;

public class PredictionTest
{

  public static void main(String[] args) throws IOException, InterruptedException
  {
    ApproximatePowerlawHawkesProcess process = new ApproximatePowerlawHawkesProcess();
    process.ε = 0.32;
    process.τ0 = 3.42;
    process.T = HawkesProcessEstimator.loadData("SPY.mat", "SPY").slice(0, 10).copy();
    process.T = process.T - process.T.fmin();
    process.predict();
    Thread.sleep(10000000);
    // TODO Auto-generated method stub

  }

}
