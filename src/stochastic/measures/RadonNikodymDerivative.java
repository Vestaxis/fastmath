package stochastic.measures;

import math.Set;
import stochastic.SigmaField;

/**
 * the Radon–Nikodym theorem is a result in measure theory which states that,
 * given a measurable space (X,Σ), if a σ-finite measure ν on (X,Σ) is
 * absolutely continuous with respect to a σ-finite measure μ on (X,Σ), then
 * there is a measurable function f:X→[0,∞), such that for any measurable subset
 * A \subset X :
 * 
 * $\nu(A) = \int_A f \, d\mu$
 * 
 * The function f is called the Radon–Nikodym derivative and denoted by
 * $\frac{d\nu}{d\mu}$.
 * 
 * @author crow
 * @param <X>
 *          the class of elements of the set
 * @param <Σ>
 *          the sigma-field of elements of the set
 * @param <ν>
 *          σ-finite measure on (X,Σ)
 * @param <μ>
 *          σ-finite measure on (X,Σ)
 */
public interface RadonNikodymDerivative<X extends Set, Σ extends SigmaField<? extends X>, ν extends SigmaFiniteMeasure<? extends Σ>, μ extends SigmaFiniteMeasure<? extends Σ>>
                                       extends
                                       MeasurableFunction<X, Σ>
{

}
