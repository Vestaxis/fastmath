package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.pow;
import static org.apache.commons.math3.util.CombinatoricsUtils.factorial;

public class ApproximatePowerlawHawkesProcess extends ExponentialHawkesProcess
{

  @Override
  public double nthMoment(int n)
  {
    return ( factorial(n) * sum(i -> pow(m, (n * (M - 1 - i) + i * ε)), 0, M - 1)) * pow(ε, n) / sum(i -> pow(m, ((M - i) * ε)), 1, M);
  }

  public ApproximatePowerlawHawkesProcess(double ε, double τ0)
  {
    super();
    this.ε = ε;
    this.τ0 = τ0;
  }

  protected static enum Parameter implements BoundedParameter
  {

    ε(0, 0.5), τ0(0, 30);

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

  protected double τ0;

  protected double ε;

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

  double m = 5;

  /**
   * branching rate
   */
  private double ρ = 1;

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
    return ρ / (pow(m, ε) - 1) * pow(τ0, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1)));
  }

}