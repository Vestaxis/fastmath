package stochastic.order;

import math.Set;

/**
 * an ordered pair (a, b) is a pair of mathematical objects. The order in which
 * the objects appear in the pair is significant: the ordered pair (a, b) is
 * different from the ordered pair (b, a) unless a = b. (In contrast, the
 * unordered pair {a, b} equals the unordered pair {b, a}.)
 * 
 * Ordered pairs are also called 2-tuples, or sequences of length 2; ordered
 * pairs of scalars are also called 2-dimensional vectors. The entries of an
 * ordered pair can be other ordered pairs, enabling the recursive definition of
 * ordered n-tuples (ordered lists of n objects). For example, the ordered
 * triple (a,b,c) can be defined as (a, (b,c)), i.e., as one pair nested in
 * another.
 * 
 * In the ordered pair (a, b), the object a is called the first entry, and the
 * object b the second entry of the pair. Alternatively, the objects are called
 * the first and second coordinates, or the left and right projections of the
 * ordered pair.
 * 
 * Cartesian products and binary relations (and hence functions) are defined in
 * terms of ordered pairs.
 * 
 * @author crow
 * 
 * @param <LEFT>
 *          ☾
 * @param <RIGHT>
 *          ☽
 */
public class OrderedPair<LEFT extends Set, RIGHT extends Set> implements OrderedSet
{
  public OrderedPair(LEFT left, RIGHT right)
  {
    this.left = left;
    this.right = right;
  }

  private LEFT left;

  private RIGHT right;

  public LEFT getLeft()
  {
    return left;
  }

  public void setLeft( LEFT left )
  {
    this.left = left;
  }

  public RIGHT getRight()
  {
    return right;
  }

  public void setRight( RIGHT right )
  {
    this.right = right;
  }

}