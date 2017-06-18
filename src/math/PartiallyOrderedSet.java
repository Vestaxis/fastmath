package math;

/**
 * A (non-strict) partial order[2] is a binary relation "≤" over a set P which
 * is reflexive, antisymmetric, and transitive, i.e., which satisfies for all a,
 * b, and c in P:
 * 
 * a ≤ a (reflexivity); if a ≤ b and b ≤ a then a = b (antisymmetry); if a ≤ b
 * and b ≤ c then a ≤ c (transitivity).
 * 
 * In other words, a partial order is an antisymmetric preorder.
 * 
 * A set with a partial order is called a partially ordered set (also called a
 * poset). The term ordered set is sometimes also used for posets, as long as it
 * is clear from the context that no other kinds of orders are meant. In
 * particular, totally ordered sets can also be referred to as "ordered sets",
 * especially in areas where these structures are more common than posets.
 * 
 * For a, b, elements of a partially ordered set P, if a ≤ b or b ≤ a, then a
 * and b are comparable. Otherwise they are incomparable. In the figure on
 * top-right, e.g. {x} and {x,y,z} are comparable, while {x} and {y} are not. A
 * partial order under which every pair of elements is comparable is called a
 * total order or linear order; a totally ordered set is also called a chain
 * (e.g., the natural numbers with their standard order). A subset of a poset in
 * which no two distinct elements are comparable is called an antichain (e.g.
 * the set of singletons {{x}, {y}, {z}} in the top-right figure). An element a
 * is said to be covered by another element b, written a<:b, if a is strictly
 * less than b and no third element c fits between them; formally: if both a≤b
 * and a≠b are true, and a≤c≤b is false for each c with a≠c≠b. A more concise
 * definition will be given below using the strict order corresponding to "≤".
 * For example, {x} is covered by {x,z} in the top-right figure, but not by
 * {x,y,z}.
 * 
 * @author crow
 *
 */
public interface PartiallyOrderedSet<P extends Set> extends Set
{

}
