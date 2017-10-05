package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.tanh;
import static java.lang.System.out;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.direct.NelderMeadSimplex;
import org.apache.commons.math3.optimization.direct.SimplexOptimizer;
import org.apache.commons.math3.util.FastMath;

import fastmath.Vector;
import fastmath.exceptions.NotANumberException;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedExponentialPowerlawHawkesProcess extends ExponentialPowerlawHawkesProcess
    implements MultivariateFunction, Serializable
{

  @Override
  public Object clone() throws CloneNotSupportedException
  {
    ExtendedExponentialPowerlawHawkesProcess newobj = new ExtendedExponentialPowerlawHawkesProcess(τ0,ε,b,τ);
    newobj.T = T;
    return newobj;
  }

  private double ρ;

  @Override
  public int getParamCount()
  {
    return Parameter.values().length;
  }

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
    initializeParameterVectors();

  }

  private static final long serialVersionUID = 1L;

  public ExtendedExponentialPowerlawHawkesProcess()
  {
    initializeParameterVectors();
  }

  private double[] getParameterArray()
  {
    return getParameters().toArray();
  }

  private Vector calculateInitialGuess(Vector durations)
  {
    final Vector vec = getParameters();

    return vec;
  }

  public String getParamString()
  {
    return Arrays.asList(Parameter.values()).toString() + "=" + getTransformedParameters().toString();

  }

  static enum Parameter
  {
    b, τ, ε, τ0
  };

  @Override
  public SimpleBounds getParameterBounds()
  {
    return new SimpleBounds(new double[]
    { -5, 0.00001, 0, 0.00001 }, new double[]
    { 10, 10, 0.5, 10.0 });
  }

  /**
   * range of the approximation
   */
  int M = 15;

  /**
   * powerlaw scale
   */
  private double τ;

  /**
   * precision of approximation
   */
  private double m = 5;

  private double b;

  /**
   * normalization factor such that the branching rate is equal to this{@link #ρ}
   * 
   * @return M * b * τ + 1 / (pow(m, -ε) - 1) * pow(η, -ε) * (pow(m, -ε * M) - 1)
   */
  public double Z()
  {
    if (!normalize) { return 1; }
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
    return t
        * ρ * (M * b * τ - τ * M * b * exp(-t / τ)
            + 1 / (-1 + pow(m, ε)) * pow(τ0, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1))) + x)
        / (M * b * τ * t + 1 / (pow(m, -ε) - 1) * pow(τ0, -ε) * (pow(m, -ε * M) - 1) * t);
  }

  @Override
  public double value(double[] point)
  {
    this.b = point[Parameter.b.ordinal()];
    this.ε = point[Parameter.ε.ordinal()];
    this.τ0 = point[Parameter.τ0.ordinal()];
    this.τ = point[Parameter.τ.ordinal()];

    double ll = logLik();

    if (Double.isNaN(ll)) { return Double.NEGATIVE_INFINITY; }

    return ll;
  }

  public Vector initializeParameterVectors()
  {
    params = getParameters();
    return params;
  }

  private int iterations = 0;

  Vector params;

  public Vector getParameters()
  {
    int paramCount = Parameter.values().length;
    params = new Vector(paramCount);
    params.set(Parameter.b.ordinal(), b);
    params.set(Parameter.ε.ordinal(), ε);
    params.set(Parameter.τ.ordinal(), τ);
    params.set(Parameter.τ0.ordinal(), τ0);
    return params;
  }

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
