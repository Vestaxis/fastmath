package fastmath.arb;

import static fastmath.Console.println;

import com.sun.jna.Native;

import junit.framework.TestCase;

public class ARFTest extends TestCase
{

  public void testDoubles()
  {
    ARF arf = new ARF();
    arf.setValue( 6.9 );
    assertEquals( arf.getValue(), 6.9 );
    println( "sizeof ARFValue is " + Native.getNativeSize( ARFValue.class ) );
  }
  
 
  
}
