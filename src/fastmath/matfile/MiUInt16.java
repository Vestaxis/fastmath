package fastmath.matfile;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import fastmath.BufferUtils;

public class MiUInt16 extends MiElement
{
  private static final long serialVersionUID = 1L;

  public final static int miUINT16 = 4;

  private ShortBuffer shortBuffer;

  /**
   * Creates a new MiUInt16 object.
   * 
   * @param slice
   *          TODO!
   */
  public MiUInt16(ByteBuffer slice)
  {
    super( slice );
    this.shortBuffer = slice.asShortBuffer();
  }

  public MiUInt16(String values)
  {
    super( BufferUtils.newNativeBuffer( values.length() ) );
    BufferUtils.copy( values, getBuffer() );
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public int getDataType()
  {
    return miUINT16;
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public String toString()
  {
    StringBuffer strBuf = new StringBuffer( getClass().getName() + "[" );

    for ( int i = 0; i < getSize(); i++ )
    {
      if ( i > 0 )
      {
        strBuf.append( "," );
      }

      strBuf.append( BufferUtils.getUnsignedShort( shortBuffer, i ) );
    }

    strBuf.append( "]" );

    return strBuf.toString();
  }

  /**
   * Get number of shorts in this buffer
   * 
   * @return
   */
  public int getSize()
  {
    return shortBuffer.limit();
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
