package stochastic.processes.pointprocesses;

import java.util.function.Function;

import fastmath.Vector;
import numbersystems.PositiveRealNumbers;
import numbersystems.RealNumbers;

/**
 * TODO \ The conditional intensity lambda(t) associated to a temporal point
 * process N is defined to be the expected infinitesimal rate at which events
 * are expected to occur around time t given the history of N at times prior to
 * time t. Algebraically,
 * lambda(t)=lim_(Deltat->0)(E{N(t,t+Deltat)|H_t})/(Deltat)
 * 
 * provided the limit exists where here, H_t is the history of N over all times
 * strictly prior to time t.
 */
public interface ConditionalIntensityFunction extends Function<RealNumbers, PositiveRealNumbers>
{

  double value( Vector times, double t );

}
