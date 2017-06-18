package math.space;

import math.Field;
import math.Module;
import math.theory.group.CommutativeGroup;

/**
 * A vector space V is a set that is closed under finite vector addition and
 * scalar multiplication. The basic example is n-dimensional Euclidean space
 * R^n, where every element is represented by a list of n real numbers, scalars
 * are real numbers, addition is componentwise, and scalar multiplication is
 * multiplication on each term separately.
 * 
 * For a general vector space, the scalars are members of a field F, in which
 * case V is called a vector space over F.
 * 
 * Euclidean n-space R^n is called a real vector space, and C^n is called a
 * complex vector space.
 * 
 * In order for V to be a vector space, the following conditions must hold for
 * all elements X,Y,Z in V and any scalars r,s in F:
 * 
 * 1. Commutativity: X+Y=Y+X. (1)
 * 
 * 2. Associativity of vector addition: (X+Y)+Z=X+(Y+Z). (2)
 * 
 * 3. Additive identity: For all X, 0+X=X+0=X. (3)
 * 
 * 4. Existence of additive inverse: For any X, there exists a -X such that
 * X+(-X)=0. (4)
 * 
 * 5. Associativity of scalar multiplication: r(sX)=(rs)X. (5)
 * 
 * 6. Distributivity of scalar sums: (r+s)X=rX+sX. (6)
 * 
 * 7. Distributivity of vector sums: r(X+Y)=rX+rY. (7)
 * 
 * 8. Scalar multiplication identity: 1X=X. (8)
 * 
 * Let V be a vector space of dimension n over the field of q elements (where q
 * is necessarily a power of a prime number). Then the number of distinct
 * nonsingular linear operators on V is M(n,q) =
 * (q^n-q^0)(q^n-q^1)(q^n-q^2)...(q^n-q^(n-1)) (9) = q^(n^2)(q^(-n);q)_n (10)
 * 
 * and the number of distinct k-dimensional subspaces of V is S(k,n,q) =
 * ((q^n-q^0)(q^n-q^1)(q^n-q^2)...(q^n-q^(k-1)))/(M(k,q)) (11) =
 * ((q^n-1)(q^(n-1)
 * -1)(q^(n-2)-1)...(q^(n-k+1)-1))/((q^k-1)(q^(k-1)-1)(q^(k-2)-1)...(q-1)) (12)
 * = (q^((k-n)n)(q^(-n);q)_k)/((q^(-n),q)_n), (13)
 * 
 * where (q;a)_n is a q-Pochhammer symbol.
 * 
 * A module is abstractly similar to a vector space, but it uses a ring to
 * define coefficients instead of the field used for vector spaces. Modules have
 * coefficients in much more general algebraic objects.
 * 
 * @author crow
 *
 */
public interface VectorSpace<F extends Field<?>> extends Module<F>, CommutativeGroup<F>
{

  public int getDimension();

  public VectorSpace<F> getDual();

}
