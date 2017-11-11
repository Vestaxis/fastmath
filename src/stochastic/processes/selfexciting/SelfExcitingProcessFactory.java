package stochastic.processes.selfexciting;

import stochastic.processes.selfexciting.multivariate.MultivariateExtendedApproximatePowerlawSelfExcitingProcess;

public class SelfExcitingProcessFactory
{
  public static enum Type
  {
    ApproximatePowerlaw, ExtendedApproximatePowerlaw, ConstrainedApproximatePowerlaw, ExtendedConstrainedExponentialPowerlawApproximation, MultivariateExtendedApproximatePowerlaw;

    public String
           getFilenameExtension()
    {
      switch (this)
      {
      case ApproximatePowerlaw:
        return "apl";
      case ExtendedApproximatePowerlaw:
        return "eapl";
      case ConstrainedApproximatePowerlaw:
        return "capl";
      case ExtendedConstrainedExponentialPowerlawApproximation:
        return "ecpl";
      case MultivariateExtendedApproximatePowerlaw:
        return "meapl";
      default:
        throw new UnsupportedOperationException("TODO: " + this);
      }
    }

    public AbstractSelfExcitingProcess
           instantiate(int dim)
    {
      return spawnNewProcess(this, dim);
    }
  };

  public static AbstractSelfExcitingProcess
         spawnNewProcess(Type type,
                         int dim)
  {
    switch (dim)
    {
    case 1:
      switch (type)
      {
      case ApproximatePowerlaw:
        return new ApproximatePowerlawSelfExcitingProcess();
      case ExtendedApproximatePowerlaw:
        return new ExtendedApproximatePowerlawSelfExcitingProcess();
      case ConstrainedApproximatePowerlaw:
        return new ConstrainedApproximatePowerlawSelfExcitingProcess();
      case ExtendedConstrainedExponentialPowerlawApproximation:
        return new ExtendedConstrainedExponentialPowerlawApproximationSelfExcitingProcess();
      default:
        throw new UnsupportedOperationException("TODO: " + type);
      }
    default:
      switch (type)
      {
      case MultivariateExtendedApproximatePowerlaw:
        return new MultivariateExtendedApproximatePowerlawSelfExcitingProcess(dim);
      default:
        throw new UnsupportedOperationException("TODO: " + type);
      }
    }
  }
}
