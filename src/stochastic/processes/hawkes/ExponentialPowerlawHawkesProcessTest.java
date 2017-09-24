package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;

import java.io.IOException;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import junit.framework.TestCase;

public class ExponentialPowerlawHawkesProcessTest extends TestCase
{
	public void testΨ()
	{
		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess(1.4, 0.25);
		double x = process.ψ(1.3);
		// assertEquals(0.11591305818947, x, pow(10,-9));
		// out.println( "x=" + x );
	}

	public void testiΨ()
	{
		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess(1.4, 0.25);
		double x = process.iψ(1.3);
		assertEquals(.18284483319013261698230044979325998875927092907043, x, pow(10, -9));
		// out.println( "x=" + x );
	}

//	public void testEstimateParmeters2() throws IOException
//	{
//		double ε = 0.16710;
//		double η = 1.58128;
//		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess(η, ε);
//		Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
//		process.T = data;
//		int midpoint = data.size() / 2;
//		data = data.slice(midpoint - 500, midpoint + 500);
//
//		process.T = data;
//		int evals = process.estimateParameters(15);
//		MatFile.write("/data/test.mat", data.setName("d").createMiMatrix(), process.Λ().setName("C").createMiMatrix());
//
//		// out.println( evals + " iterations");
//
//	}

	public void testLogLik() throws IOException
	{

		double ε = 0.16710;
		double η = 1.58128;
		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess(η, ε);
		Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
		int midpoint = data.size() / 2;
		data = data.slice(midpoint - 250, midpoint + 250);
		process.T = data;
		double llNonRecursive = process.logLik();
		process.recursive = true;
		double llRecursive = process.logLik();
		assertEquals( llNonRecursive, llRecursive, pow( 10, -10 ) );
	}

	public void testΛ() throws IOException
	{
		double ε = 0.15;
		double η = 1.6;
		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess(η, ε);
		Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
		int midpoint = data.size() / 2;
		data = data.slice(midpoint - 250, midpoint + 250);
		process.T = data;
		process.estimateParameters(15);
		Vector comp = process.Λ();
		double compMean = comp.mean();
		double compVar = comp.variance();
		out.println( "mean=" + compMean );
		out.println( "var=" + compVar );

	}

}
