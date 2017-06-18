package math;

import math.properties.Commutative;
import math.space.Space;
import math.theory.ring.CommutativeRing;

/**
 * A field is any set of elements that satisfies the field axioms for both
 * addition and multiplication and is a commutative division algebra. An archaic
 * name for a field is rational domain. The French term for a field is corps and
 * the German word is Körper, both meaning "body." A field with a finite number
 * of members is known as a finite field or Galois field.
 * 
 * Because the identity condition is generally required to be different for
 * addition and multiplication, every field must have at least two elements.
 * Examples include the complex numbers (C), rational numbers (Q), and real
 * numbers (R), but not the integers (Z), which form only a ring.
 * 
 * It has been proven by Hilbert and Weierstrass that all generalizations of the
 * field concept to triplets of elements are equivalent to the field of complex
 * numbers.
 * 
 * a field is a nonzero commutative ring that contains a multiplicative inverse
 * for every nonzero element, or equivalently a ring whose nonzero elements form
 * an abelian group under multiplication. As such it is an algebraic structure
 * with notions of addition, subtraction, multiplication, and division
 * satisfying the appropriate abelian group equations and distributive law. The
 * most commonly used fields are the field of real numbers, the field of complex
 * numbers, and the field of rational numbers, but there are also finite fields,
 * fields of functions, algebraic number fields, p-adic fields, and so forth.
 * 
 * Any field may be used as the scalars for a vector space, which is the
 * standard general context for linear algebra. The theory of field extensions
 * (including Galois theory) involves the roots of polynomials with coefficients
 * in a field; among other results, this theory leads to impossibility proofs
 * for the classical problems of angle trisection and squaring the circle with a
 * compass and straightedge, as well as a proof of the Abel–Ruffini theorem on
 * the algebraic insolubility of quintic equations. In modern mathematics, the
 * theory of fields (or field theory) plays an essential role in number theory
 * and algebraic geometry.
 * 
 * As an algebraic structure, every field is a ring, but not every ring is a
 * field. The most important difference is that fields allow for division
 * (though not division by zero), while a ring need not possess multiplicative
 * inverses; for example the integers form a ring, but 2x = 1 has no solution in
 * integers. Also, the multiplication operation in a field is required to be
 * commutative. A ring in which division is possible but commutativity is not
 * assumed (such as the quaternions) is called a division ring or skew field.
 * (Historically, division rings were sometimes referred to as fields, while
 * fields were called commutative fields.)
 * 
 * As a ring, a field may be classified as a specific type of integral domain,
 * and can be characterized by the following (not exhaustive) chain of class
 * inclusions:
 * 
 * Commutative rings ⊃ integral domains ⊃ integrally closed domains ⊃ unique
 * factorization domains ⊃ principal ideal domains ⊃ Euclidean domains ⊃ fields
 * ⊃ finite fields.
 * 
 * @author crow
 *
 */
@Commutative
public interface Field<X extends Set> extends CommutativeRing<X>, Space
{

}
