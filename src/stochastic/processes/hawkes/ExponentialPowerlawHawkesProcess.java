package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.tanh;
import static java.lang.System.out;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.direct.NelderMeadSimplex;
import org.apache.commons.math3.optimization.direct.SimplexOptimizer;
import org.apache.commons.math3.util.FastMath;

import fastmath.Vector;
import fastmath.exceptions.NotANumberException;

public class ExponentialPowerlawHawkesProcess implements MultivariateFunction, Serializable
{

	/**
	 * 
	 * @param η
	 *            exponential multiplier
	 * @param τ
	 *            power multiplier
	 * @param ε
	 *            degree of fractional integration
	 * @param b
	 *            amplitude of short-term exponential
	 */
	public ExponentialPowerlawHawkesProcess(double η, double ε)
	{
		super();
		this.η = log(η);
		this.ε = FastMath.atanh(-1 + 4 * ε);
	}

	private static final long serialVersionUID = 1L;

	public ExponentialPowerlawHawkesProcess()
	{
		initializeParameterVectors();
	}

	public Vector T;

	/**
	 * TODO: rewrite this part to not use deprecated stuff
	 */
	public int estimateParameters(int digits)
	{
		SimplexOptimizer optimizer = new SimplexOptimizer(pow(0.1, digits), pow(0.1, digits));
		optimizer.setSimplex(new NelderMeadSimplex(Parameter.values().length, 0.001));

		int maxIters = Integer.MAX_VALUE;
		double[] initialEstimate = calculateInitialGuess(T).toPrimitiveArray();
		PointValuePair params = optimizer.optimize(maxIters, this, GoalType.MAXIMIZE, initialEstimate);
		double[] key = params.getKey();
		getParameters().assign(key);
		out.println("parameter estimates=" + getParamString());
		return optimizer.getEvaluations();
	}

	private double[] getParameterArray()
	{
		return getParameters().toPrimitiveArray();
	}

	private Vector calculateInitialGuess(Vector durations)
	{
		final Vector vec = getParameters();

		return vec;
		// return null;
	}

	public String getParamString()
	{
		return Arrays.asList(Parameter.values()).toString() + "=" + getTransformedParameters().toString();
		// return "kappa=" + getKappa() + " alpha=" + alpha.toString().replace(
		// "\n", "" ) + " bη="
		// + bη.toString().replace( "\n", "" );
	}

	static enum Parameter
	{
		ε, η
	};

	/**
	 * range of the approximation
	 */
	public int M = 15;

	/**
	 * smallest timescale
	 */
	private double η;

	/**
	 * Tail exponent
	 */
	private double ε;

	/**
	 * precision of approximation
	 */
	private double m = 5;

	/**
	 * kernel function
	 * 
	 * @param t
	 * @return
	 */
	public double ψ(double t)
	{
		double a = sum(i -> getAlpha(i) * exp(-getBeta(i) * t), 0, M - 1);
		a -= αS() * exp(-t * βS());
		return a / Z();
	}

	public double Z()
	{
		final double eta = exp(η);
		double eps = 0.25 * tanh(ε) + 0.25;
		double a = pow(m, (-eps * M + eps + 1)) - pow(m, (1 + eps));
		double b = pow(m, eps) - 1;
		double c = pow(eta, -1 - eps);
		return -eta * a / m / b * c + αS() * eta / m;
	}

	public double βS()
	{
		final double eta = exp(η);
		return m / eta;
	}

	public double αS()
	{
		final double eta = exp(η);
		double eps = 0.25 * tanh(ε) + 0.25;
		return -(pow(eta, -1 - eps) * (pow(m, -(1 + eps) * (M - 1)) - pow(m, (1 + eps)))) / (pow(m, 1 + eps) - 1);

	}

	public double getBeta(int i)
	{
		final double eta = exp(η);
		return 1 / eta / pow(m, i);
	}

	public double getAlpha(int i)
	{
		final double eta = exp(η);
		double eps = 0.25 * tanh(ε) + 0.25;
		return pow(1 / ( eta * pow(m, i) ), 1 + eps);
	}

