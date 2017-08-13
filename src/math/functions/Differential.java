package math.functions;

import java.util.function.Function;

import math.Set;

public interface Differential<F extends Function<D, R>, D extends Set, R extends Set> extends Function<D, R>
{

}
