package math.space;

import numbersystems.RealNumbers;
import stochastic.SigmaField;

/**
 * Euclidean n-space, sometimes called Cartesian space or simply n-space, is the
 * space of all n-tuples of real numbers, (x_1, x_2, ..., x_n). Such n-tuples
 * are sometimes called points, although other nomenclature may be used (see
 * below).
 * 
 * R^n is a vector space and has Lebesgue covering dimension n. For this reason,
 * elements of R^n are sometimes called n-vectors. R^1=R is the set of real
 * numbers (i.e., the real line), and R^2 is called the Euclidean plane. In
 * Euclidean space, covariant and contravariant quantities are equivalent so
 * e^->^j=e^->_j.
 * 
 * @author crow
 *
 */
public interface EuclideanSpace
                                extends
                                VectorSpace<RealNumbers>,
                                FiniteDimensionalSpace,
                                RadonSpace,
                                SigmaField<RealNumbers>
{
	/**
	 * @return the dimension of the space
	 */
 public int getDimension();
 
 public double getDist( EuclideanSpace other );
}
