package fastmath;

import static java.lang.Math.min;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

import com.sleepycat.persist.model.NotPersistent;
import com.sleepycat.persist.model.Persistent;

import fastmath.matfile.MiDouble;
import fastmath.matfile.Writable;

/**
 * Basic functionality of buffered classes
 */
@Persistent
public abstract class AbstractBufferedObject implements Writable
{

  @Override
  public void write( SeekableByteChannel channel ) throws IOException
  {
    writeBuffer( channel );
  }

  public transient ByteBuffer buffer;

  public AbstractBufferedObject()
  {

  }

  /**
   * Resizes the internal buffer, preserving contents if the new size is at
   * least as big as the existing present size
   * 
   * @param newSize
   *          number of doubles to hold
   * 
   *          TODO: This moves the backing buffer, if there are any views into
   *          this matrix then they will no longer point to this matrix and the
   *          coupling will be lost. Solution: Views should register themselves
   *          as listeners to buffer changes, e.g. BufferResizeListener
   * 
   *          CAUTION: This function uses a direct memory copy, so this means
   *          for row-major matrices the number of columns cannot change and
   *          vice versa.
   */
  protected void resizeBuffer( int prevSize, int newSize )
  {
    ByteBuffer newBuffer = BufferUtils.newNativeBuffer( newSize * MiDouble.BYTES );

    newBuffer.mark();
    if  ( buffer != null )
    {
    	newBuffer.put(buffer);
    }
    newBuffer.reset();
    buffer = newBuffer;
  }

  /**
   * Construct wrapper around ByteBuffer
   * 
   * @param buffer
   *          if null then getByteBuffer and getBuffer() will throw
   *          UnsupportedOperationException and thus should be overridden
   * 
   */
  public AbstractBufferedObject(final ByteBuffer buffer)
  {
    this.buffer = buffer;
  }

  public AbstractBufferedObject(int bufferSize)
  {
    this( BufferUtils.newNativeBuffer( bufferSize ) );
  }

  public void writeBuffer( SeekableByteChannel channel ) throws IOException
  {
    buffer.mark();
    while (buffer.hasRemaining())
    {
      channel.write( buffer );
    }
    buffer.reset();
  }

  public ByteBuffer getBuffer()
  {
    return buffer;
  }

  /**
   * Return the size of this element in bytes, excluding header and padding
   * bytes if written from this pos Note: this will always be the same
   * regardless of position, except for Matrix types
   * 
   * @param pos
   *          TODO!
   * 
   * @return TODO!
   */
  public long numBytes( long pos )
  {
    assert getBuffer() != null : "buffer is null. Class=" + getClass();
    return getBuffer().capacity();
  }

  public int limit()
  {
    assert getBuffer() != null : "buffer is null. Class=" + getClass();
    return getBuffer().limit();
  }

  public int capactity()
  {
    return getBuffer().capacity();
  }

  public int position()
  {
    return getBuffer().position();
  }

}
