package math.space;

import math.Set;
import math.Topology;
import math.metrics.Metric;

/**
 * 
 * metric space, n. a set endowed with a metric; this induces a topology on the
 * set in which W is open if and only if for all x in W, there is a positive e
 * such that the open ball Be(x) is contained in W.
 * 
 * 
 * @author crow
 *
 * @see
 */
public interface MetricSpace<X extends Set> extends Space, Topology<X>
{
  public Metric<X> getMetric();
}
