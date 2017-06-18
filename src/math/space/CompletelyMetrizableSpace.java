package math.space;

import math.Set;
import math.Topology;
import math.geometry.differential.TopologicalSpace;

/**
 * a completely metrizable space[1] (metrically topologically complete space[2])
 * is a topological space (X, T) for which there exists at least one metric d on
 * X such that (X, d) is a complete metric space and d induces the topology T.
 * The term topologically complete space is employed by some authors as a
 * synonym for completely metrizable space,[3] but sometimes also used for other
 * classes of topological spaces, like completely uniformizable spaces
 * 
 * @author crow
 *
 */
public interface CompletelyMetrizableSpace<X extends Set, B extends Topology<? extends X>> extends TopologicalSpace<X, B>
{

}
