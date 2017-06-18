package fastmath.arb;

import static fastmath.Console.println;

import com.sun.jna.Native;

import junit.framework.TestCase;

public class DirichletGroupTest extends TestCase
{

  public static void main( String args[] )
  {
    testStuff();
  }

  public static void testStuff()
  {
    println( "sizeof NMod=" + Native.getNativeSize( NMod.class ) + " ptr=" );
    println( "sizeof DirichletCharacter=" + Native.getNativeSize( DirichletCharacter.class ) );
    println( "sizeof DirichletGroupValue=" + Native.getNativeSize( DirichletGroupValue.class ) );
    println( "sizeof DirichletPrimeGroupValue " + Native.getNativeSize( DirichletPrimeGroupValue.class ) );
    DirichletGroup G = new DirichletGroup();

  }

}
