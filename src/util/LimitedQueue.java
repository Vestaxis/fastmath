package util;

import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E>
{

  private static final long serialVersionUID = 1L;
  private int limit;

  public LimitedQueue(int limit)
  {
    this.limit = limit;
  }

  @Override
  public boolean add( E o )
  {
    super.add( o );
    while (size() > limit)
    {
      E removed = super.remove();
      System.out.println( "removed " + removed );
    }
    return true;
  }

  public int getLimit()
  {
    return limit;
  }

  public void setLimit( int limit )
  {
    this.limit = limit;
  }
}