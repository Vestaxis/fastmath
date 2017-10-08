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
public class ConstrainedExponentialPowerlawHawkesProcess extends ExponentialPowerlawHawkesProcess
    implements MultivariateFunction, Serializable
{
  private static final long serialVersionUID = 1L;

  public ConstrainedExponentialPowerlawHawkesProcess(double τ0, double ε)
  {
    this.τ0 = τ0;
    this.ε = ε;
  }

  public ConstrainedExponentialPowerlawHawkesProcess()
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
    double a = m * (-1 + pow(m, ε))
               * (-sum(i -> pow(1 / τ0 / pow(m, i), 1 + ε) * τ0 * pow(m, i) * exp(-t / τ0 / pow(m, i)), 0, M - 1)
                  + 1 / (pow(m, 1 + ε) - 1) * pow(τ0, -1 - ε) * (pow(m, 1 + ε) - pow(m, -(1 + ε) * (M - 1))) * τ0 / m * exp(-t / τ0 * m))
               / τ0
               / (pow(τ0, -1 - ε) * pow(m, 1 + ε)
                  - 1 / (pow(m, 1 + ε) - 1) * pow(τ0, -1 - ε) * (pow(m, 1 + ε) - pow(m, -0.4e1 - 0.4e1 * ε)) * pow(m, ε)
                  - pow(τ0, -1 - ε) * pow(m, -0.4e1 * ε + 1)
                  + 1 / (pow(m, 1 + ε) - 1) * pow(τ0, -1 - ε) * (pow(m, 1 + ε) - pow(m, -0.4e1 - 0.4e1 * ε)));
    double b = m * (-1 + pow(m, ε))
               * (-1 / (-1 + pow(m, ε)) * pow(τ0, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1)))
                  + 1 / (pow(m, 1 + ε) - 1) * pow(τ0, -1 - ε) * (pow(m, 1 + ε) - pow(m, -(1 + ε) * (M - 1))) * τ0 / m)
               / τ0
               / (pow(τ0, -1 - ε) * pow(m, 1 + ε)
                  - 1 / (pow(m, 1 + ε) - 1) * pow(τ0, -1 - ε) * (pow(m, 1 + ε) - pow(m, -0.4e1 - 0.4e1 * ε)) * pow(m, ε)
                  - pow(τ0, -1 - ε) * pow(m, -0.4e1 * ε + 1)
                  + 1 / (pow(m, 1 + ε) - 1) * pow(τ0, -1 - ε) * (pow(m, 1 + ε) - pow(m, -0.4e1 - 0.4e1 * ε)));

    double c = τ0 * (pow(τ0, (-1 - ε)) * pow(m, (1 + ε))
                     - 1 / (pow(m, (1 + ε)) - 1) * pow(τ0, (-1 - ε)) * (pow(m, (1 + ε)) - pow(m, (-4 - 4 * ε))) * pow(m, ε)
                     - pow(τ0, (-1 - ε)) * pow(m, (-4 * ε + 1))
                     + 1 / (pow(m, (1 + ε)) - 1) * pow(τ0, (-1 - ε)) * (pow(m, (1 + ε)) - pow(m, (-4 - 4 * ε))));

    double d = m * (-1 + pow(m, ε))
               * (-1 / (-1 + pow(m, ε)) * pow(τ0, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1)))
                  + 1 / (pow(m, 1 + ε) - 1) * pow(τ0, -1 - ε) * (pow(m, 1 + ε) - pow(m, -(1 + ε) * (M - 1))) * τ0 / m);

    return -((a - b) * c) / d;
  }

  @Override
  public int order()
  {
    return M + 1;
  }


}
