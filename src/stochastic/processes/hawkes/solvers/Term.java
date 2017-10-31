package stochastic.processes.hawkes.solvers;

import java.util.ArrayList;
import java.util.Collection;

public class Term extends ArrayList<String>
{
  public Term(int multiplier, Collection<? extends String> arg0)
  {
    super(arg0);
    this.multiplier = multiplier;
  }

  private static final long serialVersionUID = 1L;
  
  private int multiplier;

  public int getMultiplier()
  {
    return multiplier;
  }

  public void setMultiplier(int multiplier)
  {
    this.multiplier = multiplier;
  }
  

}
