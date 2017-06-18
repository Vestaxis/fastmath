package stochastic.measures;

import math.Set;
import stochastic.SigmaField;

/**
 * δ_x(A)=1 if x∈Σ and 0 otherwise, that is, x∉Σ, also known as an indicator or
 * characteristic function
 * 
 * @author sc
 *
 * @param <Σ>
 */
public interface DiracMeasure<Σ extends SigmaField<? extends Set>> extends Measure<Σ>
{

}
