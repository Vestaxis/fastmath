package math.functions;

import math.Product;
import math.Set;

/**
 * Binary Operator
 * 
 * An operator defined on a set S which takes two elements from S as inputs and
 * returns a single element of S. Binary operators are called compositions by
 * Rosenfeld (1968). Sets possessing a binary multiplication operation include
 * the group, groupoid, monoid, quasigroup, and semigroup. Sets possessing both
 * a binary multiplication and a binary addition operation include the division
 * algebra, field, ring, ringoid, semiring, and unit ring. SEE ALSO: AND, Binary
 * Operation, Boolean Algebra, Connective, Division Algebra, Field, Group,
 * Groupoid, Monoid, NOT, Operator, OR, Quasigroup, Ring, Ringoid, Semigroup,
 * Semiring, Set Closure, Unit Ring, XNOR, XOR REFERENCES:
 * 
 * Rosenfeld, A. An Introduction to Algebraic Structures. New York: Holden-Day,
 * 1968.
 * 
 * @param <A>
 */
public interface BinaryOperator<A extends Set> extends Function<Product<A, A>, A>
{
  /**
   * 
   * @param left
   *          an element of A
   * @param right
   *          an element of A
   * @return an element of A
   */
  public A compose( A left, A right );
}
