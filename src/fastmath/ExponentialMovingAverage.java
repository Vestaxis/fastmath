package fastmath;

import static java.lang.System.currentTimeMillis;

public class ExponentialMovingAverage
{
  private double α;
  private double oldValue = Double.NaN;

  public ExponentialMovingAverage(double α)
  {
    this.α = α;
  }

  /**
   * 
   * @param α
   *          the higher this value the more weight given to recent observations
   *          relative to previous values (of observations)
   * @param initialValue
   */
  public ExponentialMovingAverage(double α, double initialValue)
  {
    this.α = α;
    this.oldValue = initialValue;
  }

  public double average( double value )
  {
    if ( Double.isNaN( oldValue ) )
    {
      oldValue = value;
      return value;
    }
    double newValue = oldValue + α * ( value - oldValue );
    oldValue = newValue;
    return newValue;
  }

  long lastTick;

  public double tick()
  {
    long thisTick = currentTimeMillis();
    double avg = average( thisTick - lastTick );
    lastTick = thisTick;
    return avg;
  }
}