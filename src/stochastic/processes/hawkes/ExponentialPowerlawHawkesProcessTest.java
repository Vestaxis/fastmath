package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;

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
}
