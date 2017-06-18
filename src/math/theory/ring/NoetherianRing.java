package math.theory.ring;

import math.Set;

/**
 * A ring is called left or right Noetherian if it does not contain an infinite
 * ascending chain of left or right ideals. In this case, the ring in question
 * is said to satisfy the ascending chain condition on left or right ideals.
 * 
 * A ring is said to be Noetherian if it is both left and right Noetherian. For
 * a ring R, the following are equivalent:
 * 
 * 1. R satisfies the ascending chain condition on ideals (i.e., is Noetherian).
 * 
 * 2. Every ideal of R is finitely generated.
 * 
 * 3. Every set of ideals contains a maximal element.
 * 
 * @author crow
 *
 */
public interface NoetherianRing<X extends Set> extends LeftNoetherianRing<X>, RightNoetherianRing<X>
{

}
