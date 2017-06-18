package math;

import math.space.Space;

/**
 * an isomorphism, from the Greek: ἴσος isos "equal", and μορφή morphe "shape",
 * is a homomorphism (or more generally a morphism) that admits an inverse.[note
 * 1] Two mathematical things are isomorphic if an isomorphism exists between
 * them. An automorphism is an isomorphism whose source and target coincide. The
 * interest of isomorphisms lies in the fact that two isomorphic objects cannot
 * be distinguished by using only the properties used to define morphisms; thus
 * isomorphic objects may be considered the same as long as one considers only
 * these properties and their consequences.
 * 
 * For most algebraic structures, including groups and rings, a homomorphism is
 * an isomorphism if and only if it is bijective.
 * 
 * In topology, where the morphisms are continuous functions, isomorphisms are
 * also called homeomorphisms or bicontinuous functions. In mathematical
 * analysis, where the morphisms are differentiable functions, isomorphisms are
 * also called diffeomorphisms.
 * 
 * @author crow
 *
 * @param <X>
 */
public interface Automorphism<X extends Space> extends Isomorphism<X, X>
{

}
