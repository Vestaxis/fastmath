package math.theory.ring;

import math.Set;
import math.Topology;
import math.geometry.differential.TopologicalSpace;

/**
 * Topological ring
 * 
 * 2010 Mathematics Subject Classification: Primary: 13Jxx [MSN][ZBL]
 * 
 * A topological ring is a ring R that is a topological space, and such that the
 * mappings (x,y)↦x−y and (x,y)↦xy are continuous. A topological ring R is
 * called separated if it is separated as a topological space (cf. Separation
 * axiom). In this case R is a Hausdorff space. Any subring M of a topological
 * ring R, and also the quotient ring R/J by an ideal J, is a topological ring.
 * If R is separated and the ideal J is closed, then R/J is a separated
 * topological ring. The closure M¯ of a subring M in R is also a topological
 * ring. A direct product of topological rings is a topological ring in a
 * natural way.
 * 
 * A homomorphism of topological rings is a ring homomorphism which is also a
 * continuous mapping. If f:R1→R2 is such a homomorphism, where f is moreover an
 * epimorphism and an open mapping, then R2 is isomorphic as a topological ring
 * to R1/Kerf. Banach algebras are an example of topological rings. An important
 * type of topological ring is defined by the property that it has a fundamental
 * system of neighbourhoods of zero consisting of some set of ideals. For
 * example, to any ideal 𝔪 in a commutative ring R one can associate the
 * 𝔪-adic topology, in which the sets 𝔪n for all natural numbers n form a
 * fundamental system of neighbourhoods of zero. This topology is separated if
 * the condition ⋂n𝔪n=0 is satisfied.
 * 
 * For a topological ring R one can define its completion R^, which is a
 * complete topological ring, and a separated topological ring R can be imbedded
 * as an everywhere-dense subset in R^, which is also separated in this case.
 * The additive group of the ring R^ coincides with the completion of the
 * additive group of R, as an commutative topological group. References [Bo] N.
 * Bourbaki, "Elements of mathematics. General topology", Addison-Wesley (1966)
 * (Translated from French) [Bo2] N. Bourbaki,
 * "Elements of mathematics. Commutative algebra", Addison-Wesley (1972)
 * (Translated from French) [Po] L.S. Pontryagin, "Topological groups",
 * Princeton Univ. Press (1958) (Translated from Russian) [Wa] B.L. van der
 * Waerden, "Algebra", 1–2, Springer (1967–1971) (Translated from German)
 * 
 * @author crow
 *
 */
public interface TopologicalRing<X extends Set, B extends Topology<? extends X>> extends Ring<X>, TopologicalSpace<X, B>
{

}
