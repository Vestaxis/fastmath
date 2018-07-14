package fastmath.io;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ThreadLocalNumberFormat extends ThreadLocal<NumberFormat>
{
  @Override
  public void set( NumberFormat value )
  {
    this.format = value;
    super.set( value );
  }

  public static ThreadLocalNumberFormat instance = new ThreadLocalNumberFormat( new DecimalFormat() );

  public ThreadLocalNumberFormat getInstance()
  {
    return instance;
  }

  protected ThreadLocalNumberFormat(NumberFormat format)
  {
    this.format = format;
  }

  @Override
  protected NumberFormat initialValue()
  {
    return format;
  }

  private NumberFormat format;

  /**
   * Thread-local accessor
   * 
   * @return thread-local instance of NumberFormat, according to the last set()
   *         called or DecimalFormat by default
   */
  public static NumberFormat getFormat()
  {
    return instance.get();
  }
}