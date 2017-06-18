package stochastic.measures;

import math.space.CompleteSeparableMetricSpace;
import stochastic.SigmaField;

/**
 * @see Daley, D. and Vere-Jones, D. An Introduction to the Theory of Point
 *      Processes, Volume II: General Theory and Structure 2008
 * 
 * @author crow
 *
 * @param <X>
 */
public interface CampbellMeasure<X extends CompleteSeparableMetricSpace<?, ?>> extends Measure<SigmaField<X>>
{

}
