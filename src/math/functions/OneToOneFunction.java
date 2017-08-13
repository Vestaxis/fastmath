package math.functions;

import java.util.function.Function;

import math.Set;
import math.properties.Injective;

/**
 * A function where each point has a unique inverse
 * 
 * @author crow
 *
 * @param <D>
 * @param <R>
 */
@Injective
public interface OneToOneFunction<D extends Set, R extends Set> extends Function<D, R>
{

}
