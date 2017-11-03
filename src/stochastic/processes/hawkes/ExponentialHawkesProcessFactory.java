package stochastic.processes.hawkes;

public class ExponentialHawkesProcessFactory
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
        return "ecepl";
      default:
        throw new UnsupportedOperationException("TODO: " + this);
      }
    }
  };

  public static ExponentialHawkesProcess spawnNewProcess(Type type)
  {
    switch (type)
    {
    case Standard:
      return new StandardExponentialHawkesProcess();
    case ApproximatePowerlaw:
      return new ApproximatePowerlawHawkesProcess();
    case ExtendedApproximatePowerlaw:
      return new ExtendedApproximatePowerlawHawkesProcess();
    case ConstrainedApproximatePowerlaw:
      return new ConstrainedApproximatePowerlawHawkesProcess();
    case ExtendedConstrainedExponentialPowerlawApproximation:
      return new ExtendedConstrainedExponentialPowerlawApproximationHawkesProcess();
    default:
      throw new UnsupportedOperationException("TODO: " + type);
    }
  }
}
