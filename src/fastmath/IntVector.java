package fastmath;

import java.nio.ByteBuffer;

import com.sleepycat.persist.model.Persistent;

import fastmath.matfile.MiInt32;

@Persistent
public class IntVector extends AbstractBufferedObject
{
  protected int size;
  private int capacity;
  private double incrementalCapacityExpansionFactor = 1.75;

  public IntVector()
  {
    
  }
  
  public IntVector(int m)
  {
    super( BufferUtils.newNativeBuffer( m * MiInt32.BYTES ) );
    size = m;
  }

  public IntVector(int... x)
  {
    this( x.length );
    for ( int i = 0; i < x.length; i++ )
    {
      setElementAt( i, x[i] );
    }
  }

  public IntVector(int size, ByteBuffer buffer)
  {
    super( buffer );
    this.size = size;
  }

  public int size()
  {
    return size;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < size; i++ )
    {
      sb.append( elementAt( i ) ).append( " " );
    }

    return sb.toString();
  }

  public int elementAt( int i )
  {
    return buffer.getInt( getOffset( i ) );
  }

  public int getOffset( int i )
  {
    return i * MiInt32.BYTES;
  }

  public void setElementAt( int i, int x )
  {
    buffer.putInt( getOffset( i ), x );
  }

  /**
   * appends one element to the vector, if the buffer needs to be expanded upon
   * any particular invociation, it expands by this
   * {@link #incrementalCapacityExpansionFactor} instead of having to reallocate
   * after every single call
   * 
   * @param x
   *          the value to append to the vector
   * @return this with this{@link #size()} being 1 greater than it was prior
   * 
   */
  public IntVector append( int x )
  {
    if ( size == capacity )
    {
      capacity += getNewElementsIncrement();

      resizeBuffer( size, capacity );
    }

    size++;
    setElementAt( size - 1, x );
    return this;
  }

  private int getNewElementsIncrement()
  {
    return Math.max( 1, (int) ( size * ( incrementalCapacityExpansionFactor ) ) );
  }

  /**
   * Returns a backwards view of this matrix
   * 
   * @return new Vector sharing the same underlying buffer as this
   */
  public IntVector reverse()
  {
    return new Sub( buffer, size, getOffset( size() - 1 ), getIncrement() * -1 );
  }

  public int getIncrement()
  {
    return 1;
  }

  static class Sub extends IntVector
  {

    private int baseOffset;

    private int increment;

    private int index = -1;

    public Sub(ByteBuffer buffer, int size, int offset, int increment)
    {
      super( size, buffer );

      this.baseOffset = offset;
      this.increment = increment;
    }

    public Sub(ByteBuffer buffer, int numRows, int offset2, int rowIncrement, int index)
    {
      this( buffer, numRows, offset2, rowIncrement );
      this.index = index;
    }

    @Override
    public int getIncrement()
    {
      return increment;
    }

    /**
     * Returns the index associated with this vector, used for row and column
     * iterators
     * 
     * @return
     */
    public int getIndex()
    {
      return index;
    }

    @Override
    public int getOffset( int i )
    {
      return baseOffset + ( increment * i * MiInt32.BYTES );
    }

  }

  public IntVector slice( int beginIndex, int endIndex )
  {
    assert beginIndex >= 0 : String.format( "beginIndex %d must be >= 0", beginIndex );
    assert endIndex <= size() : String.format( "endIndex %d must be <= %d", endIndex, size() );

    return new IntVector.Sub( buffer, endIndex - beginIndex, getOffset( beginIndex ), getIncrement() );
  }

}
