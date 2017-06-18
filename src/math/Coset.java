package math;

/**
 * For a subgroup H of a group G and an element x of G, define xH to be the set
 * {xh:h in H} and Hx to be the set {hx:h in H}. A subset of G of the form xH
 * for some x in G is said to be a left coset of H and a subset of the form Hx
 * is said to be a right coset of H.
 * 
 * For any subgroup H, we can define an equivalence relation ∼ by x∼y if x=yh
 * for some h in H. The equivalence classes of this equivalence relation are
 * exactly the left cosets of H, and an element x of G is in the equivalence
 * class xH. Thus the left cosets of H form a partition of G.
 * 
 * It is also true that any two left cosets of H have the same cardinal number,
 * and in particular, every coset of H has the same cardinal number as eH=H,
 * where e is the identity element. Thus, the cardinal number of any left coset
 * of H has cardinal number the order of H.
 * 
 * The same results are true of the right cosets of G and, in fact, one can
 * prove that the set of left cosets of H has the same cardinal number as the
 * set of right cosets of H.
 */
public interface Coset<Group, Subgr> extends Set
{

}
