package fastmath;

/**
 * Used as argument to {@link DoubleMatrix#prod(DoubleMatrix, MatrixContainer)}
 * to cache creation of "temporary" matrices
 */
public class MatrixContainer
{
  private DoubleColMatrix matrix;

  public DoubleColMatrix getMatrix( int m, int n )
  {
    if ( matrix == null )
    {
      matrix = new DoubleColMatrix( m, n );
    }
    assert m == matrix.getRowCount();
    assert n == matrix.getColCount();
    return matrix;
  }

}
