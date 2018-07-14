package fastmath;

import java.nio.ByteBuffer;

import fastmath.matfile.MiDouble;

@SuppressWarnings("unchecked")
public class DoubleSubMatrix extends DoubleMatrix
{

  private final int columnIncrement;

  private final boolean isTranspose;

  private final int baseOffset;

  private final int rowIncrement;

  public DoubleSubMatrix(ByteBuffer buffer, int numRows, int numColumns, int offset, int rowIncrement, int columnIncrement, boolean isTranspose)
  {
    super( buffer, numRows, numColumns );

    this.baseOffset = offset;
    this.rowIncrement = rowIncrement;
    this.columnIncrement = columnIncrement;
    this.isTranspose = isTranspose;
  }

  @Override
  public Vector asVector()
  {
    return super.asVector();
  }

  /**
   * Creates a copy of this sub matrix, depending on whether this submatrix is
   * row or column order it will return DenseDoubleMatrix or DoubleRowMatrix
   */
  @Override
  public DoubleMatrix copy( boolean reuseBuffer )
  {
    if ( getColIncrement() == 1 )
    {
      return new DoubleRowMatrix( this );
    }
    else
    {
      return new DoubleColMatrix( this );
    }
  }

 
  @Override
  public int getColIncrement()
  {
    return columnIncrement;
  }

  @Override
  public int getOffset( int i, int j )
  {
    return baseOffset + ( i * rowIncrement * MiDouble.BYTES ) + ( j * columnIncrement * MiDouble.BYTES );
  }

  @Override
  public int getRowIncrement()
  {
    return rowIncrement;
  }

  @Override
  public boolean isTranspose()
  {
    return isTranspose;
  }

  @Override
  public int getMainIncrement()
  {
    throw new UnsupportedOperationException();
  }

}