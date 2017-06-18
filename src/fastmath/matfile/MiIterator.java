package fastmath.matfile;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import fastmath.BufferUtils;
import fastmath.io.ByteUtils;
import fastmath.matfile.exceptions.MatFileParsingException;

public class MiIterator implements Iterator<MiElement>, Iterable<MiElement>
{

  private Exception exception;

  private int dataType = 0;

  private long size = 0;

  private boolean hasMore = true;

  private boolean hasRead = false;

  private final ByteBuffer buffer;

  private MatFile matFile;

  public MiIterator(MatFile matFile, ByteBuffer buffer)
  {
    this.buffer = buffer;
    this.matFile = matFile;
  }

  public int getDataType()
  {
    return dataType;
  }

  public long getNumberOfBytes()
  {
    return size;
  }

  private long firstSize;

  private int firstDataType;

  private boolean smallDataTypeFormat;

  private void readElementHeader() throws IOException
  {
    hasRead = true;

    // When reading a MAT-file, you can tell if you are
    // processing a compressed data element by comparing the value of the first
    // two bytes of the tag with the value zero (0). If these two bytes are not
    // zero, the tag uses the compressed format. When writing a MAT-file, use of
    // the compressed data element format is optional.
    buffer.mark();

    int dataTypeBeforeSwap = buffer.getInt();
    dataType = ByteUtils.swap( dataTypeBeforeSwap );
    size = ByteUtils.swap( (int) BufferUtils.getUnsignedInt( buffer ) );

    smallDataTypeFormat = false;
    if ( ( dataType & 0xffff0000 ) != 0 )
    {
      smallDataTypeFormat = true;
      buffer.reset();
      firstSize = size;
      short unswappedSize = (short) BufferUtils.getUnsignedShort( buffer );

      size = ByteUtils.swap( unswappedSize );

      firstDataType = dataType;
      short unswappedDatatype = (short) buffer.getShort();
      dataType = ByteUtils.swap( unswappedDatatype );
    }

    if ( size < 0 )
    {
      throw new IllegalArgumentException( "parsing error, size=" + size + " firstSize=" + firstSize + " dataType+" + dataType + " firstDataType="
                                          + firstDataType );
    }
  }

  @Override
  public boolean hasNext()
  {
    if ( !hasMore )
    {
      return false;
    }
    else if ( buffer.remaining() == 0 )
    {
      return hasMore = false;
    }
    else if ( !hasRead )
    {
      try
      {
        hasMore = false;
        readElementHeader();
        hasMore = true;
      }
      catch( Exception e )
      {
        exception = e;
      }
    }

    return hasMore;
  }

  /**
   * A more flexible version of this{@link #next()}
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public <E extends MiElement> E nextElement()
  {
    return (E) next();
  }

  @Override
  public MiElement next() throws NoSuchElementException
  {
    if ( !hasRead )
    {
      hasNext();
    }

    if ( !hasMore )
    {
      NoSuchElementException nse = new NoSuchElementException();

      if ( exception != null )
      {
        nse.initCause( exception );
      }

      throw nse;
    }

    MiElement obj = null;
    int padding = 0;
    long debugOffset = 0;
    int paddedOffset = 0;

    try
    {
      int sliceSize = BufferUtils.toUnsignedInt( size );

      ByteBuffer chunk = buffer.slice();
      int chunkSize = chunk.limit();
      if ( chunkSize < sliceSize )
      {
        throw new MatFileParsingException( format( "sliceSize=%d but remaining chunk is only %d", sliceSize, chunkSize ) );
      }
      ByteBuffer slice = (ByteBuffer) chunk.limit( sliceSize );
      int offset = sliceSize % 8;
      padding = offset != 0 ? 8 - offset : 0;
      debugOffset = MatFile.pad( sliceSize );
      paddedOffset = buffer.position() + sliceSize + padding;

      buffer.position( paddedOffset );

      switch( dataType )
      {
      case MiInt8.miINT8:
        obj = new MiInt8( slice );
        break;
      case MiUInt8.miUINT8:
        obj = new MiUInt8( slice );
        break;
      case MiUInt16.miUINT16:
        obj = new MiUInt16( slice );
        break;
      case MiMatrix.miMATRIX:
        obj = new MiMatrix( matFile, slice, smallDataTypeFormat );
        break;
      case MiUInt32.miUINT32:
        obj = new MiUInt32( slice );
        break;
      case MiInt32.miINT32:
        obj = new MiInt32( slice );
        break;
      case MiDouble.miDOUBLE:
        obj = new MiDouble( slice );
        break;
      default:
        int fileOffset = paddedOffset + MatFile.HEADER_OFFSET;

        throw new NoSuchElementException( format( "padding=%d padding2=%d size=%d dataType=%d, fileOffset=0x%x: %s",
                                                  paddedOffset,
                                                  debugOffset,
                                                  size,
                                                  dataType,
                                                  fileOffset,
                                                  matFile ) );
      }

    }
    catch( Exception e )
    {
      if ( e instanceof NoSuchElementException )
      {
        throw (NoSuchElementException) e;
      }
      else
      {
        NoSuchElementException nse = new NoSuchElementException( e.getMessage() );
        nse.initCause( e );
        hasMore = false;
        throw nse;
      }
    }

    try
    {
      hasMore = false;

      if ( buffer.remaining() > 0 )
      {
        readElementHeader();
        hasMore = true;
      }
    }
    catch( Exception e )
    {
      exception = e;
    }

    return obj;
  }

  @Override
  public void remove() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<MiElement> iterator()
  {
    return this;
  }
}
