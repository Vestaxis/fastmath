package stochastic.measures;

import math.space.CompleteSeparableMetricSpace;
import stochastic.SigmaField;

/**
 * 𝔐^#_𝒳
 * 
 * @author crow
 *
 * @param <𝒳>
 */
public interface BoundedlyFiniteMeasure<𝒳 extends CompleteSeparableMetricSpace<?, ?>> extends Measure<SigmaField<𝒳>>
{

}
