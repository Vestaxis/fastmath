package stochastic.measures;

import java.util.IntSummaryStatistics;

import math.Set;
import math.space.CompleteSeparableMetricSpace;
import stochastic.SigmaField;

/**
 * also known as a <b>point measure</b>
 * 
 * a {@link CountingMeasure} is a {@link CompleteSeparableMetricSpace} in its
 * own right
 * 
 * TODO: see if this can work with {@link IntSummaryStatistics}, etc
 * 
 * @author crow
 *
 * @param <X>
 */
public interface CountingMeasure<X extends Set> extends MeasurableSpace<X, SigmaField<X>>
{
  public int count( X point );
}
