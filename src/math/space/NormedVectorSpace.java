package math.space;

import math.Field;
import math.Scalar;

/**
 * A normed vector space is a pair (V, ‖·‖ ) where V is a vector space and ‖·‖ a
 * norm on V.
 * 
 * A seminormed vector space is a pair (V,p) where V is a vector space and p a
 * seminorm on V.
 * 
 * We often omit p or ‖·‖ and just write V for a space if it is clear from the
 * context what (semi) norm we are using.
 * 
 * In a more general sense, a vector norm can be taken to be any real-valued
 * function that satisfies these three properties. The properties 1. and 2.
 * together imply that
 * 
 * \|x\|= 0 if and only if x=0.
 * 
 * A useful variation of the triangle inequality is
 * 
 * \|x-y\| \ge | \|x\|-\|y\| | for any vectors x and y.
 * 
 * This also shows that a vector norm is a continuous function. Topological
 * structure
 * 
 * If (V, ‖·‖) is a normed vector space, the norm ‖·‖ induces a metric (a notion
 * of distance) and therefore a topology on V. This metric is defined in the
 * natural way: the distance between two vectors u and v is given by ‖u−v‖. This
 * topology is precisely the weakest topology which makes ‖·‖ continuous and
 * which is compatible with the linear structure of V in the following sense:
 * 
 * The vector addition + : V × V → V is jointly continuous with respect to this
 * topology. This follows directly from the triangle inequality. The scalar
 * multiplication · : K × V → V, where K is the underlying scalar field of V, is
 * jointly continuous. This follows from the triangle inequality and homogeneity
 * of the norm.
 * 
 * Similarly, for any semi-normed vector space we can define the distance
 * between two vectors u and v as ‖u−v‖. This turns the seminormed space into a
 * pseudometric space (notice this is weaker than a metric) and allows the
 * definition of notions such as continuity and convergence. To put it more
 * abstractly every semi-normed vector space is a topological vector space and
 * thus carries a topological structure which is induced by the semi-norm.
 * 
 * Of special interest are complete normed spaces called Banach spaces. Every
 * normed vector space V sits as a dense subspace inside a Banach space; this
 * Banach space is essentially uniquely defined by V and is called the
 * completion of V.
 * 
 * All norms on a finite-dimensional vector space are equivalent from a
 * topological viewpoint as they induce the same topology (although the
 * resulting metric spaces need not be the same).[2] And since any Euclidean
 * space is complete, we can thus conclude that all finite-dimensional normed
 * vector spaces are Banach spaces. A normed vector space V is locally compact
 * if and only if the unit ball B = {x : ‖x‖ ≤ 1} is compact, which is the case
 * if and only if V is finite-dimensional; this is a consequence of Riesz's
 * lemma. (In fact, a more general result is true: a topological vector space is
 * locally compact if and only if it is finite-dimensional. The point here is
 * that we don't assume the topology comes from a norm.)
 * 
 * The topology of a seminormed vector space has many nice properties. Given a
 * neighbourhood system \mathcal{N}(0) around 0 we can construct all other
 * neighbourhood systems as
 * 
 * \mathcal{N}(x)= x + \mathcal{N}(0) := \{x + N \mid N \in \mathcal{N}(0) \}
 * 
 * with
 * 
 * x + N := \{x + n \mid n \in N \}.
 * 
 * Moreover there exists a neighbourhood basis for 0 consisting of absorbing and
 * convex sets. As this property is very useful in functional analysis,
 * generalizations of normed vector spaces with this property are studied under
 * the name locally convex spaces. Linear maps and dual spaces
 * 
 * The most important maps between two normed vector spaces are the continuous
 * linear maps. Together with these maps, normed vector spaces form a category.
 * 
 * The norm is a continuous function on its vector space. All linear maps
 * between finite dimensional vector spaces are also continuous.
 * 
 * An isometry between two normed vector spaces is a linear map f which
 * preserves the norm (meaning ‖f(v)‖ = ‖v‖ for all vectors v). Isometries are
 * always continuous and injective. A surjective isometry between the normed
 * vector spaces V and W is called an isometric isomorphism, and V and W are
 * called isometrically isomorphic. Isometrically isomorphic normed vector
 * spaces are identical for all practical purposes.
 * 
 * When speaking of normed vector spaces, we augment the notion of dual space to
 * take the norm into account. The dual V ' of a normed vector space V is the
 * space of all continuous linear maps from V to the base field (the complexes
 * or the reals) — such linear maps are called "functionals". The norm of a
 * functional φ is defined as the supremum of |φ(v)| where v ranges over all
 * unit vectors (i.e. vectors of norm 1) in V. This turns V ' into a normed
 * vector space. An important theorem about continuous linear functionals on
 * normed vector spaces is the Hahn–Banach theorem. Normed spaces as quotient
 * spaces of seminormed spaces
 * 
 * The definition of many normed spaces (in particular, Banach spaces) involves
 * a seminorm defined on a vector space and then the normed space is defined as
 * the quotient space by the subspace of elements of seminorm zero. For
 * instance, with the Lp spaces, the function defined by
 * 
 * \|f\|_p = \left( \int |f(x)|^p \;dx \right)^{1/p}
 * 
 * is a seminorm on the vector space of all functions on which the Lebesgue
 * integral on the right hand side is defined and finite. However, the seminorm
 * is equal to zero for any function supported on a set of Lebesgue measure
 * zero. These functions form a subspace which we "quotient out", making them
 * equivalent to the zero function. Finite product spaces
 * 
 * Given n seminormed spaces Xi with seminorms qi we can define the product
 * space as
 * 
 * X := \prod_{i=1}^{n} X_i
 * 
 * with vector addition defined as
 * 
 * (x_1,\ldots,x_n)+(y_1,\ldots,y_n):=(x_1 + y_1, \ldots, x_n + y_n)
 * 
 * and scalar multiplication defined as
 * 
 * \alpha(x_1,\ldots,x_n):=(\alpha x_1, \ldots, \alpha x_n).
 * 
 * We define a new function q
 * 
 * q:X \mapsto \mathbb{R}
 * 
 * for example as
 * 
 * q:(x_1,\ldots,x_n) \to \sum_{i=1}^n q_i(x_i).
 * 
 * which is a seminorm on X. The function q is a norm if and only if all qi are
 * norms.
 * 
 * More generally, for each real p≥1 we have the seminorm:
 * 
 * q:(x_1,\ldots,x_n) \to \left( \sum_{i=1}^n q_i(x_i)^p \right)^\frac{1}{p}.
 * 
 * For each p this defines the same topological space.
 * 
 * A straightforward argument involving elementary linear algebra shows that the
 * only finite-dimensional seminormed spaces are those arising as the product
 * space of a normed space and a space with trivial seminorm. Consequently, many
 * of the more interesting examples and applications of seminormed spaces occur
 * for infinite-dimensional vector spaces.
 * 
 * @author crow
 *
 */
