package stochastic.processes.selfexciting;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

import java.io.Serializable;

import org.apache.commons.math3.analysis.MultivariateFunction;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedConstrainedExponentialPowerlawApproximationSelfExcitingProcess extends ConstrainedApproximatePowerlawSelfExcitingProcess
    implements MultivariateFunction, Serializable
{
  static enum Parameter implements BoundedParameter
  {

    y(0, 50), τ(0.00001, 20), ε(0, 0.5), τ0(0.00001, 50);

    Parameter(double min, double max)
    {
      this.min = min;
      this.max = max;
    }

    double min;

    double max;

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
    public String getName()
    {
      return name();
    }

    @Override
    public int getOrdinal()
    {
      return ordinal();
    }

  };

  /**
   * 
   * @param ρ
   *          branching rate
   * @param τ0
   *          exponential multiplier
   * @param ε
   *          degree of fractional integration
   * @param b
   *          amplitude of short-term exponential
   * @param τ
   *          power multiplier
   */
  public ExtendedConstrainedExponentialPowerlawApproximationSelfExcitingProcess(double τ0, double ε, double b, double τ)
  {
    assert 0 <= ε && ε <= 0.5 : "ε must be in [0,1/2]";
    assert τ0 > 0 : "τ0 must be positive";
    assert τ > 0 : "τ must be positive";
    this.τ0 = τ0;
    this.τ = τ;
    this.ε = ε;
    this.b = b;
    this.κ = 0;
  }

  private static final long serialVersionUID = 1L;

  public ExtendedConstrainedExponentialPowerlawApproximationSelfExcitingProcess()
  {
  }

  private double[] getParameterArray()
  {
    return getParameters().toDoubleArray();
  }

  @Override
  public BoundedParameter[] getBoundedParameters()
  {
    Parameter[] parameters = Parameter.values();
    return parameters;
    // return (Bound[]) Arrays.stream( parameters ).toArray();
  }

  /**
   * powerlaw scale
   */
  private double τ0;

  private double b;

  /**
   * 
   * @returnnormalization factor such that the branching rate is equal to
   *                      this{@link #ρ}
   */
  public double Z()
  {
    if (abs(ε - pow(10, -15)) < 0)
    {
      return (M * τ + αS()) / (τ * ρ);
    }
    else
    {
      return (-pow(m, -(ε * (M - 1))) / (-1 + pow(m, ε)) * pow(τ, -ε) + pow(m, ε) / (-1 + pow(m, ε)) * pow(τ, -ε) + αS() / τ) / ρ;
    }
  }

  /**
   * integrated kernel function which is the anti-derivative/indefinite integral
   * of this{@link #ρ}
   * 
   * @param t
   * @return ∫this{@link #ψ}(t)dt
   */
  public double iν(double t)
  {
    throw new UnsupportedOperationException("TODO");
  }

  private int iterations = 0;

  @Override
  public int order()
  {
    return M + 1;
  }

  @Override
  public double βS()
  {
    return τ;
  }

  @Override
  public double αS()
  {
    return τ
           * (-1 / (pow(m, (1 + ε)) - 1)
              * pow(τ, (-1 - ε))
              * (pow(m, (1 + ε)) - pow(m, (1 + 2 * ε)) - pow(m, -((1 + ε) * (M - 1))) + pow(m, (-ε * M - M + 2 * ε + 1)))
              * ρ + y * (pow(τ, -ε) * pow(m, -(ε * (M - 1))) - pow(τ, -ε) * pow(m, ε)))
           / (pow(m, ε) * ρ * τ - pow(m, ε) * y - τ * ρ + y);

  }

}
