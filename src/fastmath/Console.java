package fastmath;

import static java.lang.System.out;

public class Console
{

  public static void println( String str )
  {
    out.println( str );
  }

  public static void printf( String format, Object... args )
  {
    out.format( format, args );
  }

}
