package fastmath.matfile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

import fastmath.BufferUtils;
import fastmath.matfile.exceptions.MatFileException;

public class MiInt8 extends MiElement
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public final static int miINT8 = 1;
  private String bufferAsString = null;

  protected MiInt8(ByteBuffer slice)
  {
    super( slice );
  }

  /**
   * Copys the string into a native byte array
   * 
   * @param string
   */
  protected MiInt8(String string)
  {
    this( ByteBuffer.allocateDirect( string.length() ) );
    BufferUtils.copy( CharBuffer.wrap( string ), getBuffer() );
  }

  /**
   * Creates a new MiInt8 object.
   * 
   * @param buffer
   * 
   * @param numberOfBytes
   *          unsigned int
   * 
   * @param pos
   *          TODO!
   * 
   * @throws IOException
   *           TODO!
   */
  public MiInt8(ByteBuffer buffer, long numberOfBytes, long pos) throws MatFileException
  {
    super( (ByteBuffer) buffer.slice().limit( BufferUtils.toUnsignedInt( numberOfBytes ) ) );
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public int getDataType()
  {
    return miINT8;
  }

  /**
   * TODO!
   * 
   * @return TODO!
   * @throws UnsupportedEncodingException
   */
  public String asString()
  {
    if ( bufferAsString == null )
    {
      byte[] ab = new byte[getBuffer().limit()];

      for ( int i = 0; i < getBuffer().limit(); i++ )
      {
        ab[i] = getBuffer().get( i );
      }

      bufferAsString = new String( ab, StandardCharsets.UTF_8 );
    }
    return bufferAsString;
  }

  /**
   * Get as char buffer
   * 
   * @return
   */
  public CharBuffer getCharBuffer()
  {
    return getBuffer().asCharBuffer();
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public String toString()
  {
    return asString();
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
