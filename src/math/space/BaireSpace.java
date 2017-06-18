package math.space;

import math.Set;
import math.Topology;

/**
 * the Baire space is the set of all infinite sequences of natural numbers with
 * a certain topology. This space is commonly used in descriptive set theory, to
 * the extent that its elements are often called “reals.” It is often denoted B,
 * NN, ωω, or ωω. Moschovakis denotes it \mathcal{N}.
 * 
 * The Baire space is defined to be the Cartesian product of countably
 * infinitely many copies of the set of natural numbers, and is given the
 * product topology (where each copy of the set of natural numbers is given the
 * discrete topology). The Baire space is often represented using the tree of
 * finite sequences of natural numbers.
 * 
 * The Baire space can be contrasted with Cantor space, the set of infinite
 * sequences of binary digits.
 * 
 * @author crow
 *
 */
public interface BaireSpace<X extends Set, B extends Topology<X>> extends CompleteSeparableMetricSpace<X, B>
{

}
