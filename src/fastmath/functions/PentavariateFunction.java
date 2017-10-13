package fastmath.functions;

public interface PentavariateFunction
{

  /**
   * Compute the value for the function.
   *
   * param v v-coordinate for which the function value should be computed.
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
  double value(double v, double w, double x, double y, double z);

}
