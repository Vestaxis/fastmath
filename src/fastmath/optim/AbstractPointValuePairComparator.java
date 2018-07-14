package fastmath.optim;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

public final class AbstractPointValuePairComparator implements PointValuePairComparator
{
  public AbstractPointValuePairComparator(GoalType goalType)
  {
    this.goalType = goalType;
  }

  final GoalType goalType;

  public int compare(final PointValuePair o1, final PointValuePair o2)
  {
    if (o1 == null)
    {
      return (o2 == null) ? 0 : 1;
    }
    else if (o2 == null) { return -1; }
    final double v1 = o1.getValue();
    final double v2 = o2.getValue();
    return compareScore(v1, v2);
  }

  public int compareScore(final double v1, final double v2)
  {
    return (goalType == GoalType.MINIMIZE) ? Double.compare(v1, v2) : Double.compare(v2, v1);
  }
}