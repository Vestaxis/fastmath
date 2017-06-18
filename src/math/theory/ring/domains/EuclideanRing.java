package math.theory.ring.domains;

import math.Field;
import math.Set;

/**
 * a Euclidean domain (also called a Euclidean ring) is a ring that can be
 * endowed with a certain structure – namely a Euclidean function, to be
 * described in detail below – which allows a suitable generalization of the
 * Euclidean division of the integers. This generalized Euclidean algorithm can
 * be put to many of the same uses as Euclid's original algorithm in the ring of
 * integers: in any Euclidean domain, one can apply the Euclidean algorithm to
 * compute the greatest common divisor of any two elements. In particular, the
 * greatest common divisor of any two elements exists and can be written as a
 * linear combination of them (Bézout identity). Also every ideal in a Euclidean
 * domain is principal, which implies a suitable generalization of the
 * Fundamental Theorem of Arithmetic: every Euclidean domain is a unique
 * factorization domain.
 * 
 * It is important to compare the class of Euclidean domains with the larger
 * class of principal ideal domains (PIDs). An arbitrary PID has much the same
 * "structural properties" of a Euclidean domain (or, indeed, even of the ring
 * of integers), but when an explicit algorithm for Euclidean division is known,
 * one may use Euclidean algorithm and extended Euclidean algorithm to compute
 * greatest common divisors and Bézout's identity. In particular, the existence
 * of efficient algorithms for Euclidean division of integers and of polynomials
 * in one variable over a field is of basic importance in computer algebra.
 * 
 * So, given an integral domain R, it is often very useful to know that R has a
 * Euclidean function: in particular, this implies that R is a PID. However, if
 * there is no "obvious" Euclidean function, then determining whether R is a PID
 * is generally a much easier problem than determining whether it is a Euclidean
 * domain.
 * 
 * Euclidean domains appear in the following chain of class inclusions:
 * 
 * Commutative rings ⊃ integral domains ⊃ integrally closed domains ⊃ unique
 * factorization domains ⊃ principal ideal domains ⊃ Euclidean domains ⊃ fields
 * 
 * @author crow
 *
 */
public interface EuclideanRing<X extends Set> extends Field<X>
{

}
