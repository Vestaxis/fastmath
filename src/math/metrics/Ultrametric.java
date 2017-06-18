package math.metrics;

import math.Set;

/**
 * TODO
 * 
 * A metric is called an ultrametric if it satisfies the following stronger
 * version of the triangle inequality where points can never fall 'between'
 * other points:
 * 
 * For all x, y, z in X,
 * 
 * d(x, z) ≤ max(d(x, y), d(y, z))
 * 
 * in addition to that inherited by its being a {@link Metric}
 * 
 * d(x, z) <= d(x, y) + d(y, z)
 * 
 * 
 * @see @Book{IsometricPolishSpaceClassification, Title = {On The Classification
 *      of Polish (Metric) Spaces up to Isometry}, Author = {Su Gao and A. S.
 *      Kechris}, Publisher = {American Mathematical Society}, Year = {2003},
 *      Month = {January}, Number = {766}, Series = {Memoirs of the American
 *      Matheamtical Society}, Volume = {161} }
 * 
 * @param <X>
 */
public interface Ultrametric<X extends Set> extends Metric<X>
{

}