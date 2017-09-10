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

import fastmath.Vector;
import fastmath.exceptions.NotANumberException;

public class ExponentialPowerlawHawkesProcess implements MultivariateFunction, Serializable
{

	public static double arctanh(double x)
	{
		return x == 0 ? 0 : 0.5 * log((x + 1.0) / (x - 1.0));
	}

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
		this.ε = arctanh(-1 + 4 * ε);
	}

	private static final long serialVersionUID = 1L;

	public ExponentialPowerlawHawkesProcess()
	{
		initializeParameterVectors();
	}

	public ExponentialPowerlawHawkesProcess(double ρ2, double η2, double ε2)
	{
		this.ρ = ρ2;
		this.η = log(η2);
		this.ε = arctanh(-1 + 4 * ε2);
	}

	protected Vector eventTimes;

	public double computeMoment(int moment)
	{
		switch (moment)
		{
		case 1:
			return ρ;
		default:
			throw new UnsupportedOperationException("only mean is supported");
		}
	}

	/**
	 * TODO: rewrite this part to not use deprecated stuff
	 */
	public int estimateParameters(int digits)
	{
		SimplexOptimizer optimizer = new SimplexOptimizer(pow(0.1, digits), pow(0.1, digits));
		optimizer.setSimplex(new NelderMeadSimplex(Parameter.values().length, 0.001));

		int maxIters = Integer.MAX_VALUE;
		double[] initialEstimate = calculateInitialGuess(eventTimes).toPrimitiveArray();
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
	int M = 15;

	private double ρ = 1; // branching rate

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
		final double eta = exp(η);
		double eps = 0.25 * tanh(ε) + 0.25;
		double a = sum(i -> pow(1 / eta / pow(m, i), 1 + eps) * exp(-t / eta / pow(m, i)), 0, M - 1);
		double b = 1 / (pow(m, 1 + eps) - 1) * pow(eta, -1 - eps) * (pow(m, 1 + eps) - pow(m, -(1 + eps) * (M - 1)))
				* exp(-t / eta * m);
		double c = ρ * m * (-1 + pow(m, eps));
		double numer = c * (a - b);
		double denom = eta * (pow(eta, (-1 - eps)) * pow(m, (1 + eps))
				- 1 / (pow(m, (1 + eps)) - 1) * pow(eta, (-1 - eps))
						* (pow(m, (1 + eps)) - pow(m, -((1 + eps) * (M - 1)))) * pow(m, eps)
				- pow(eta, (-1 - eps)) * pow(m, (-M * eps + eps + 1)) + 1 / (pow(m, (1 + eps)) - 1)
						* pow(eta, (-1 - eps)) * (pow(m, (1 + eps)) - pow(m, -((1 + eps) * (M - 1)))));
		return numer / denom;
	}

	/**
	 * 
	 * @param t
	 * 
	 * @return
	 */
	public double λ(double t)
	{
		final int n = eventTimes.size();
		double intensity = 0;
		double itime;

		for (int i = 0; i < n && (itime = eventTimes.get(i)) < t; i++)
		{
			intensity += ψ(t - itime);

		}
		return intensity;
	}

	/**
	 * 
	 * @param eventTimes
	 * @param deterministicIntensity
	 * @param lambda
	 * @param alpha
	 * @param bη
	 * @return Pair<logLik,E[Lambda]>
	 */
	public double logLik()
	{
		final int n = eventTimes.size();
		double ll = eventTimes.sum();

		for (int i = 1; i < n; i++)
		{
			double prevt = eventTimes.get(i - 1);
			double thist = eventTimes.get(i);
			ll += log(λ(thist)) - Ψ(prevt, thist);
		}
		out.println("LL{" + getParamString() + "}=" + ll);
		if (Double.isNaN(ll))
		{
			ll = Double.NEGATIVE_INFINITY;
		}
		return ll;
	}

	/**
	 * compensator
	 * 
	 * @param s
	 *            < t
	 * @param t
	 *            > s
	 * 
	 * @return ∫this{@link #ψ}(t)dt -∫this{@link #ψ}(s)ds
	 */
	public double Ψ(double s, double t)
	{
		assert t > s;
		return iψ(t) - iψ(s);
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

		double a = ρ * m * (-1 + pow(m, eps))
				* (-sum(i -> pow(1 / eta / pow(m, i), 1 + eps) * eta * pow(m, i) * exp(-t / eta / pow(m, i)), 0, M - 1)
						+ 1 / (pow(m, 1 + eps) - 1) * pow(eta, -1 - eps)
								* (pow(m, 1 + eps) - pow(m, -(1 + eps) * (M - 1))) * eta / m * exp(-t / eta * m))
				/ eta
				/ (pow(eta, -1 - eps) * pow(m, 1 + eps)
						- 1 / (pow(m, 1 + eps) - 1) * pow(eta, -1 - eps)
								* (pow(m, 1 + eps) - pow(m, -0.4e1 - 0.4e1 * eps)) * pow(m, eps)
						- pow(eta, -1 - eps) * pow(m, -0.4e1 * eps + 1) + 1 / (pow(m, 1 + eps) - 1) * pow(eta, -1 - eps)
								* (pow(m, 1 + eps) - pow(m, -0.4e1 - 0.4e1 * eps)));
		double b = ρ * m * (-0.1e1 + pow(m, eps))
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
	public Vector calculateCompensator()
	{
		final int n = eventTimes.size();
		Vector compensator = new Vector(n);
		for (int i = 1; i < n; i++)
		{
			compensator.set(i, Ψ(i));
		}
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
	 * @return Ψ(eventTimes.get(i-1), eventTimes.get(i)
	 */
	private double Ψ(int i)
	{
		return Ψ(eventTimes.get(i - 1), eventTimes.get(i));
	}

	public Vector simulate(double T)
	{
		// if ( order != 1 )
		// {
		// throw new
		// UnsupportedOperationException("only order 1 Hawkes processes are currently
		// supported");
		// }
		// double lambdastar = getLambda();
		// int n = 1;
		// UniformRealDistribution uniformDist = new UniformRealDistribution();
		// double U = uniformDist.sample();
		// double s = -1.0/lambdastar*Math.log(U);
		// while( s < T )
		// {
		// double t1 = s;
		// lambdastar = getLambda() + alpha.get(0);
		// U = uniformDist.sample();
		// s = -1.0/lambdastar*Math.log(U);
		// if ( s >= T )
		// {
		// continue;
		// }
		// double D = uniformDist.sample();
		// if ( D <= )
		// }
		return null;
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

	public double getΡ()
	{
		return ρ;
	}

	public void setρ(double ρ)
	{
		this.ρ = ρ;
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
