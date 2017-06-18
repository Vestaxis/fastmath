package math.properties;

/**
 * A function (or mapping) is called injective if distinct arguments have
 * distinct images.
 * 
 * In other words, a function f:A→B from a set A to a set B is
 * 
 * an injective function or an injection or one-to-one function
 * 
 * if and only if
 * 
 * a1≠a2 implies f(a1)≠f(a2), or equivalently f(a1)=f(a2) implies a1=a2
 * 
 * for all a1,a2∈A. Equivalent conditions
 * 
 * A function f is injective if and only if f−1(f(S))=S for all subsets S of the
 * domain A.
 * 
 * A function f is injective if and only if, for every pair of functions g,h
 * with values in A, the condition f∘g=f∘h implies g=h. (In category theory,
 * this property is used to define monomorphisms.)
 * 
 * A function f is injective if and only if there is a left-inverse function g
 * with g∘f=IdA. Related notions
 * 
 * A special case is the inclusion function defined on a subset A⊂B by f(a)=a.
 * 
 * A function that is both injective and surjective is called bijective (or, if
 * domain and range coincide, in some contexts, a permutation).
 * 
 * An injective homomorphism is called monomorphism.
 * 
 * Injective mappings that are compatible with the underlying structure are
 * often called embeddings.
 */
public @interface Injective
{

}
