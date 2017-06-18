package math.theory.ring;

import math.Set;
import math.theory.group.AdditiveGroup;

/**
 * a ring is an algebraic structure with operations generalizing the arithmetic
 * operations of addition and multiplication. By means of this generalization,
 * theorems from arithmetic are extended to non-numerical objects like
 * polynomials, series, matrices and functions.
 * 
 * Rings were first formalized as a common generalization of Dedekind domains
 * that occur in number theory, and of polynomial rings and rings of invariants
 * that occur in algebraic geometry and invariant theory. They are also used in
 * other branches of mathematics such as geometry and mathematical analysis. The
 * formal definition of rings is relatively recent, dating from the 1920s.
 * 
 * Briefly, a ring is an commutative group with a second binary operation that
 * is distributive over the commutative group operation and is associative. The
 * commutative group operation is called "addition" and the second binary
 * operation is called "multiplication" in analogy with the integers. One
 * familiar example of a ring is the set of integers. The integers are a
 * commutative ring, since a times b is equal to b times a. The set of
 * polynomials also forms a commutative ring. An example of a non-commutative
 * ring is the ring of square matrices of the same size. Finally, a field is a
 * commutative ring in which one can divide by any nonzero element: an example
 * is the field of real numbers.
 * 
 * Whether a ring is commutative or not has profound implication in the study of
 * rings as abstract objects, the field called the ring theory. The development
 * of the commutative theory, commonly known as commutative algebra, has been
 * greatly influenced by problems and ideas occurring naturally in algebraic
 * number theory and algebraic geometry: important commutative rings include
 * fields, polynomial rings, the coordinate ring of an affine algebraic variety,
 * and the ring of integers of a number field. On the other hand, the
 * noncommutative theory takes examples from representation theory (group
 * rings), functional analysis (operator algebras) and the theory of
 * differential operators (rings of differential operators), and the topology
 * (cohomology ring of a topological space.)
 * 
 * @author crow
 *
 */
public interface Ring<X extends Set> extends AdditiveGroup<X>, SemiRing
{

}
