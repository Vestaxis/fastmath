package stochastic.processes.hawkes;

import static java.lang.Math.pow;
import static java.lang.System.out;

import fastmath.Vector;
import junit.framework.TestCase;

public class HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernelTest extends TestCase
{
	public void testKernel()
	{
		double ρ = 0.75;
		double ε = 0.2;
		double τ = 0.9;
		double η = 0.35;
		double b = 0.1;
		HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernel process = new HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernel( ρ, η, τ, ε, b );
		double r = process.ψ(1.3);
		out.println( "r=" + r );
		assertEquals( 0.9818810701e-1, r, pow(10,-10));
	}
	
	public void testIntensity()
	{
		HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernel process = new HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernel( 0.75, 0.2, 0.9, 0.35, 0.1 );
		Vector T = new Vector( new double[] { 0.3, 0.5, 0.9, 1.2, 1.4, 2.0 } );
		double intensity = process.λ(T, 1.3);
		out.println( "intensity=" + intensity );
	}
}
