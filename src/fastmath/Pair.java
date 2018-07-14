package fastmath;

import java.io.Serializable;

public class Pair<A, B> implements Serializable
{
  private static final long serialVersionUID = 1L;

  public A left;

  public B right;


  public Pair()
  {
  }

  public Pair(A firstValue, B secondValue)
  {
    this.left = firstValue;
    this.right = secondValue;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals( Object obj )
  {
    if ( !( obj instanceof Pair ) )
    {
      return false;
    }
    Pair<A, B> otherPair = (Pair<A, B>) obj;
    return leftEquals( otherPair ) && rightEquals( otherPair );
  }

  public boolean leftEquals( Pair<A, B> otherPair )
  {
    return left != null ? left.equals( otherPair.left ) : otherPair.left == null;
  }

  /**
   * @return the left elememet
   */
  public final A getLeft()
  {
    return left;
  }

  /**
   * @return the right element
   */
  public final B getRight()
  {
    return right;
  }

  /**
   * @return first.hashCode() * second.hashCode()
   */
  @Override
  public int hashCode()
  {
    return safeHashCode( left ) + 31 * safeHashCode( right );
  }

  /**
   * 
   * @param obj
   * 
   * @return 1 if obj == null otherwise obj.hashCode()
   */
  protected final int safeHashCode( Object obj )
  {
    return ( obj == null ? 1 : obj.hashCode() );
  }

  public boolean rightEquals( Pair<A, B> otherPair )
  {
    return right != null ? right.equals( otherPair.right ) : otherPair.right == null;
  }

  /**
   * @param first
   *          the first to set
   */
  public final void setLeft( A first )
  {
    this.left = first;
  }

  /**
   * @param second
   *          the second to set
   */
  public final void setRight( B second )
  {
    this.right = second;
  }

  @Override
  public String toString()
  {
    return String.format( "[%s,%s]", left, right );
  }

}