package fastmath;

import static java.lang.Math.min;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.Comparator;

import com.sleepycat.persist.model.Persistent;

import fastmath.exceptions.FastMathException;
import fastmath.matfile.MiDouble;
import fastmath.matfile.MiInt32;
import fastmath.matfile.MiMatrix;
import fastmath.matfile.MxDouble;
import fastmath.matfile.Writable;
import util.AutoHashMap;

@Persistent
public abstract class AbstractMatrix extends AbstractBufferedObject implements  Writable
{
  protected String name;
  protected int numCols;
  protected int numRows;

  public AbstractMatrix()
  {

  }

  public AbstractMatrix(ByteBuffer buffer)
  {
    super( buffer );
  }

  public AbstractMatrix(int bufferSize)
  {
    super( bufferSize );
  }

  public abstract Vector asVector();

  transient AutoHashMap<Integer, Vector> colVectors = new AutoHashMap<Integer, Vector>( i -> new Vector.Sub( buffer,
                                                                                                             numRows,
                                                                                                             getOffset( 0, i ),
                                                                                                             getRowIncrement(),
                                                                                                             i ) );

  public Vector col( int i )
  {
    assert i <= numCols;
    return colVectors.getOrCreate( i );
  }

  /**
   * Prints the matrix, rows are newline separated, cols are space separated
   * 
   * @param printWriter
   */
  public void print( PrintWriter printWriter )
  {
    print( printWriter, 15 );
  }

  public void print( PrintWriter printWriter, int digits )
  {
    String format = format( "%%5.%df ", digits );
    for ( int i = 0; i < getRowCount(); i++ )
    {
      for ( int j = 0; j < getColCount(); j++ )
      {
        printWriter.format( format, get( i, j ) );
      }
      if ( getRowCount() > 1 )
      {
        printWriter.println();
      }
    }
  }

  /**
   * Iterates over the columns
   * 
   * @return
   */
  public ColIterator<Vector> cols()
  {
    return new ColIterator<Vector>( this );
  }

  /**
   * word up: this uses toDenseDoubleMatrix(), so it has the potential to use
   * lots of temp space just for reading/writing non DenseDoubleMatrix objects
   */
  public MiMatrix createMiMatrix()
  {
    MiInt32 dimensions = new MiInt32( numRows, numCols );

    if ( name == null )
    {
      throw new IllegalArgumentException( "name is null" );
    }
    return new MiMatrix( new MxDouble( new MiDouble( toColMajorMatrix().asVector() ), dimensions ), dimensions, name );
  }

  /**
   * Offset needed to get to next column
   */
  public abstract int getColIncrement();

  public String getDimensionString()
  {
    return numRows + "x" + numCols;
  }

  public String getName()
  {
    return name;
  }

  /**
   * Gets the offset of the beginning of this matrix within the backing
   * ByteBuffer, 0 is first index
   * 
   * @param i
   * @param j
   * 
   * @return >=0
   */
  public abstract int getOffset( int i, int j );

  /**
   * Offset needed to get to next element going down the column
   */
  public abstract int getRowIncrement();

  /**
   * Returns true if the underlying matrix is column major
   */
  public final boolean isColMajor()
  {
    return getRowIncrement() == 1;
    // return ( isTranspose() ? getColumnIncrement() : getRowIncrement() ) == 1;
  }

  /**
   * Returns true if there are no gaps in the matrix storage
   * 
   * @return true if this is a dense matrix
   */
  public boolean isDense()
  {
    return ( getRowIncrement() == 1 && getColIncrement() == numRows ) || ( getRowIncrement() == numCols && getColIncrement() == 1 );
  }

  public boolean isSquare()
  {
    return numRows == numCols;
  }

  /**
   * Test for symmetry
   * 
   * TODO: terribly ineffecient, should really look into storing some flag
   * instead
   */
  public boolean isSymmetric()
  {
    if ( !isSquare() )
    {
      return false;
    }

    for ( int i = 0; i < numRows; i++ )
    {
      if ( !col( i ).equals( row( i ) ) )
      {
        return false;
      }
    }

    return true;
  }

  public boolean isTranspose()
  {
    return false;
  }

  public int getColCount()
  {
    return numCols;
  }

  public int getRowCount()
  {
    return numRows;
  }

  public abstract <X extends DoubleMatrix> X slice( int beginRow, int beginCol, int endRow, int endCol );

  /**
   * Reallocate this matrix
   * 
   * @param newRowCount
   * @param newColCount
   * @return
   */
  public AbstractMatrix resize( int newRowCount, int newColCount )
  {
    DoubleMatrix backup = copy( true );
    try
    {
      this.buffer = BufferUtils.newNativeBuffer( ( newRowCount * newColCount ) * MiDouble.BYTES );
    }
    catch( OutOfMemoryError oom )
    {
      throw new RuntimeException( format( "OutOfMemoryError encountered when trying to resize matrix from [%d,%d] to [%d,%d]: %s",
                                          getRowCount(),
                                          getColCount(),
                                          newRowCount,
                                          newColCount,
                                          oom.getMessage() ),
                                  oom );
    }

    this.numRows = newRowCount;
    this.numCols = newColCount;
    int rowToCopy = min( getRowCount(), backup.getRowCount() );
    int colsToCopy = min( getColCount(), backup.getColCount() );
    slice( 0, 0, rowToCopy, colsToCopy ).assign( backup.slice( 0, 0, rowToCopy, colsToCopy ) );
    return this;
  }

