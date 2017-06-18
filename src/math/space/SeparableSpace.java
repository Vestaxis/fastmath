package math.space;

import math.Set;
import math.Topology;
import math.geometry.differential.TopologicalSpace;
import math.properties.measure.CountablyDetermined;

/**
 * separable, adj. <br>
 * 1. (of a topological space ) containing a countable dense subset. Every
 * compact metric space or second-countable space is separable, as is Euclidean
 * space since it contains the rational n-tuples, which are countable and dense.
 * <br>
 * 
 * @author crow
 *
 */
@CountablyDetermined
public interface SeparableSpace<X extends Set, B extends Topology<?>> extends TopologicalSpace<X, B>
{

}
