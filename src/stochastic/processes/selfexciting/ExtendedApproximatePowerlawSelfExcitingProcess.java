package stochastic.processes.selfexciting;

import static java.lang.Math.pow;

import java.io.Serializable;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.arblib.Real;

import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;

public class ExtendedApproximatePowerlawSelfExcitingProcess extends ApproximatePowerlawSelfExcitingProcess implements MultivariateFunction, Serializable
{

  @Override
  public Type
         getType()
  {
    return Type.ExtendedApproximatePowerlaw;
  }

  public ExtendedApproximatePowerlawSelfExcitingProcess()
  {
  }

  public static enum Parameter implements BoundedParameter
  {
    τ(1, 15), ε(0, 0.5), η(0.001, 10), b(0, 5),;

    Parameter(double min, double max)
    {
      this.min = min;
      this.max = max;
    }

    private double max;

    private double min;

    @Override
    public double
           getMax()
    {
      return max;
    }

    @Override
    public double
           getMin()
    {
      return min;
    }

    @Override
    public String
           getName()
    {
      return name();
    }

    @Override
    public int
           getOrdinal()
    {
      return ordinal();
    }

  }

  public double b;

  public double η;

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
    return M + 1;
  }

  @Override
  public double
         Z()
  {
    return (ε < 1E-14) ? (b * η + M) : ((pow(τ, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1)))) / (pow(m, ε) - 1) + b * η);
  }

  @Override
  public double
         α(int i)
  {
    return i < M ? super.α(i) : αS();
  }

  @Override
  public Real
         αReal(int i)
  {
    return i < M ? super.αReal(i) : αSReal();
  }

  public double
         αS()
  {
    return b;
  }

  public Real
         αSReal()
  {
    return new Real(b);
  }

  @Override
  public double
         β(int i)
  {
    return i < M ? super.β(i) : βS();
  }

  @Override
  public Real
         βReal(int i)
  {
    return i < M ? super.βReal(i) : βSReal();
  }

  public double
         βS()
  {
    return 1 / η;
  }

  public Real
         βSReal()
  {
    return Real.ONE.div(new Real(η));
  }

}
