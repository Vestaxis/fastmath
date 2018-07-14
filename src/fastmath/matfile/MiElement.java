package fastmath.matfile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;

import fastmath.AbstractBufferedObject;
import fastmath.BufferUtils;

public abstract class MiElement extends AbstractBufferedObject implements NamedWritable
{

  @Override
  public String getName()
  {
    return null;
  }

  public void write( SeekableByteChannel channel ) throws IOException
  {
    writeHeader( channel );
    writeBuffer( channel );
  }

  public MiElement(ByteBuffer buffer)
  {
    super( buffer );
  }

  /**
   * Construct element of specific size
   * 
   * @param bytes
   */
  public MiElement(int bytes)
  {
    super( bytes );
  }

  /**
   * Return total size, including padding and header bytes, if written from pos
   * 
   * @param pos
   *          TODO!
   * 
   * @return TODO!
   */
  public long totalSize( long pos )
  {
    long startPos = pos;

    pos += headerSize();
    pos += numBytes( pos );

    return MatFile.pad( pos ) - startPos;
  }

  /**
   * Returns the number of bytes that will be padded if written at pos
   * 
   * @param pos
   *          position in file
   * 
   * @return the offset required for storage format
   */
  public int padding( long pos )
  {
    pos += headerSize();
    pos += numBytes( pos );

    return (int) ( MatFile.pad( pos ) - pos );
  }

  /**
   * 
   * @return an integer representing the data type
   */
  public abstract int getDataType();

  /**
   * 
   * @return false since it only saves 4 bytes per variable and the spec says it
   *         is option for write-compatibility
   */
  protected final boolean compressedHeader()
  {
    return false;
  }

  /**
   * Get the size of the header in bytes
   * 
   * @return 4 if the header is compressed, otherwise 8
   */
  public final int headerSize()
  {
    return compressedHeader() ? 4 : 8;
  }

  /**
   * Writes 4 or 8 byte header depending upon if its compressable or not
   * 
   * @param destBuffer
   *          TODO
   * 
   * @return position
   * 
   * @throws IOException
   */
  protected final long writeHeader( SeekableByteChannel channel ) throws IOException
  {
    ByteBuffer headerBuffer = ByteBuffer.allocateDirect( headerSize() ).order( ByteOrder.nativeOrder() );

    {
      long bytes = numBytes( channel.position() );
      if ( !compressedHeader() )
      {
        headerBuffer.putInt( getDataType() );
        BufferUtils.putUnsignedInt( headerBuffer, bytes );
      }
      else
      {
        if ( headerBuffer.order() == ByteOrder.LITTLE_ENDIAN )
        {
          headerBuffer.putShort( (short) getDataType() );
          BufferUtils.putUnsignedShort( headerBuffer, (int) bytes );
        }
        else
        {
          throw new UnsupportedOperationException( "Native bigendian support needs work" );
          // BufferUtils.writeUnsignedShort( byteBuffer, (int) sizeInBytes( pos
          // ) );
          // byteBuffer.putShort( (short) getDataType() );
        }
      }
    }
    headerBuffer.flip();
    channel.write( headerBuffer );
    return channel.position() + headerSize();
  }

}
