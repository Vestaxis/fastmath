package fastmath;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Reference: [Hitchens2002] -> Java NIO: Regular Expressions & High Performance
 * I/O, Ch.2, Ron Hitchens, 2002, O'Reilly
 * 
 * @author crow
 */
public class BufferUtils
{
  /**
   * High performance copy from one direct byte buffer to another
   * 
   * @param src
   * @param dst
   * 
   * @return dst to facilitate invocation chains
   */
  public static native ByteBuffer copy( ByteBuffer src, ByteBuffer dst );

  /**
   * @see [Hitchens2002] p.47: Accessing Unsigned Data
   */
  public static void putUnsignedInt( ByteBuffer buffer, long v )
  {
    buffer.putInt( (int) ( v & 0xffffffffl ) );
  }

  /**
   * @see [Hitchens2002] p.47: Accessing Unsigned Data
   * 
   * @param intBuffer
   * @return
   */
  public static void putUnsignedInt( ByteBuffer intBuffer, int byteOffset, long value )
  {
    intBuffer.putInt( byteOffset, (int) ( value & 0xffffffffl ) );
  }

  /**
   * @see [Hitchens2002] p.47: Accessing Unsigned Data
   */
  public static void putUnsignedShort( ByteBuffer buffer, int s )
  {
    buffer.putShort( (short) ( s & 0xffff ) );
  }

  /**
   * Allocates a direct order native byte buffer. Silly that it's not the
   * standard api.
   * 
   * @param numBytes
   * @return
   */
  public static ByteBuffer newNativeBuffer( int numBytes )
  {
    assert numBytes >= 0 : "size must be non-negative, was " + numBytes;
    return ByteBuffer.allocateDirect( numBytes ).order( ByteOrder.nativeOrder() );
  }

  public static IntBuffer newIntBuffer( int numInts )
  {
    return newNativeBuffer( numInts * 4 ).asIntBuffer();
  }

  public static FloatBuffer newFloatBuffer( int numValues )
  {
    return newNativeBuffer( numValues * 4 ).asFloatBuffer();
  }

  /**
   * Copys the elements of a char buffer to a byte buffer
   * 
   * Concurency: access must be externally synchronized
   * 
   * @param src
   * @param dest
   * 
   * @return dest for easy invocation chaining
   */
  public static ByteBuffer copy( CharBuffer src, ByteBuffer dest )
  {
    int length = src.length();
    int i;
    for ( i = 0; i < length; i++ )
    {
      dest.put( i, (byte) src.get( i ) );
    }
    for ( ; i < dest.limit(); i++ )
    {
      dest.put( i, (byte) 0 );
    }

    return dest;
  }

  /**
   * @see [Hitchens2002] p.47: Accessing Unsigned Data
   * 
   * @param buffer
   * @return
   */
  public static short getUnsignedByte( ByteBuffer buffer )
  {
    return (short) ( buffer.get() & (short) 0xff );
  }

  /**
   * @see [Hitchens2002] p.47: Accessing Unsigned Data
   * 
   * @param buffer
   * @return
   */
  public static int getUnsignedShort( ByteBuffer buffer )
  {
    return buffer.getShort() & 0xffff;
  }

  /**
   * @see [Hitchens2002] p.47: Accessing Unsigned Data
   * 
   * @param buffer
   * @return
   */
  public static long getUnsignedInt( ByteBuffer buffer )
  {
    return buffer.getInt() & 0xffffffffl;
  }

  /**
   * @see [Hitchens2002] p.47: Accessing Unsigned Data
   * 
   * @param doubleBuffer
   * @return
   */
  public static int getUnsignedShort( ShortBuffer shortBuffer, int index )
  {
    return shortBuffer.get( index ) & 0xffff;
  }

  /**
   * Get unsigned int from signed int buffer
   * 
   * @param intBuffer
   * @param index
   * 
   * @return long
   */
  public static long getUnsignedInt( ByteBuffer intBuffer, int index )
  {
    return intBuffer.getInt( index ) & 0xffffffffl;
  }

  /**
   * cast long to unsigned int, throwing exception in the event of overflow
   * 
   * @param x
   * @return
   * 
   * @throws IllegalArgumentException
   *           if x cannot be contained in a positive Integer
   */
  public static int toUnsignedInt( long x )
  {
    int cast = (int) x;
    if ( cast == x )
    {
      return cast;
    }
    else
    {
      throw new IllegalArgumentException( "Signed long " + x + " cannot be cast to positive signed integer" );
    }

  }

  /**
   * Copies a string to ByteBuffer, if byteBuffer overflows then not all of
   * string is copied
   * 
   * @param string
   * 
   * @param byteBuffer
   *          destination buffer is duplicated first so that position is not
   *          list
   * 
   * @return new byteBuffer
   */
  public static ByteBuffer copy( String string, ByteBuffer byteBuffer )
  {
    ByteBuffer dupe = byteBuffer.duplicate();
    for ( int i = 0; i < string.length() && dupe.hasRemaining(); i++ )
    {
      dupe.put( (byte) string.charAt( i ) );
    }
    return dupe;
  }

  /**
   * Pretty-print byte buffer in hex
   * 
   * @param buffer
   * @return
   */
  public static String print( ByteBuffer buffer )
  {
    StringBuffer sb = new StringBuffer();
    int lim = buffer.limit();
    for ( int i = 0; i < lim; i++ )
    {
      sb.append( String.format( "%x", buffer.get( i ) ) );
      if ( i % 8 == 7 )
      {
        sb.append( " " );
      }
    }
    return sb.toString().trim();
  }

  public static final int BYTES_PER_INTEGER = ( Integer.SIZE / Byte.SIZE );

  /**
   * Allocate a native direct byte buffer and return it as an IntBuffer
   * 
   * @param numInts
   * @return
   */
  public static IntBuffer allocateDirectIntBuffer( int numInts )
  {
    System.gc(); // Free Mem before new alloc
    return ByteBuffer.allocateDirect( numInts * BYTES_PER_INTEGER ).order( ByteOrder.nativeOrder() ).asIntBuffer();
  }

  /**
   * Turns an IntBuffer into a HashSet of integers
   * 
   * @param buffer
   * @return
   */
  public static HashSet<Integer> toHashSet( IntBuffer buffer )
  {
    int limit = buffer.limit();
    HashSet<Integer> ints = new HashSet<Integer>( limit );
    for ( int i = 0; i < limit; i++ )
    {
      ints.add( buffer.get( i ) );
    }
    return ints;
  }

  public static List<Integer> toList( IntBuffer buffer )
  {
    int limit = buffer.limit();
    List<Integer> list = new ArrayList<Integer>( limit );
    for ( int i = 0; i < limit; i++ )
    {
      list.add( buffer.get( i ) );
    }
    return list;
  }

  public static ByteBuffer toByteBuffer( IntBuffer buffer )
  {
    List<Integer> list = toList( buffer );
    return integerListToByteBuffer( list );
  }

  public static ByteBuffer integerListToByteBuffer( List<Integer> termIds )
  {
    ByteBuffer byteBuffer = ByteBuffer.allocate( termIds.size() * 4 ).order( ByteOrder.nativeOrder() );
    IntBuffer intBuffer = byteBuffer.asIntBuffer();
    for ( Integer termId : termIds )
    {
      intBuffer.put( termId );
    }
    return byteBuffer;
  }

  public static ByteBuffer arrayToBuffer( final byte[] bytes )
  {
    return ByteBuffer.wrap( bytes ).order( ByteOrder.nativeOrder() );
  }

  public static native long bufferAddress( Buffer buffer );

}
