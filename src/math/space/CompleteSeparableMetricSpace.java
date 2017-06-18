package math.space;

import math.Set;
import math.Topology;

/**
 * usually abbreviated in the literature as <b>c.s.m.s.</b>
 * 
 * @see intro2pp#5.3
 * 
 * @author crow
 *
 */
public interface CompleteSeparableMetricSpace<X extends Set, B extends Topology<?>> extends CompleteSeparableSpace<X, B>, MetricSpace<X>
{

}
