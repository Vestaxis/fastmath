package stochastic.measures;

import math.Set;
import stochastic.SigmaField;

/**
 * * The Palm measure is the unique finite measure P^∗(x)=ν_N Q_N^∗(ε_0,x) on Ω,
 * and where ε_x(A)= 1 x∈A 0 x∉A is the 'degenerate' point mass distribution
 * function, also known as the indicator function.
 * 
 *
 * @see karr p.5 x
 * @author sc
 *
 * @param <Σ>
 */
public interface PalmMeasure<Σ extends SigmaField<? extends Set>> extends Measure<Σ>
{

}
