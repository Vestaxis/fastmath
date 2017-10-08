package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;

import java.io.File;
import java.io.IOException;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import junit.framework.TestCase;

public class ExponentialPowerlawHawkesProcessTest extends TestCase
{
	public void testΨ()
	{
		ExponentialHawkesProcess process = new ConstrainedExponentialPowerlawHawkesProcess(1.4, 0.25);
		double x = process.ψ(1.3);
		// assertEquals(0.11591305818947, x, pow(10,-9));
		// out.println( "x=" + x );
	}

	public void testiΨ()
	{
		ExponentialHawkesProcess process = new ConstrainedExponentialPowerlawHawkesProcess(1.4, 0.25);
		double x = process.iψ(1.3);
		assertEquals(.18284483319013261698230044979325998875927092907043, x, pow(10, -9));
		// out.println( "x=" + x );
	}

	public void testEstimateParmeters2() throws IOException, CloneNotSupportedException
	{
		double ε = 0.16710;
		double η = 1.58128;
		ExponentialPowerlawHawkesProcess process = new ConstrainedExponentialPowerlawHawkesProcess(η, ε);
		Vector data = MatFile.loadMatrix("/home/stephen/git/fastmath/SPY.mat", "SPY").col(0).setName("data");
		process.T = data;
		int midpoint = data.size() / 2;
		data = data.slice(midpoint - 5000, midpoint + 5000);

		process.T = data;
		process.recursive = true;
		int evals = process.estimateParameters(15);
		File testFile = new File("test.mat");
		Vector compensator = process.Λ().setName("comp");
		out.println( "writing timestamp data and compensator to " + testFile.getAbsolutePath() );
    MatFile.write(testFile, data.createMiMatrix(), compensator.createMiMatrix());

		Vector comp = process.Λ();
		out.println( "comp mean=" + comp.mean() );
		out.println( "comp var=" + comp.variance() );
		
		 out.println( evals + " iterations");

	}

//	public void testLogLik() throws IOException
//	{
//
//		double ε = 0.16710;
//		double η = 1.58128;
//		ExponentialHawkesProcess process = new ExponentialPowerlawHawkesProcess(η, ε);
//		Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
//		int midpoint = data.size() / 2;
//		data = data.slice(midpoint - 250, midpoint + 250);
//		process.T = data;
//		double llNonRecursive = process.logLik();
//		process.recursive = true;
//		double llRecursive = process.logLik();
//		assertEquals( llNonRecursive, llRecursive, pow( 10, -10 ) );
//	}

	public void testΛ() throws IOException
	{
		double ε = 0.15;
		double η = 1.6;
		ExponentialHawkesProcess process = new ConstrainedExponentialPowerlawHawkesProcess(η, ε);
		Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
		StandardExponentialHawkesProcessTest.doTest(process, data);

	}

}
