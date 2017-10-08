package stochastic.processes.hawkes;

public class ExponentialHawkesProcessFactory
{
  public static enum Type
  {
    Standard, ExponentialPowerlawApproximation, ConstraintedExponentialPowerlawApproximation, ExtendedExponentialPowerlawApproximation
  };

  public static ExponentialHawkesProcess spawnNewProcess(Type type)
  {
    switch (type)
    {
    case Standard:
      return new StandardExponentialHawkesProcess();
    case ExponentialPowerlawApproximation:
      return new ExponentialPowerlawHawkesProcess();
    case ConstraintedExponentialPowerlawApproximation:
      return new ConstrainedExponentialPowerlawHawkesProcess();
    case ExtendedExponentialPowerlawApproximation:
      return new ExtendedExponentialPowerlawHawkesProcess();
    default:
      throw new UnsupportedOperationException("TODO: " + type);
    }
  }
}
