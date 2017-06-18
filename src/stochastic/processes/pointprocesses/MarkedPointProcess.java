package stochastic.processes.pointprocesses;

import math.space.PolishSpace;

public interface MarkedPointProcess<M extends MarkedPoint> extends PointProcess<PolishSpace<?, ?>>
{

  MarkedPointProcess<M> splitIntoUpAndDownThinnedProcesses( int i, int pennies );

  int size();

  MarkedPoint get( int i );

}
