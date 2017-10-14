package stochastic.processes.hawkes;

import static java.lang.Math.pow;

public class ApproximatePowerlawHawkesProcess extends ExponentialHawkesProcess
{

  @Override
  public double nthNormalizedMoment(int n)
  {
    return -1 / (pow(m, ε) - pow(m, n)) * (-pow(m, ε * M) + pow(m, n * M)) * pow(τ0, n) * (-1 + pow(m, ε)) / (-1 + pow(m, ε * M));
  }

  public ApproximatePowerlawHawkesProcess(double ε, double τ0)
  {
    super();
    this.ε = ε;
    this.τ0 = τ0;
  }

  protected static enum Parameter implements BoundedParameter
  {

    α(0.1, 10), κ(0, 1), ε(0, 0.5), τ0(0, 10), ρ(0, 1);

    private double min;
    private double max;

    Parameter(double min, double max)
    {
      this.min = min;
      this.max = max;
    }

    @Override
    public String getName()
    {
      return name();
    }

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
    public int getOrdinal()
    {
      return ordinal();
    }

  }

  public int M = 15;

  public double τ0;

  public double ε;

  @Override
  public double α(int i)
  {
    return pow(1 / (τ0 * pow(m, i)), 1 + ε);
  }

  @Override
  public double β(int i)
  {
    return 1 / τ0 / pow(m, i);
  }

  public double m = 5;

  /**
   * branching rate
   */
  public double ρ = 1;

  public ApproximatePowerlawHawkesProcess()
  {
    super();
  }

  @Override
  public BoundedParameter[] getBoundedParameters()
  {
    return Parameter.values();
  }

  @Override
  public double λ()
  {
    throw new UnsupportedOperationException("TODO: unconditional λ");
  }

  @Override
  public int order()
  {
    return M;
  }

  @Override
  public double Z()
  {
    return ( 1 / (pow(m, ε) - 1) * pow(τ0, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1))) ) / ρ;
  }

}