package math.theory.ring;

import math.Set;

/**
 * an integral domain is a nonzero commutative ring in which the product of any
 * two nonzero elements is nonzero.[1][2] Integral domains are generalizations
 * of the ring of integers and provide a natural setting for studying
 * divisibility.
 * 
 * 
 * Commutative rings ⊃ integral domains ⊃ integrally closed domains ⊃ unique
 * factorization domains ⊃ principal ideal domains ⊃ Euclidean domains ⊃ fields
 * 
 * The absence of nonzero zero divisors implies that in an integral domain the
 * cancellation property holds for multiplication by any nonzero element a: an
 * equality ab = ac implies b = c.
 * 
 * @author crow
 *
 */
public interface IntegralRing<X extends Set> extends CommutativeRing<X>
{

}
