package fastmath.matfile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MiInt32 extends MiElement
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public final static int miINT32 = 5;

  public static final int BYTES = Integer.SIZE / Byte.SIZE;

  /**
   * Creates a new MiInt32 object.
   * 
   * @param slice
   *          TODO!
   */
  public MiInt32(ByteBuffer slice)
  {
    super( slice );
    getBuffer().order( ByteOrder.nativeOrder() );
  }

  /**
   * Creates a new MiInt32 object.
   * 
   * @param values
   *          TODO!
   */
  public MiInt32(int... values)
  {
    this( values.length );
    int i = 0;
    for ( int x : values )
    {
      getBuffer().putInt( i, x );
      i += 4;

    }
  }

  /**
   * 
   * @param len
   *          number of integers to allocate
   */
  public MiInt32(int len)
  {
    super( MiUInt32.SIZE * len );
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public int getDataType()
  {
    return miINT32;
  }

  /**
   * TODO!
   * 
   * @param index
   *          TODO!
   * 
   * @return TODO!
   */
  public int elementAt( int index )
  {
    return getBuffer().getInt( index * 4 );
  }

  /**
   * TODO!
   * 
   * @param index
   *          TODO!
   * @param value
   *          TODO!
   */
  public void setElementAt( int index, int value )
  {
    getBuffer().putInt( index * 4, value );
  }

  /**
   * Number of ints in this buffer
   * 
   * @return TODO!
   */
  public int getSize()
  {
    return getBuffer().limit() / 4;
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public String toString()
  {
    StringBuffer strBuf = new StringBuffer( getClass().getSimpleName() + "[" + getSize() + "]={" );

    for ( int i = 0; i < getSize(); i++ )
    {
      if ( i > 0 )
      {
        strBuf.append( "," );
      }

      strBuf.append( elementAt( i ) );
    }

    strBuf.append( "}" );

    return strBuf.toString();
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  public Object toObject()
  {
    throw new UnsupportedOperationException();
  }

}
