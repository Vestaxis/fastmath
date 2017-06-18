package fastmath.exceptions;

import fastmath.AbstractMatrix;

public class SingularFactorException extends FastMathException
{
  private final AbstractMatrix factor;

  private static final long serialVersionUID = 1L;

  /**
   * Constructor
   * 
   * @param msg
   * @param factor
   *          the singular factor
   */
  public SingularFactorException(String msg, AbstractMatrix factor)
  {
    super( msg );
    this.factor = factor;
  }

  /**
   * @return Returns the factor.
   */
  public AbstractMatrix getFactor()
  {
    return factor;
  }

}
