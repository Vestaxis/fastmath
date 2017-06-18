package fastmath.arb;

import static java.lang.System.out;

import com.sun.jna.Native;

import junit.framework.TestCase;

public class MantissaTest extends TestCase
{

  public static void testSize()
  {
    final int mantissaValueSize = Native.getNativeSize( MantissaValue.class );
    out.println( "MantissaValue size=" + mantissaValueSize );
    assertEquals( 16, mantissaValueSize );
  }

}
