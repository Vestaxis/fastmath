package util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import fastmath.Vector;
import stochastics.annotations.Units;

public class DateUtils
{

  public final static ThreadLocalDateFormat YMDFormat = new ThreadLocalDateFormat( "yyyy-MM-dd" );

  final static ThreadLocalDateFormat millisecondTimeFormat = new ThreadLocalDateFormat( "HH:mm:ss.SSS" );


  /**
   * @param date
   *          input date
   * @param hour
   *          24-hour format
   * @param min
   *          minute
   * @return Date with only year-month-day-hour-min set
   */
  public static Date setHoursAndMins( Date date, int hour, int min )
  {
    Calendar cal = new GregorianCalendar();
    cal.clear();
    cal.setTime( date );
    cal.set( Calendar.HOUR_OF_DAY, hour );
    cal.set( Calendar.MINUTE, min );
    cal.set( Calendar.SECOND, 0 );
    cal.set( Calendar.MILLISECOND, 0 );
    return cal.getTime();
  }
  
  public static Date getMidnight( int year, int month, int day )
  {
    Calendar cal = new GregorianCalendar();
    cal.clear();
    cal.set( Calendar.YEAR, year );
    cal.set( Calendar.MONTH, month );
    cal.set( Calendar.DAY_OF_MONTH, day );
    cal.set( Calendar.HOUR_OF_DAY, 0 );
    cal.set( Calendar.MINUTE, 0 );
    cal.set( Calendar.SECOND, 0 );
    cal.set( Calendar.MILLISECOND, 0 );    
    return cal.getTime();
  }

  /**
   * @param date1
   * @param date2
   * @return true if date1 and date2 are on the same year, month, and day
   */
  public static boolean isOnSameDay( Date date1, Date date2 )
  {
    Calendar cal1 = new GregorianCalendar();
    cal1.setTime( date1 );
    Calendar cal2 = new GregorianCalendar();
    cal2.setTime( date2 );
    return cal1.get( Calendar.YEAR ) == cal2.get( Calendar.YEAR ) && cal1.get( Calendar.MONTH ) == cal2.get( Calendar.MONTH )
           && cal1.get( Calendar.DAY_OF_MONTH ) == cal2.get( Calendar.DAY_OF_MONTH );
  }

  /**
   * @param date
   * @return date + 1day
   */
  public static Date nextDay( Date date )
  {
    Calendar cal = new GregorianCalendar();
    cal.setTime( date );
    cal.add( Calendar.DATE, 1 );
    return cal.getTime();
  }

  /**
   * @param date
   * @param start
   * @param end
   * @return true if date is strictly between start and end
   */
  public static boolean isBetween( Date date, Date start, Date end )
  {
    return date.compareTo( start ) > 0 && date.compareTo( end ) < 0;
  }

  /**
   * @param date
   * @return true if date is within the current day
   */
  public static boolean isToday( Date date )
  {
    return isOnSameDay( new Date(), date );
  }

  /**
   * @param timeString
   * @return long time in milliseconds
   * 
   *         Returns a time in milliseconds representing the time stated in the
   *         string on today's date
   */
  public static Calendar calFromString( String timeString )
  {
    Calendar cal = Calendar.getInstance();
    timeString = timeString.replaceAll("\'","");
    String[] parsedTime = timeString.split( ":" );
    int hour = Integer.parseInt( parsedTime[0] );
    int minute = Integer.parseInt( parsedTime[1] );
    int second = Integer.parseInt( parsedTime[2] );
    int millis = 0;
    cal.setTimeZone( TimeZone.getTimeZone( "EST5EDT" ) );
    cal.set( Calendar.HOUR_OF_DAY, hour );
    cal.set( Calendar.MINUTE, minute );
    cal.set( Calendar.SECOND, second );
    cal.set( Calendar.MILLISECOND, millis );

    return cal;
  }

  public static String getCalTimeStr( Calendar time )
  {
    String timeStr = String.format( "%02d:%02d:%02d", time.get( Calendar.HOUR_OF_DAY ), time.get( Calendar.MINUTE ), time.get( Calendar.SECOND ) );
    return timeStr;
  }


  public static String makeMillisecondTimestamp()
  {
    long currentMilliseconds = System.currentTimeMillis();
    return millisecondTimeFormat.format( currentMilliseconds );
  }

