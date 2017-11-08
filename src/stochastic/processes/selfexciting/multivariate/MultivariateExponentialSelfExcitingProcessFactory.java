package stochastic.processes.selfexciting.multivariate;

import stochastic.processes.pointprocesses.finance.TradingFiltration;

public class MultivariateExponentialSelfExcitingProcessFactory
{
  public static enum Type
  {
    ExtendedApproximatePowerlaw;

    public String getFilenameExtension()
    {
      switch (this)
      {
      case ExtendedApproximatePowerlaw:
        return "meapl";
      default:
        throw new UnsupportedOperationException("TODO: " + this);
      }
    }
  };

  /**
   * 
   * @param type
   *          type of process to spawn
   * @param tradingProcess
   * @return
   */
  public static MultivariateExponentialSelfExcitingProcess spawnNewProcess(Type type, TradingFiltration tradingProcess)
  {
    assert tradingProcess.times != null : "tradingProcess.times is null";
    assert tradingProcess.types != null : "tradingProcess.types is null";
    assert tradingProcess.markedPoints != null : "tradingProcess.markedPoints is null";

    if (type == Type.ExtendedApproximatePowerlaw)
    {
      MultivariateExtendedApproximatePowerlawSelfExcitingProcess process = new MultivariateExtendedApproximatePowerlawSelfExcitingProcess(2);
      process.T = tradingProcess.times;
      process.K = tradingProcess.types;
      process.X = tradingProcess.markedPoints;
      return process;
    }
    else
    {
      throw new UnsupportedOperationException("TODO: " + type);
    }
  }
}