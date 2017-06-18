package math.theory.ring;

import math.Set;

/**
 * A Noetherian ring A is excellent if: <br>
 * <ul>
 * <li>A is universally catenary</li>
 * <li>A is a G-ring</li>
 * <li>Reg(B)⊂Spec(B) is open for every finitely-generated A-algebra B</li>
 * </ul>
 * 
 * @see Matsumura, H. Commutative Ring Theory(Kakan kan ron) (1986 English
 *      translation) (1980 Original Japanese version) (Cambridge studies in
 *      advanced mathematics) p.260
 * 
 * @author crow
 *
 */
public interface ExcellentRing<X extends Set> extends NoetherianRing<X>
{

}
