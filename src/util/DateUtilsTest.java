package util;

import static java.lang.System.currentTimeMillis;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class DateUtilsTest extends TestCase
{

  public static final double millisecondsPerHour = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS);

  public void
         testConvertFractionalTime()
  {
    double millis = 300;
    double h = DateUtils.convertTimeUnits(millis, TimeUnit.MILLISECONDS, TimeUnit.HOURS);
    double j = h * millisecondsPerHour;
    assertEquals(millis, j);

    assertEquals(3600.0, DateUtils.convertTimeUnits(1, TimeUnit.HOURS, TimeUnit.SECONDS));
    assertEquals(3600000.0, DateUtils.convertTimeUnits(1, TimeUnit.HOURS, TimeUnit.MILLISECONDS));
    assertEquals(1.0 / 3600.0, DateUtils.convertTimeUnits(1, TimeUnit.SECONDS, TimeUnit.HOURS));

  }

  public void
         testConversionBetweenMicrosecondsAndHours()
  {
    long midnight = currentTimeMillis();
    midnight -= (midnight % DateUtils.MS_IN_DAY);

    // for ( long m = 0; m < TimeUtils.MS_IN_DAY; m++ )
    // {
    // double h = TimeUtils.getHoursSinceMidnight( m );
    // }
  }

  protected long
            getTimeToday(int hour,
                         int minute)
  {
    Calendar cal = Calendar.getInstance(DateUtils.EAST_COAST_TIME);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minute);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime().getTime() * 1000;
  }

}
