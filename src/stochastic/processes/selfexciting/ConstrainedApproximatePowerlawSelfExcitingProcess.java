package stochastic.processes.selfexciting;

import static java.lang.Math.pow;

import java.io.Serializable;

import org.apache.commons.math3.analysis.MultivariateFunction;

import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;

/**
 * Exponential power-law approximation kernel with arbitrary
 * instantaneous-response constant ψ(0)=y and branching rate exactly equal to 1
 * 
 */
@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ConstrainedApproximatePowerlawSelfExcitingProcess extends ApproximatePowerlawSelfExcitingProcess implements MultivariateFunction, Serializable
{
  @Override
  public Type
         getType()
  {
    return Type.ConstrainedApproximatePowerlaw;
  }

  private static final long serialVersionUID = 1L;

  @Override
  public BoundedParameter[]
         getBoundedParameters()
  {
    return Parameter.values();
  }

  protected static enum Parameter implements BoundedParameter
  {

    ρ(0.00001, 1 - 0.00001), y(0, 0.25), ε(0, 0.5), τ(0.0001, 10);

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

  public ConstrainedApproximatePowerlawSelfExcitingProcess(double τ0, double ε)
  {
    this.τ = τ0;
    this.ε = ε;
  }

  public ConstrainedApproximatePowerlawSelfExcitingProcess()
  {
  }

  public double y;

  @Override
  public double
         Z()
  {
    return (-pow(m, -(ε * (M - 1))) / (-1 + pow(m, ε)) * pow(τ, -ε) + pow(m, ε) / (-1 + pow(m, ε)) * pow(τ, -ε)
            + (1 / (pow(m, (1 + ε)) - 1)
               * pow(τ, (-1 - ε))
               * (pow(m, -((1 + ε) * (M - 1))) - pow(m, (-ε * M - M + 2 * ε + 1)) + pow(m, (2 * ε + 1)) - pow(m, (1 + ε)))
               * ρ + pow(m, -(ε * (M - 1))) * pow(τ, -ε) * y
               - pow(τ, -ε) * pow(m, ε) * y)
              / ((-m * pow(m, ε) + m) * ρ + pow(m, ε) * τ * y - τ * y)
              * τ)
           / ρ;
  }

  @Override
  public double
         α(int i)
  {
    return i == 0 ? αS() : super.α(i - 1);
  }

  @Override
  public double
         β(int i)
  {
    return i == 0 ? βS() : super.β(i - 1);
  }

  public double
         αS()
  {
    return m
           * (1 / (pow(m, (1 + ε)) - 1)
              * pow(τ, (-1 - ε))
              * (pow(m, -((1 + ε) * (M - 1))) - pow(m, (-ε * M - M + 2 * ε + 1)) + pow(m, (2 * ε + 1)) - pow(m, (1 + ε)))
              * ρ + pow(m, -(ε * (M - 1))) * pow(τ, -ε) * y
              - pow(τ, -ε) * pow(m, ε) * y)
           / ((-m * pow(m, ε) + m) * ρ + pow(m, ε) * τ * y - τ * y);
  }

  public double
         βS()
  {
    return m / τ;
  }

  @Override
  public int
         order()
  {
    return M + 1;
  }

}
