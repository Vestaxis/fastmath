package math.properties;

/**
 * For any metric space M, one can construct a complete metric space M′ (which
 * is also denoted as M), which contains M as a dense subspace. It has the
 * following universal property: if N is any complete metric space and f is any
 * uniformly continuous function from M to N, then there exists a unique
 * uniformly continuous function f′ from M′ to N, which extends f. The space M'
 * is determined up to isometry by this property, and is called the completion
 * of M.
 * 
 * The completion of M can be constructed as a set of equivalence classes of
 * Cauchy sequences in M. For any two Cauchy sequences (xn)n and (yn)n in M, we
 * may define their distance as
 * 
 * d(x, y) = \lim_n d\left(x_n, y_n\right)
 * 
 * (This limit exists because the real numbers are complete.) This is only a
 * pseudometric, not yet a metric, since two different Cauchy sequences may have
 * the distance 0. But "having distance 0" is an equivalence relation on the set
 * of all Cauchy sequences, and the set of equivalence classes is a metric
 * space, the completion of M. The original space is embedded in this space via
 * the identification of an element x of M with the equivalence class of
 * sequences converging to x (i.e., the equivalence class containing the
 * sequence with constant value x). This defines an isometry onto a dense
 * subspace, as required. Notice, however, that this construction makes explicit
 * use of the completeness of the real numbers, so completion of the rational
 * numbers needs a slightly different treatment.
 * 
 * Cantor's construction of the real numbers is similar to the above
 * construction; the real numbers are the completion of the rational numbers
 * using the ordinary absolute value to measure distances. The additional
 * subtlety to contend with is that it is not logically permissible to use the
 * completeness of the real numbers in their own construction. Nevertheless,
 * equivalence classes of Cauchy sequences are defined as above, and the set of
 * equivalence classes is easily shown to be a field that has the rational
 * numbers as a subfield. This field is complete, admits a natural total
 * ordering, and is the unique totally ordered complete field (up to
 * isomorphism). It is defined as the field of real numbers (see also
 * Construction of the real numbers for more details). One way to visualize this
 * identification with the real numbers as usually viewed is that the
 * equivalence class consisting of those Cauchy sequences of rational numbers
 * that "ought" to have a given real limit is identified with that real number.
 * The truncations of the decimal expansion give just one choice of Cauchy
 * sequence in the relevant equivalence class.
 * 
 * For a prime p, the p-adic numbers arise by completing the rational numbers
 * with respect to a different metric.
 * 
 * If the earlier completion procedure is applied to a normed vector space, the
 * result is a Banach space containing the original space as a dense subspace,
 * and if it is applied to an inner product space, the result is a Hilbert space
 * containing the original space as a dense subspace.
 * 
 * @author crow
 *
 */
public @interface Complete
{

}
