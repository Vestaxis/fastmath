package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math3.analysis.MultivariateFunction;

import fastmath.Vector;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExponentialPowerlawHawkesProcess extends ExponentialHawkesProcess implements MultivariateFunction, Serializable
{
  private static final long serialVersionUID = 1L;

  public ExponentialPowerlawHawkesProcess(double τ0, double ε)
  {
    this.τ0 = τ0;
    this.ε = ε;
    initializeParameterVectors();
  }

  public ExponentialPowerlawHawkesProcess()
  {
  }

  static enum Parameter implements BoundedParameter
  {

    ε(0, 0.5), τ0(0, 30);

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

  };

  /**
   * range of the approximation
   */
  public int M = 15;

  /**
   * smallest timescale
   */
  protected double τ0;

  /**
   * Tail exponent
   */
  protected double ε;

  /**
   * precision of approximation
   */
  private double m = 5;

  boolean normalize = true;

  @Override
  public double Z()
  {
    if (!normalize) { return 1; }

    double a = pow(m, (-ε * M + ε + 1)) - pow(m, (1 + ε));
    double b = pow(m, ε) - 1;
    double c = pow(τ0, -1 - ε);
    return -τ0 * a / m / b * c - αS() * τ0 / m;
  }

  public double βS()
  {
    return m / τ0;
  }

  public double αS()
  {
    return (pow(τ0, -1 - ε) * (pow(m, -(1 + ε) * (M - 1)) - pow(m, (1 + ε)))) / (pow(m, 1 + ε) - 1);
  }

  @Override
  public double β(int i)
  {
    if (i == M) { return βS(); }
    return 1 / τ0 / pow(m, i);
  }

  @Override
  public double α(int i)
  {
    if (i == M) { return αS(); }
    return pow(1 / (τ0 * pow(m, i)), 1 + ε);
  }

//  @Override
//  public void assignParameters(double[] point)
//  {
//    this.ε = point[Parameter.ε.ordinal()];
//    this.τ0 = point[Parameter.τ0.ordinal()];
//  }

  public Vector initializeParameterVectors()
  {
    Vector params = getParameters();
    return params;
  }

  private int iterations = 0;

  // public Vector getParameters()
  // {
  // int paramCount = getParamCount();
  // Vector params = new Vector(paramCount);
  // params.set(Parameter.ε.ordinal(), ε);
  // params.set(Parameter.τ0.ordinal(), τ0);
  // return params;
  // }

  public Vector getTransformedParameters()
  {
    return getParameters();
  }

  public double getε()
  {
    return ε;
    // return 0.25 * tanh(ε) + 0.25;
  }

  public void setε(double ε)
  {
    this.ε = ε;
    // this.ε = FastMath.atanh(-1 + 4 * ε);
  }

  public void setτ0(double η)
  {
    this.τ0 = η;
    // this.η = log(η);
  }

  public int getParamCount()
  {
    return Parameter.values().length;
  }

  private double[] getParameterArray()
  {
    return getParameters().toArray();
  }

  public String getParamString()
  {
    return Arrays.asList(Parameter.values()).toString() + "=" + getTransformedParameters().toString();
    // return "kappa=" + getKappa() + " alpha=" + alpha.toString().replace(
    // "\n", "" ) + " bη="
    // + bη.toString().replace( "\n", "" );
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
    // double eta = exp(η);
    // double eps = 0.25 * tanh(ε) + 0.25;
    final double eta = τ0;
    final double eps = ε;

    double a = m * (-1 + pow(m, eps))
               * (-sum(i -> pow(1 / eta / pow(m, i), 1 + eps) * eta * pow(m, i) * exp(-t / eta / pow(m, i)), 0, M - 1)
                  + 1 / (pow(m, 1 + eps) - 1)
                    * pow(eta, -1 - eps)
                    * (pow(m, 1 + eps) - pow(m, -(1 + eps) * (M - 1)))
                    * eta
                    / m
                    * exp(-t / eta * m))
               / eta
               / (pow(eta, -1 - eps) * pow(m, 1 + eps)
                  - 1 / (pow(m, 1 + eps) - 1) * pow(eta, -1 - eps) * (pow(m, 1 + eps) - pow(m, -0.4e1 - 0.4e1 * eps)) * pow(m, eps)
                  - pow(eta, -1 - eps) * pow(m, -0.4e1 * eps + 1)
                  + 1 / (pow(m, 1 + eps) - 1) * pow(eta, -1 - eps) * (pow(m, 1 + eps) - pow(m, -0.4e1 - 0.4e1 * eps)));
    double b = m * (-0.1e1 + pow(m, eps))
               * (-0.1e1 / (-0.1e1 + pow(m, eps)) * pow(eta, -eps) * (pow(m, eps) - pow(m, -eps * (M - 1)))
                  + 0.1e1 / (pow(m, 0.1e1 + eps) - 0.1e1)
                    * pow(eta, -0.1e1 - eps)
                    * (pow(m, 0.1e1 + eps) - pow(m, -(0.1e1 + eps) * (M - 1)))
                    * eta
                    / m)
               / eta
               / (pow(eta, -0.1e1 - eps) * pow(m, 0.1e1 + eps)
                  - 0.1e1 / (pow(m, 0.1e1 + eps) - 0.1e1)
                    * pow(eta, -0.1e1 - eps)
                    * (pow(m, 0.1e1 + eps) - pow(m, -0.4e1 - 0.4e1 * eps))
                    * pow(m, eps)
                  - pow(eta, -0.1e1 - eps) * pow(m, -0.4e1 * eps + 0.1e1)
                  + 0.1e1 / (pow(m, 0.1e1 + eps) - 0.1e1) * pow(eta, -0.1e1 - eps) * (pow(m, 0.1e1 + eps) - pow(m, -0.4e1 - 0.4e1 * eps)));

    double c = eta
               * (pow(eta, (-1 - eps)) * pow(m, (1 + eps))
                  - 0.1e1 / (pow(m, (1 + eps)) - 0.1e1) * pow(eta, (-1 - eps)) * (pow(m, (1 + eps)) - pow(m, (-4 - 4 * eps))) * pow(m, eps)
                  - pow(eta, (-1 - eps)) * pow(m, (-4 * eps + 1))
                  + 0.1e1 / (pow(m, (1 + eps)) - 0.1e1) * pow(eta, (-1 - eps)) * (pow(m, (1 + eps)) - pow(m, (-4 - 4 * eps))));

    double d = m * (-0.1e1 + pow(m, eps))
               * (-0.1e1 / (-0.1e1 + pow(m, eps)) * pow(eta, -eps) * (pow(m, eps) - pow(m, -eps * (M - 1)))
                  + 0.1e1 / (pow(m, 0.1e1 + eps) - 0.1e1)
                    * pow(eta, -0.1e1 - eps)
                    * (pow(m, 0.1e1 + eps) - pow(m, -(0.1e1 + eps) * (M - 1)))
                    * eta
                    / m);

    return -((a - b) * c) / d;
  }

  @Override
  public int order()
  {
    return M + 1;
  }

  public Object clone()
  {
    ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess(τ0, ε);
    process.T = T;
    return process;
  }

  @Override
  public BoundedParameter[] getBoundedParameters()
  {
    return Parameter.values();
  }

  @Override
  public double λ()
  {
    throw new UnsupportedOperationException("TODO: unconditional λ");
  }

}
