package stochastic.processes.hawkes;

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
public class ConstrainedApproximatePowerlawHawkesProcess extends ApproximatePowerlawHawkesProcess implements MultivariateFunction, Serializable
{
  private static final long serialVersionUID = 1L;

  @Override
  public BoundedParameter[] getBoundedParameters()
  {
    return Parameter.values();
  }

  protected static enum Parameter implements BoundedParameter
  {

    
    ρ( 0.25, 1 ), y(0.5, 0.6), ε(0, 0.5), τ0(0, 10);

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

  public ConstrainedApproximatePowerlawHawkesProcess(double τ0, double ε)
  {
    this.τ0 = τ0;
    this.ε = ε;
  }

  public ConstrainedApproximatePowerlawHawkesProcess()
  {
  }

  public double y;

  @Override
  public double Z()
  {
    return (-pow(m, -(ε * (M - 1))) / (-1 + pow(m, ε)) * pow(τ0, -ε) + pow(m, ε) / (-1 + pow(m, ε)) * pow(τ0, -ε)
            + (1 / (pow(m, (1 + ε)) - 1)
               * pow(τ0, (-1 - ε))
               * (pow(m, -((1 + ε) * (M - 1))) - pow(m, (-ε * M - M + 2 * ε + 1)) + pow(m, (2 * ε + 1)) - pow(m, (1 + ε)))
               * ρ + pow(m, -(ε * (M - 1))) * pow(τ0, -ε) * y
               - pow(τ0, -ε) * pow(m, ε) * y)
              / ((-m * pow(m, ε) + m) * ρ + pow(m, ε) * τ0 * y - τ0 * y)
              * τ0)
           / ρ;
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
              * pow(τ0, (-1 - ε))
              * (pow(m, -((1 + ε) * (M - 1))) - pow(m, (-ε * M - M + 2 * ε + 1)) + pow(m, (2 * ε + 1)) - pow(m, (1 + ε)))
              * ρ + pow(m, -(ε * (M - 1))) * pow(τ0, -ε) * y
              - pow(τ0, -ε) * pow(m, ε) * y)
           / ((-m * pow(m, ε) + m) * ρ + pow(m, ε) * τ0 * y - τ0 * y);
  }

  public double βS()
  {
    return m / τ0;
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
