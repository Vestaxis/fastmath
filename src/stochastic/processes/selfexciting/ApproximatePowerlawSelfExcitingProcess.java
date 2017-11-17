package stochastic.processes.selfexciting;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;

import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;

public class ApproximatePowerlawSelfExcitingProcess extends ExponentialSelfExcitingProcess
{

  public ApproximatePowerlawSelfExcitingProcess(double ε, double τ)
  {
    super();
    this.ε = ε;
    this.τ = τ;
  }

  protected static enum Parameter implements BoundedParameter
  {

    ε(0, 0.5), τ(0, 3);

    private double min;
    private double max;

    Parameter(double min, double max)
    {
      this.min = min;
      this.max = max;
    }

    @Override
    public String
           getName()
    {
      return name();
    }

    @Override
    public double
           getMin()
    {
      return min;
    }

    @Override
    public double
           getMax()
    {
      return max;
    }

    @Override
    public int
           getOrdinal()
    {
      return ordinal();
    }

  }


  public double τ;

  public double ε = 0;

  @Override
  public double
         α(int i)
  {
    return pow(1 / (τ * pow(m, i)), 1 + ε);
  }

  @Override
  public double
         β(int i)
  {
    return 1 / (τ * pow(m, i));
  }

  public int M = 10;

  
  /**
   * choose m such that m^M=1 minute, in milliseconds
   */
  public double m = exp(log(60000) / M);

  public ApproximatePowerlawSelfExcitingProcess()
  {
    super();
  }

  @Override
  public BoundedParameter[]
         getBoundedParameters()
  {
    return Parameter.values();
  }

  @Override
  public int
         order()
  {
    return M;
  }

  @Override
  public double
         Z()
  {
    return sum(j -> α(j) / β(j), 0, order() - 1) / ρ;
  }

  @Override
  public Type
         getType()
  {
    return Type.ApproximatePowerlaw;
  }

}