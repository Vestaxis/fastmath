package fastmath.optim;

import java.util.function.Supplier;

import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;

public interface ObjectiveFunctionSupplier extends OptimizationData, Supplier<ObjectiveFunction>
{

}
