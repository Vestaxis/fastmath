package fastmath.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * This class is a data input stream for little-endian data.
 * <p>
 * 
 * LEInputStream tries very hard to be like {@link java.io.DataInputStream
 * java.io.DataInputStream}which describes:
 * <p>
 * <ul>
 * A data input stream lets an application read primitive Java data types from
 * an underlying input stream in a machine-independent way. An application uses
 * a data output stream to write data that can later be read by a data input
 * stream.
 * </ul>
 * <p>
 * 
 * A <a href="http://info.astrian.net/jargon/terms/l/little-endian.html">little-
 * endian </a> data input stream lets an application read primitive Java data
 * types from an underlying input stream in a little-endian way.
 * <p>
 * 
 * Additionally, this class implements {@link UnsignedDataInput
 * UnsignedDataInput}. Java chooses not to provide any unsigned data types, so
 * unsigned values are are cast to the next larger integral type (or class),
 * thereby assuring that the values are never negative.
 * <p>
 * 
 * Use {@link LittleEndianOutputStream LEOutputStream}to write unsigned
 * little-endian data to a stream.
 * 
 * @see java.io.DataInput
 * @see java.io.DataInputStream
 * @see java.io.DataOutputStream
 * @see LittleEndianOutputStream
 * @see UnsignedDataInput
 */

public class BigEndianInputStream extends DataInputStream implements UnsignedDataInput
{
  byte[] b = new byte[9];

  @Override
  public InputStream getInputStream()
  {
    return this;
  }

  /**
   * Primitive <code>long</code> value 2 <sup>32 </sup>- 1, or 4294967295.
   * <p>
   * <b>Note: </b>The type of this constant is <code>long</code> while the value
   * represented is intended to be an unsigned <code>int</code>, hence the name.
   */
  public static final long UNSIGNED_INT_MAX_VALUE = 0x00000000ffffffffL;

  /**
   * <code>BigInteger</code> value 2 <sup>64 </sup>- 1, or 18446744073709551615.
   * <p>
   * <b>Note: </b>The type of this constant is <code>BigInteger</code> while the
   * value represented is intended to be an unsigned <code>long</code>, hence
   * the name.
   */
  public static final BigInteger UNSIGNED_LONG_MAX_VALUE = new BigInteger( "18446744073709551615" );

  /**
   * Primative <code>int</code> value 2 <sup>16 </sup>- 1, or 65535.
   * <p>
   * <b>Note: </b>The type of this constant is <code>int</code> while the value
   * represented is intended to be an unsigned <code>short</code>, hence the
   * name.
   */
  public static final int UNSIGNED_SHORT_MAX_VALUE = 0x0000ffff;

  /**
   * Creates a DataInput stream and saves its argument, the input stream in, for
   * later use.
   * <p>
   * After construction, data can be read in the same manner a
   * {@link java.io.DataInputStream DataInputStream}can be used.
   * <p>
   * 
   * @param in
   *          - the input stream
   */
  public BigEndianInputStream(InputStream in)
  {
    super( in );
  }

  /**
   * Returns int for the 32 bits read, treated as unsigned. Valid values range
   * from 0 to 4294967295 (2 <sup>32 </sup>- 1), thus requiring a long return
   * type to accurately represent the value. A negative value is never returned.
   * <p>
   * 
   * @throws IOException
   *           if there is a problem with the underlying input stream
   */
  @Override
  public long readUnsignedInt() throws IOException
  {
    long v = readInt();
    long b4 = ( v & 0xff000000L ) >> 24;
    long b3 = ( v & 0x00ff0000L ) >> 16;
    long b2 = ( v & 0x0000ff00L ) >> 8;
    long b1 = ( v & 0x000000ffL );
    long newLong = ( b4 << 24 ) + ( b3 << 16 ) + ( b2 << 8 ) + b1;

    if ( newLong < 0 || newLong > UNSIGNED_INT_MAX_VALUE )
    {
      throw new RuntimeException( "postcondition failed" );
    }
    return newLong;
  }

  /**
   * Returns BigInteger for the 64 bits read, treated as unsigned. Valid values
   * range from 0 to 18446744073709551615 (2 <sup>64 </sup>- 1), thus requiring
   * BigInteger return type to accurately represent the value. A negative value
   * is never returned.
   * <p>
   * 
   * @throws IOException
   *           if there is a problem with the underlying input stream
   */
  @Override
  public BigInteger readUnsignedLong() throws IOException
  {
    // TODO: unit test
    b[0] = 0x00;
    readFully( b, 1, 8 );
    BufferTools.reverseBytes( b, 1, 8 );
    BigInteger bi = new BigInteger( b );
    if ( bi.compareTo( UNSIGNED_LONG_MAX_VALUE ) == 1 )
    {
      throw new RuntimeException( "postcondition failed" );
    }
    return bi;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.crowlogic.io.UnsignedDataInput#readDoubles(int)
   */
  @Override
  public double[] readDoubles( int len ) throws IOException
  {
    // TODO: optimize

    double[] d = new double[len];

    for ( int i = 0; i < len; i++ )
    {
      d[i] = readDouble();
    }

    return d;
  }
}
