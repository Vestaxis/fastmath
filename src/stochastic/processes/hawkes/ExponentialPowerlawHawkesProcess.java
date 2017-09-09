package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.System.out;

import java.io.Serializable;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.direct.NelderMeadSimplex;
import org.apache.commons.math3.optimization.direct.SimplexOptimizer;

import fastmath.Vector;
import fastmath.exceptions.NotANumberException;
import math.DoublePair;

public class ExponentialPowerlawHawkesProcess
		implements MultivariateFunction, Serializable
{
	/**
	 * 
	 * @param η exponential multiplier
	 * @param τ power multiplier
	 * @param ε degree of fractional integration
	 * @param b amplitude of short-term exponential 
	 */
	public ExponentialPowerlawHawkesProcess( double η, double τ, double ε,
			double b)
	{
		super();
		this.η = η;
		this.τ = τ;
		this.ε = ε;
		this.b = b;
	}

	private static final long serialVersionUID = 1L;

	public ExponentialPowerlawHawkesProcess()
	{
		initializeParameterVectors();
	}

	public ExponentialPowerlawHawkesProcess(double ρ2, double η2, double τ2, double ε2,
			double b2)
	{
		this.ρ = ρ2;
		this.η = η2;
		this.τ = τ2;
		this.ε = ε2;
		this.b = b2;
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
		optimizer.setSimplex(new NelderMeadSimplex(Parameter.values().length, 0.01));

		int maxIters = 1000;
		double[] initialEstimate = calculateInitialGuess(eventTimes).toPrimitiveArray();
		PointValuePair params = optimizer.optimize(maxIters, this, GoalType.MAXIMIZE,
				initialEstimate);
		getParameters().assign(params.getKey());

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
		//return null;
	}

	public String getParamString()
	{
		return getParameters().toString();
		// return "kappa=" + getKappa() + " alpha=" + alpha.toString().replace(
		// "\n", "" ) + " beta="
		// + beta.toString().replace( "\n", "" );
	}


	static enum Parameter
	{
		b, τ, ε, η
	};

	/**
	 * range of the approximation
	 */
	int M = 15;

	private double ρ = 1; // branching rate
	/**
	 * short-time cutoff
	 */
	private double η;

	/**
	 * powerlaw scale
	 */
	private double τ;

	/**
	 * Tail exponent
	 */
	private double ε;

	/**
	 * precision of approximation
	 */
	private double m = 5;

	private double b;

	public double ψ(double t)
	{
		return ρ / getZ()
				* (M * b * exp(-t / τ) + sum(i -> pow(1.0 / η / pow(m, i), 1.0 + ε) * exp(-t / η / pow(m, i)), 0, M - 1));
	}

	/**
	 * 
	 * @return M * b * τ + 1 / (pow(m, -ε) - 1) * pow(η, -ε) * (pow(m, -ε * M) - 1)
	 */
	private double getZ()
	{
		return M * b * τ + 1.0 / (pow(m, -ε) - 1.0) * pow(η, -ε) * (pow(m, -ε * M) - 1.0);
	}

	/**
	 * subs({alpha[i] = , beta[i] = }, nu(t));
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
	 * @param beta
	 * @return Pair<logLik,E[Lambda]>
	 */
	public double logLik()
	{
		final int n = eventTimes.size();
		double ll = eventTimes.sum();
	

		for (int i = 1; i < n; i++)
		{			
			double prevt = eventTimes.get(i-1);
			double thist = eventTimes.get(i);
			ll += log(λ(thist)) - Ψ( prevt, thist );
		}
		out.println( getParamString() + "=" + ll );
		if ( Double.isNaN( ll ) ) 
		{
			ll = Double.NEGATIVE_INFINITY;
		}
		return ll;
	}

	/**
	 * 
	 * @param t
	 * @return M*b*e^(-t/τ)
	 */
	private double getKappa(double t)
	{
		return M*b*exp(-t/τ);
	}

	public void setKappa(double kappa)
	{
		getParameters().set(0, kappa);
	}

	public double Ψ(double s, double t)
	{
		return iΨ(t) - iΨ(s); 
	}



	public double iΨ(double t)
	{
		return t * ρ
				* (M * b * τ - τ * M * b * exp(-t / τ)
						+ 1 / (-1 + pow(m, ε)) * pow(η, -ε) * (pow(m, ε) - pow(m, -ε * (M - 1)))
						+ sum(i -> -pow(η, -ε) * pow(m, i) * pow(pow(m, -i), 1 + ε) * exp(-t / η * pow(m, -i)), 0,
								M - 1))
				/ (M * b * τ * t + 1 / (pow(m, -ε) - 1) * pow(η, -ε) * (pow(m, -ε * M) - 1) * t);
	}
	
	/**
	 * The random variable defined by 1-exp(-ξ(i)-ξ(i-1)) indicates a better fit the
	 * more uniformly distributed it is.

	 * 
	 * @see UniformRealDistribution on [0,1]
	 * 
	 * @param times
	 * 
	 * @return ξ
	 */
	public Vector calculateCompensator(Vector times)
	{
		final int n = times.size();
		Vector compensator = new Vector(n);
		for ( int i = 1; i < n; i++ )
		{
			compensator.set(Ψ(times.get(i-1), times.get(i)));
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
		// final double b = beta.get( j );
		// double exphi = exp( -b * x );
		// A[j] = 1 + ( exphi * A[j] );
		// secondSum += ( a / b ) * ( 1 - exphi ) * A[j];
		// }
		// compensator.set( i, secondSum );
		// }
		return compensator;
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
		this.b = point[Parameter.b.ordinal()];
		this.ε = point[Parameter.ε.ordinal()];
		this.η = point[Parameter.η.ordinal()];
		this.τ = point[Parameter.τ.ordinal()];
		
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
			params = new Vector( paramCount );				
		}
		params.set(Parameter.b.ordinal(), b );
		params.set(Parameter.ε.ordinal(), ε );
		//params.set(Parameter.ρ.ordinal(), ρ );
		params.set(Parameter.τ.ordinal(), τ );
		params.set(Parameter.η.ordinal(), η );		
		return params;
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
