package math.theory.ring;

/**
 * @see Matsumura, H. Commutative Ring Theory(Kakan kan ron) 1986(1980) Ch2.4
 * 
 *      A {@link Localisation} is also sometimes referred to as a
 *      "ring of fractions of A with respect to S"
 * 
 * @author crow
 *
 * @param <A>
 * @param <S>
 */
public interface Localisation<A extends Ring<?>, S extends MultiplicativeSet<A>> extends RingHomomorphism<A, S>
{

}
