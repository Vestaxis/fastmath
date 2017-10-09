package stochastic.processes.hawkes;

public class ExponentialHawkesProcessFactory
{
  public static enum Type
  {
    Standard, ApproximatePowerlaw, ConstrainedApproximatePowerlaw, ExtendedExponentialPowerlawApproximation
  };

  public static ExponentialHawkesProcess spawnNewProcess(Type type)
  {
    switch (type)
    {
    case Standard:
      return new StandardExponentialHawkesProcess();
    case ApproximatePowerlaw:
      return new ApproximatePowerlawHawkesProcess();
    case ConstrainedApproximatePowerlaw:
      return new ConstrainedApproximatePowerlawHawkesProcess();
    case ExtendedExponentialPowerlawApproximation:
      return new ExtendedApproximatePowerlawHawkesProcess();
    default:
      throw new UnsupportedOperationException("TODO: " + type);
    }
  }
}
