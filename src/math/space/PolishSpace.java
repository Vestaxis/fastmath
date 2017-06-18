package math.space;

import math.Set;
import math.Topology;
import math.geometry.differential.TopologicalSpace;

/**
 * 
 * @author crow
 *
 */
public interface PolishSpace<X extends Set, B extends Topology<? extends X>>
                            extends
                            CompletelyMetrizableSpace<X, B>,
                            SeparableSpace<X, B>,
                            TopologicalSpace<X, B>,
                            CompleteSeparableMetricSpace<X, B>
{

}