	/**
	 * 
	 * @param t
	 * 
	 * @return
	 */
	public double λ(double t)
	{
		final int n = T.size();
		double intensity = 0;
		double itime;

		for (int i = 0; i < n && (itime = T.get(i)) < t; i++)
		{
			intensity += ψ(t - itime);

		}
		return intensity;
	}

	/**
	 * 
	 * @param T
	 * @param deterministicIntensity
	 * @param lambda
	 * @param alpha
	 * @param bη
	 * @return Pair<logLik,E[Lambda]>
	 */
	public double logLik()
	{
		final int n = T.size();
		double ll = T.sum();

		for (int i = 1; i < n; i++)
		{
			double thist = T.get(i);
			ll += log(λ(thist)) - Λ(i);
		}
		out.println("LL{" + getParamString() + "}=" + ll);
		if (Double.isNaN(ll))
		{
			ll = Double.NEGATIVE_INFINITY;
		}
		return ll;
	}

	/**
	 * integrated kernel function which is the anti-derivative/indefinite integral
	 * of this{@link #ρ}
	 * 
	 * @param t
	 * @return ∫this{@link #ψ}(t)dt
	 */
	public double iψ(double t)
	{
		double eta = exp(η);
		double eps = 0.25 * tanh(ε) + 0.25;

		double a = m * (-1 + pow(m, eps))
				* (-sum(i -> pow(1 / eta / pow(m, i), 1 + eps) * eta * pow(m, i) * exp(-t / eta / pow(m, i)), 0, M - 1)
						+ 1 / (pow(m, 1 + eps) - 1) * pow(eta, -1 - eps)
								* (pow(m, 1 + eps) - pow(m, -(1 + eps) * (M - 1))) * eta / m * exp(-t / eta * m))
				/ eta
				/ (pow(eta, -1 - eps) * pow(m, 1 + eps)
						- 1 / (pow(m, 1 + eps) - 1) * pow(eta, -1 - eps)
								* (pow(m, 1 + eps) - pow(m, -0.4e1 - 0.4e1 * eps)) * pow(m, eps)
						- pow(eta, -1 - eps) * pow(m, -0.4e1 * eps + 1) + 1 / (pow(m, 1 + eps) - 1) * pow(eta, -1 - eps)
								* (pow(m, 1 + eps) - pow(m, -0.4e1 - 0.4e1 * eps)));
		double b = m * (-0.1e1 + pow(m, eps))
				* (-0.1e1 / (-0.1e1 + pow(m, eps)) * pow(eta, -eps) * (pow(m, eps) - pow(m, -eps * (M - 1)))
						+ 0.1e1 / (pow(m, 0.1e1 + eps) - 0.1e1) * pow(eta, -0.1e1 - eps)
								* (pow(m, 0.1e1 + eps) - pow(m, -(0.1e1 + eps) * (M - 1))) * eta / m)
				/ eta
				/ (pow(eta, -0.1e1 - eps) * pow(m, 0.1e1 + eps)
						- 0.1e1 / (pow(m, 0.1e1 + eps) - 0.1e1) * pow(eta, -0.1e1 - eps)
								* (pow(m, 0.1e1 + eps) - pow(m, -0.4e1 - 0.4e1 * eps)) * pow(m, eps)
						- pow(eta, -0.1e1 - eps) * pow(m, -0.4e1 * eps + 0.1e1) + 0.1e1 / (pow(m, 0.1e1 + eps) - 0.1e1)
								* pow(eta, -0.1e1 - eps) * (pow(m, 0.1e1 + eps) - pow(m, -0.4e1 - 0.4e1 * eps)));

		double c = eta * (pow(eta, (-1 - eps)) * pow(m, (1 + eps))
				- 0.1e1 / (pow(m, (1 + eps)) - 0.1e1) * pow(eta, (-1 - eps))
						* (pow(m, (1 + eps)) - pow(m, (-4 - 4 * eps))) * pow(m, eps)
				- pow(eta, (-1 - eps)) * pow(m, (-4 * eps + 1)) + 0.1e1 / (pow(m, (1 + eps)) - 0.1e1)
						* pow(eta, (-1 - eps)) * (pow(m, (1 + eps)) - pow(m, (-4 - 4 * eps))));

		double d = m * (-0.1e1 + pow(m, eps))
				* (-0.1e1 / (-0.1e1 + pow(m, eps)) * pow(eta, -eps) * (pow(m, eps) - pow(m, -eps * (M - 1)))
						+ 0.1e1 / (pow(m, 0.1e1 + eps) - 0.1e1) * pow(eta, -0.1e1 - eps)
								* (pow(m, 0.1e1 + eps) - pow(m, -(0.1e1 + eps) * (M - 1))) * eta / m);

		return -((a - b) * c) / d;
	}

