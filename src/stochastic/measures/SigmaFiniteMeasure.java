package stochastic.measures;

import stochastic.SigmaField;

/**
 * a positive (or signed) measure μ defined on a σ-algebra Σ of subsets of a set
 * X is called finite if μ(X) is a finite real number (rather than ∞). The
 * measure μ is called σ-finite if X is the countable union of measurable sets
 * with finite measure. A set in a measure space is said to have σ-finite
 * measure if it is a countable union of sets with finite measure.
 * 
 * @author crow
 *
 * @param <Σ>
 */
public interface SigmaFiniteMeasure<Σ extends SigmaField<?>> extends Measure<Σ>
{

}
