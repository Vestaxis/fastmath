package math.space;

import math.Field;

/**
 * the Lp spaces are function spaces defined using a natural generalization of
 * the p-norm for finite-dimensional vector spaces. They are sometimes called
 * Lebesgue spaces, named after Henri Lebesgue (Dunford & Schwartz 1958, III.3),
 * although according to the Bourbaki group (Bourbaki 1987) they were first
 * introduced by Frigyes Riesz (Riesz 1910). Lp spaces form an important class
 * of Banach spaces in functional analysis, and of topological vector spaces.
 * Lebesgue spaces have applications in physics, statistics, finance,
 * engineering, and other disciplines.
 * 
 * @author crow
 *
 */
public interface LpSpace<F extends Field<?>> extends BanachSpace<F>
{

}
