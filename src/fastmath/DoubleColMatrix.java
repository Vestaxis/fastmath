package fastmath;

import static java.lang.Math.abs;
import static java.lang.Math.floorDiv;
import static java.lang.System.getProperties;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import com.sleepycat.persist.model.Persistent;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import fastmath.exceptions.NotInvertableException;
import fastmath.matfile.MiDouble;

@Persistent
public class DoubleColMatrix extends DoubleMatrix
{

  private int columnCapacity;

  private final int baseOffset;

  /**
   * When this{@link #appendColumn(Vector)} is called and there isn't enough
   * capacity in the underlying {@link DirectByteBuffer} then a new
   * {@link DirectByteBuffer} is created with a capacity of (1 + this
   * {@link #incrementalCapacityExpasionFactor} ) * this{@link #getColCount()}
   * and the existing memory contents copied over and the previous buffer
   * returned to the heap
   */
  private double incrementalCapacityExpasionFactor = 1.75;

  public DoubleColMatrix()
  {
    this( 0, 0 );
    columnCapacity = 0;
  }

  public DoubleColMatrix(int m, int n)
  {
    super( m, n );
    columnCapacity = n;
    baseOffset = 0;
  }

  public DoubleColMatrix(int m, int n, BiFunction<Integer, Integer, Double> x)
  {
    super( m, n, x );
    columnCapacity = n;
    baseOffset = 0;
  }

  public DoubleColMatrix(List<Pair<Double, Double>> twoColMatrix)
  {
    this( twoColMatrix.size(), 2 );
    int i = 0;
    for ( Pair<Double, Double> pair : twoColMatrix )
    {
      set( i, 0, pair.left );
      set( i, 1, pair.right );
      i++;
    }
  }

  public DoubleColMatrix(int m, int n, String name)
  {
    this( m, n );
    columnCapacity = n;
    setName( name );
  }

  public DoubleColMatrix(ByteBuffer buffer, int m, int n)
  {
    super( buffer, m, n );
    columnCapacity = n;
    baseOffset = 0;
  }

  public DoubleColMatrix(double[][] arr)
  {
    this( arr.length, arr[0].length );

    // TODO: optimize
    for ( int i = 0; i < numRows; i++ )
    {
      for ( int j = 0; j < numCols; j++ )
      {
        set( i, j, arr[i][j] );
      }
    }
  }

  /**
   * Constructs a new matrix, copied from X
   */
  public DoubleColMatrix(AbstractMatrix x)
  {
    super( x.getRowCount(), x.getColCount() );
    baseOffset = 0;
    columnCapacity = getColCount();
    setName( x.getName() );
    for ( int i = 0; i < numCols; i++ )
    {
      Vector src = x.col( i );
      Vector dst = col( i );
      dst.assign( src );
    }
  }

  public DoubleColMatrix(ByteBuffer buffer, int baseOffset, int numRows, int numCols)
  {
    super( buffer, numRows, numCols );
    columnCapacity = numCols;
    this.baseOffset = baseOffset;
  }

  public DoubleColMatrix(String string, double[][] ds)
  {
    this( ds );
    setName( string );
  }

  /**
   * Create a copy of this matrix
   */
  @SuppressWarnings("unchecked")
  @Override
  public DoubleColMatrix copy( boolean reuseBuffer )
  {
    return reuseBuffer ? new DoubleColMatrix( buffer, getBaseOffset(), numRows, numCols ) : new DoubleColMatrix( this );
  }

  /**
   * Create a copy of this matrix
   */
  @Override
  public DoubleColMatrix copy( MatrixContainer container )
  {
    return container.getMatrix( numRows, numCols ).assign( this );
  }

  /**
   * Return offset in BYTES
   * 
   */
  @Override
  public int getOffset( int i, int j )
  {
    assert i >= 0 && i <= numRows : "Row=" + i + ", numRows=" + numRows;
    assert j >= 0 && j <= numCols : "Column=" + j + ", numColumns=" + numCols;

    return getBaseOffset() + ( i * MiDouble.BYTES + ( getColIncrement() * j * MiDouble.BYTES ) );
  }

  public int getOffsetRow( int intOffset )
  {
    return floorDiv( intOffset, getColIncrement() );
  }

  public int getOffsetCol( int intOffset )
  {
    return intOffset % getColIncrement();

  }

  /**
   * Offset (in DOUBLES) needed to get to next column
   */
  @Override
  public int getColIncrement()
  {
    return numRows;
  }

  /**
   * Offset in doubles needed to get to next row
   */
  @Override
  public int getRowIncrement()
  {
    return 1;
  }

