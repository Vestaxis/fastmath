package stochastic.processes.selfexciting;

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

  public static MultivariateExponentialSelfExcitingProcess spawnNewProcess(Type type)
  {
    switch (type)
    {
//    case ExtendedApproximatePowerlaw:
//      return new MultivariateExponentialSelfExcitingProcess();
    default:
      throw new UnsupportedOperationException("TODO: " + type);
    }
  }
}