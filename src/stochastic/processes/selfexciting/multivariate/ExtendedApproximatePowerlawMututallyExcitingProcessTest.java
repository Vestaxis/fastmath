package stochastic.processes.selfexciting.multivariate;

import static java.lang.Math.random;
import static java.lang.System.out;
import static org.fusesource.jansi.Ansi.ansi;

import org.arblib.Real;

import fastmath.IntVector;
import fastmath.Vector;
import junit.framework.TestCase;
import stochastic.processes.selfexciting.ExtendedApproximatePowerlawSelfExcitingProcess;

public class ExtendedApproximatePowerlawMututallyExcitingProcessTest extends TestCase
{

  public void
         testA()
  {
    ExtendedApproximatePowerlawMututallyExcitingProcess process = constructProcess();
    process.T = new Vector(3);
    process.T.set(0, 0);
    process.T.set(1, 19);
    process.T.set(2, 27);
    process.K = new IntVector(3);
    process.T.set(0, 0);
    process.T.set(1, 1);
    process.T.set(2, 0);

    for (int type = 0; type < process.dim(); type++)
    {
      for (int tk = 0; tk < process.T.size(); tk++)
      {
        for (int j = 0; j < process.order(); j++)
        {
          double a = process.Asum(type, tk, j);
          double b = process.A(type, tk, j);
          Real c = process.AReal(type, tk, j);
          double d = process.B(type, tk, j);
          //out.println("a=" + a + " b=" + b + " c=" + c + " d=" + d);
          assertEquals(a, b, 1E-14);
          assertEquals(a, 1 + d, 1E-14);
        }
      }
    }

  }

  public void
         testGetAndAssignParameters()
  {
    ExtendedApproximatePowerlawMututallyExcitingProcess process = new ExtendedApproximatePowerlawMututallyExcitingProcess(2);
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

  public static ExtendedApproximatePowerlawMututallyExcitingProcess
         constructProcess()
  {
    final ExtendedApproximatePowerlawMututallyExcitingProcess process = new ExtendedApproximatePowerlawMututallyExcitingProcess(2);

    process.assignParameters(new double[]
    { 1, 0, 3, 1.78, 1, 0.01, 2.99, 1.75 });
    return process;
  }

  public void
         testInvLambda()
  {
    ExtendedApproximatePowerlawMututallyExcitingProcess process = constructProcess();
    // process.ε = 0.05;

    process.T = new Vector(new double[]
    { 65, 67, 86, 140, 141, 149, 151, 163, 201, 205 }).setName("T");
    int n = process.T.size();

    // process.T = process.T.subtract(process.T.get(0));
    process.trace = false;

    // process.printResults( process.estimateParameters(25) );

    // out.println( " Λ=" + process.Λ() );
    // for (int tk = 0; tk < 10; tk++)
    // {
    // out.println("A[" + tk + "]=" + Arrays.toString(process.A[tk]));
    // }
    out.println("estimated " + ansi().fgBrightYellow() + process + ansi().fgDefault() + " from " + process.T.size() + " points");
    out.println(process.getαβString());
    process.trace = false;
    for (int k = 0; k < process.dim(); k++)
    {
      double Λmean = process.Λ(k).mean();
      process.trace = false;
      double Λvar = process.Λ(k).variance();
      out.println("k=" + k + " Λmean=" + ansi().fgBrightRed() + Λmean + ansi().fgDefault() + " Λvar=" + ansi().fgBrightRed() + Λvar + ansi().fgDefault());
    }

    out.println(ansi().fgBrightGreen() + process.T.toString() + ansi().fgDefault());
    process.dT = null;
    out.println(ansi().fgBrightGreen() + process.dT().toString() + ansi().fgDefault());

  }

}
