package fastmath.optim;

import java.util.function.Supplier;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;

import stochastic.processes.hawkes.ExponentialHawkesProcess;

public class ObjectiveFunctionSupplier<E extends ExponentialHawkesProcess> extends ObjectiveFunction implements
    OptimizationData
{
 

  @Override
  public MultivariateFunction getObjectiveFunction()
  {
    try
    {
      MultivariateFunction func = (MultivariateFunction) process.clone();
      return func;
    }
    catch (CloneNotSupportedException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  E process;

  public ObjectiveFunctionSupplier(E f)
  {
    super(null);
    process = f;
  }

}
