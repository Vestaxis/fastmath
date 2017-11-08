package stochastic.processes.selfexciting.multivariate;

import fastmath.IntVector;
import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;

public abstract class MultivariateSelfExcitingProcess extends AbstractSelfExcitingProcess
{


  /**
   * integer-array indicating which dimension to which each point in
   * this{@link #T} corresponds
   */
  public IntVector K;
  
  public MultivariateSelfExcitingProcess()
  {
    super();
  }

  @Override
  public Object clone()
  {
    assert T != null : "T is null";
    assert K != null : "K is null";
    assert X != null : "X is null";
    
    try
    {
      MultivariateSelfExcitingProcess spawn = getClass().newInstance();
      spawn.assignParameters(getParameters().toArray());
      spawn.T = T;
      spawn.X = X;
      spawn.K = K;
      return spawn;
    }
    catch (InstantiationException | IllegalAccessException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  
  }

}