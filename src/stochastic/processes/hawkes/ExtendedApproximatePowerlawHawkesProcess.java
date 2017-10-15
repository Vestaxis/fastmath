package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

import java.io.Serializable;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.function.Abs;

import fastmath.Vector;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedApproximatePowerlawHawkesProcess extends ConstrainedApproximatePowerlawHawkesProcess
    implements MultivariateFunction, Serializable
{
  static enum Parameter implements BoundedParameter
  {

    b(-4, 4), τ(0.00001, 20), ε(0, 0.5), τ0(0.00001, 10);

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
  public ExtendedApproximatePowerlawHawkesProcess(double τ0, double ε, double b, double τ)
  {
    assert 0 <= ε && ε <= 0.5 : "ε must be in [0,1/2]";
    assert τ0 > 0 : "η must be positive";
    assert τ > 0 : "τ must be positive";
    assert 0 <= ρ && ρ <= 1 : "ρ must be in [0,1]";
    this.η = τ0;
    this.τ = τ;
    this.ε = ε;
    this.b = b;
    this.κ = 0;
  }

  private static final long serialVersionUID = 1L;

  public ExtendedApproximatePowerlawHawkesProcess()
  {
  }

  private double[] getParameterArray()
  {
    return getParameters().toArray();
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
  private double τ;

  private double b;

  /**
   * 
   * @returnnormalization factor such that the branching rate is equal to
   *                      this{@link #ρ}
   */
  public double Z()
  {
    if (Math.abs(ε-pow(10,-15)) < 0)
    {
      return (b * τ + M) / ρ;
    }
    else
    {
      double inner = pow(m, -ε * (M - 1)) + pow(m, ε);
      return (b * τ - ( ( pow(η, -ε) * inner ) / (pow(m, ε) - 1) )) / ρ;
    }
  }

  /**
   * integrated kernel function which is the anti-derivative/indefinite integral
   * of this{@link #ρ}
   * 
   * @param t
   * @return ∫this{@link #ψ(double)}(t)dt
   */
  public double iψ(double t)
  {
    double x = sum(i -> -pow(η, -ε) * pow(m, i) * pow(pow(m, -i), 1 + ε) * exp(-t / η * pow(m, -i)), 0, M - 1);
    return t * ρ
           * (M * b * τ - τ * M * b * exp(-t / τ) + 1 / (-1 + pow(m, ε)) * pow(η, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1))) + x)
           / (M * b * τ * t + 1 / (pow(m, -ε) - 1) * pow(η, -ε) * (pow(m, -ε * M) - 1) * t);
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
    return 1 / τ;
    // double τ = exp(τ);
    // return τ;
  }

  @Override
  public double αS()
  {
    return b;

  }

}
