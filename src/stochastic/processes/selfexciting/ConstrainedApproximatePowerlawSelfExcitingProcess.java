package stochastic.processes.selfexciting;

import static java.lang.Math.pow;

import java.io.Serializable;

import org.apache.commons.math3.analysis.MultivariateFunction;

/**
 * Exponential power-law approximation kernel with the no-instantaneous-response
 * constaint ψ(0)=0
 * 
 * @author stephen
 *
 */
@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ConstrainedApproximatePowerlawSelfExcitingProcess extends ApproximatePowerlawSelfExcitingProcess implements MultivariateFunction, Serializable
{
  private static final long serialVersionUID = 1L;

  @Override
  public BoundedParameter[] getBoundedParameters()
  {
    return Parameter.values();
  }

  protected static enum Parameter implements BoundedParameter
  {

    
    y(0, 0.25), ε(0, 0.5), τ(0, 10);

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
  public double Z()
  {
    return (-pow(m, -(ε * (M - 1))) / (-1 + pow(m, ε)) * pow(τ, -ε) + pow(m, ε) / (-1 + pow(m, ε)) * pow(τ, -ε)
            + (1 / (pow(m, (1 + ε)) - 1)
               * pow(τ, (-1 - ε))
               * (pow(m, -((1 + ε) * (M - 1))) - pow(m, (-ε * M - M + 2 * ε + 1)) + pow(m, (2 * ε + 1)) - pow(m, (1 + ε)))
               * getρ() + pow(m, -(ε * (M - 1))) * pow(τ, -ε) * y
               - pow(τ, -ε) * pow(m, ε) * y)
              / ((-m * pow(m, ε) + m) * getρ() + pow(m, ε) * τ * y - τ * y)
              * τ)
           / getρ();
  }

  @Override
  public double α(int i)
  {
    return i < M ? super.α(i) : αS();
  }

  @Override
  public double β(int i)
  {
    return i < M ? super.β(i) : βS();
  }

  public double αS()
  {
    return m
           * (1 / (pow(m, (1 + ε)) - 1)
              * pow(τ, (-1 - ε))
              * (pow(m, -((1 + ε) * (M - 1))) - pow(m, (-ε * M - M + 2 * ε + 1)) + pow(m, (2 * ε + 1)) - pow(m, (1 + ε)))
              * getρ() + pow(m, -(ε * (M - 1))) * pow(τ, -ε) * y
              - pow(τ, -ε) * pow(m, ε) * y)
           / ((-m * pow(m, ε) + m) * getρ() + pow(m, ε) * τ * y - τ * y);
  }

  public double βS()
  {
    return m / τ;
  }

  /**
   * integrated kernel function which is the anti-derivative/indefinite integral
   * of this{@link #ρ}
   * 
   * @param t
   * @return ∫this{@link #ψ}(t)dt
   */
  @Override
  public double iψ(double t)
  {

    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public int order()
  {
    return M + 1;
  }

}