public interface NormedVectorSpace<F extends Field<?>> extends VectorSpace<F>
{
  /**
   * The norm of a mathematical object is a quantity that in some (possibly
   * abstract) sense describes the length, size, or extent of the object. Norms
   * exist for complex numbers (the complex modulus, sometimes also called the
   * complex norm or simply "the norm"), Gaussian integers (the same as the
   * complex modulus, but sometimes unfortunately instead defined to be the
   * absolute square), quaternions (quaternion norm), vectors (vector norms),
   * and matrices (matrix norms). A generalization of the absolute value known
   * as the p-adic norm is also defined.
   * 
   * Norms are variously denoted |x|, |x|_p, ||x||, or ||x||_p. In this work,
   * single bars are used to denote the complex modulus, quaternion norm, p-adic
   * norms, and vector norms, while the double bar is reserved for matrix norms.
   * 
   * The term "norm" is often used without additional qualification to refer to
   * a particular type of norm (such as a matrix norm or vector norm). Most
   * commonly, the unqualified term "norm" refers to the flavor of vector norm
   * technically known as the L2-norm. This norm is variously denoted ||x||_2,
   * ||x||, or |x|, and gives the length of an n-vector x=(x_1,x_2,...,x_n). It
   * can be computed as |x|=sqrt(x_1^2+x_2^2+...+x_n^2).
   * 
   * @return
   */
  public <S extends Scalar> S getNorm();
}
