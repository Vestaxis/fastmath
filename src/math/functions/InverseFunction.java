package math.functions;

import java.util.function.Function;

import math.Set;

/**
 * {x:f(x)=y}
 * 
 * @param <D>
 *          x∈D
 * @param <R>
 *          x∈R
 */
public interface InverseFunction<D extends Set, R extends Set, F extends Function<D, R>> extends Function<R, D>
{

}
