package stochastic.processes.selfexciting;

public class ExponentialSelfExcitingProcessFactory
{
  public static enum Type
  {
    Standard, ApproximatePowerlaw, ExtendedApproximatePowerlaw, ConstrainedApproximatePowerlaw, ExtendedConstrainedExponentialPowerlawApproximation;

    public String getFilenameExtension()
    {
      switch (this)
      {
      case Standard:
        return "stdexp";
      case ApproximatePowerlaw:
        return "apl";
      case ExtendedApproximatePowerlaw:
        return "eapl";
      case ConstrainedApproximatePowerlaw:
        return "capl";
      case ExtendedConstrainedExponentialPowerlawApproximation:
        return "ecpl";
      default:
        throw new UnsupportedOperationException("TODO: " + this);
      }
    }
  };

  public static ExponentialSelfExcitingProcess spawnNewProcess(Type type)
  {
    switch (type)
    {
    case Standard:
      return new StandardExponentialSelfExcitingProcess();
    case ApproximatePowerlaw:
      return new ApproximatePowerlawSelfExcitingProcess();
    case ExtendedApproximatePowerlaw:
      return new ExtendedApproximatePowerlawSelfExcitingProcess();
    case ConstrainedApproximatePowerlaw:
      return new ConstrainedApproximatePowerlawSelfExcitingProcess();
    case ExtendedConstrainedExponentialPowerlawApproximation:
      return new ExtendedConstrainedExponentialPowerlawApproximatioSelfExcitingProcess();
    default:
      throw new UnsupportedOperationException("TODO: " + type);
    }
  }
}
