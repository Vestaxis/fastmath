package math.space;

import math.Field;

/**
 * a Birnbaum–Orlicz, which is the same thing as an Orlicvz space, is a type of
 * function space which generalizes the Lp spaces. Like the Lp spaces, they are
 * Banach spaces. The spaces are named for Władysław Orlicz and Zygmunt William
 * Birnbaum, who first defined them in 1931.
 * 
 * Besides the Lp spaces, a variety of function spaces arising naturally in
 * analysis are Birnbaum–Orlicz spaces. One such space L log+ L, which arises in
 * the study of Hardy–Littlewood maximal functions, consists of measurable
 * functions f such that the integral
 * 
 * ∫ℝn|f(x)|log+|f(x)|dx<∞.
 * 
 * Here log+ is the positive part of the logarithm. Also included in the class
 * of Birnbaum–Orlicz spaces are many of the most important Sobolev spaces.
 * 
 * Contents
 * 
 * 1 Formal definition 1.1 Example 2 Properties 3 Relations to Sobolev spaces 4
 * Orlicz Norm of a Random Variable 5 References
 * 
 * Formal definition
 * 
 * Suppose that μ is a σ-finite measure on a set X, and Φ : [0, ∞) → [0, ∞) is a
 * Young function, i.e., a convex function such that
 * 
 * Φ(x)x→∞,as x→∞,
 * 
 * Φ(x)x→0,as x→0.
 * 
 * Let L†Φ be the set of measurable functions f : X → R such that the integral
 * 
 * ∫XΦ(|f|)dμ
 * 
 * is finite, where as usual functions that agree almost everywhere are
 * identified.
 * 
 * This may not be a vector space (it may fail to be closed under scalar
 * multiplication). The vector space of functions spanned by L†Φ is the
 * Birnbaum–Orlicz space, denoted LΦ.
 * 
 * To define a norm on LΦ, let Ψ be the Young complement of Φ; that is,
 * 
 * Ψ(x)=∫x0(Φ′)−1(t)dt.
 * 
 * Note that Young's inequality holds:
 * 
 * ab≤Φ(a)+Ψ(b).
 * 
 * The norm is then given by
 * 
 * ∥f∥Φ=sup{∥fg∥1∣∫Ψ∘|g|dμ≤1}.
 * 
 * Furthermore, the space LΦ is precisely the space of measurable functions for
 * which this norm is finite.
 * 
 * An equivalent norm (Rao & Ren 1991, §3.3) is defined on LΦ by
 * 
 * ∥f∥′Φ=inf{k∈(0,∞)∣∫XΦ(|f|/k)dμ≤1},
 * 
 * and likewise LΦ(μ) is the space of all measurable functions for which this
 * norm is finite.
 */
public interface OrliczSpace<F extends Field<?>> extends BanachSpace<F>
{

}
