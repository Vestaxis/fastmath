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
	
	public void testEstimateParmeters2() throws IOException
	{
		double ε = 0.25;
		double η = 0.8;
		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess(  η, ε  );
		Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
		process.eventTimes = data;
		int midpoint = data.size() / 2;
		data = data.slice( midpoint - 500, midpoint + 500 );
		
//		while ( data.diff().find(0.0, Condition.EQUAL, 0) != -1 )
//		{
//			data = randomizeContemperaneousPoints( data );	
//		}
		
		//process.eventTimes = new Vector( new double[] { 0.3, 0.5, 0.9, 1.2, 1.4, 2.0, 2.04, 2.06, 2.08 } );
		process.eventTimes = data;
		int evals = process.estimateParameters(15);
		MatFile.write("/data/test.mat", data.setName("d").createMiMatrix(), process.calculateCompensator().setName("C").createMiMatrix() );

		out.println( evals + " iterations");
		
	}
}
