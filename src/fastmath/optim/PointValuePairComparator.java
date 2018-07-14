package fastmath.optim;

import java.util.Comparator;

import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;

public interface PointValuePairComparator extends Comparator<PointValuePair>, OptimizationData
{

}