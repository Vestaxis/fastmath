package stochastic.processes.hawkes;

import static java.lang.Math.pow;

import java.util.Arrays;

public class ExponentialPowerlawHawkesProcess extends ExponentialHawkesProcess
{

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


  public ExponentialPowerlawHawkesProcess()
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
    return 1 / (pow(m, ε) - 1) * pow(τ0, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1)));
  }

}