	/**
	 * The random variable defined by 1-exp(-ξ(i)-ξ(i-1)) indicates a better fit the
	 * more uniformly distributed it is.
	 * 
	 * 
	 * @see UniformRealDistribution on [0,1]
	 * 
	 * @param times
	 * 
	 * @return ξ
	 */
	public Vector Λ()
	{
		final int n = T.size();
		Vector compensator = new Vector(n);

		for (int i = 0; i < n - 1; i++)
		{
			compensator.set(i, Λ(i));
		}
		compensator = compensator.cumsum();
		// double lambda = getKappa();
		//
		// double A[] = new double[order];
		// for ( int i = 0; i < n; i++ )
		// {
		// double x = durations.get( i );
		// double secondSum = x * lambda;
		// for ( int j = 0; j < order; j++ )
		// {
		// final double a = alpha.get( j );
		// final double b = bη.get( j );
		// double exphi = exp( -b * x );
		// A[j] = 1 + ( exphi * A[j] );
		// secondSum += ( a / b ) * ( 1 - exphi ) * A[j];
		// }
		// compensator.set( i, secondSum );
		// }
		return compensator;
	}

	/**
	 * n-th compensated point
	 * 
	 * @param i
	 *            >= 1 and <= n
	 * @return sum(k -> iψ(T.get(i + 1) - T.get(k)) - iψ(T.get(i) - T.get(k)), 0,
	 *         i-1)
	 */
	private double Λ(int i)
	{

		double sum = sum(k -> {
			double t0 = T.get(i) - T.get(k);
			double t1 = T.get(i - 1) - T.get(k);
			return iψ(t0) - iψ(t1);
		}, 0, i - 1);
		return sum;
	}

	public Vector recursiveΛ()
	{
		double A = 0;
		Vector compensator = new Vector(T.size());
		int eye = 1;
		for (int i = 1; i < T.size(); i++)
		{
			double upperTime = T.get(i);
			double lowerTime = T.get(i - 1);

			double ktime;
			int k;
			double subsum = iψ(upperTime - lowerTime) * A;

			A = subsum;

			double innerSum = subsum * (eye - iψ(upperTime - lowerTime));
			for (k = i - 1; k < i; k++)
			{
				ktime = T.get(k);
				innerSum += eye - iψ((upperTime - ktime));
			}

			compensator.set(i, innerSum);
		}

		return compensator;
	}

	@Override
	public double value(double[] point)
	{
		getParameters().assign(point);
		this.ε = point[Parameter.ε.ordinal()];
		this.η = point[Parameter.η.ordinal()];

		double ll = logLik();

		if (Double.isNaN(ll))
		{
			throw new RuntimeException(new NotANumberException("(log)likelihood is NaN"));
		}

		return ll;
	}

	public Vector initializeParameterVectors()
	{
		Vector params = getParameters();
		return params;
	}

	private int iterations = 0;

	Vector params;

	public Vector getParameters()
	{
		if (params == null)
		{
			int paramCount = Parameter.values().length;
			params = new Vector(paramCount);
		}
		params.set(Parameter.ε.ordinal(), ε);
		// params.set(Parameter.ρ.ordinal(), ρ );
		params.set(Parameter.η.ordinal(), η);
		return params;
	}

	public Vector getTransformedParameters()
	{
		Vector tparams = new Vector(Parameter.values().length);
		tparams.set(Parameter.ε.ordinal(), 0.25 * tanh(ε) + 0.25);
		// params.set(Parameter.ρ.ordinal(), ρ );
		tparams.set(Parameter.η.ordinal(), exp(η));
		return tparams;
	}

	public double getΕ()
	{
		return ε;
	}

	public void setε(double ε)
	{
		this.ε = ε;
	}

	public void setη(double η)
	{
		this.η = η;
	}

}
