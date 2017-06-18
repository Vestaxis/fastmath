package math;

import math.functions.ContinuousFunction;

/**
 * absolute continuity is a smoothness property of functions that is stronger
 * than continuity and uniform continuity. The notion of absolute continuity
 * allows one to obtain generalisations of the relationship between the two
 * central operations of calculus, differentiation and integration, expressed by
 * the fundamental theorem of calculus in the framework of Riemann integration.
 * Such generalisations are often formulated in terms of Lebesgue integration.
 * For real-valued functions on the real line two interrelated notions appear,
 * absolute continuity of functions and absolute continuity of measures. These
 * two notions are generalized in different directions. The usual derivative of
 * a function is related to the Radon–Nikodym derivative, or density, of a
 * measure.
 * 
 * We have the following chains of inclusions for functions over a compact
 * subset of the real line:
 * 
 * absolutely continuous ⊆ uniformly continuous ⊆ continuous
 * 
 * and:
 * 
 * Continuously differentiable ⊆ Lipschitz continuous ⊆ absolutely continuous ⊆
 * bounded variation ⊆ differentiable almost everywhere
 */
public interface AbsolutelyContinuousFunction<D extends Set, R extends Set> extends ContinuousFunction<D, R>
{

}
