package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
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
public class ConstrainedApproximatePowerlawHawkesProcess extends ApproximatePowerlawHawkesProcess
    implements MultivariateFunction, Serializable
{
  private static final long serialVersionUID = 1L;

  public ConstrainedApproximatePowerlawHawkesProcess(double τ0, double ε)
  {
    this.τ0 = τ0;
    this.ε = ε;
  }

  public ConstrainedApproximatePowerlawHawkesProcess()
  {
  }

  @Override
  public double Z()
  {
    double a = pow(m, (-ε * M + ε + 1)) - pow(m, (1 + ε));
    double b = pow(m, ε) - 1;
    double c = pow(τ0, -1 - ε);
    return -τ0 * a / m / b * c - αS() * τ0 / m;
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
    return (pow(τ0, -1 - ε) * (pow(m, -(1 + ε) * (M - 1)) - pow(m, (1 + ε)))) / (pow(m, 1 + ε) - 1);
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
  
    throw new UnsupportedOperationException( "TODO" );
  }

  @Override
  public int order()
  {
    return M + 1;
  }


}
