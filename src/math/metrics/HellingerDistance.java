package math.metrics;

import math.Product;
import numbersystems.RealNumbers;
import stochastic.probability.ProbabilityMeasure;

/**
 * Measures the "distance" between 2 probability measures
 *
 * Probability theory using Lebesgue measure
 * 
 * To define the Hellinger distance in terms of elementary probability theory,
 * we take λ to be Lebesgue measure, so that dP / dλ and dQ / dλ are simply
 * probability density functions. If we denote the densities as f and g,
 * respectively, the squared Hellinger distance can be expressed as a standard
 * calculus integral
 * 
 * \frac{1}{2}\int \left(\sqrt{f(x)} - \sqrt{g(x)}\right)^2 dx = 1 - \int
 * \sqrt{f(x) g(x)} \, dx,
 * 
 * where the second form can be obtained by expanding the square and using the
 * fact that the integral of a probability density over its domain must be one.
 * 
 * The Hellinger distance H(P, Q) satisfies the property (derivable from the
 * Cauchy-Schwarz inequality)
 * 
 * 0\le H(P,Q) \le 1.
 */
public class HellingerDistance implements Metric<ProbabilityMeasure<?, ?>>
{

  @Override
  public RealNumbers apply( Product<ProbabilityMeasure<?, ?>, ProbabilityMeasure<?, ?>> t )
  {
    // TODO Auto-generated method stub
    return null;
  }

}
