package fastmath;

import junit.framework.TestCase;

public class IntVectorTest extends TestCase
{
  public void testReverse()
  {
    IntVector x = new IntVector( 2, 4, 6, 8, 10 );
    IntVector y = x.reverse();
    IntVector z = new IntVector( 10, 8, 6, 4, 2);
    assertEquals(z, y);
  }

  public void testSetAndGet()
  {
    IntVector v = new IntVector(1);
    v.set(0, 12);
    assertEquals( 12, v.get(0));
  }
  
  public void testExtend()
  {
    IntVector v = new IntVector(0);
    v = v.extend(1);
    v.set(0, 12);
    assertEquals( 12, v.get(0));
  }
  
  public void testCopyAndAppend()
  {
    IntVector v = new IntVector(0);
    v = v.copyAndAppend(12);
    assertEquals( 12, v.get(0));
  }
  

}
