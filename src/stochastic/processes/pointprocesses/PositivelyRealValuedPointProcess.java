package stochastic.processes.pointprocesses;

import numbersystems.PositiveRealNumbers;

/**
 * 
 * @see karr Ch2.3
 * 
 * @author sc
 *
 */
public interface PositivelyRealValuedPointProcess extends PointProcess<PositiveRealNumbers>
{
  /**
   * 
   * @param Filtration
   * @return
   */
  // public <F extends Filtration<PositiveRealNumber,
  // SigmaField<PositiveRealNumber>>> RandomMeasure<?, ?> getCompensator( F
  // Filtration );

}
