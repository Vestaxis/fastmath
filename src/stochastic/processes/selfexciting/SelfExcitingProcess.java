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

  public double
         Φδ(double t,
            double y);

  public double
         Φδ(double t,
            double y,
            int tk);

  /**
   * 
   * @param t
   * @return conditional mean intensity
   */
  double λ(double t);

  public Vector
         Λ();

}