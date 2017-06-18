package stochastic.processes.pointprocesses;

import math.space.CompleteSeparableMetricSpace;

/**
 * A PointProcess N is called self-exciting if cov(N(s,t),N(t,u))>0 for s<t<u
 * where here, cov denotes the covariance of the two quantities. Intuitively, a
 * process is self-exciting if the occurrence of past points makes the
 * occurrence of future points more probable.
 * 
 * @param <X>
 */
public interface SelfExcitingPointProcess<X extends CompleteSeparableMetricSpace<?, ?>> extends PointProcess<X>
{

}
