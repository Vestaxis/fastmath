package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;

import java.io.IOException;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import junit.framework.TestCase;

public class StandardExponentialHawkesProcessTest extends TestCase
{
	public void testΛ() throws IOException
	{
		double[] α = new double[]
		{ 0.1, 0.4 };
		double[] β = new double[]
		{ 1.3, 1.7 };
		StandardExponentialHawkesProcess process = new StandardExponentialHawkesProcess(0.1, α, β);
		Vector data = MatFile.loadMatrix("/data/SPY.mat", "SPY").col(0);
		int midpoint = data.size() / 2;
		data = data.slice(midpoint - 50, midpoint + 50);
		process.T = data;
		
		double totalΛ = process.totalΛ();
		out.println( "totalΛ=" + totalΛ );
		
		// process.estimateParameters(15);
		process.recursive = true;
		Vector recursiveComp = process.Λ();
		double recursiveMean = recursiveComp.mean();
		double recursiveVar = recursiveComp.variance();
		out.println( "recursive Λ=" + recursiveComp ); 
		out.println( "recursive Λ size " + recursiveComp.size() );
		out.println("recursive mean=" + recursiveMean);
		out.println("recursive var=" + recursiveVar);
		process.recursive = false;
		Vector comp = process.Λ();
		double mean = comp.mean();
		double var = comp.variance();
		out.println( "comp Λ=" + comp );
		out.println( "Λ size " + recursiveComp.size() );
		out.println("mean=" + mean);
		out.println("var=" + var);
		
		double compSum = comp.sum();
    out.println( "Σcomp = " + compSum );
    
		out.println( "Σrecursivecomp = " + recursiveComp.sum() );
		
		assertEquals( totalΛ, compSum, pow( 10, -14 ) );
		
		assertEquals(mean, recursiveMean);
		assertEquals(var, recursiveVar);

	}

}
