package math.properties;

/**
 * From The Free On-line Dictionary of Computing (20 July 2014) [foldoc]:
 * 
 * surjection onto surjective
 * 
 * <mathematics> A function f : A -> B is surjective or onto or a surjection if
 * f A = B. I.e. f can return any value in B. This means that its {image} is its
 * {codomain}.
 * 
 * Only surjections have {right inverses}, f' : B -> A where f (f' x) = x since
 * if f were not a surjection there would be elements of B for which f' was not
 * defined.
 * 
 * See also {bijection}, {injection}.
 * 
 * (1995-05-27)
 */
public @interface Onto
{

}
