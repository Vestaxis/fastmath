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
      return new ExponentialPowerlawHawkesProcess();
    case ConstrainedApproximatePowerlaw:
      return new ConstrainedExponentialPowerlawHawkesProcess();
    case ExtendedExponentialPowerlawApproximation:
      return new ExtendedExponentialPowerlawHawkesProcess();
    default:
      throw new UnsupportedOperationException("TODO: " + type);
    }
  }
}
