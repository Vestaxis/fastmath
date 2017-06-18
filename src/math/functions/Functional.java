package math.functions;

import math.Field;
import math.space.VectorSpace;

/**
 * A mapping f of an arbitrary set X into the set ℝ of real numbers or the set ℂ
 * of complex numbers. If X is endowed with the structure of a vector space, a
 * topological space or an ordered set, then there arise the important classes
 * of linear, continuous and monotone functionals, respectively (cf. Linear
 * functional; Continuous functional; Monotone mapping). <br>
 * <code>
 * [1] A.N. Kolmogorov, S.V. Fomin, "Elements of the theory of functions and functional analysis" , 1–2 , Graylock (1957–1961) (Translated from Russian)
 * </code>
 * 
 * @param <D>
 *          a {@link VectorSpace}
 * @param <R>
 *          a {@link Field} over which the {@link VectorSpace} D is defined`
 */
public interface Functional<D extends VectorSpace<? extends R>, R extends Field<?>> extends Function<D, R>
{

}
