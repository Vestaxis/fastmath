package fastmath.arb;

import com.sun.jna.Native;

import junit.framework.TestCase;

public class MPFRTest extends TestCase
{

  public static void testSize()
  {
    assertEquals( 32, Native.getNativeSize( MPFRValue.class ) );
  }

}
