package math.space;

/**
 * <b>Complete space</b>
 * 
 * A term of relevance for a metric space, a uniform space, a topological space,
 * a proximity space, the space of a topological group, a space with a symmetry,
 * and a pseudo-metric space; it is also possible to use this term in still
 * other cases. All definitions of completeness are based on a single general
 * idea, the concrete expression of which depends on the particular type of
 * space. The general feature in the various definitions of completeness lies in
 * the requirement of convergence of a sufficiently broad class of sequences,
 * directions or centred systems.
 * 
 * A metric space is called complete if each Cauchy sequence in it converges. In
 * the same sense one understands the completeness of a pseudo-metric space and
 * a space with a symmetry. A uniform space is called complete if for each
 * centred system of sets in it containing sets which are arbitrarily small in
 * relation to the coverings from the given uniform structure, the intersection
 * of the elements of this system is not empty. On a topological group there are
 * natural right- and left-uniform structures. If the space of the group in one
 * of these structures is complete, then it is complete also in the other, and
 * the topological group is then called Weyl complete. The term Raikov
 * completeness is used in relation to the two-sided uniform structure on a
 * group obtained by the union of the right- and left-uniform structures.
 * Completeness of a metric space and Raikov completeness can be interpreted as
 * absolute closure with respect to any representation of the given space as a
 * subspace of a space of the same type. In particular, a metric space is
 * complete if and only if it is closed in any metric space containing it. A
 * topological group is Raikov complete if and only if it is closed in any
 * topological group containing it as a topological subgroup. This is related to
 * the fundamental construction of completions: Each metric space is put into
 * correspondence with its completion in a canonical fashion, this being a
 * complete metric space containing the original space as an everywhere-dense
 * subspace. Similarly, each topological group is Raikov completeable, but not
 * every topological group is Weyl completeable.
 * 
 * For topological spaces, the requirement of absolute closure (i.e. closure in
 * any space containing it) leads to compact spaces if one restricts oneself to
 * the class of completely-regular Hausdorff spaces: Those spaces and only those
 * spaces have this property. However, there is another useful and natural
 * approach to defining completeness in a topological space. A
 * completely-regular Hausdorff space is called Čech complete if it can be
 * represented as the intersection of a countable family of open sets in a
 * certain Hausdorff compactification. All such spaces have the Baire property:
 * The intersection of a countable family of non-empty open everywhere-dense
 * sets is always non-empty. A metrizable space is Čech complete if and only if
 * it is metrizable by a complete metric (the Aleksandrov–Hausdorff theorem).
 * Čech completeness provides for correct behaviour of topological spaces in
 * many important respects. For example, a countable Čech-complete space has a
 * countable base and is metrizable. Paracompactness is retained in the product
 * operation when the spaces are Čech complete. Čech completeness is also
 * preserved by perfect mappings (cf. Perfect mapping), and in the class of
 * metrizable spaces it is preserved under transformation by open continuous
 * mappings.
 * 
 * Another useful approach to defining completeness in a completely-regular
 * Hausdorff space is related to considering the maximal uniform structure on
 * it: If such a uniform space is complete, the topological space is called
 * Dieudonné complete. Dieudonné completeness applies to precisely those spaces
 * that are homeomorphic to closed subspaces of a topological product of
 * metrizable spaces. In the presence of Dieudonné completeness, a single
 * property represents pseudo-compactness, countable compactness and
 * compactness. All paracompact spaces are Dieudonné complete, and this applies
 * in particular to all metric spaces. This shows that Dieudonné completeness
 * does not imply that the Baire property holds in the space. A special case of
 * Dieudonné completeness is Hewitt completeness of a topological space, which
 * means that the space is homeomorphic to a closed subspace of the topological
 * product of a certain family of real straight lines. References [1] A.V.
 * Arkhangel'skii, V.I. Ponomarev,
 * "Fundamentals of general topology: problems and exercises" , Reidel (1984)
 * (Translated from Russian)
 * 
 * @author crow
 *
 */
public interface CompleteSpace extends Space
{

}
