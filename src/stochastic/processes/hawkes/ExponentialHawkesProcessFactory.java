package stochastic.processes.hawkes;

public class ExponentialHawkesProcessFactory
{
  public static enum Type
  {
    Standard, ApproximatePowerlaw, ExtendedApproximatePowerlaw, ConstrainedApproximatePowerlaw, ExtendedConstrainedExponentialPowerlawApproximation
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
