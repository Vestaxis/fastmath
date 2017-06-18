package fastmath.arb;

import static java.lang.System.out;

import com.sun.jna.Native;

import junit.framework.TestCase;

public class MPFRTest extends TestCase
{

  public static void testSize()
  {
    assertEquals( 32, Native.getNativeSize( MPFRValue.class ) );
  }

}
