package stochastic.measures;

import java.util.function.Function;

import math.Isomorphism;

public interface MeasurableMapping<FROM extends MeasurableSpace<?, ?>, TO extends MeasurableSpace<?, ?>> extends Isomorphism<FROM, TO>, Function<FROM, TO>
{

}
