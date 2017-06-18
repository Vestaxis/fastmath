package math.algebra.geometry;

/**
 * a projective variety over an algebraically closed field k is a subset of some
 * projective n-space Pn over k that is the zero-locus of some finite family of
 * homogeneous polynomials of n + 1 variables with coefficients in k, that
 * generate a prime ideal, the defining ideal of the variety. If the condition
 * of generating a prime ideal is removed, such a set is called a projective
 * algebraic set. Equivalently, an algebraic variety is projective if it can be
 * embedded as a Zariski closed subvariety of Pn. A Zariski open subvariety of a
 * projective variety is called a quasi-projective variety.
 * 
 * If X is a projective variety defined by a homogeneous prime ideal I, then the
 * quotient ring
 * 
 * k[x_0, \ldots, x_n]/I
 * 
 * is called the homogeneous coordinate ring of X. The ring comes with the
 * Hilbert polynomial P, an important invariant (depending on embedding) of X.
 * The degree of P is the topological dimension r of X and its leading
 * coefficient times r! is the degree of the variety X. The arithmetic genus of
 * X is (−1)r (P(0) − 1) when X is smooth. For example, the homogeneous
 * coordinate ring of Pn is k[x_0, \ldots, x_n] and its Hilbert polynomial is
 * P(z) = \binom{z+n}{n}; its arithmetic genus is zero.
 * 
 * Another important invariant of a projective variety X is the Picard group
 * \operatorname{Pic}(X) of X, the set of isomorphism classes of line bundles on
 * X. It is isomorphic to H^1(X, {\mathcal{O}_X}^*). It is an intrinsic notion
 * (independent of embedding). For example, the Picard group of Pn is isomorphic
 * to Z via the degree map. The kernel of \operatorname{deg}:
 * \operatorname{Pic}(X) \to \mathbf{Z} is called the Jacobian variety of X. The
 * Jacobian of a (smooth) curve plays an important role in the study of the
 * curve.
 * 
 * The classification program, classical and modern, naturally leads to the
 * construction of moduli of projective varieties.[1] A Hilbert scheme, which is
 * a projective scheme, is used to parametrize closed subschemes of Pn with the
 * prescribed Hilbert polynomial. For example, a Grassmannian \mathbb{G}(k, n)
 * is a Hilbert scheme with the specific Hilbert polynomial. The geometric
 * invariant theory offers another approach. The classical approaches include
 * the Teichmüller space and Chow varieties.
 * 
 * For complex projective varieties, there is an intertwining of algebraic and
 * complex-analytic approaches. Chow's theorem says that a subset of the
 * projective space is the zero-locus of a family of holomorphic functions if
 * and only if it is the zero-locus of homogeneous polynomials. (A corollary of
 * this is that a "compact" complex space admits at most one variety structure.)
 * The GAGA says that the theory of holomorphic vector bundles (more generally
 * coherent analytic sheaves) on X coincide with that of algebraic vector
 * bundles.
 * 
 * @author crow
 *
 */
public interface ProjectiveVariety extends Variety
{

}
