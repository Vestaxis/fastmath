package math.geometry.differential;

import math.Set;
import math.Topology;
import math.space.Space;

/**
 * 
 * In topology and related branches of mathematics, a topological space is a set
 * of points, along with a set of neighbourhoods for each point, that satisfy a
 * set of axioms relating points and neighbourhoods. The definition of a
 * topological space relies only upon set theory and is the most general notion
 * of a mathematical space that allows for the definition of concepts such as
 * continuity, connectedness, and convergence.[1] Other spaces, such as
 * manifolds and metric spaces, are specializations of topological spaces with
 * extra structures or constraints. Being so general, topological spaces are a
 * central unifying notion and appear in virtually every branch of modern
 * mathematics. The branch of mathematics that studies topological spaces in
 * their own right is called point-set topology or general topology.
 * 
 * @param <X>
 *          the points
 * @param the
 *          topology of the points
 * 
 * @author crow
 *
 */
public interface TopologicalSpace<X extends Set, B extends Topology<?>> extends Space
{

}
