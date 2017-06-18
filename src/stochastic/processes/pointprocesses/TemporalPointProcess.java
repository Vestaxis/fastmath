package stochastic.processes.pointprocesses;

import math.space.CompleteSeparableMetricSpace;

/**
 * TODO
 * 
 * @see http://mathworld.wolfram.com/ConditionalIntensityFunction.html
 * 
 *      The conditional intensity lambda(t) associated to a temporal point
 *      process N is defined to be the expected infinitesimal rate at which
 *      events are expected to occur around time t given the history of N at
 *      times prior to time t. Algebraically,
 *      lambda(t)=lim_(Deltat->0)(E{N(t,t+Deltat)|H_t})/(Deltat)
 * 
 *      provided the limit exists where here, H_t is the history of N over all
 *      times strictly prior to time t. SEE ALSO: Expectation Value, Limit,
 *      Point Process, Temporal Point Process
 * 
 *      This entry contributed by Christopher Stover REFERENCES:
 * 
 *      Schoenberg, F. P. "Introduction to Point Processes." CITE THIS AS:
 * 
 *      Stover, Christopher. "Conditional Intensity Function." From MathWorld--A
 *      Wolfram Web Resource, created by Eric W. Weisstein.
 *      http://mathworld.wolfram.com/ConditionalIntensityFunction.html
 * @param <X>
 */
public interface TemporalPointProcess<X extends CompleteSeparableMetricSpace<?, ?>> extends PointProcess<X>
{

}
