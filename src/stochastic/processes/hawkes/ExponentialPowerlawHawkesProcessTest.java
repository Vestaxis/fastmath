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
		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess( 1.4, 0.25 );
		double x = process.ψ(1.3);
		assertEquals(0.132305903, x, pow(10,-9));
		//out.println( "x=" + x );
	}
	
	public void testiΨ()
	{
		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess( 1.4, 0.25 );
		double x = process.iψ(1.3);
		assertEquals(.18284483319013261698230044979325998875927092907043, x, pow(10,-9));
		//out.println( "x=" + x );
	}
	
//	public void testEstimateParmeters2() throws IOException
//	{
//		double ε = 0.15;
//		double η = 1.6;
//		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess(  η, ε  );
//		Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
//		process.T = data;
//		int midpoint = data.size() / 2;
//		data = data.slice( midpoint - 250, midpoint + 250 );
//		
//		process.T = data;
//		int evals = process.estimateParameters(15);
//		MatFile.write("/data/test.mat", data.setName("d").createMiMatrix(), process.Λ().setName("C").createMiMatrix() );
//
//		out.println( evals + " iterations");
//		
//	}
	
	public void testΛ() throws IOException
	{
		double ε = 0.15;
		double η = 1.6;
		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess(  η, ε  );
		Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
		int midpoint = data.size() / 2;
		data = data.slice( midpoint - 250, midpoint + 250 );
		process.T = data;
		Vector comp = process.Λ();
		Vector rcomp = process.recursiveΛ();

		out.println( "Λ=" + comp );
		out.println( "rΛ=" + rcomp );
		
	}

}
