package numbersystems;

import math.properties.Associative;
import math.properties.Commutative;
import math.properties.Distributive;
import math.properties.Identity;
import math.space.Space;
import math.theory.ring.CommutativeRing;
import stochastic.order.IndexSet;

/**
 * 
 * <code> 1, 2, 3, 4, ...</code>
 * 
 * 
 * @see http://mathworld.wolfram.com/CountingNumber.html
 * 
 * 
 * @author crow
 *
 */
@Associative
@Commutative
@Distributive
@Identity
public interface PositiveIntegers extends IndexSet<Integers>, Space, CommutativeRing<Integers>
{

}
