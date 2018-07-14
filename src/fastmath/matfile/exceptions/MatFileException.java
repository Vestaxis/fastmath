package fastmath.matfile.exceptions;

public class MatFileException extends Exception
{
  private static final long serialVersionUID = 1L;

  public MatFileException(Exception e)
  {
    super( e );
  }

  /**
   * Wrap exception, new exception has wrapped exception msg plus optional msg
   * appended to it
   * 
   * @param e
   * @param msg
   *          optional msg to append
   */
  public MatFileException(Exception e, String msg)
  {
    super( ( msg == null ) ? e.getMessage() : ( e.getMessage() + ":" + msg ) );
    initCause( e );
  }

  public MatFileException(String string)
  {
    super( string );
  }

}
