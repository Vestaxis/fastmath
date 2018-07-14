package fastmath.matfile.exceptions;

public class MatFileParsingException extends MatFileException
{

  public MatFileParsingException(Exception e, String msg)
  {
    super( e, msg );
  }

  public MatFileParsingException(Exception e)
  {
    super( e );
  }

  public MatFileParsingException(String string)
  {
    super( string );
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
