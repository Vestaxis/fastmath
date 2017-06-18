package stochastic.measures;

import math.space.CompleteSeparableMetricSpace;
import stochastic.SigmaField;

/**
 * 
 * A concept introduced originally by J. Radon (1913), whose original
 * constructions referred to measures on the Borel σ-algebra of the Euclidean
 * space ℝn. Contents
 * 
 * 1 Definition 2 Radon space 3 Duality with continuous functions 3.1 References
 * 
 * Definition
 * 
 * A measure μ (cf. Measure in a topological vector space) defined on the
 * σ-algebra (X) of Borel sets of a topological Hausdorff space X which is
 * locally finite (i.e. for any point x∈X there is a neighborhood which has
 * finite measure) and having the following property: μ(B)=sup{μ(K):K⊂B,K
 * compact}(1) (see [Sc]).
 * 
 * If X is locally compact every finite Radon measure on X is also outer
 * regular, i.e. μ(N)=inf{μ(U):U⊃N,U open},(2) (cp. therefore with Definition
 * 2.2.5 of [Fe] and Definition 1.5 of [Ma]).
 * 
 * The property (1) is called inner regularity or also tightness of the measure
 * μ (see Tight measure), whereas property (2) is called outer regularity. Some
 * authors require also that the measure μ be finite. If X has a countable
 * basis, Radon measures as defined above are necessarily σ-finite. Radon space
 * 
 * A topological space X is called a Radon space if every finite measure defined
 * on the σ-algebra (X) of Borel sets is a Radon measure. For instance the
 * Euclidean space is a Radon space (cp. with Theorem 1.11 and Corollary 1.12 of
 * [Ma]).
 * 
 * If (X) is countably generated, X is a Radon space if and only if it is Borel
 * isomorphic to a universally measurable subset of [0,1]ℕ (or any other
 * uncountable compact metrizable space). In particular, any polish space (see
 * Descriptive set theory), or more generally Suslin space (see measure) in the
 * sense of Bourbaki, is Radon. Duality with continuous functions
 * 
 * Following N. Bourbaki (and ideas going back to W.H. Young and Ch. de la
 * Vallée-Poussin), a (non-negative) Radon measure on a locally compact space X
 * is a continuous linear functional L on the space Cc(X) of continuous
 * functions with compact support (endowed with its natural inductive topology)
 * which is nonnegative, i.e. such that L(f)≥0 whenever f≥0 (cp. with Section
 * 2.2 of Chapter III in [Bo] or Section 9 of Chapter III in [HS]). One can
 * prove with the help of the Riesz representation theorem that any non-negative
 * and bounded Radon measure in this sense is the restriction to Cc(X) of the
 * integral with respect to a unique (non-finite) Radon measure in the sense of
 * the definition above.
 * 
 * 
 * @author crow
 *
 */
public interface RadonMeasure<E extends CompleteSeparableMetricSpace<?, ?>>
                             extends
                             BoundedlyFiniteMeasure<E>,
                             SigmaFiniteMeasure<SigmaField<E>>,
                             MeasurableSpace<E, SigmaField<E>>,
                             InnerRegularMeasure<E, SigmaField<E>>,
                             OuterRegularMeasure<E, SigmaField<E>>
{

}
