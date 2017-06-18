package fastmath.exceptions;

import fastmath.AbstractMatrix;

public class NotPositiveDefiniteException extends FastMathException
{
  private static final long serialVersionUID = 1L;

  private final AbstractMatrix matrix;

  public NotPositiveDefiniteException(AbstractMatrix m)
  {
    super( "Matrix is not positive definite" );
    this.matrix = m;
  }

  /**
   * @return Returns the matrix.
   */
  public AbstractMatrix getMatrix()
  {
    return matrix;
  }

}
