package stochastic.order;

import numbersystems.PositiveRealNumbers;
import stochastic.SigmaField;
import stochastic.measures.MeasurableSpace;
import stochastic.probability.ProbabilitySpace;
import stochastic.probability.RandomVariable;

/**
 * @see @Book{karr, Title = {Point processes and their statistical inference},
 *      Author = {Karr, A.}, Publisher = {CRC}, Year = {1991}, Volume = {7} }
 *      Section 2.1 p.54
 * 
 *      TODO: associated to each stopping time there is the associated
 *      {@link SigmaField}
 * 
 * @author crow
 *
 * @param <P>
 * @param <X>
 * @param <Y>
 */
public interface StoppingTime<P extends ProbabilitySpace<?, ?, ?>, X extends MeasurableSpace<?, ?>, Y extends MeasurableSpace<?, ?>>
                             extends
                             Time<PositiveRealNumbers>,
                             RandomVariable<P, X, Y>
{

}
