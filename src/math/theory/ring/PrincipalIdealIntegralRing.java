package math.theory.ring;

import math.Set;
import math.theory.ring.domains.EuclideanRing;

/**
 * a principal ideal ring, or PID, is an integral ring in which every ideal is
 * principal, i.e., can be generated by a single element. More generally, a
 * principal ideal ring is a nonzero commutative ring whose ideals are
 * principal, although some authors (e.g., Bourbaki) refer to PIDs as principal
 * rings.
 * 
 * Principal ideal rings are thus mathematical objects which behave somewhat
 * like the integers, with respect to divisibility: any element of a PID has a
 * unique decomposition into prime elements (so an analog of the fundamental
 * theorem of arithmetic holds); any two elements of a PID have a greatest
 * common divisor (although it may not be possible to find it using the
 * Euclidean algorithm). If x and y are elements of a PID without common
 * divisors, then every element of the PID can be written in the form ax + by.
 * 
 * Principal ideal rings are noetherian, they are integrally closed, they are
 * unique factorization rings and Dedekind rings. All Euclidean rings and all
 * fields are principal ideal rings.
 * 
 * Commutative rings ⊃ integral rings ⊃ integrally closed rings ⊃ unique
 * factorization rings ⊃ principal ideal rings ⊃ Euclidean rings ⊃ fields
 * 
 * @author crow
 *
 */
public interface PrincipalIdealIntegralRing<X extends Set> extends EuclideanRing<X>
{

}
