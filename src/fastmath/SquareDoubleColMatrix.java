package fastmath;

import java.nio.ByteBuffer;
import java.util.List;

public class SquareDoubleColMatrix extends DoubleColMatrix implements  Cloneable
{

  @Override
  public DoubleColMatrix copy( boolean reuseBuffer )
  {
    return reuseBuffer ? new SquareDoubleColMatrix( buffer, getBaseOffset(), numRows, numCols ) : super.copy( reuseBuffer );
  }

  @Override
  protected Object clone() throws CloneNotSupportedException
  {
    SquareDoubleColMatrix copy = new SquareDoubleColMatrix( buffer, getBaseOffset(), numRows, numCols );
    return copy;
  }

  public SquareDoubleColMatrix()
  {
    super();
  }

  public SquareDoubleColMatrix(AbstractMatrix x)
  {
    super( x );
  }

  public SquareDoubleColMatrix(ByteBuffer buffer, int baseOffset, int dim)
  {
    super( buffer, baseOffset, dim, dim );
  }

  public SquareDoubleColMatrix(ByteBuffer buffer, int dim)
  {
    super( buffer, dim, dim );
  }

  public SquareDoubleColMatrix(double[][] arr)
  {
    super( arr );
  }

  public SquareDoubleColMatrix(int dim, String name)
  {
    super( dim, dim, name );
  }

  public SquareDoubleColMatrix(int dim)
  {
    super( dim, dim );
  }

  public SquareDoubleColMatrix(List<Pair<Double, Double>> twoColMatrix)
  {
    super( twoColMatrix );
  }

  public SquareDoubleColMatrix(ByteBuffer buffer, int baseOffset, int numRows, int numCols)
  {
    super( buffer, baseOffset, numRows, numCols );
  }

  private static final long serialVersionUID = 1L;

}
