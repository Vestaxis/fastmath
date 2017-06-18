package math;

/**
 * Equivalence Class
 * 
 * An equivalence class is defined as a subset of the form {x in X:xRa}, where a
 * is an element of X and the notation "xRy" is used to mean that there is an
 * equivalence relation between x and y. It can be shown that any two
 * equivalence classes are either equal or disjoint, hence the collection of
 * equivalence classes forms a partition of X. For all a,b in X, we have aRb iff
 * a and b belong to the same equivalence class.
 * 
 * A set of class representatives is a subset of X which contains exactly one
 * element from each equivalence class.
 * 
 * For n a positive integer, and a,b integers, consider the congruence a=b (mod
 * n), then the equivalence classes are the sets {...,-2n,-n,0,n,2n,...},
 * {...,1-2n,1-n,1,1+n,1+2n,...} etc. The standard class representatives are
 * taken to be 0, 1, 2, ..., n-1. SEE ALSO: Congruence, Coset, Equivalence
 * Relation REFERENCES:
 * 
 * Shanks, D. Solved and Unsolved Problems in Number Theory, 4th ed. New York:
 * Chelsea, pp. 56-57, 1993.
 * 
 * @param X
 *          the set to which this set is "equivalent"
 * 
 * @author crow
 *
 */
public interface EquivalenceClass<X extends Set> extends Set
{

}