  public static ThreadLocalDateFormat getMillisecondTimeFormat()
  {
    return millisecondTimeFormat;
  }

  public static String makeMillisecondTimestamp( long timestamp )
  {
    return millisecondTimeFormat.format( timestamp );
  }

  public static final long MS_IN_DAY = 86400000;
  public static final TimeZone EAST_COAST_TIME = TimeZone.getTimeZone( "EST5EDT" );
  public static final long MICROS_IN_DAY = MS_IN_DAY * 1000;
  public static final List<Integer> WEEKDAY = Arrays.asList( Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY );

  @Units(time = TimeUnit.HOURS)
  public static double getTimeOfDay( @Units(time = TimeUnit.MICROSECONDS)
  long unixTime )
  {
    double hoursSinceMidnight = getSecondsSinceMidnight( unixTime ) / TimeUnit.SECONDS.convert( 1, TimeUnit.HOURS );
    return hoursSinceMidnight;
  }

  @Units(time = TimeUnit.HOURS)
  public static long getTimeZoneOffset( @Units(time = TimeUnit.MILLISECONDS)
  long unixTime )
  {
    int offset = EAST_COAST_TIME.getOffset( unixTime );
    return TimeUnit.HOURS.convert( offset, TimeUnit.MILLISECONDS );
  }

  /**
   * 
   * @param unixTime
   *          the time as the number of seconds since the epoch. The epoch is
   *          referenced to 00:00:00 CUT (Coordinated Universal Time) 1 Jan
   *          1970. For example, on Monday February 17, 1997 at 07:15:06 CUT,
   *          the value returned by 'time' was 856163706.
   * 
   * @return
   */
  @Units(time = TimeUnit.SECONDS)
  protected static double getSecondsSinceMidnight( @Units(time = TimeUnit.MICROSECONDS)
  long unixTime )
  {
    unixTime -= DateUtils.EAST_COAST_TIME.getOffset( TimeUnit.MILLISECONDS.convert( unixTime, TimeUnit.MICROSECONDS ) );
    return ( unixTime / TimeUnit.MICROSECONDS.convert( 1, TimeUnit.SECONDS ) ) % ( TimeUnit.SECONDS.convert( 24, TimeUnit.HOURS ) );
  }

  /**
   * Converts a vector of unix (millisecond) to a real number between 0 and 24
   * representing the hours since midnight
   * 
   * @param col
   */
  public static void convertUnixTimeToHourOfDay( @Units(time = TimeUnit.MICROSECONDS)
  Vector col )
  {
    for ( int i = 0; i < col.size(); i++ )
    {
      @Units(time = TimeUnit.MICROSECONDS)
      long unixTime = (long) ( col.get( i ) );
      col.set( i, getTimeOfDay( unixTime ) );
    }
  }

  public static double getTimeOfDay()
  {
    return getTimeOfDay( System.currentTimeMillis() );
  }

  /**
   * @see Calendar#getInstance()
   * @return value returned by {@link Calendar#get(int)} called with
   *         {@link Calendar#DAY_OF_MONTH} as its argument
   */
  public static int getDayOfWeek()
  {
    return Calendar.getInstance().get( Calendar.DAY_OF_WEEK );
  }

  public static boolean isMarketOpenOn( int dayOfWeek )
  {
    return WEEKDAY.contains( dayOfWeek );
  }

  /**
   * @return isMarketOpenOn( getDayOfWeek()
   */
  public static boolean isMarketOpenToday()
  {
    return isMarketOpenOn( getDayOfWeek() );
  }

  /**
   * @see this{@link #convertTimeUnits(double, TimeUnit, TimeUnit)}
   * @param fromUnit
   * @param from
   * @return
   */
  public static double convertToHours( TimeUnit fromUnit, double from )
  {
    double ratio = fromUnit.convert( 1, TimeUnit.HOURS );
    return from / ratio;
  }

  public static double convertTimeUnits( double from, TimeUnit fromUnit, TimeUnit toUnit )
  {
    double ratio = fromUnit.convert( 1, toUnit );
    if ( !Double.isFinite( ratio ) || ratio == 0.0 )
    {
      ratio = toUnit.convert( 1, fromUnit );
      return from * ratio;
    }
    else
    {
      return from / ratio;
    }
  }
}
