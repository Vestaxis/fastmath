package stochastic.processes.hawkes;

import fastmath.DoubleColMatrix;
import fastmath.Vector;

public interface HawkesProcess
{

	double getBranchingRatio();

	double logLikelihood(Vector t);

	double getUnconditionalIntensity();

	public DoubleColMatrix getJacobian(Vector t);

	public DoubleColMatrix getHessian(Vector t);

	int order();

	double evolveλ(double dt, double[] R);

}