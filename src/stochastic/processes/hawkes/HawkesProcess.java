package stochastic.processes.hawkes;

import fastmath.Vector;

public interface HawkesProcess
{

  double getBranchingRatio();

  double logLikelihood(Vector t);

  /**
   * 
   * @return unconditional mean intensity
   */
  double λ();

  /**
   * 
   * @param t
   * @return conditional mean intensity
   */
  double λ(double t);

}