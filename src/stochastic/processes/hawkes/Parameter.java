package stochastic.processes.hawkes;

enum Parameter implements BoundedParameter
{

  b(-1, 2), τ(0.00001, 20), ε(0, 0.5), τ0(0.00001, 10);

  Parameter(double min, double max)
  {
    this.min = min;
    this.max = max;
  }

  double min;

  double max;

  @Override
  public double getMin()
  {
    return min;
  }

  @Override
  public double getMax()
  {
    return max;
  }

  @Override
  public String getName()
  {
    return name();
  }

  @Override
  public int getOrdinal()
  {
    return ordinal();
  }


}