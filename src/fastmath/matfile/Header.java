package fastmath.matfile;

import java.nio.ByteBuffer;

import fastmath.AbstractBufferedObject;
import fastmath.BufferUtils;

public class Header extends AbstractBufferedObject
{
  private static final long serialVersionUID = 1L;

  public static final int TEXT_LEN = 124;

  // text + 2 shorts = 128 bytes
  public static final int HEADER_LEN = TEXT_LEN + 4;

  /**
   * Allocate HEADER_LEN=128 bytes
   */
  public Header()
  {
    super( HEADER_LEN );
  }

  /**
   * Assign shortData and charBUffer
   * 
   */
  public Header(ByteBuffer byteBuffer)
  {
    super( byteBuffer );
  }

  /**
   * Allocate 128 bytes and assigned header string
   * 
   * @param buffer
   *          existing buffer to use
   * @param header
   *          text to assign to preceeding buffer param
   */
  public Header(ByteBuffer buffer, String header)
  {
    this( buffer );
    BufferUtils.copy( header, buffer );
  }

  /**
   * Allocate 128 bytes and assigned header string, little-endian storage
   * 
   * @param header
   */
  public Header(String header)
  {
    this();
    int i = 0;
    final int length = header.length();
    for ( ; i < length && i < TEXT_LEN; i++ )
    {
      buffer.put( (byte) header.charAt( i ) );
    }
    for ( ; i < TEXT_LEN; i++ )
    {
      buffer.put( (byte) ' ' );
    }
    buffer.putShort( MatFile.VERSION );
    buffer.putShort( MatFile.LITTLE_ENDIAN );
    buffer.flip();
  }

  @Override
  public ByteBuffer getBuffer()
  {
    return buffer;
  }

}