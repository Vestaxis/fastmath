package math.metrics;

import math.Product;
import math.space.EuclideanSpace;

/**
 * 
 * @param <E>
 */
public class EuclideanDistanceMetric<E extends EuclideanSpace> implements Metric<E> {

	@Override
	public Double apply(Product<E, E> t) 
	{

		return t.getLeft().getDist(t.getRight());

	}

}
