package stochastic.processes.hawkes;

interface Bound 
{
  public String getName();
  
  public double getMin();
  
  public double getMax();
}