package math.space;

import math.Set;
import math.Topology;

/**
 * TODO: clarify and expand as n-tuple
 * 
 * a product space is the Cartesian product of a family of topological spaces
 * equipped with a natural topology called the product topology. This topology
 * differs from another, perhaps more obvious, topology called the box topology,
 * which can also be given to a product space and which agrees with the product
 * topology when the product is over only finitely many spaces. However, the
 * product topology is "correct" in that it makes the product space a
 * categorical product of its factors, whereas the box topology is too fine;
 * this is the sense in which the product topology is "natural".
 * 
 * @author crow
 *
 */
public interface ProductSpace extends Space, Topology<Set>
{

}
