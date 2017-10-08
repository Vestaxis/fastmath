package stochastic.processes;

import static java.lang.Math.pow;
import static java.lang.System.out;

import fastmath.DoubleColMatrix;
import junit.framework.TestCase;

public class ZIPINARProcessTest extends TestCase
{
	public static void testP()
	{
		
		ZIPINARProcess process = new ZIPINARProcess( 0.5, 1.4, 0.3 );
		DoubleColMatrix p = new DoubleColMatrix( 5, 5 );
		for ( int i = 0; i < p.getRowCount(); i++ )
			for ( int j = 0; j < p.getColCount(); j++ )			
		{
			p.set(i,j,process.getTransitionProbability(i, j));
		}
		out.println(p);
		assertEquals( p.get( 0, 0 ), 0.6360293468, pow(10,-10) );
		assertEquals( p.get( 1, 0 ), 0.1748501433, pow(10,-10) );
		assertEquals( p.get( 2, 0 ), 0.1147555387, pow(10,-9) );
		assertEquals( p.get( 0, 1 ), 0.6313588845979898, pow(10,-10) );

	}
	
	public static void testForecasting()
	{
		  ZIPINARProcess ziProcess = new ZIPINARProcess( 0.14567629234644944, 56.21526081783455, 58.18806831717725);
		  	out.println( ziProcess );
		  	
		    //t.println( "Estimated " + ziProcess );
		    double zeroProb = ziProcess.forecastExpectedValue(0, 1);
		    out.println( "zero probability " + zeroProb );
		    

		    double oneProb = ziProcess.forecastExpectedValue(1, 1);
		    out.println( "one probability " + oneProb );		
//		    
//		    for ( int i = 0 ; i < 100; i++ )
//		    {
//		    	double ev = ziProcess.forecastExpectedValue(i, 1);
//		    	out.println( "ev=" + ev );
//		    }
	}
	
	public static void testMode()
	{
	
			  ZIPINARProcess ziProcess = new ZIPINARProcess( 0.14567629234644944, 56.21526081783455, 58.18806831717725);
			  	out.println( ziProcess );
			  	
			    //t.println( "Estimated " + ziProcess );
			    int zeroMode = ziProcess.getMode(0);
			    out.println( "zero mode " + zeroMode );
			    
			    for ( int i = 0 ; i < 20; i++ )
			    {
				    int nextMode = ziProcess.getMode(i);
				    out.format("%d -> %d\n", i, nextMode );
			    }
	}
}
