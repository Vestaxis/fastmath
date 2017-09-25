package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.System.out;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import fastmath.DoubleColMatrix;
import fastmath.Fastmath;
import fastmath.Vector;
import fastmath.Vector.Condition;
import fastmath.exceptions.FastMathException;

public class ExponentialHawkesProcess implements HawkesProcess
{
	public static void main(String args[]) throws InterruptedException
	{
		double[] alpha = new double[]
		{ 0.1, 0.4 };
		double[] beta = new double[]
		{ 1.3, 1.7 };
		ExponentialHawkesProcess hp = new ExponentialHawkesProcess(0.1, alpha, beta);
		hp.T = new Vector(new double[]
		{ 1.3, 1.4, 1.9, 2.5, 3.3, 3.9, 4.2, 4.5 });

		ExponentialPowerlawHawkesProcess eplhp = new ExponentialPowerlawHawkesProcess(1.6, 0.25);
		eplhp.T = hp.T;
		XYChart chart = new XYChart(800, 600);

		double W = hp.T.getRightmostValue();
		double dt = 0.001;
		int n = (int) (W / dt);

		int idx = 0;

		Vector X = new Vector(n);
		Vector Y1 = new Vector(n);
		Vector Y2 = new Vector(n);
		Vector Y3 = new Vector(n);
		Vector Svector = new Vector(n);
		for (int i = 0; i < X.size(); i++)
		{
			double t = i * dt;
			X.set(i, t);
			Y1.set(i, hp.λ(t));
			Y2.set(i, hp.λrecursive(t));
			Y3.set(i, eplhp.λ(t));
		}
		chart.addSeries("λexp", X.toPrimitiveArray(), Y1.toPrimitiveArray());
		chart.addSeries("λr", X.toPrimitiveArray(), Y2.toPrimitiveArray());
		chart.addSeries("λepl", X.toPrimitiveArray(), Y3.toPrimitiveArray());
		chart.getStyler().setMarkerSize(1);

		n = hp.T.size();
		X = new Vector(n);
		Y1 = new Vector(n);
		Svector = new Vector(n);
		final Vector Y5 = new Vector(n);

		double R[] = new double[hp.getOrder()];
		double S[] = new double[eplhp.M + 1];
		X.set(0, hp.T.get(0));
		Y1.set(0, hp.getLambda());
		for (int i = 1; i < n; i++)
		{
			double innersum = hp.getLambda();

			dt = hp.T.get(i) - hp.T.get(i - 1);

			innersum = hp.evolveR(dt, R, innersum);
			double othersum = eplhp.evolveλ(dt, S);

			X.set(i, hp.T.get(i));
			Y1.set(i, innersum);

			//othersum = othersum / eplhp.Z();
			Svector.set(i, othersum);
			Y5.set(i, eplhp.λ(hp.T.get(i)));
		}
		out.println("Y1=" + Y1);
		out.println("Y4=" + Svector);
		out.println("Y5=" + Y5);
		chart.addSeries("R[i]", X.toPrimitiveArray(), Y1.toPrimitiveArray());
		chart.addSeries("S[i]", X.toPrimitiveArray(), Svector.toPrimitiveArray());

		new SwingWrapper<>(chart).displayChart();

		// chartFunctions( new String[] { "λ", "λr" }, 0.01, 5, hp::λ, t ->
		// hp.λrecursive(t) );

		double intensity = hp.λ(1.7);
		out.println("intensity=" + intensity);
		while (true)
		{
			Thread.sleep(1000);
		}
	}

	@Override
	public String toString()
	{
		return String.format(
				"HawkesProcess[lambda=%f, alpha=%s, beta=%s, branchingRatio=%f, unconditionalIntensity=%f]", lambda, α,
				β, getBranchingRatio(), getUnconditionalIntensity());
	}

