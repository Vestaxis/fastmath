package stochastic.processes.selfexciting.multivariate;

import static java.lang.Math.random;
import static java.lang.System.out;

import fastmath.Vector;
import junit.framework.TestCase;

public class MultivariateExtendedApproximatePowerlawSelfExcitingProcessTest extends TestCase
{
  public void testAssignParameters()
  {
    MultivariateExtendedApproximatePowerlawSelfExcitingProcess process = new MultivariateExtendedApproximatePowerlawSelfExcitingProcess(2);
    int paramCount = process.getParamCount() * process.dim;
    Vector randomParams = new Vector(paramCount);
    for (int i = 0; i < randomParams.size(); i++)
    {
      randomParams.set(i, random());
    }
    process.assignParameters(randomParams.toArray());
    Vector gotParams = process.getParameters();
    assertEquals( randomParams, gotParams );
    // out.println( "paramCount=" + paramCount );
  }
}
