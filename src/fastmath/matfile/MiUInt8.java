package fastmath.matfile;

import java.nio.ByteBuffer;

public class MiUInt8 extends MiElement
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public final static int miUINT8 = 2;

  @Override
  public int getDataType()
  {
    return miUINT8;
  }

  public MiUInt8(ByteBuffer slice)
  {
    super( slice );
  }

  @Override
  public String toString()
  {
    StringBuffer strBuf = new StringBuffer( getClass().getName() + "[" );

    final ByteBuffer buffer = getBuffer();
    final int bufferLimit = buffer.limit();
    for ( int i = 0; i < bufferLimit; i++ )
    {
      if ( i > 0 )
      {
        strBuf.append( "," );
      }

      strBuf.append( buffer.getChar( i ) );
    }

    strBuf.append( "]" );

    return strBuf.toString();
  }

  public Object toObject()
  {
    throw new UnsupportedOperationException();
  }

}
