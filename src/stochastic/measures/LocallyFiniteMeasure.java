package stochastic.measures;

import math.Set;

/**
 * Let (X, T) be a Hausdorff topological space and let Σ be a σ-algebra on X
 * that contains the topology T (so that every open set is a measurable set, and
 * Σ is at least as fine as the Borel σ-algebra on X). A measure/signed
 * measure/complex measure μ defined on Σ is called locally finite if, for every
 * point p of the space X, there is an open neighbourhood Np of p such that the
 * μ-measure of Np is finite.
 * 
 * In more condensed notation, μ is locally finite if and only if
 * 
 * ∀p∈X, ∃N_p∈T s.t. p∈N_p and |μ(N_p)|<+∞
 * 
 * @author crow
 *
 * @param <X>
 */
public interface LocallyFiniteMeasure<X extends Set> extends Measure<X>
{

}
