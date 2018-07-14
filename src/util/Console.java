package util;

import static java.lang.System.out;

public class Console
{

  public static void println( Object str )
  {
    out.println( str );
  }

  public static void printf( String format, Object... args )
  {
    out.format( format, args );
  }

}
