package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.Math.random;
import static java.lang.System.out;

import java.io.IOException;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import junit.framework.TestCase;

@SuppressWarnings( { "deprecation", "unused", "unchecked" } )
public class ExtendedExponentialPowerlawHawkesProcessTest extends TestCase
{
	public void testKernel()
	{
		double ε = 0.2;
		double τ = 0.9;
		double η = 0.35;
		double b = 0.1;
		ExtendedExponentialPowerlawHawkesProcess process = new ExtendedExponentialPowerlawHawkesProcess( η, τ, ε, b );
		double r = process.ψ(1.3);
		out.println( "r=" + r );
		assertEquals( 0.9818810701e-1, r, pow(10,-10));
	}
	
	public void testIntensity()
	{
		double ρ = 0.75;
		double ε = 0.2;
		double τ = 0.9;
		double η = 0.35;
		double b = 0.1;
		ExtendedExponentialPowerlawHawkesProcess process = new ExtendedExponentialPowerlawHawkesProcess( η, τ, ε, b );
		process.eventTimes = new Vector( new double[] { 0.3, 0.5, 0.9, 1.2, 1.4, 2.0 } );
		double intensity = process.λ(1.8);
		out.println( "intensity=" + intensity );
		assertEquals( .9073429721, intensity, pow(10,-9));
	}
	
	public void testLL()
	{
		double ρ = 0.75;
		double ε = 0.2;
		double τ = 0.9;
		double η = 0.35;
		double b = 0.01;
		ExtendedExponentialPowerlawHawkesProcess process = new ExtendedExponentialPowerlawHawkesProcess( η, τ, ε, b );
		process.eventTimes = new Vector( new double[] { 0.3, 0.5, 0.9, 1.2, 1.4, 2.0, 2.04, 2.06, 2.08 } );
		out.println( "log-likelihood=" + process.logLik() );
		process.setη(1.0);
		out.println( "log-likelihood=" + process.logLik() );
		process.setη(0.7);
		out.println( "log-likelihood=" + process.logLik() );
	}
	
	public void testiΨ()
	{		
		double ρ = 0.75;
		double ε = 0.2;
		double τ = 0.9;
		double η = 0.35;
		double b = 0.1;
		ExtendedExponentialPowerlawHawkesProcess process = new ExtendedExponentialPowerlawHawkesProcess( η, τ, ε, b );
		double phi = process.iψ( 0.4 );
		assertEquals( .1994241620, phi, pow(10,-9));
	}
	
	
	public void testEstimateParmeters()
	{
//		double ρ = 1;
//		double ε = 0.2;
//		double τ = 0.9;
//		double η = 0.35;
//		double b = 0.1;
//		ExponentialPowerlawHawkesProcess process = new ExponentialPowerlawHawkesProcess( ρ, η, τ, ε, b );
//		process.eventTimes = new Vector( new double[] { 0.3, 0.5, 0.9, 1.2, 1.4, 2.0, 2.04, 2.06, 2.08 } );
//		int evals = process.estimateParameters(9);
//		out.println( evals + " iterations");
		
	}
	
	public void testEstimateParmeters2() throws IOException
	{
		double ρ = 1;
		double ε = 0.25;
		double τ = 0.4;
		double η = 0.8;
		double b = 0.1;
		ExtendedExponentialPowerlawHawkesProcess process = new ExtendedExponentialPowerlawHawkesProcess( η, τ, ε, b );
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

	private Vector randomizeContemperaneousPoints(Vector data)
	{
		Vector d = data.diff();
		for ( int i = 0; i < d.size(); i++ )
		{
			if ( d.get(i) == 0 )
			{
				d.set(i, random() );
			}			
		}
		return d.cumsum();
	}
	
}
