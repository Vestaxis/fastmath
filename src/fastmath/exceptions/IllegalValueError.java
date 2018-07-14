package fastmath.exceptions;

public class IllegalValueError extends FastMathException
{
  private static final long serialVersionUID = 1L;

  private final int param;

  /**
   * Construct an IllegalValueError
   * 
   * @param msg
   * @param p
   *          param which was illegal
   */
  public IllegalValueError(String msg, int p)
  {
    super( msg + ": param " + p + " had an illegal value" );
    this.param = p;
  }

  /**
   * @return Returns the param.
   */
  public int getParam()
  {
    return param;
  }
}
