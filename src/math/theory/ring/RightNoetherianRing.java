package math.theory.ring;

import math.Set;

/**
 * A ring is called right Noetherian if it does not contain an infinite
 * ascending chain of right ideals. In this case, the ring in question is said
 * to satisfy the ascending chain condition on right ideals.
 * 
 * A ring is said to be Noetherian if it is both right and right Noetherian. For
 * a ring R, the following are equivalent:
 * 
 * 1. R satisfies the <i>ascending chain condition</i> on ideals (i.e., is
 * Noetherian).
 * 
 * 2. Every ideal of R is <i>finitely generated</i>.
 * 
 * 3. Every set of ideals contains a <i>maximal element</i>.
 * 
 * @author crow
 *
 */
public interface RightNoetherianRing<X extends Set> extends Ring<X>
{

}