package stochastic.processes.pointprocesses;

import math.space.CompleteSeparableMetricSpace;
import stochastic.measures.RandomMeasure;
import stochastic.probability.ProbabilitySpace;

/**
 * A point process N is a random distribution of indistinguishable points in E.
 * N(A) is the number of points in the set A.
 * 
 * N=sum_(i=1)^Kθ(X_i) where θ is the Heaviside step function and k is a random
 * integer from 0 to infinity
 * 
 * 
 * @see karr 1.1 equation (1.1)
 * 
 * @author sc
 *
 *         TODO: extend the {@link StochasticProcess} interface
 * @param <X>
 *
 * @param <X>
 */
public interface PointProcess<E extends CompleteSeparableMetricSpace<?, ?>> extends RandomMeasure<ProbabilitySpace<?, ?, ?>>
{

  /**
   * FIXME: impement The zero-probability functional of a point process N is the
   * mapping z_N:ℰ→[0,1] defined by z_N(A)=P{N(A)=0}.
   * 
   * @see karr Definition 1.11
   * 
   * @return
   */
  // public Operator<SigmaField<CompleteSeparableMetricSpace<?, ?>>, RealNumber>
  // getZeroProbabilityFunctional( PositiveMeasurableFunction<? extends X> f );

  /**
   * FIXME: The zero-probability functional of a point process N is the mapping
   * L_N defined by L_N(f)=E[e^(-N(f))]=E[e^(-sum_i(f(X_i)))] where E is the
   * expectation integral
   * 
   * @see karr Definition 1.10
   * 
   * @return
   */
  // public Operator<?, ?> getLaplaceFunctional( PositiveMeasurableFunction<?
  // extends X> f );
  // .public X getMetricSpace();

  // public CumulantMeasure<SigmaField<? extends X>> getCumulantMeasure( int k
  // );
}
