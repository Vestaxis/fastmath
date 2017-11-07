package stochastic.processes.selfexciting;

import static fastmath.Functions.prod;
import static fastmath.Functions.sum;
import static java.lang.Math.pow;

import java.io.Serializable;

import org.apache.commons.math3.analysis.MultivariateFunction;

import fastmath.DoubleMatrix;

@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class ExtendedApproximatePowerlawSelfExcitingProcess extends ApproximatePowerlawSelfExcitingProcess implements MultivariateFunction, Serializable
{
  private static final long serialVersionUID = 1L;

  @Override
  public BoundedParameter[] getBoundedParameters()
  {
    return Parameter.values();
  }

  protected static enum Parameter implements BoundedParameter
  {

    κ(0, 1), η(0, 10), b(0, 2), ε(0, 0.5), τ0(0, 3);

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

  public ExtendedApproximatePowerlawSelfExcitingProcess()
  {
  }

  public double b;

  public double η;


  @Override
  public double Z()
  {
    //return (M * b * τ0 + sum(k -> pow(τ0, -ε) * pow(m, -k * ε), 0, order() - 1)) / getρ();
    return (M * b * τ0 + sum(k -> pow(τ0, -ε) * pow(m, -k * ε), 0, order() - 1)) ;
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
    return b;
  }

  public double βS()
  {
    return 1 / τ0;
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

  @Override
  public double getρ()
  {
    if (!Double.isNaN(cachedρ)) { return cachedρ; }
    double x = sum(j -> prod(k -> k == j ? α(j) : pow(β(j), 2), 0, order() - 1), 0, order() - 1);
    double res = -(κ * prod(j -> pow(β(j), 2), 0, order() - 1) - x) / x;
    cachedρ = res;
    return res;

  }
}
