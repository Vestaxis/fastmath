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
    this.η = τ0;
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
    double c = pow(η, -1 - ε);
    return -η * a / m / b * c - αS() * η / m;
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
    return (pow(η, -1 - ε) * (pow(m, -(1 + ε) * (M - 1)) - pow(m, (1 + ε)))) / (pow(m, 1 + ε) - 1);
  }

  public double βS()
  {
    return m / η;
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
               * (-sum(i -> pow(1 / η / pow(m, i), 1 + ε) * η * pow(m, i) * exp(-t / η / pow(m, i)), 0, M - 1)
                  + 1 / (pow(m, 1 + ε) - 1) * pow(η, -1 - ε) * (pow(m, 1 + ε) - pow(m, -(1 + ε) * (M - 1))) * η / m * exp(-t / η * m))
               / η
               / (pow(η, -1 - ε) * pow(m, 1 + ε)
                  - 1 / (pow(m, 1 + ε) - 1) * pow(η, -1 - ε) * (pow(m, 1 + ε) - pow(m, -0.4e1 - 0.4e1 * ε)) * pow(m, ε)
                  - pow(η, -1 - ε) * pow(m, -0.4e1 * ε + 1)
                  + 1 / (pow(m, 1 + ε) - 1) * pow(η, -1 - ε) * (pow(m, 1 + ε) - pow(m, -0.4e1 - 0.4e1 * ε)));
    double b = m * (-1 + pow(m, ε))
               * (-1 / (-1 + pow(m, ε)) * pow(η, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1)))
                  + 1 / (pow(m, 1 + ε) - 1) * pow(η, -1 - ε) * (pow(m, 1 + ε) - pow(m, -(1 + ε) * (M - 1))) * η / m)
               / η
               / (pow(η, -1 - ε) * pow(m, 1 + ε)
                  - 1 / (pow(m, 1 + ε) - 1) * pow(η, -1 - ε) * (pow(m, 1 + ε) - pow(m, -0.4e1 - 0.4e1 * ε)) * pow(m, ε)
                  - pow(η, -1 - ε) * pow(m, -0.4e1 * ε + 1)
                  + 1 / (pow(m, 1 + ε) - 1) * pow(η, -1 - ε) * (pow(m, 1 + ε) - pow(m, -0.4e1 - 0.4e1 * ε)));

    double c = η * (pow(η, (-1 - ε)) * pow(m, (1 + ε))
                     - 1 / (pow(m, (1 + ε)) - 1) * pow(η, (-1 - ε)) * (pow(m, (1 + ε)) - pow(m, (-4 - 4 * ε))) * pow(m, ε)
                     - pow(η, (-1 - ε)) * pow(m, (-4 * ε + 1))
                     + 1 / (pow(m, (1 + ε)) - 1) * pow(η, (-1 - ε)) * (pow(m, (1 + ε)) - pow(m, (-4 - 4 * ε))));

    double d = m * (-1 + pow(m, ε))
               * (-1 / (-1 + pow(m, ε)) * pow(η, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1)))
                  + 1 / (pow(m, 1 + ε) - 1) * pow(η, -1 - ε) * (pow(m, 1 + ε) - pow(m, -(1 + ε) * (M - 1))) * η / m);

    return -((a - b) * c) / d;
  }

  @Override
  public int order()
  {
    return M + 1;
  }


}
