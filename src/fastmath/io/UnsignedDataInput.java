package fastmath.io;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public interface UnsignedDataInput
{

  InputStream getInputStream();

  BigInteger readUnsignedLong() throws IOException;

  double[] readDoubles( int len ) throws IOException;

  long readUnsignedInt() throws IOException;

}
