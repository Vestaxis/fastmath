package math.theory.ring;

import math.properties.Commutative;

/**
 * @see Commutative Ring Theory #2.4 Example 4
 * 
 *      Let S be a multiplicative set, and set S~={a∈A:ab∈S} for some b∈A. Then
 *      S~ is also a multiplicative set called the saturation of S.
 * 
 * @author crow
 *
 * @param <A>
 * @param <S>
 */
public interface Saturation<A extends Ring<?>, S extends MultiplicativeSet<? extends A>> extends MultiplicativeSet<A>
{

}
