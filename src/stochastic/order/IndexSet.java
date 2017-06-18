package stochastic.order;

import math.Set;
import math.Topology;
import stochastic.SigmaField;
import stochastic.measures.MeasurableSpace;

/**
 * 
 * @author crow
 *
 * @param <X>
 *          the set over which <b>this</b> index (which usually refers to time
 *          time reference) ranges
 */
public interface IndexSet<X extends Set> extends TotallyOrderedSet, Topology<X>, MeasurableSpace<X, SigmaField<X>>
{

}
