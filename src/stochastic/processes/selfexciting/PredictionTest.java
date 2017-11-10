package stochastic.processes.selfexciting;

import java.io.IOException;

public class PredictionTest
{

  public static void main(String[] args) throws IOException, InterruptedException
  {
    ApproximatePowerlawSelfExcitingProcess process = new ApproximatePowerlawSelfExcitingProcess();
    process.ε = 0.32;
    process.τ = 3.42;
    process.T = SelfExcitingProcessEstimator.loadTimes("SPY.mat", "SPY").slice(0, 10).copy();
    process.T = process.T.subtract( process.T.fmin() );
    process.predict();
    Thread.sleep(10000000);
    // TODO Auto-generated method stub

  }

}
