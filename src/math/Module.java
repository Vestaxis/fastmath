package math;

import math.space.VectorSpace;
import math.theory.group.AdditiveGroup;
import math.theory.group.CommutativeGroup;
import math.theory.ring.Ring;

/**
 * the concept of a module over a ring is a generalization of the notion of
 * vector space over a field, wherein the corresponding scalars are the elements
 * of an arbitrary ring. Modules also generalize the notion of commutative
 * groups, which are modules over the ring of integers.
 * 
 * Thus, a {@link Module}, like a {@link VectorSpace}, is an additive
 * commutative group; a product is defined between elements of the ring and
 * elements of the module that is distributive over both parameters and is
 * compatible with the ring multiplication.
 * 
 * Modules are very closely related to the representation theory of groups. They
 * are also one of the central notions of commutative algebra and homological
 * algebra, and are used widely in algebraic geometry and algebraic topology.
 * 
 * @author crow
 *
 */
public interface Module<R extends Ring<?>> extends CommutativeGroup<R>, AdditiveGroup<R>
{

}
