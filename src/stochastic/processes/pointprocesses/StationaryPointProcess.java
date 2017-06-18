package stochastic.processes.pointprocesses;

import math.space.EuclideanSpace;
import numbersystems.PositiveIntegers;
import stochastic.SigmaField;
import stochastic.measures.MomentMeasure;
import stochastic.measures.PalmMeasure;
import stochastic.measures.ReducedMomentMeasure;
import stochastic.probability.ProbabilitySpace;

/**
 * 
 * Stationary point processes in {@link EuclideanSpace} or the
 * {@link PositiveIntegers} have distributions which remain the same regardless
 * of their relative positions
 * 
 * @author sc
 *
 *
 * @see Definition 1.7,1.55-1.56 in karr
 */
public interface StationaryPointProcess<E extends ProbabilitySpace<?, ?, ?>> extends PointProcess<E>
{

  /**
   * The Palm measure is the unique finite measure P^∗(x)=ν_N Q_N^∗(ε_0,x) on Ω,
   * and where ε_x(A)= 1 x∈A 0 x∉A is the 'degenerate' point mass distribution
   * function, also known as the indicator function.
   * 
   * @return
   */
  public PalmMeasure<SigmaField<? extends E>> getPalmMeasure();

  /**
   * The intensity of a stationary point process N is the constant ν_N∈[0,∞]
   * which is a scalar multiple of the normalized Haar measure λ and related to
   * the first moment measure μ_N by μ_N=ν_N*λ.
   * 
   * @return the intensity of the process, ranging from and including 0 thru
   *         positive infinity, the {@link PalmMeasure} of this process exists
   *         if this {@link #getIntensity()} is finite, that is, less than
   *         infinity
   */
  public double getIntensity();

  /**
   * the moment measure μ^k is a (k-1)-dimensional mixture of certain images of
   * Lebesgue measure. For z∈E^(k-1) let λ_z(x) be the image of λ under the
   * mapping x→(z_1+x,…,z_(k-1)+x,x)
   * 
   * 
   * @param k
   *          order
   * 
   * @return the reduced moment measure of order k
   */
  public MomentMeasure<SigmaField<? extends E>> getMomentMeasure( int k );

  /**
   * μ^k(⋅)=int_(E^(k-1))λ_z(⋅)μ_∗^k(z)dz
   * 
   * @param k
   *          order
   * 
   * @return the reduced moment measure of order k
   */
  public ReducedMomentMeasure<SigmaField<? extends E>> getReducedMomentMeasure( int k );

}
