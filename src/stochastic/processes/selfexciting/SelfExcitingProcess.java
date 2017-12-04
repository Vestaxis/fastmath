package stochastic.processes.selfexciting;

import fastmath.Vector;

public interface SelfExcitingProcess
{

  double getBranchingRatio();

  double logLikelihood(Vector t);

  /**
   * 
   * @return unconditional mean intensity
   */
  double getStationaryλ();

  /**
   * 
   * @param t
   * @return conditional mean intensity
   */
  double λ(double t);

  public Vector
         dΛ();

  public Vector
         λvector();

  /*
   * maximum hazard rate
   */
  public double
         maxh();

  /**
   * minimum hazard rate
   */
  public double
         minh();

}