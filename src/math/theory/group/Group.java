package math.theory.group;

import math.Set;
import math.algebra.geometry.Variety;
import math.properties.Distributive;

/**
 * A group G is a finite or infinite set of elements together with a binary
 * operation (called the group operation) that together satisfy the four
 * fundamental properties of closure, associativity, the identity property, and
 * the inverse property. The operation with respect to which a group is defined
 * is often called the "group operation," and a set is said to be a group
 * "under" this operation. Elements A, B, C, ... with binary operation between A
 * and B denoted AB form a group if
 * 
 * 1. Closure: If A and B are two elements in G, then the product AB is also in
 * G.
 * 
 * 2. Associativity: The defined multiplication is associative, i.e., for all
 * A,B,C in G, (AB)C=A(BC).
 * 
 * 3. Identity: There is an identity element I (a.k.a. 1, E, or e) such that
 * IA=AI=A for every element A in G.
 * 
 * 4. Inverse: There must be an inverse (a.k.a. reciprocal) of each element.
 * Therefore, for each element A of G, the set contains an element B=A^(-1) such
 * that AA^(-1)=A^(-1)A=I.
 * 
 * A group is a monoid each of whose elements is invertible.
 * 
 * A group must contain at least one element, with the unique (up to
 * isomorphism) single-element group known as the trivial group.
 * 
 * The study of groups is known as group theory. If there are a finite number of
 * elements, the group is called a finite group and the number of elements is
 * called the group order of the group. A subset of a group that is closed under
 * the group operation and the inverse operation is called a subgroup. Subgroups
 * are also groups, and many commonly encountered groups are in fact special
 * subgroups of some more general larger group.
 * 
 * @author crow
 *
 */
@Distributive
public interface Group<X extends Set> extends Variety
{

}
