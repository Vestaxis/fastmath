package fastmath.optim;

import java.util.function.Supplier;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;

public abstract class ObjectiveFunctionSupplier extends ObjectiveFunction implements Supplier<ObjectiveFunction>, OptimizationData
{

  @Override
  public ObjectiveFunction get()
  {
    Object ass = super.getObjectiveFunction().clone();
    
    // TODO Auto-generated method stub
    return null;
  }

  public ObjectiveFunctionSupplier(MultivariateFunction f)
  {
    super(f);
  }


}
