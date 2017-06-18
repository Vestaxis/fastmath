package stochastic.probability;

import fastmath.SquareDoubleColMatrix;

/**
 * A matrix which has both rows and columns summing to 1.
 * 
 * @see http://trac.oz.canaccord.com/trac/wiki/RandomMatrix
 * 
 * @author crow
 */
public class RandomMatrix extends SquareDoubleColMatrix
{
  public RandomMatrix(int dim)
  {
    super( dim );
  }

  private static final long serialVersionUID = 1L;

}
