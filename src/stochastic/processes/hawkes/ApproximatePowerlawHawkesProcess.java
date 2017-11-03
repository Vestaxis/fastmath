package stochastic.processes.hawkes;

import static fastmath.Functions.prod;
import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.log;
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

    κ(0, 1), ε(0, 0.5), τ0(0, 2);

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
    return 1 / (τ0 * pow(m, i));
  }

  /**
   * choose m such that m^M=1 minute, in milliseconds
   */
  public double m = exp(log(60000) / M);

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
  public int order()
  {
    return M;
  }

  @Override
  public double Z()
  {
    return sum(j -> α(j) / β(j), 0, order() - 1) / getρ();
  }


  /**
   * `
   * 
   * @return the branching rate which will result in k/(1-r)=this{@link #mean()}
   */
  @Override
  public double getρ()
  {
    if (!Double.isNaN(cachedρ)) { return cachedρ; }
    double x = sum(j -> prod(k -> k == j ? α(j) : pow(β(j), 2), 0, order() - 1), 0, order() - 1);
    double res = -(κ * prod(j -> pow(β(j), 2), 0, order() - 1) - x) / x;
    cachedρ = res;
    return res;

  }

}