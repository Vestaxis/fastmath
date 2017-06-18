package math;

import math.space.Space;

/**
 * an endomorphism is a morphism (or homomorphism) from a mathematical object to
 * itself. For example, an endomorphism of a vector space V is a linear map ƒ: V
 * → V, and an endomorphism of a group G is a group homomorphism ƒ: G → G.
 * 
 * @author crow
 *
 * @param <FROM>
 * @param <TO>
 */
public interface Endomorphism<SPACE extends Space> extends Morphism<SPACE, SPACE>
{

}
