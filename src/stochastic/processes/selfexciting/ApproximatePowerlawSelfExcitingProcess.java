package stochastic.processes.selfexciting;

import static fastmath.Functions.realSum;
import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;

import org.arblib.Real;

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

    ε(0, 0.5), τ(0.1, 3);

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
    return pow(τ * pow(m, i), -(1 + ε));
  }

  public Real
         αReal(int i)
  {
    // Real copy = new Real(α(i));
    Real hmm = new Real(τ).mul(new Real(m).pow(i)).pow(Real.ONE.add(new Real(ε)).neg());
    return hmm;
    // return new Real(α(i));
  }

  @Override
  public double
         β(int i)
  {
    return τ * pow(m, -i);
  }

  public Real
         βReal(int i)
  {
    return (i == 0 && τ == 1) ? Real.ONE : new Real(τ).div(new Real(m).pow(new Real(i)));
  }

  final public int M = 15;

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
    return sum(j -> α(j) / β(j), 0, order() - 1);
  }

  @Override
  public Real
         ZReal()
  {
    return realSum(j -> αReal(j).div(βReal(j)), 0, order() - 1);
  }

  @Override
  public Type
         getType()
  {
    return Type.ExtendedApproximatePowerlaw;
  }

}