  /**
   * 
   * @return the newly appended {@link Vector}
   */
  public Vector appendColumn()
  {
    if ( numCols == columnCapacity )
    {
      columnCapacity += getNewColumnsIncrement();
      int prevSize = numRows * numCols;
      int nextSize = numRows * columnCapacity;
      resizeBuffer( prevSize, nextSize );
    }

    numCols++;
    return (Vector) col( numCols - 1 );
  }

  /**
   * Compacts the internal buffer to the minimum size required
   */
  public void trimToSize()
  {
    int prevColCapacity = columnCapacity;
    columnCapacity = numCols;

    resizeBuffer( prevColCapacity * numCols, numRows * numCols );
  }

  private int getNewColumnsIncrement()
  {
    return Math.max( 1, (int) ( numCols * ( incrementalCapacityExpasionFactor ) ) );
  }

  /**
   * Divide each element in this matrix by a constant scalar
   * 
   * @param x
   * 
   * @return this
   */
  @Override
  public DoubleColMatrix divide( double x )
  {
    for ( Vector col : cols() )
    {
      col.divide( x );
    }

    return this;
  }

  /**
   * Construct an identity matrix
   * 
   * @param i
   *          size of matrix
   * @param v
   *          value to put on the diagonal
   * 
   * @return new DenseDoubleMatrix
   */
  public static AbstractMatrix eye( int i, double v )
  {
    DoubleMatrix x = new DoubleColMatrix( i, i );
    x.diag().assign( v );
    return x;
  }

  /**
   * Construct an identity matrix
   * 
   * @param i
   *          size of matrix
   * 
   * @return new DenseDoubleMatrix
   */
  public static AbstractMatrix eye( int i )
  {
    return eye( i, 1.0 );
  }

  @Override
  public int getMainIncrement()
  {
    return getColIncrement();
  }

  public int getBaseOffset()
  {
    return baseOffset;
  }

  public Vector appendColumn( String name )
  {
    Vector newColumn = appendColumn();
    newColumn.setName( name );
    columnMap.put( name, newColumn );
    return newColumn;
  }

  public Vector getColumn( String name )
  {
    return columnMap.get( name );
  }

  private HashMap<String, Vector> columnMap = new HashMap<>();

  /**
   * 
   * @param i
   * @param expand
   *          if true, and the matrix isnt large enough, it resizes to
   *          accommodate the requested column
   * @return
   */
  public Vector col( int i, boolean expand )
  {
    if ( !expand )
    {
      return col( i );
    }
    else
    {
      while (i >= getColCount())
      {
        appendColumn();
      }

      return col( i );
    }

  }

  public Vector row( int i, boolean resize )
  {
    if ( resize && i >= getRowCount() )
    {
      resize( i + 1, getColCount() );
    }
    return row( i );
  }

  public DoubleColMatrix invert() throws NotInvertableException
  {
    IntVector pivots = new IntVector( getRowCount() );
    Pointer rowCount = newIntParam( getRowCount() );
    Pointer colCount = newIntParam( getColCount() );
    IntVector output = new IntVector( 1 );

    Pointer A = Native.getDirectBufferPointer( buffer );
    Pointer outBuffer = Native.getDirectBufferPointer( output.getBuffer() );
    Pointer pivotBuffer = Native.getDirectBufferPointer( pivots.getBuffer() );
    Lapack.instance.dgetrf_( rowCount, colCount, A, rowCount, pivotBuffer, outBuffer );

    if ( output.elementAt( 0 ) > 0 )
    {
      throw new NotInvertableException( output.elementAt( 0 ) );
    }
    else if ( output.elementAt( 0 ) < 0 )
    {
      throw new IllegalArgumentException( "the " + -output.elementAt( 0 ) + "-th argument had an illegal value" );
    }

    Vector workspace = new Vector( 1024 );
    Pointer lwork = newIntParam( workspace.size );
    Lapack.instance.dgetri_( rowCount, A, rowCount, pivotBuffer, workspace.getPointer(), lwork, outBuffer );
    if ( output.elementAt( 0 ) > 0 )
    {
      throw new NotInvertableException( output.elementAt( 0 ) );
    }
    else if ( output.elementAt( 0 ) < 0 )
    {
      throw new IllegalArgumentException( "the " + -output.elementAt( 0 ) + "-th argument had an illegal value" );
    }

    return this;
  }

  private Pointer newIntParam( int rowCount )
  {
    IntVector ib = new IntVector( 1 );
    ib.setElementAt( 0, rowCount );
    return Native.getDirectBufferPointer( ib.getBuffer() );
  }

  public double supNorm()
  {
    return rows().stream( false ).mapToDouble( row -> row.stream().map( val -> abs( val ) ).sum() ).max().getAsDouble();
  }
}
