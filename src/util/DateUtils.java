package util;

import static java.util.Arrays.asList;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import stochastics.annotations.Units;

public class DateUtils
{

  final static ThreadLocalDateFormat millisecondTimeFormat = new ThreadLocalDateFormat("HH:mm:ss.SSS");

  /**
   * @param date1
   * @param date2
   * @return true if date1 and date2 are on the same year, month, and day
   */
  public static boolean
         isOnSameDay(Date date1,
                     Date date2)
  {
    Calendar cal1 = new GregorianCalendar();
    cal1.setTime(date1);
    Calendar cal2 = new GregorianCalendar();
    cal2.setTime(date2);
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
           && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
  }

  public static final long MS_IN_DAY = 86400000;
  
  public static final TimeZone EAST_COAST_TIME = TimeZone.getTimeZone("EST5EDT");
  
  public static final long MICROS_IN_DAY = MS_IN_DAY * 1000;
  
  public static final List<Integer> WEEKDAY = asList(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY);

  @Units(time = TimeUnit.HOURS)
  public static double
         getTimeOfDay(@Units(time = TimeUnit.MICROSECONDS) long unixTime)
  {
    double hoursSinceMidnight = getSecondsSinceMidnight(unixTime) / TimeUnit.SECONDS.convert(1, TimeUnit.HOURS);
    return hoursSinceMidnight;
  }

  @Units(time = TimeUnit.HOURS)
  public static long
         getTimeZoneOffset(@Units(time = TimeUnit.MILLISECONDS) long unixTime)
  {
    int offset = EAST_COAST_TIME.getOffset(unixTime);
    return TimeUnit.HOURS.convert(offset, TimeUnit.MILLISECONDS);
  }

  /**
   * 
   * @param unixTime
   *          the time as the number of seconds since the epoch. The epoch is
   *          referenced to 00:00:00 CUT (Coordinated Universal Time) 1 Jan 1970.
   *          For example, on Monday February 17, 1997 at 07:15:06 CUT, the value
   *          returned by 'time' was 856163706.
   * 
   * @return
   */
  @Units(time = TimeUnit.SECONDS)
  protected static double
            getSecondsSinceMidnight(@Units(time = TimeUnit.MICROSECONDS) long unixTime)
  {
    unixTime -= DateUtils.EAST_COAST_TIME.getOffset(TimeUnit.MILLISECONDS.convert(unixTime, TimeUnit.MICROSECONDS));
    return (unixTime / TimeUnit.MICROSECONDS.convert(1, TimeUnit.SECONDS)) % (TimeUnit.SECONDS.convert(24, TimeUnit.HOURS));
  }

  /**
   * @see Calendar#getInstance()
   * @return value returned by {@link Calendar#get(int)} called with
   *         {@link Calendar#DAY_OF_MONTH} as its argument
   */
  public static int
         getDayOfWeek()
  {
    return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
  }

  public static boolean
         isWeekDay(int dayOfWeek)
  {
    return WEEKDAY.contains(dayOfWeek);
  }

  /**
   * @return isMarketOpenOn( getDayOfWeek()
   */
  public static boolean
         isMarketOpenToday()
  {
    return isWeekDay(getDayOfWeek());
  }

  /**
   * @see this{@link #convertTimeUnits(double, TimeUnit, TimeUnit)}
   * @param fromUnit
   * @param from
   * @return
   */
  public static double
         convertToHours(TimeUnit fromUnit,
                        double from)
  {
    double ratio = fromUnit.convert(1, TimeUnit.HOURS);
    return from / ratio;
  }

  public static double
         convertTimeUnits(double from,
                          TimeUnit fromUnit,
                          TimeUnit toUnit)
  {
    double ratio = fromUnit.convert(1, toUnit);
    if (!Double.isFinite(ratio) || ratio == 0.0)
    {
      ratio = toUnit.convert(1, fromUnit);
      return from * ratio;
    }
    else
    {
      return from / ratio;
    }
  }
}