  public abstract <T extends DoubleMatrix> T assign( AbstractMatrix x );

  /**
   * 
   * @return null if this matrix has no rows
   */
  public Vector lastRow()
  {
    if ( getRowCount() == 0 )
    {
      return null;
    }
    return row( getRowCount() - 1 );
  }

  /**
   * 
   * @return null if this matrix has no cols
   */
  public Vector lastCol()
  {
    if ( getColCount() < 1 )
    {
      return null;
    }
    return col( getColCount() - 1 );
  }

  /**
   * 
   * @return null if this matrix has no cols
   */
  public Vector nextToLastCol()
  {
    if ( getColCount() < 2 )
    {
      return null;
    }
    return col( getColCount() - 2 );
  }

  public Vector row( int i )
  {
    assert i >= 0 && i < getRowCount() : "illegal row, " + i + ", numRows = " + getRowCount();

    int offset = getOffset( i, 0 );
    Vector.Sub rowVector = new Vector.Sub( buffer, numCols, offset, getColIncrement(), i );
    return rowVector;
  }

  /**
   * Iterates over the rows
   * 
   * @return
   */
  public <V extends Vector> RowIterator<V> rows()
  {
    return new RowIterator<V>( this );
  }

  /**
   * Set an element of the matrix
   * 
   * @param i
   * @param j
   * @param x
   * 
   * @return this
   */
  public AbstractMatrix set( int i, int j, double x )
  {
    assert i >= 0 : format( "row i=%d must be non-negative", i );
    assert j >= 0 : format( "col j=%d must be non-negative", i );
    assert i < numRows : format( "i=%d >= numRows=%d", i, numRows );
    assert j < numCols : format( "j=%d >= numCols=%d", j, numCols );
    int offset = getOffset( i, j );
    buffer.putDouble( offset, x );
    return this;
  }

  /**
   * Gets the element at the specified row/column
   * 
   * @param i
   * @param j
   * @return
   */
  public double get( int i, int j )
  {
    return buffer.getDouble( getOffset( i, j ) );
  }

  @SuppressWarnings("unchecked")
  public <T extends AbstractMatrix> T setName( String name )
  {
    this.name = name;
    return (T) this;
  }

  /**
   * Converts this Matrix to a column major dense matrix.
   * 
   * If it already is then nothing is done and nothing is done.
   * 
   * @return new DenseDoubleMatrix or this
   */
  public DoubleMatrix toColMajorMatrix()
  {
    if ( this instanceof DoubleColMatrix && isColMajor() && isDense() && !isTranspose() )
    {
      return (DoubleMatrix) this;
    }
    else
    {
      return new DoubleColMatrix( this );
    }
  }

  public abstract <M extends AbstractMatrix> M copy( boolean reuseBuffer );

  /**
   * @return A {@link Vector} of length this{@link #getColCount()} with column
   *         sums of these matrix entries
   */
  public Vector sum()
  {
    Vector sums = new Vector( getColCount() );
    for ( int i = 0; i < sums.size; i++ )
    {
      sums.set( i, col( i ).sum() );
    }
    return sums;
  }

  public abstract AbstractMatrix trans();

  /**
   * Element wise raise-to-the-power
   * 
   * @param x
   * @return this
   */
  public AbstractMatrix pow( double x )
  {
    asVector().pow( x );
    return this;
  }

  public void sort( Comparator<Vector> cmp )
  {
    try
    {
      sort( 0, numRows - 1, cmp );
    }
    catch( FastMathException e )
    {
      throw new RuntimeException( e.getMessage(), e );
    }
  }

  protected void sort( int left, int right, Comparator<Vector> cmp ) throws FastMathException
  {
    if ( right <= left )
    {
      return;
    }

    int i = partition( left, right, cmp );

    sort( left, i - 1, cmp );
    sort( i + 1, right, cmp );
  }

  private int partition( int left, int right, Comparator<Vector> cmp )
  {
    int i = left - 1;
    int j = right;

    while (true)
    {
      // find item on left to swap
      while (cmp.compare( row( ++i ), row( right ) ) > 0)
        ;

      // find item on right to swap
      while (cmp.compare( row( right ), row( --j ) ) > 0)
      {
        if ( j == left )
        {
          break;
        }
      }

      // check if pointers cross
      if ( i >= j )
      {
        break;
      }

      exch( i, j );
    }

    // swap with partition element
    if ( i != right )
    {
      exch( i, right );
    }

    return i;
  }

  Vector tmpVec = new Vector( getColCount() );

  private void exch( int i, int j )
  {
    Vector tmp = row( i ).copy();
    row( i ).assign( row( j ) );
    row( j ).assign( tmp );
  }

  public String toMatrixString()
  {
    StringBuffer sb = new StringBuffer( "[" );
    rows().forEach( row -> {
      row.forEach( x -> sb.append( x + "," ) );
      sb.append( ";" );
    } );
    sb.append( "]" );
    return sb.toString();
  }
}