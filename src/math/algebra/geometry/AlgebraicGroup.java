package math.algebra.geometry;

import math.Set;
import math.theory.group.Group;

/**
 * A {@link Group} $G$ provided with the structure of an {@link Variety} in
 * which the multiplication $ \mu: G\times G \to G $ and the inversion mapping
 * $\nu: G \to G$ are regular mappings (morphisms) of algebraic varieties.
 * 
 */
public interface AlgebraicGroup<X extends Set> extends Group<X>, Variety
{

}
