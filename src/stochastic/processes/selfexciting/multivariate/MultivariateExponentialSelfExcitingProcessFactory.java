package stochastic.processes.selfexciting.multivariate;

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
   * @param dim
   *          dimension
   * @return
   */
  public static MultivariateExponentialSelfExcitingProcess spawnNewProcess(Type type, int dim)
  {
    if (type == Type.ExtendedApproximatePowerlaw)
    {
      return new MultivariateExtendedApproximatePowerlawSelfExcitingProcess(dim);
    }
    else
    {
      throw new UnsupportedOperationException("TODO: " + type);
    }
  }
}