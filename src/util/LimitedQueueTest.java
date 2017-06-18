package util;

import junit.framework.TestCase;

/**
 * TODO: make tests more robust
 * 
 * @author crow
 *
 */
public class LimitedQueueTest extends TestCase
{

  public void testLimitedQueue()
  {
    LimitedQueue<Integer> queue = new LimitedQueue<>( 3 );
    queue.add( 1 );
    assertEquals( 1, queue.size() );
    queue.add( 1 );
    assertEquals( 2, queue.size() );
    queue.add( 1 );
    assertEquals( 3, queue.size() );
    queue.add( 1 );
    assertEquals( 3, queue.size() );
    queue.add( 1 );
    assertEquals( 3, queue.size() );
  }

}
