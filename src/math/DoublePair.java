package math;

import static java.lang.String.format;

import java.util.function.BinaryOperator;

import fastmath.Pair;

public class DoublePair extends Pair<Double, Double>
{
  @Override
  public String toString()
  {
    return format( "[%d,%f]", (int)left.doubleValue(), right );
  }

  private static final long serialVersionUID = 1L;

  public DoublePair()
  {
    super();
  }

  public DoublePair(Double firstValue, Double secondValue)
  {
    super( firstValue, secondValue );
  }

  public double getLeftValue()
  {
    return left.doubleValue();
  }

  public double getRightValue()
  {
    return right.doubleValue();
  }

  public static BinaryOperator<DoublePair> compareRight = ( a, b ) -> a.right > b.right ? a : b;
}
