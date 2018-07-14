package fastmath.functions;

public interface QuadvariateFunction
{

  /**
   * Compute the value for the function.
   *
   * @param w
   *          w-coordinate for which the function value should be computed.
   * @param x
   *          x-coordinate for which the function value should be computed.
   * @param y
   *          y-coordinate for which the function value should be computed.
   * @param z
   *          z-coordinate for which the function value should be computed.
   * 
   * @return the value.
   */
  double value(int m, int k, double y, double z);

}
