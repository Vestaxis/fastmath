package stochastic;

import numbersystems.RealNumbers;
import stochastic.order.IndexSet;

/**
 * 
 * A σ-algebra defines the set of events that can be measured, which in a
 * probability context is equivalent to events that can be discriminated, or
 * "questions that can be answered at time t". Therefore a filtration is often
 * used to represent the change in the set of events that can be measured,
 * through gain or loss of information. A typical example is in mathematical
 * finance, where a filtration represents the information available up to and
 * including each time t, and is more and more precise (the set of measurable
 * events is staying the same or increasing) as more information from the
 * evolution of the stock price becomes available.
 * 
 * @author crow
 *
 */
public interface Filtration extends IndexSet<RealNumbers>
{

}
