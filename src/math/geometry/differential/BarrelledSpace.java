package math.geometry.differential;

import math.Field;
import math.space.LocallyConvexSpace;

/**
 * Barrelled space
 * 
 * A locally convex linear topological space displaying several properties of
 * Banach spaces and Fréchet spaces without the metrizability condition (cf.
 * Banach space; Fréchet space). It is one of the most extensive class of spaces
 * to which the Banach–Steinhaus theorem applies. Barrelled spaces were first
 * introduced by N. Bourbaki.
 * 
 * A set in a vector space is said to be a balanced set if for all and for all
 * such that . A balanced set is said to be an absorbing set if it absorbs each
 * point of , i.e. if for each there exists an such that .
 * 
 * A barrel in a linear topological space is a closed, balanced, absorbing,
 * convex set. A barrelled space is a linear topological space with a locally
 * convex topology in which every barrel is a neighborhood of zero. Fréchet
 * spaces and, in particular, Banach spaces are examples of barrelled spaces.
 * Montel spaces (cf. Montel space) are an important class of barrelled spaces,
 * and display remarkable properties. A quotient space of a barrelled space, a
 * direct sum and inductive limits of barrelled spaces are barrelled spaces.
 * Every pointwise-bounded set of continuous linear mappings of a barrelled
 * space into a locally convex linear topological space is equicontinuous. In a
 * space dual to a barrelled space, a bounded set in the weak topology is
 * bounded in the strong topology and relatively compact in the weak topology.
 * 
 *
 * References [1] N. Bourbaki,
 * "Elements of mathematics. Topological vector spaces" , Addison-Wesley (1977)
 * (Translated from French) [2] R.E. Edwards,
 * "Functional analysis: theory and applications" , Holt, Rinehart & Winston
 * (1965)
 *
 * Comments
 * 
 * Barrelled spaces are the most extensive class of locally convex spaces to
 * which the Banach–Steinhaus theorem can be extended. They were first
 * introduced in [a4].
 * 
 * A not necessarily balanced set in is called absorbing if for every there is
 * an such that for all . For the dual of a barrelled space the following four
 * statements are equivalent: 1) is weakly bounded; 2) is strongly bounded; 3)
 * is equi-continuous; and 4) is weakly compact. The last statement follows from
 * the stronger statement that the dual of a barrelled space is quasi-complete
 * for any -topology. (For the last notion see Topological vector space; Space
 * of mappings, topological.)
 * 
 * References [a1] H.H. Schaefer, "Topological vector spaces" , Macmillan (1966)
 * [a2] J.L. Kelley, I. Namioka, "Linear topological spaces" , Springer (1963)
 * [a3] G. Köthe, "Topological vector spaces" , 1 , Springer (1969) [a4] N.
 * Bourbaki, "Sur certains espaces vectoriels topologiques" Ann. Inst. Fourier ,
 * 2 (1950) pp. 5–16
 * 
 * @author crow
 *
 * @param <F>
 */
public interface BarrelledSpace<F extends Field<?>> extends LocallyConvexSpace, TopologicalVectorSpace<F>
{

}
