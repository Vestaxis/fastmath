package stochastic.processes.selfexciting;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;

import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;

public class ApproximatePowerlawSelfExcitingProcess extends ExponentialSelfExcitingProcess
{

//  @Override
//  public double nthNormalizedMoment(int n)
//  {
//    if (ε == 0)
//    {
//      return (pow(m, n * M) - 1) / M / (pow(m, n) - 1) * pow(τ0, n);
//    }
//    else
//    {
//      return -1 / (pow(m, ε) - pow(m, n)) * (-pow(m, ε * M) + pow(m, n * M)) * pow(τ0, n) * (-1 + pow(m, ε)) / (-1 + pow(m, ε * M));
//    }
//  }

  public ApproximatePowerlawSelfExcitingProcess(double ε, double τ)
  {
    super();
    this.ε = ε;
    this.τ = τ;
  }

  protected static enum Parameter implements BoundedParameter
  {

    κ(0, 1), ε(0, 0.5), τ0(0, 3);

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

  public double τ;

  public double ε = 0;

  @Override
  public double α(int i)
  {
    return pow(1 / (τ * pow(m, i)), 1 + ε);
  }

  @Override
  public double β(int i)
  {
    return 1 / (τ * pow(m, i));
  }

  /**
   * choose m such that m^M=1 minute, in milliseconds
   */
  public double m = exp(log(60000) / M);

  public ApproximatePowerlawSelfExcitingProcess()
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
    return sum(j -> α(j) / β(j), 0, order() - 1);
  }

  /**
   * `
   * 
   * @return the branching rate which will result in k/(1-r)=this{@link #mean()}
   */
  @Override
  public double ρ()
  {
    return 1;

  }

  @Override
  public Type getType()
  {
    return Type.ApproximatePowerlaw;
  }

}