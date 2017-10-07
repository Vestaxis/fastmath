package stochastic.processes.hawkes;

public interface BoundedParameter
{
  public String getName();

  public int getOrdinal();

  public double getMin();

  public double getMax();
  

}