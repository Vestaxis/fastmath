package math.space;

import math.Set;
import math.Topology;

/**
 * A complete metric space is a Baire space,
 * BaireSpace</a>
 * 
 * @author crow
 *
 * @param <X>
 *          a set
 * @param <B>
 *          a topology on X
 */
public interface CompleteMetricSpace<X extends Set, B extends Topology<X>> extends MetricSpace<X>, CompleteSpace, BaireSpace<X, B>
{

}
