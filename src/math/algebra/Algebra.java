package math.algebra;

import math.Field;
import math.theory.ring.Ring;

/**
 * an algebra is a vector space V over a field F with a multiplication. The
 * multiplication must be distributive and, for every f in F and x,y in V must
 * satisfy f(xy)=(fx)y=x(fy).
 * 
 * An algebra over a field is a vector space equipped with a bilinear product.
 * An algebra such that the product is associative and has an identity is
 * therefore a ring that is also a vector space, and thus equipped with a field
 * of scalars. Such an algebra is called here a unital associative algebra for
 * clarity, because there are also nonassociative algebras.
 * 
 * In other words, an algebra over a field is a set together with operations of
 * multiplication, addition, and scalar multiplication by elements of the
 * underlying field, that satisfy the axioms implied by "vector space" and
 * "bilinear".[1]
 * 
 * One may generalize this notion by replacing the field of scalars by a
 * commutative ring, and thus defining an algebra over a ring.
 * 
 * Because of the ubiquity of associative algebras, and because many textbooks
 * teach more associative algebra than nonassociative algebra, it is common for
 * authors to use the term algebra to mean associative algebra. However, this
 * does not diminish the importance of nonassociative algebras, and there are
 * texts that give both structures and names equal priority.
 * 
 * Examples of algebras include the algebra of real numbers, vectors and
 * matrices, tensors, complex numbers, and quaternions. (Note that linear
 * algebra, which is the study of linear sets of equations and their
 * transformation properties, is not an algebra in the formal sense of the
 * word.) Other more exotic algebras that have been investigated and found to be
 * of interest are usually named after one or more of their investigators. This
 * practice unfortunately leads to entirely unenlightening names which are
 * commonly used by algebraists without further explanation or elaboration.
 * 
 * @author crow
 *
 */
public interface Algebra<F extends Field<?>> extends Ring<F>
{

}
