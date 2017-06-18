package math;

import stochastic.order.IndexSet;

/**
 * A sequence is an ordered set of mathematical objects. Sequences of object are
 * most commonly denoted using braces. For example, the symbol {2n}_(n=1)^infty
 * denotes the infinite sequence of even numbers {2,4,...,2n,...}.
 * 
 * @author crow
 *
 * @param <X>
 */
public interface Sequence<X extends Set> extends Family<X, IndexSet<? extends X>>
{

}
