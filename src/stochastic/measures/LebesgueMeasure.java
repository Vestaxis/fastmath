package stochastic.measures;

import math.space.EuclideanSpace;
import stochastic.SigmaField;

/**
 * In measure theory, the Lebesgue measure, named after French mathematician
 * Henri Lebesgue, is the standard way of assigning a measure to subsets of
 * n-dimensional Euclidean space. For n = 1, 2, or 3, it coincides with the
 * standard measure of length, area, or volume. In general, it is also called
 * n-dimensional volume, n-volume, or simply volume.[1] It is used throughout
 * real analysis, in particular to define Lebesgue integration. Sets that can be
 * assigned a Lebesgue measure are called Lebesgue measurable; the measure of
 * the Lebesgue measurable set A is denoted by λ(A).
 * 
 * @param <Σ>
 */
public interface LebesgueMeasure<Σ extends SigmaField<? extends EuclideanSpace>> extends Measure<Σ>
{

}
