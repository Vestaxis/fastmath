package math.theory.ring;

import math.Set;

/**
 * A Dedekind ring is a commutative Noetherian ring which is also an <b>integral
 * domain</b> in which the following hold.
 * 
 * 1. It is the set of algebraic integers in its field of fractions.
 * 
 * 2. Every nonzero prime ideal is also a maximal ideal. Of course, in any ring,
 * maximal ideals are always prime.
 * 
 * The main example of a Dedekind domain is the ring of algebraic integers in a
 * number field, an extension field of the rational numbers. An important
 * consequence of the above axioms is that every ideal can be written uniquely
 * as a product of prime ideals. This compensates for the possible failure of
 * unique factorization of elements into irreducibles.
 * 
 * @author crow
 *
 */
public interface DedekindRing<X extends Set> extends NoetherianRing<X>, CommutativeRing<X>
{

}
