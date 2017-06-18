package fastmath.io;

import java.io.IOException;
import java.math.BigInteger;

public interface UnsignedDataOutput
{

  void writeUnsignedInt( long v ) throws IOException;

  boolean isLE();

  void writeDoubles( double[] d ) throws IOException;

  void writeUnsignedLong( BigInteger value ) throws IOException;

  void writeUnsignedShort( int v ) throws IOException;

}
