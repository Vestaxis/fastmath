package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fastmath.Pair;

public class ThreadLocalDateFormat extends ThreadLocal<Pair<Date, DateFormat>>
{
  private final String formatStr;

  public static final ThreadLocalDateFormat dFormat = new ThreadLocalDateFormat( "yyyy-MM-dd" );

  public static final ThreadLocalDateFormat sFormat = new ThreadLocalDateFormat( "yyyy-MM-dd kk:mm:ss" );

  public static final ThreadLocalDateFormat msFormat = new ThreadLocalDateFormat( "yyyy-MM-dd kk:mm:ss.SSS" );

  public static final ThreadLocalDateFormat hmsFormat = new ThreadLocalDateFormat( "kk:mm:ss" );

  public static final ThreadLocalDateFormat hmsMillisFormat = new ThreadLocalDateFormat( "kk:mm:ss.SSS" );

  public static final String formatD( long time )
  {
    return dFormat.format( time );
  }

  public static final String formatD( Date time )
  {
    return dFormat.format( time );
  }

  public static final String formatS( long time )
  {
    return sFormat.format( time );
  }

  public static final String formatS( Date time )
  {
    return sFormat.format( time );
  }

  public static final String formatMS( long time )
  {
    return msFormat.format( time );
  }

  public static final String formatMS( Date time )
  {
    return msFormat.format( time );
  }

  public ThreadLocalDateFormat(String format)
  {
    this.formatStr = format;
  }

  @Override
  protected Pair<Date, DateFormat> initialValue()
  {
    return new Pair<Date, DateFormat>( new Date(), new SimpleDateFormat( formatStr ) );
  }

  public String format( Date date )
  {
    return get().right.format( date );
  }

  public String format( long time )
  {
    Pair<Date, DateFormat> pair = get();
    pair.left.setTime( time );
    return pair.right.format( pair.left );
  }

  public Date parse( String timeString ) throws ParseException
  {
    return get().right.parse( timeString );
  }

}
