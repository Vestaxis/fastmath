package util;

import static java.lang.String.format;

import java.util.Comparator;
import java.util.function.BinaryOperator;

import fastmath.Pair;

public class DoublePair extends Pair<Double, Double>
{
  @Override
  public String
         toString()
  {
    return format("[%d,%f]", (int) left.doubleValue(), right);
  }

  private static final long serialVersionUID = 1L;

  public DoublePair()
  {
    super();
  }

  public DoublePair(Double firstValue, Double secondValue)
  {
    super(firstValue, secondValue);
  }

  public double
         getLeftValue()
  {
    return left.doubleValue();
  }

  public double
         getRightValue()
  {
    return right.doubleValue();
  }

  public static Comparator<DoublePair> compareLeft = (a,
                                                      b) -> Double.compare(a.left, b.left);

  public static Comparator<DoublePair> rightComparator = (a,
                                                          b) -> Double.compare(a.right, b.right);
}
