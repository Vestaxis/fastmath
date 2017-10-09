package stochastic.processes.hawkes;

import static fastmath.Console.println;
import static java.lang.System.out;

import java.io.File;
import java.io.IOException;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import stochastic.processes.hawkes.ExponentialHawkesProcessFactory.Type;

public class HawkesProcessEstimator
{
  public static void main(String[] args) throws IOException, CloneNotSupportedException
  {
    String filename = args.length > 0 ? args[0] : "/home/stephen/git/fastmath/SPY.mat";

    HawkesProcessEstimator estimator = new HawkesProcessEstimator();
    Vector data = loadData(filename, "SPY");
    estimator.type = Type.ExponentialPowerlawApproximation;
    estimator.estimate(data);
  }

  ExponentialHawkesProcessFactory.Type type = Type.ConstraintedExponentialPowerlawApproximation;
  
  private boolean verbose = true;
  
  public void estimate(Vector data) throws IOException
  {
    ExponentialHawkesProcess process = ExponentialHawkesProcessFactory.spawnNewProcess(type);
    if ( verbose )
    {
      println( "spawning " + process.getClass().getSimpleName() );
    }

    process.T = data;
    int evals = process.estimateParameters();
    
    File testFile = new File("test.mat");
    Vector compensator = process.Λ().setName("comp");
    out.println("writing timestamp data and compensator to " + testFile.getAbsolutePath());
    MatFile.write(testFile, data.createMiMatrix(), compensator.createMiMatrix());

    Vector comp = process.Λ();
    out.println("comp mean=" + comp.mean());
    out.println("comp var=" + comp.variance());

    out.println(evals + " iterations");
  }

  public static Vector loadData(String filename, String symbol) throws IOException
  {
    Vector data = MatFile.loadMatrix(filename, symbol).col(0).setName("data");
    int midpoint = data.size() / 2;
    data = data.slice(midpoint - 5000, midpoint + 5000);
    return data;
  }
}
