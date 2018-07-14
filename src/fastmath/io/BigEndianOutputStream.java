package fastmath.io;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class BigEndianOutputStream extends DataOutputStream implements DataOutput, UnsignedDataOutput
{

  /**
   * <code>BigInteger</code> value 2 <sup>64 </sup>- 1 or 18446744073709551615
   */
  public static final BigInteger UNSIGNED_LONG_MAX_VALUE = new BigInteger( "18446744073709551615" );

  /**
   * Primitive <code>long</code> value 2 <sup>32 </sup>- 1 or 4294967295
   */
  public static final long UNSIGNED_INT_MAX_VALUE = 0x00000000ffffffffL;

  /**
   * Primative <code>int</code> value 2 <sup>16 </sup>- 1 or 65535
   */
  public static final int UNSIGNED_SHORT_MAX_VALUE = 0x0000ffff;

  /**
   * @uml.property name="_dataOutputStream"
   */
  private DataOutputStream _dataOutputStream;

  /**
   * Creates a DataOutput stream and saves its argument, the output stream
   * outputStream, for later use. After construction, data can be written in the
   * same manner a {@link java.io.DataOutputStream DataOutputStream}can be used.
   * <p>
   * 
   * @param outputStream
   *          - the output stream
   */
  public BigEndianOutputStream(OutputStream outputStream)
  {
    super( outputStream );

    // if (outputStream instanceof DataOutputStream) {
    // _dataOutputStream = (DataOutputStream)outputStream;
    // }
    // else {
    // _dataOutputStream = new DataOutputStream(outputStream);
    // }
  }

  /**
   * Closes this output stream and releases any system resources associated with
   * this stream.
   * <p>
   * 
   * @throws IOException
   *           if there is a problem with the underlying output stream.
   */
  @Override
  public void close() throws IOException
  {
    _dataOutputStream.close();
  }

  /**
   * Flushes this output stream and forces any buffered output bytes to be
   * written out.
   * <p>
   * 
   * @throws IOException
   *           if there is a problem with the underlying output stream.
   */
  @Override
  public void flush() throws IOException
  {
    _dataOutputStream.flush();
  }

  /**
   * Writes 32 bits of data, the bottom half (4 bytes) of the long argument.
   * <p>
   * Use <a href="#UNSIGNED_INT_MAX_VALUE">UNSIGNED_INT_MAX_VALUE </a> when
   * neccesary.
   * <p>
   * 
   * @throws IllegalArgumentException
   *           if argument <code>value</code> is out of the range 0 to
   *           4294967295 (2 <sup>32 </sup>- 1).
   * @throws IOException
   *           if there is a problem with the underlying output stream.
   */
  @Override
  public void writeUnsignedInt( long v ) throws IOException
  {
    if ( v < 0 || v > UNSIGNED_INT_MAX_VALUE )
    {
      throw new IllegalArgumentException( "argument out of range for unsigned int" );
    }
    long b1 = ( v & 0xff000000L ) >> 24;
    long b2 = ( v & 0x00ff0000L ) >> 16;
    long b3 = ( v & 0x0000ff00L ) >> 8;
    long b4 = v & 0x000000ffL;
    write( (int) b1 );
    write( (int) b2 );
    write( (int) b3 );
    write( (int) b4 );
  }

  /**
   * Writes 16 bits of data, the bottom half (2 bytes) of the int argument.
   * <p>
   * Use <a href="#UNSIGNED_SHORT_MAX_VALUE">UNSIGNED_SHORT_MAX_VALUE </a> when
   * neccesary.
   * <p>
   * 
   * @throws IllegalArgumentException
   *           if argument <code>value</code> is out of the range 0 to 65535 (2
   *           <sup>16 </sup>- 1).
   * @throws IOException
   *           if there is a problem with the underlying output stream.
   */
  @Override
  public void writeUnsignedShort( int v ) throws IOException
  {
    if ( v < 0 || v > UNSIGNED_SHORT_MAX_VALUE )
    {
      throw new IllegalArgumentException( "int argument out of range for unsigned short" );
    }
    long b1 = ( v & 0xff00L ) >> 8;
    long b2 = v & 0x00ffL;
    write( (int) b1 );
    write( (int) b2 );
  }

  /**
   * Write 64 bits of data, 8 bytes of the BigInteger value.
   * <p>
   * Use <a href="#UNSIGNED_LONG_MAX_VALUE">UNSIGNED_LONG_MAX_VALUE </a> when
   * neccesary.
   * <p>
   * 
   * @throws IllegalArgumentException
   *           if argument <code>value</code> is out of the range 0 to
   *           18446744073709551615 (2 <sup>64 </sup>- 1).
   * @throws IOException
   *           if there is a problem with the underlying output stream.
   */
  @Override
  public void writeUnsignedLong( BigInteger value ) throws IOException
  {
    if ( value.compareTo( UNSIGNED_LONG_MAX_VALUE ) == -1 || value.compareTo( BigInteger.ZERO ) == -1 )
    {
      throw new IllegalArgumentException( "BigInteger argument out of bounds for unsigned long" );
    }
    byte[] buffer = value.toByteArray();
    if ( buffer.length > 9 )
    {
      throw new RuntimeException( "length = " + buffer.length );
    }
    if ( buffer.length == 9 && buffer[0] != (byte) 0x01 )
    {
      throw new RuntimeException( "buffer[0] = " + buffer[0] );
    }
    BufferTools.reverseBytes( buffer, 1, 8 );
    write( buffer, 1, 8 );
  }

  @Override
  public boolean isLE()
  {
    return false;
  }

  @Override
  public void writeDoubles( double[] d ) throws IOException
  {
    byte[] bytes = new byte[d.length * 8];
    int pos = 0;

    for ( double dval : d )
    {
      long val = Double.doubleToLongBits( dval );

      for ( int i = 0; i < 8; i++ )
      {
        bytes[pos++] = (byte) val;
        val = val >> 8;
      }
    }

    write( bytes );
  }

}
