package stochastic.processes.selfexciting.multivariate;

import static java.lang.Math.random;

import fastmath.Vector;
import junit.framework.TestCase;
import stochastic.processes.selfexciting.ExtendedApproximatePowerlawSelfExcitingProcess;

public class MultivariateExtendedApproximatePowerlawSelfExcitingProcessTest extends TestCase
{

  public void testGetAndAssignParameters()
  {
    MultivariateExtendedApproximatePowerlawSelfExcitingProcess process = new MultivariateExtendedApproximatePowerlawSelfExcitingProcess(2);
    int paramCount = process.getParamCount() * process.dim;
    Vector randomParams = new Vector(paramCount);
    for (int i = 0; i < randomParams.size(); i++)
    {
      randomParams.set(i, random());
    }
    process.assignParameters(randomParams.toDoubleArray());
    Vector gotParams = process.getParameters();
    assertEquals(randomParams, gotParams);
    // out.println( "paramCount=" + paramCount );
  }

  
}
