package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

import java.io.Serializable;

import org.apache.commons.math3.analysis.MultivariateFunction;

import fastmath.Vector;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawHawkesProcess extends ConstrainedExponentialPowerlawHawkesProcess
    implements MultivariateFunction, Serializable
{
  static enum Parameter implements BoundedParameter
  {

    b(-2, 2), τ(0.00001, 20), ε(0, 0.5), τ0(0.00001, 10);

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

  private double ρ;

  /**
   * 
   * @param ρ
   *          TODO
   * @param τ0
   *          exponential multiplier
   * @param ε
   *          degree of fractional integration
   * @param b
   *          amplitude of short-term exponential
   * @param τ
   *          power multiplier
   */
  public ExtendedExponentialPowerlawHawkesProcess(double τ0, double ε, double b, double τ)
  {
    assert 0 <= ε && ε <= 0.5 : "ε must be in [0,1/2]";
    assert τ0 > 0 : "η must be positive";
    assert τ > 0 : "τ must be positive";
    assert 0 <= ρ && ρ <= 1 : "ρ must be in [0,1]";
    this.τ0 = τ0;
    this.τ = τ;
    this.ε = ε;
    this.b = b;

  }

  private static final long serialVersionUID = 1L;

  public ExtendedExponentialPowerlawHawkesProcess()
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
   * normalization factor such that the branching rate is equal to this{@link #ρ}
   * 
   * @return M * b * τ + 1 / (pow(m, -ε) - 1) * pow(η, -ε) * (pow(m, -ε * M) - 1)
   */
  public double Z()
  {
    if (ε == 0)
    {
      return M * b * τ + M;
    }
    else
    {
      double a = pow(m, (-ε * M + ε + 1)) - pow(m, (1 + ε));
      double b = pow(m, ε) - 1;
      double c = pow(τ0, -1 - ε);
      return -τ0 * a / m / b * c - αS() * τ0 / m;
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
    double x = sum(i -> -pow(τ0, -ε) * pow(m, i) * pow(pow(m, -i), 1 + ε) * exp(-t / τ0 * pow(m, -i)), 0, M - 1);
    return t * ρ
           * (M * b * τ - τ * M * b * exp(-t / τ) + 1 / (-1 + pow(m, ε)) * pow(τ0, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1))) + x)
           / (M * b * τ * t + 1 / (pow(m, -ε) - 1) * pow(τ0, -ε) * (pow(m, -ε * M) - 1) * t);
  }

  private int iterations = 0;

  public Vector getTransformedParameters()
  {
    return getParameters();
  }

  public double getΕ()
  {
    return ε;
  }

  public void setε(double ε)
  {
    this.ε = ε;
  }

  public void setτ0(double η)
  {
    this.τ0 = η;
  }

  @Override
  public int order()
  {
    return M + 1;
  }

  @Override
  public double βS()
  {
    return τ;
    // double tau = exp(τ);
    // return tau;
  }

  @Override
  public double αS()
  {
    return b;
    // return -pow(η, (-1 - ε))
    // * ((pow(m, (-ε * M + 2 * ε + 2)) * η - pow(m, (2 + 2 * ε)) * η - pow(m, (-ε *
    // M + ε + 1)) * η
    // + pow(m, (1 + ε)) * η) * C + pow(m, (2 + 2 * ε)) - pow(m, (-ε * M - M + 2 * ε
    // + 2)) - pow(m, (2 + ε))
    // + pow(m, (-ε * M - M + ε + 2)))
    // / ((pow(m, (1 + 2 * ε)) * η - pow(m, (1 + ε)) * η - pow(m, ε) * η + η) * C -
    // pow(m, (2 + 2 * ε))
    // + pow(m, (2 + ε)) + pow(m, (1 + ε)) - m);

    // return b;
  }

}
