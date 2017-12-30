package stochastic.processes.selfexciting.multivariate;

import static java.lang.System.out;

import junit.framework.TestCase;

public class ExponentialMutuallyExcitingProcessTest extends TestCase
{
  public void
         testA()
  {
    ExtendedApproximatePowerlawMututallyExcitingProcess process = ExtendedApproximatePowerlawMututallyExcitingProcessTest.constructProcess();
    int j = 0;
    int m = 0;
    int n = 0;
    for (int i = 0; i < process.N(m, Double.POSITIVE_INFINITY); i++)
    {
      double a = process.Asum(j, m, n, i);
      out.format("A(j=%d,m=%d,n=%d,i=%d)=%f\n", j, m, n, i, a);
    }

  }
}
