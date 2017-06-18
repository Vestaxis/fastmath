package stochastic.measures;

import stochastic.SigmaField;

/**
 * the Haar measure is a way to assign an "invariant volume" to subsets of
 * locally compact topological groups and subsequently define an integral for
 * functions on those groups.
 * 
 * This measure was introduced by Alfréd Haar in 1933.[1] Haar measures are used
 * in many parts of analysis and number theory, group theory, representation
 * theory, estimation theory and ergodic theory.
 * 
 * @author crow
 *
 */
public interface HaarMeasure<Σ extends SigmaField<?>> extends Measure<Σ>
{

}
