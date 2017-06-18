package fastmath.arb;

import static fastmath.Console.println;

import com.sun.jna.Native;

import junit.framework.TestCase;

public class NModTest extends TestCase
{

  public static void testStuff()
  {
    NMod nmod = new NMod( 1 );
    println( "nmod test " + Native.getNativeSize( NMod.class ) );
  }

}
