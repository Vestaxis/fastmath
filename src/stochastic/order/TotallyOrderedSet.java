package stochastic.order;

/**
 * a linear order, total order, simple order, or (non-strict) ordering is a
 * binary relation (here denoted by infix ≤) on some set X which is transitive,
 * antisymmetric, and total. A set paired with a total order is called a totally
 * ordered set, a linearly ordered set, a simply ordered set, or a chain.
 * 
 * If X is totally ordered under ≤, then the following statements hold for all
 * a, b and c in X:
 * 
 * If a ≤ b and b ≤ a then a = b (antisymmetry); If a ≤ b and b ≤ c then a ≤ c
 * (transitivity); a ≤ b or b ≤ a (totality).
 * 
 * Antisymmetry eliminates uncertain cases when both a precedes b and b precedes
 * a.[1] A relation having the property of "totality" means that any pair of
 * elements in the set of the relation are comparable under the relation. This
 * also means that the set can be diagrammed as a line of elements, giving it
 * the name linear.[2] Totality also implies reflexivity, i.e., a ≤ a.
 * Therefore, a total order is also a partial order. The partial order has a
 * weaker form of the third condition (it only requires reflexivity, not
 * totality). An extension of a given partial order to a total order is called a
 * linear extension of that partial order.
 * 
 * @author crow
 *
 */
public interface TotallyOrderedSet extends OrderedSet
{

}
