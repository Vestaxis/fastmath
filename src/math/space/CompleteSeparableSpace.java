package math.space;

import math.Set;
import math.Topology;

/**
 * 
 * @param <X>
 * @param <B>
 */
public interface CompleteSeparableSpace<X extends Set, B extends Topology<?>> extends SeparableSpace<X, B>, CompleteSpace
{

}
