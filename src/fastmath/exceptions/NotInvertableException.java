package fastmath.exceptions;

public class NotInvertableException extends FastMathException
{
  private static final long serialVersionUID = 1L;

  private final int factor;

  /**
   * Construct a NotInvertableException exception
   * 
   * @param msg
   * @param factor
   *          the factor that was 0
   */
  public NotInvertableException(int factor)
  {
    super( "the (" + factor + "," + factor + ") element of the factor U or L is zero, and the inverse could not be computed" );
    this.factor = factor;
  }

  /**
   * @return Returns the factor.
   */
  public int getFactor()
  {
    return factor;
  }

}
