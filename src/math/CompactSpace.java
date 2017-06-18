package math;

import math.geometry.differential.TopologicalSpace;
import math.space.Space;

/**
 * A subset S of a topological space X is compact if for every open cover of S
 * there exists a finite subcover of S.
 * 
 * @author crow
 *
 */
public interface CompactSpace<X extends TopologicalSpace<?, ?>> extends Space
{

}
