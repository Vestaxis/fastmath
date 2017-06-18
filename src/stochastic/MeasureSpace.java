package stochastic;

import math.Set;
import stochastic.measures.MeasurableSpace;
import stochastic.measures.Measure;

/**
 * A measure space is a triple (X,Σ,u) where X is a {@link Set}, Σ a σ-algebra
 * of its subsets, and u:Σ⇒[0,∞] a {@link Measure}. Thus, a measure space
 * consists of a {@link MeasurableSpace} and a {@link Measure}. The notation
 * (X,Σ,u) is often shortened to (X,μ) and one says that μ is a measure on X;
 * sometimes the notation is shortened to X.
 * 
 * @author crow
 *
 * @param <X>
 * @param <Σ>
 * @param <u>
 */
public interface MeasureSpace<X extends Set, Σ extends SigmaField<X>, u extends Measure<? extends X>> extends MeasurableSpace<X, Σ>
{

}