	public String toStringPos()
	{
		return String.format(
				"HawkesProcess[lambda=%f, alpha=%s, beta=%s, branchingRatio=%f, unconditionalIntensity=%f]",
				exp(lambda), α.copy().exp(), β.copy().exp(), getBranchingRatioPos(), getUnconditionalIntensityPos());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pp.HawkesProcess#getBranchingRatio()
	 */
	@Override
	public double getBranchingRatio()
	{
		return sum(i -> α.get(i) / β.get(i), 0, P);
	}

	public double getBranchingRatioPos()
	{
		return sum(i -> exp(α.get(i)) / exp(β.get(i)), 0, P - 1);

	}

	private int P;

	public Vector T;

	double evolveR(double dt, double[] R, double innersum)
	{
		for (int j = 0; j < getOrder(); j++)
		{
			R[j] = exp(-β.get(j) * dt) * (1 + R[j]);
			innersum += α.get(j) * R[j];
		}
		return innersum;
	}

	public double ν(double t)
	{
		return sum(i -> α.get(i) * exp(-β.get(i) * t), 0, P - 1);
	}

	public double λ(double t)
	{
		final int n = T.size();
		double λ = lambda;
		double s;
		for (int i = 0; i < n && (s = T.get(i)) < t; i++)
		{
			λ += ν(t - s);
		}
		return λ;
	}

	public double λrecursive(double t)
	{
		final int n = T.findLast(t, Condition.LT);
		double λ = lambda;
		if (n <= 0)
		{
			return λ;
		}
		// } else if ( n < 1 )
		// {
		// return λ + sum( i-> α.get(i), 0, getOrder() - 1 );
		// }
		double R[] = new double[n + 1];
		double Rsum = 0;

		for (int i = 1; i <= n; i++)
		{
			double impulse = getLambda();
			double dt = T.get(i) - T.get(i - 1);

			for (int j = 0; j < getOrder(); j++)
			{
				R[j] = exp(-β.get(j) * dt) * (1 + R[j]);
				impulse += α.get(j) * R[j];
				Rsum += R[j];
			}

			λ = impulse;
		}
		// λ += sum( j-> α.get(j) * exp(-β.get(j) * ( t - T.get(n) ) ), 0, getOrder() -
		// 1 );
		// λ += sum( i-> ν( t - T.get(i) ), 0, n );
		// λ += ν( t - Rsum );
		// λ += λ(t);
		// double dt = t - tn;
		// if (dt > 0)
		// {
		// for (int j = 0; j < P; j++)
		// {
		// R[j] = exp(-β.get(j) * dt) * (1 + R[j]);
		// λ += α.get(j) * R[j];
		// }
		// }

		return λ;
	}

	public ExponentialHawkesProcess(int order)
	{
		α = new Vector(order);
		β = new Vector(order);
		this.P = order;
	}

	public ExponentialHawkesProcess(double lambda2, Vector alpha2, Vector beta2)
	{
		assert alpha2.size() == beta2.size() : "alpha and beta dimensions must agree";
		this.lambda = lambda2;
		this.α = alpha2;
		this.β = beta2;
		this.P = α.size();

	}

	public ExponentialHawkesProcess(double lambda, double[] alpha, double[] beta)
	{
		this(lambda, new Vector(alpha), new Vector(beta));
	}

	public ExponentialHawkesProcess(ExponentialHawkesProcess hp)
	{
		this(hp.getLambda(), hp.getAlpha().copy(), hp.getBeta().copy());
		this.P = hp.P;
	}

	public Vector getAlpha()
	{
		return α;
	}

	public void setAlpha(Vector alpha)
	{
		this.α = alpha;
	}

	public Vector getBeta()
	{
		return β;
	}

	public void setBeta(Vector beta)
	{
		this.β = beta;
	}

	public int getOrder()
	{
		return P;
	}

	public void setOrder(int order)
	{
		this.P = order;
	}

	public double getLambda()
	{
		return lambda;
	}

	public void setLambda(double lambda)
	{
		this.lambda = lambda;
	}

	private double lambda;

	Vector α;

	Vector β;

	/*
	 * (non-Javadoc)
	 * 
	 * @see pp.HawkesProcess#logLikelihood(fastmath.Vector)
	 */
	@Override
	public double logLikelihood(Vector t)
	{
		return Fastmath.getHawkesLL(t, lambda, α, β);
	}

	public double logLikelihoodPos(Vector t)
	{
		return Fastmath.getHawkesLLPos(t, lambda, α, β);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pp.HawkesProcess#getUnconditionalIntensity()
	 */
	@Override
	public double getUnconditionalIntensity()
	{
		return lambda / (1 - getBranchingRatio());
	}

	public double getUnconditionalIntensityPos()
	{
		return exp(lambda) / (1 - getBranchingRatioPos());
	}

	public DoubleColMatrix getJacobian(Vector t)
	{
		assert α.size() == β.size() : "alpha and beta dimensions must be equal";
		int order = α.size();
		int dim = 1 + order * 2;

		DoubleColMatrix J = new DoubleColMatrix(dim, 1);

		double llLambdaDeriv = Fastmath.getHawkesLLLambdaDeriv(t, lambda, α, β);

		J.set(0, 0, llLambdaDeriv);

		for (int j = 0; j < order; j++)
		{

			double llAlphaDeriv = Fastmath.getHawkesLLAlphaDeriv(t, lambda, α, β, j);

			J.set(1 + (j * 2), 0, llAlphaDeriv);

			double llBetaDeriv = Fastmath.getHawkesLLBetaDeriv(t, lambda, α, β, j);

			J.set(2 + (j * 2), 0, llBetaDeriv);
		}

		return J;
	}

	public double iteratePos(Vector t) throws FastMathException
	{
		// out.println(
		// "********************************************ITERATINGPos***********************************************************"
		// );
		// TestCase.assertEquals( 1.4219737624112004435, llScoreSum, eps );
		// double llBefore = Fastmath.getHawkesLLPos( t, lambda, alpha, beta );
		// out.println( "LL before iteration " + llBefore );
		int order = α.size();

		DoubleColMatrix H = getHessianPos(t);
		Vector eigenVals = H.eig().getRealEigenvalues();
		double minEig = eigenVals.fmin();
		// out.println( "H=" + H.toMatrixString() );
		if (minEig < 0)
		{
			double meanAbsEig = eigenVals.abs().mean();
			H.diag().add(minEig + 1);
			out.println("corrected minEig=" + minEig);
		}

		double normBefore = H.supNorm();
		// System.out.println( "H= " + H );
		DoubleColMatrix Hinv = H.invert();
		double normAfter = Hinv.supNorm();
		// System.out.println( "1/H= " + Hinv );
		double cond = normBefore * normAfter;
		// System.out.println( "condition " + cond );

		DoubleColMatrix J = getJacobianPos(t);
		// System.out.println( "J= " + J );

		DoubleColMatrix delta = Hinv.prod(J);
		// System.out.println( "delta= " + delta );

		double lr = 0.5;
		lambda = lambda - lr * delta.get(0, 0);
		for (int j = 0; j < order; j++)
		{
			α.set(j, α.get(j) - lr * delta.get(1 + (j * 2), 0));
			β.set(j, β.get(j) - lr * delta.get(2 + (j * 2), 0));
		}
		// System.out.println( "new lambda " + Math.exp( lambda ) );
		// System.out.println( "new alpha " + alpha.copy().exp() );
		// System.out.println( "new beta " + beta.copy().exp() );

		// double llAfter = Fastmath.getHawkesLLPos( t, lambda, alpha, beta );

		// out.println( "LL after iteration " + llAfter );

		// double branchingRatio = 0;
		// for ( int i = 0; i < order; i++ )
		// {
		// branchingRatio += exp( alpha.get( i ) ) / exp( beta.get( 0 ) );
		// }
		// out.println( "branching ratio is " + branchingRatio );

		return lambda;
	}

	public double iterate(Vector t, double lr) throws FastMathException
	{
		int order = α.size();
		// out.println(
		// "********************************************ITERATING***********************************************************"
		// );

		// TestCase.assertEquals( 1.4219737624112004435, llScoreSum, eps );
		// double llBefore = Fastmath.getHawkesLL( t, lambda, alpha, beta );
		// out.println( "LL before iteration " + llBefore );

		DoubleColMatrix H = getHessian(t);
		// H.tanh();
		// Vector eigenVals = H.eig().getRealEigenvalues();
		// double minEig = eigenVals.fmin();
		// out.println( "H=" + H.toMatrixString() );
		// if ( minEig < 0 )
		// {
		// double meanAbsEig = eigenVals.abs().mean();
		// H.diag().add( minEig + 1 );
		// out.println( "corrected minEig=" + minEig + " meanAbsEig=" + meanAbsEig );
		// }

		// H.set( 0, 0, 0 );
		double normBefore = H.supNorm();
		// System.out.println( "H= " + H );
		DoubleColMatrix Hinv = H.invert();
		double normAfter = Hinv.supNorm();
		// System.out.println( "1/H= " + Hinv );
		double cond = normBefore * normAfter;
		// System.out.println( "condition " + cond );

		DoubleColMatrix J = getJacobian(t);
		// System.out.println( "J= " + J );

		DoubleColMatrix delta = Hinv.prod(J);
		// System.out.println( "delta= " + delta );

		lambda = lambda - lr * delta.get(0, 0);

		for (int j = 0; j < order; j++)
		{
			α.set(j, α.get(j) - lr * delta.get(1 + (j * 2), 0));
			β.set(j, β.get(j) - lr * delta.get(2 + (j * 2), 0));
		}
		// System.out.println( "new lambda " + lambda );
		// System.out.println( "new alpha " + alpha );
		// System.out.println( "new beta " + beta );
		// double llAfter = Fastmath.getHawkesLL( t, lambda, alpha, beta );

		// out.println( "LL after iteration " + llAfter );

		// double llGain = llAfter - llBefore;
		// out.format( "ll went from %f to %f so that llGain = %f\n", llBefore,
		// llAfter, llGain );

		// double branchingRatio = 0;
		// for ( int i = 0; i < order; i++ )
		// {
		// branchingRatio += alpha.get( i ) / beta.get( 0 );
		// }
		// out.println( "branching ratio is " + branchingRatio );

		return lambda;
	}

	public DoubleColMatrix getJacobianPos(Vector t)
	{
		assert α.size() == β.size() : "alpha and beta dimensions must be equal";
		int order = α.size();
		int dim = 1 + order * 2;

		DoubleColMatrix J = new DoubleColMatrix(dim, 1);

		double llLambdaDeriv = Fastmath.getHawkesLLLambdaDerivPos(t, lambda, α, β);

		J.set(0, 0, llLambdaDeriv);

		for (int j = 0; j < order; j++)
		{

			double llAlphaDeriv = Fastmath.getHawkesLLAlphaDerivPos(t, lambda, α, β, j);

			J.set(1 + (j * 2), 0, llAlphaDeriv);

			double llBetaDeriv = Fastmath.getHawkesLLBetaDerivPos(t, lambda, α, β, j);

			J.set(2 + (j * 2), 0, llBetaDeriv);
		}

		return J;
	}

	public DoubleColMatrix getHessian(Vector t)
	{
		assert α.size() == β.size() : "alpha and beta dimensions must be equal";
		int order = α.size();
		int dim = 1 + order * 2;
		DoubleColMatrix H = new DoubleColMatrix(dim, dim);

		double llLambdaDeriv2 = Fastmath.getHawkesLLLambda2ndDeriv(t, lambda, α, β);
		H.set(0, 0, llLambdaDeriv2);

		for (int j = 0; j < order; j++)
		{
			double llAlphaDeriv2 = Fastmath.getHawkesLLAlpha2ndDeriv(t, lambda, α, β, j);
			H.set(1 + (j * 2), 1 + (j * 2), llAlphaDeriv2);

			double llBetaDeriv2 = Fastmath.getHawkesLLBeta2ndDeriv(t, lambda, α, β, j);
			H.set(2 + (j * 2), 2 + (j * 2), llBetaDeriv2);

			double llLambdaAlphaDeriv = Fastmath.getHawkesLLLambdaAlphaDeriv(t, lambda, α, β, j);
			H.set(0, 1 + (j * 2), llLambdaAlphaDeriv);
			H.set(1 + (j * 2), 0, llLambdaAlphaDeriv);

			double llLambdaBetaDeriv = Fastmath.getHawkesLLLambdaBetaDeriv(t, lambda, α, β, j);

			H.set(0, 2 + (j * 2), llLambdaBetaDeriv);
			H.set(2 + (j * 2), 0, llLambdaBetaDeriv);

			double llAlphaBetaDeriv = Fastmath.getHawkesLLAlphaBetaDeriv(t, lambda, α, β, j);

			H.set(1 + (j * 2), 2 + (j * 2), llAlphaBetaDeriv);
			H.set(2 + (j * 2), 1 + (j * 2), llAlphaBetaDeriv);

		}

		return H;
	}

	public DoubleColMatrix getHessianPos(Vector t)
	{
		assert α.size() == β.size() : "alpha and beta dimensions must be equal";
		int order = α.size();
		int dim = 1 + order * 2;
		DoubleColMatrix H = new DoubleColMatrix(dim, dim);

		double llLambdaDeriv2 = Fastmath.getHawkesLLLambda2ndDerivPos(t, lambda, α, β);

		H.set(0, 0, llLambdaDeriv2);

		for (int j = 0; j < order; j++)
		{
			double llAlphaDeriv2 = Fastmath.getHawkesLLAlpha2ndDerivPos(t, lambda, α, β, j);

			H.set(1 + (j * 2), 1 + (j * 2), llAlphaDeriv2);

			double llBetaDeriv2 = Fastmath.getHawkesLLAlpha2ndDerivPos(t, lambda, α, β, j);
			H.set(2 + (j * 2), 2 + (j * 2), llBetaDeriv2);

			double llLambdaAlphaDeriv = Fastmath.getHawkesLLLambdaAlphaDerivPos(t, lambda, α, β, j);

			H.set(0, 1 + (j * 2), llLambdaAlphaDeriv);
			H.set(1 + (j * 2), 0, llLambdaAlphaDeriv);

			double llLambdaBetaDeriv = Fastmath.getHawkesLLLambdaBetaDerivPos(t, lambda, α, β, j);

			H.set(0, 2 + (j * 2), llLambdaBetaDeriv);
			H.set(2 + (j * 2), 0, llLambdaBetaDeriv);

			double llAlphaBetaDeriv = Fastmath.getHawkesLLAlphaDerivPos(t, lambda, α, β, j);

			H.set(1 + (j * 2), 2 + (j * 2), llAlphaBetaDeriv);
			H.set(2 + (j * 2), 1 + (j * 2), llAlphaBetaDeriv);

		}

		return H;
	}

	public double logLikelihoodAlphaDerivative(Vector t, int j)
	{
		return Fastmath.getHawkesLLAlphaDeriv(t, lambda, α, β, j);
	}

	public double logLikelihoodAlphaDerivativePos(Vector t, int j)
	{
		return Fastmath.getHawkesLLAlphaDerivPos(t, lambda, α, β, j);
	}

	public double logLikelihoodBetaDerivative(Vector t, int j)
	{
		return Fastmath.getHawkesLLBetaDeriv(t, lambda, α, β, j);
	}

	public double logLikelihoodBetaDerivativePos(Vector t, int j)
	{
		return Fastmath.getHawkesLLBetaDerivPos(t, lambda, α, β, j);
	}

	public double logLikelihoodLambdaDerivative(Vector t, int j)
	{
		return Fastmath.getHawkesLLLambdaDeriv(t, lambda, α, β);
	}

	public double logLikelihoodLambdaDerivativePos(Vector t, int j)
	{
		return Fastmath.getHawkesLLLambdaDerivPos(t, lambda, α, β);
	}

	public double logLikelihoodAlpha2ndDerivative(Vector t, int j)
	{
		return Fastmath.getHawkesLLAlpha2ndDeriv(t, lambda, α, β, j);
	}

	public double logLikelihoodAlpha2ndDerivativePos(Vector t, int j)
	{
		return Fastmath.getHawkesLLAlpha2ndDerivPos(t, lambda, α, β, j);
	}

	public double logLikelihoodBeta2ndDerivative(Vector t, int j)
	{
		return Fastmath.getHawkesLLBeta2ndDeriv(t, lambda, α, β, j);
	}

	public double logLikelihoodBeta2ndDerivativePos(Vector t, int j)
	{
		return Fastmath.getHawkesLLBeta2ndDerivPos(t, lambda, α, β, j);
	}

	public double logLikelihoodLambda2ndDerivative(Vector t, int j)
	{
		return Fastmath.getHawkesLLLambda2ndDeriv(t, lambda, α, β);
	}

	public double logLikelihoodLambda2ndDerivativePos(Vector t, int j)
	{
		return Fastmath.getHawkesLLLambda2ndDerivPos(t, lambda, α, β);
	}

	public double logLikelihoodLambdaAlphaDerivative(Vector t, int j)
	{
		return Fastmath.getHawkesLLLambdaAlphaDeriv(t, lambda, α, β, j);
	}

	public double logLikelihoodLambdaAlphaDerivativePos(Vector t, int j)
	{
		return Fastmath.getHawkesLLLambdaAlphaDerivPos(t, lambda, α, β, j);
	}

	public double logLikelihoodLambdaBetaDerivative(Vector t, int j)
	{
		return Fastmath.getHawkesLLLambdaBetaDeriv(t, lambda, α, β, j);
	}

	public double logLikelihoodLambdaBetaDerivativePos(Vector t, int j)
	{
		return Fastmath.getHawkesLLLambdaBetaDerivPos(t, lambda, α, β, j);
	}

	public double logLikelihoodAlphaBetaDerivative(Vector t, int j)
	{
		return Fastmath.getHawkesLLAlphaBetaDeriv(t, lambda, α, β, j);
	}

	public double logLikelihoodAlphaBetaDerivativePos(Vector t, int j)
	{
		return Fastmath.getHawkesLLAlphaBetaDerivPos(t, lambda, α, β, j);
	}

}
