package stochastic.probability;

import stochastic.measures.MeasurableSpace;

/**
 * 
 * @see Hiai, F. and Petz, D. The Semicircle Law, Free Random Variables and
 *      Entropy 2000 Vol. 77
 * 
 * @author crow
 *
 */
public interface FreeRandomVariable<P extends ProbabilitySpace<?, ?, ?>, X extends MeasurableSpace<?, ?>, Y extends MeasurableSpace<?, ?>>
                                   extends
                                   RandomVariable<P, X, Y>
{

}
