package stochastic.processes.hawkes;

import static java.lang.System.out;

import fastmath.Vector;
import junit.framework.TestCase;

public class HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernelTest extends TestCase
{
	public void testIntensity()
	{
		HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernel process = new HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernel( 0.75, 0.2, 0.9, 0.35, 0.1 );
		Vector T = new Vector( new double[] { 0.3, 0.5, 0.9, 1.2, 1.4, 2.0 } );
		double intensity = process.λ(T, 1.3);
		out.println( "intensity=" + intensity );
	}
}
