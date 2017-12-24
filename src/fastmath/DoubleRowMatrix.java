package fastmath;

import static java.lang.String.format;

import java.util.function.Consumer;

import fastmath.Vector.Condition;
import fastmath.matfile.MiDouble;

/**
 * a row-major double matrix representation. most notible is the this
 * {@link #appendRow(Vector)} method, which {@link DoubleColMatrix} does not
 * have, since it has {@link DoubleColMatrix#appendColumn(Vector)}
 */
@SuppressWarnings("unchecked")
public class DoubleRowMatrix extends DoubleMatrix
{

  public DoubleRowMatrix()
  {

  }


  private int rowCapacity;

  private double incrementalCapacityExpansionFactor = 1.25;

  public DoubleRowMatrix(int m, int n)
  {
    super( m, n );
    rowCapacity = m;
  }

  public DoubleRowMatrix(String name, int m, int n)
  {
    super( m, n );
    rowCapacity = m;
    setName( name );
  }

  /**
   * Contstructs a new matrix, copied from X
   */
  public DoubleRowMatrix(AbstractMatrix x)
  {
    super( x.getRowCount(), x.getColCount() );
    for ( int i = 0; i < numRows; i++ )
    {
      row( i ).assign( x.row( i ) );
    }
    setName( x.getName() );
  }

  public DoubleRowMatrix(double[][] arr)
  {
    super( arr.length, arr[0].length );

    // TODO: optimize
    for ( int i = 0; i < numRows; i++ )
    {
      for ( int j = 0; j < numCols; j++ )
      {
        set( i, j, arr[i][j] );
      }
    }
  }

  public DoubleRowMatrix(int numRows, int numCols, String name)
  {
    this( numRows, numCols );
    setName( name );
  }

  /**
   * Create a copy of this matrix
   */
  @Override
  public DoubleMatrix copy( boolean reuseBuffer )
  {
    return new DoubleRowMatrix( this );
  }


  /**
   * 
   * @return the newly appended {@link Vector}
   */
  public Vector appendRow()
  {
    if ( numRows == rowCapacity )
    {
      rowCapacity += getNewRowsIncrement();
      int prevSize = numRows * numCols;
      int nextSize = numCols * rowCapacity;
      resizeBuffer( prevSize, nextSize );
    }

    numRows++;
    return (Vector) row( numRows - 1 );
  }

  public Vector appendRow( Consumer<Vector> newRowInitializer )
  {
    Vector newRow = appendRow();
    newRowInitializer.accept( newRow );
    return newRow;

  }

  public synchronized Vector appendRow( Vector newRow )
  {
    if ( numRows == rowCapacity )
    {
      int prevRowCapacity = rowCapacity;
      rowCapacity += getNewRowsIncrement();

      try
      {
        resizeBuffer( prevRowCapacity * numCols, rowCapacity * numCols );
      }
      catch( OutOfMemoryError oom )
      {
        throw new OutOfMemoryError( format( "not enough free RAM to expand matrix buffer capacity to %d rows, numRows=%d ", rowCapacity, numRows ) );
      }
    }

    numRows++;
    Vector dstRow = row( numRows - 1 );
    BLAS1.dcopy( newRow, dstRow );
    return dstRow;
  }

  public void appendRow( double... newRow )
  {
    if ( numRows >= rowCapacity )
    {
      int prevRowCapacity = rowCapacity;
      rowCapacity += getNewRowsIncrement();

      resizeBuffer( prevRowCapacity * numCols, rowCapacity * numCols );
    }

    numRows++;
    row( numRows - 1 ).assign( newRow );
  }

  /**
   * Compacts the internal buffer to the minimum size required
   * 
   * @return this
   */
  public DoubleRowMatrix trimCapacityToSize()
  {
    rowCapacity = numRows;
    resizeBuffer( rowCapacity * numCols, numRows * numCols );
    return this;
  }

  private int getNewRowsIncrement()
  {
    return Math.max( 1, (int) ( numRows * incrementalCapacityExpansionFactor ) );
  }

  @Override
  public int getOffset( int i, int j )
  {
    assert i >= 0 && i <= numRows : "Row=" + i + ", numRows=" + numRows;
    assert j >= 0 && j <= numCols : "Column=" + j + ", numColumns=" + numCols;

    int offset = ( i * MiDouble.BYTES * getRowIncrement() ) + ( getColIncrement() * j * MiDouble.BYTES );

    return offset;
  }

  @Override
  public int getColIncrement()
  {
    return 1;
  }

  @Override
  public int getRowIncrement()
  {
    return numCols;
  }

  @Override
  public int getMainIncrement()
  {
    return getRowIncrement();
  }

  public double getIncrementalCapacityExpansionFactor()
  {
    return incrementalCapacityExpansionFactor;
  }

  public void setIncrementalCapacityExpansionFactor( double incrementalCapacityExpansionFactor )
  {
    this.incrementalCapacityExpansionFactor = incrementalCapacityExpansionFactor;
  }

  public void append( DoubleMatrix q )
  {
    for ( Vector vec : q.rows() )
    {
      appendRow( vec );
    }
  }

  /**
   * 
   * @param i
   * @param expand
   *          if true, and the matrix isnt large enough, it resizes to
   *          accommodate the requested column
   * @return
   */
  public Vector row( int i, boolean expand )
  {
    if ( !expand )
    {
      return row( i );
    }
    else
    {
      while (i >= getRowCount())
      {
        appendRow();
      }

      return row( i );
    }

  }

  public Vector row( int i, Consumer<Vector> newRowInitializer )
  {

    while (i >= getRowCount())
    {
      appendRow( newRowInitializer );
    }

    return row( i );

  }

  /**
   * A getter for Run-Length encoded matrix
   * 
   * @param t
   * @param yk
   * @return
   */
  public double getSparse(double t, int yk) 
  {	
	int row = col(0).findLast(t, Condition.LTE);
	return row >= 0  ? get(row,yk+1) : 0;
  }

}
