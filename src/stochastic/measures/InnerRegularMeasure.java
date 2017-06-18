package stochastic.measures;

import math.geometry.differential.TopologicalSpace;
import stochastic.SigmaField;

/**
 * Let m be a measure on the σ-algebra of Borel sets of a Hausdorff topological
 * space X . The measure m is called inner regular or tight if, for any Borel
 * set B , m ( B ) is the supremum of m ( K ) over all compact subsets K of B
 * 
 * an inner regular measure is one for which the measure of a set can be
 * approximated from within by compact subsets. Let ( X , T ) be a Hausdorff
 * topological space and let Σ be a σ-algebra on X that contains the topology T
 * (so that every open set is a measurable set , and Σ is at least as fine as
 * the Borel σ-algebra on X ). Then a measure μ on the measurable space ( X , Σ)
 * is called inner regular if, for every set A in Σ, This property is sometimes
 * referred to in words as "approximation from within by compact sets." Some
 * authors use the term tight as a synonym for inner regular. This use of the
 * term is closely related to tightness of a family of measures , since a
 * measure μ is inner regular if and only if , for all ε > 0, there is some
 * compact subset K of X such that μ ( X \ K ) < ε . This is precisely the
 * condition that the singleton collection of measures { μ } is tight
 * 
 * @author crow
 *
 */
public interface InnerRegularMeasure<X extends TopologicalSpace, Σ extends SigmaField<X>> extends Measure<Σ>
{

}
