package math.space;

public interface FiniteDimensionalSpace extends Space
{

  /**
   * @return the (integer) dimensionality of the space. TODO: A space having
   *         non-integer dimension is generalized by the so-called α-dimensional
   *         spaces
   */
  public int getN();
}
