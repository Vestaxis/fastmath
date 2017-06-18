package stochastic.order;

import math.Set;

/**
 * 
 * @param <LEFT>
 * @param <MIDDLE>
 * @param <RIGHT>
 */
public class OrderedTriple<LEFT extends Set, MIDDLE extends Set, RIGHT extends Set> extends OrderedPair<LEFT, RIGHT>
{
  public OrderedTriple(LEFT left, RIGHT right, MIDDLE middle)
  {
    super( left, right );
    this.middle = middle;
  }

  protected MIDDLE middle;

  public MIDDLE getMiddle()
  {
    return middle;
  }

  public void setMiddle( MIDDLE middle )
  {
    this.middle = middle;
  }
}
