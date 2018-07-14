package fastmath.matfile;

import java.nio.ByteBuffer;

import fastmath.BufferUtils;

public class MiUInt32 extends MiElement
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public final static int miUINT32 = 6;

  public static final long MAX_VALUE = Integer.MAX_VALUE - Integer.MIN_VALUE;

  public static final int SIZE = 4;

  /**
   * Creates a new MiUInt32 object.
   * 
   * @param buffer
   */
  public MiUInt32(ByteBuffer buffer)
  {
    super( buffer );
  }

  /**
   * Constuct new buffer
   * 
   * @param i
   *          number of UInt32s to allocate
   */
  public MiUInt32(int i)
  {
    super( SIZE * i );
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public int getDataType()
  {
    return miUINT32;
  }

  /**
   * TODO!
   * 
   * @param index
   *          TODO!
   * 
   * @return TODO!
   */
  public long elementAt( int index )
  {
    return BufferUtils.toUnsignedInt( getBuffer().getInt( index * SIZE ) );
  }

  /**
   * TODO!
   * 
   * @param index
   *          TODO!
   * @param value
   *          TODO!
   */
  public void setElementAt( int index, long value )
  {
    BufferUtils.putUnsignedInt( getBuffer(), index * SIZE, value );
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public String toString()
  {
    StringBuffer strBuf = new StringBuffer( getClass().getName() + " [" );

    for ( int i = 0; i < getSize(); i++ )
    {
      if ( i > 0 )
      {
        strBuf.append( "," );
      }

      strBuf.append( elementAt( i ) );
    }

    strBuf.append( "]" );

    return strBuf.toString();
  }

  public int getSize()
  {
    return getBuffer().limit() / 4;
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
