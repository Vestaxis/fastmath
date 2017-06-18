package stochastic.measures;

import math.Isomorphism;
import math.functions.Function;

public interface MeasurableMapping<FROM extends MeasurableSpace<?, ?>, TO extends MeasurableSpace<?, ?>> extends Isomorphism<FROM, TO>, Function<FROM, TO>
{

}
