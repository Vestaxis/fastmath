package stochastic.processes.pointprocesses;

public interface MarkedPointProcess<M extends MarkedPoint>
{

  MarkedPointProcess<M> splitIntoUpAndDownThinnedProcesses( int i, int pennies );

  int size();

  MarkedPoint get( int i );

}
