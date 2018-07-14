package fastmath.optim;

import java.util.function.Function;

import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;

public interface SolutionValidator extends Function<PointValuePair, Boolean>, OptimizationData
{

}
