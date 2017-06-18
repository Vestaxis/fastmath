package math.theory.ring;

import math.Set;
import math.properties.Involutive;

/**
 * a *-ring(involutive ring) is a ring with a map * : A → A that is an
 * antiautomorphism and an involution.
 * 
 * More precisely, * is required to satisfy the following properties:
 * 
 * (x + y)* = x* + y* (x y)* = y* x* 1* = 1 (x*)* = x
 * 
 * for all x, y in A.
 * 
 * This is also called an involutive ring, involutory ring, and ring with
 * involution. Note that the third axiom is actually redundant, because the
 * second and fourth axioms imply 1* is also a multiplicative identity, and
 * identities are unique.
 * 
 * Elements such that x* = x are called self-adjoint.
 * 
 * Archetypical examples of a *-ring are fields of complex numbers and algebraic
 * numbers with complex conjugation as the involution. One can define a
 * sesquilinear form over any *-ring.
 * 
 * Also, one can define *-versions of algebraic objects, such as ideal and
 * subring, with the requirement to be *-invariant: x ∈ I ⇒ x* ∈ I and so on.
 * 
 * @author crow
 *
 */
@Involutive
public interface InvolutiveRing<X extends Set> extends CommutativeRing<X>
{

}
