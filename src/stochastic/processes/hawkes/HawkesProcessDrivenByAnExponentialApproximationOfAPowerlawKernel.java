package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

import java.io.Serializable;

import javax.swing.JProgressBar;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.direct.NelderMeadSimplex;
import org.apache.commons.math3.optimization.direct.SimplexOptimizer;

import fastmath.Vector;
import fastmath.exceptions.NotANumberException;
import math.DoublePair;

public class HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernel implements MultivariateFunction, Serializable
{
	public HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernel(double ρ, double η, double τ, double ε,
			double b)
	{
		super();
		this.ρ = ρ;
		this.η = η;
		this.τ = τ;
		this.ε = ε;
		this.b = b;
	}

	private static final long serialVersionUID = 1L;


	public HawkesProcessDrivenByAnExponentialApproximationOfAPowerlawKernel()
	{
		initializeParameterVectors();
	}

	protected Vector intereventTimes;

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
		optimizer.setSimplex(new NelderMeadSimplex(4, 0.01));

		PointValuePair params = optimizer.optimize(Integer.MAX_VALUE, this, GoalType.MAXIMIZE,
				calculateInitialGuess(intereventTimes).toPrimitiveArray());
		getParameters().assign(params.getKey());

		return optimizer.getEvaluations();
	}

	private double[] getParameterArray()
	{
		return getParameters().toPrimitiveArray();
	}

	private Vector calculateInitialGuess(Vector durations)
	{
		// final Vector vec = getParameters();
		// final double meanduration = durations.mean();
		// if ( log.isDebugEnabled() )
		// {
		// log.debug( "meanDuration=" + meanduration );
		// }
		// vec.set( 0, ( ( 0.5 / meanduration ) ) );
		// vec.slice( 1, order + 1 ).assign( 1.0 / order );
		// vec.slice( order + 1, 2 * order + 1 ).assign( 2 );
		//
		// if ( log.isDebugEnabled() )
		// {
		// log.debug( "initial guess of the optimal parameter set = " + vec );
		// }
		//
		// if ( vec.hasAnyInfinities() )
		// {
		// throw new IllegalArgumentException(
		// "initial guess contains at least one infinite value" );
		// }
		//
		// return vec;
		return null;
	}

	public String getParamString()
	{
		return "";
		// return "kappa=" + getKappa() + " alpha=" + alpha.toString().replace(
		// "\n", "" ) + " beta="
		// + beta.toString().replace( "\n", "" );
	}

	public static native double calculateLogLikelihood(final Vector times, double lambda, Vector alpha, Vector beta);

	static enum Parameter { ρ, b, τ, τ0, ε };

	/**
	 * range of the approximation
	 */
	int M = 15;
	
	private double ρ; // branching rate
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
	
	public double ψ( double t )
	{		
		return ρ / getZ() * (M * b * exp(-t / τ) + sum( i-> pow(1 / η / pow(m, i), 1 + ε) * exp(-t / η / pow(m, i)), 0, M - 1));
	}

	/**
	 * 
	 * @return M * b * τ + 1 / (pow(m, -ε) - 1) * pow(η, -ε) * (pow(m, -ε * M) - 1)
	 */
	private double getZ()
	{
		return M * b * τ + 1 / (pow(m, -ε) - 1) * pow(η, -ε) * (pow(m, -ε * M) - 1);
	}
	
	/**
	 * subs({alpha[i] = , beta[i] = }, nu(t));

	 * @param times
	 * @param t
	 * @return
	 */
	public double λ(Vector times, double t)
	{
		final int n = times.size();
		double B[] = new double[M];
		double intensity = 0;
		double kappa = M*b*exp(-t*τ);
		double itime;
		double normalizationFactor = ρ / getZ();

		for (int i = 0; i < n && (itime = times.get(i)) < t; i++)
		{
			double firstSum = kappa;
			double x = t - itime;
			for (int j = 0; j < M; j++)
			{
				final double alphaj = pow(1/(η*pow(m,i)),1+ε);				
				final double betaj = 1/(η*pow(m,i));
				double exphi = exp(-betaj * x);
				B[j] = (1 + B[j]) * exphi;
				firstSum += alphaj * B[j];
			}
			intensity = normalizationFactor * firstSum;
		}
		return intensity;
	}

	// public Vector calculateIntensity( Vector durations )
	// {
	// final int n = durations.size();
	// final int hawkesOrder = alpha.size();
	// assert alpha.size() == beta.size() :
	// "α and β must be of the same dimension";
	// double B[] = new double[hawkesOrder];
	// Vector intensity = new Vector( n );
	// double kappa = getKappa();
	// for ( int i = 0; i < n; i++ )
	// {
	// double firstSum = kappa;
	// double x = durations.get( i );
	// for ( int j = 0; j < hawkesOrder; j++ )
	// {
	// final double a = alpha.get( j );
	// final double b = beta.get( j );
	// double exphi = exp( -b * x );
	// B[j] = ( 1 + B[j] ) * exphi;
	// firstSum += a * B[j];
	// }
	// intensity.set( i, firstSum );
	// }
	// return intensity;
	// }

	/**
	 * 
	 * @param intereventTimes
	 * @param deterministicIntensity
	 * @param lambda
	 * @param alpha
	 * @param beta
	 * @return Pair<logLik,E[Lambda]>
	 */
	public DoublePair calculateLogLikelihoodHawkes()
	{
		final int n = intereventTimes.size();
		// double ll = intereventTimes.sum();
		// double A[] = new double[hawkesOrder];
		// double B[] = new double[hawkesOrder];
		// double ecomp = 0;
		// for ( int i = 0; i < n; i++ )
		// {
		// double firstSum = getKappa();
		// double x = intereventTimes.get( i );
		// double secondSum = x * getKappa();
		// for ( int j = 0; j < hawkesOrder; j++ )
		// {
		// final double a = alpha.get( j );
		// final double b = beta.get( j );
		// double exphi = exp( -b * x );
		// B[j] = ( 1 + B[j] ) * exphi;
		// A[j] = 1 + ( exphi * A[j] );
		// firstSum += a * B[j];
		// secondSum += ( a / b ) * ( 1 - exphi ) * A[j];
		// }
		// ecomp += secondSum;
		// double llTerm = Math.log( firstSum ) - secondSum;
		// if ( !Double.isNaN( llTerm ) )
		// {
		// ll += llTerm;
		// }
		// else
		// {
		// ll += Double.NEGATIVE_INFINITY;
		// }
		// }
		throw new UnsupportedOperationException("TODO");
		// return new DoublePair( ll, ecomp / n );
	}



	public void setKappa(double kappa)
	{
		getParameters().set(0, kappa);
	}

	/**
	 * The random variable defined by 1-exp(-ξ(i)-ξ(i-1)) indicates a better fit the
	 * more uniformly distributed it is.
	 * 
	 * TODO: if there were a place to insert some wise observation about "the
	 * wheel", this might be it, since the desired uniform distribution on [0,1]
	 * means "good"
	 * 
	 * 
	 * @see UniformRealDistribution on [0,1]
	 * 
	 * @param durations
	 * 
	 * @return ξ
	 */
	public Vector calculateCompensator(Vector durations)
	{
		final int n = durations.size();
		Vector compensator = new Vector(n);
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

		double ll = calculateLogLikelihoodHawkes().left;

		
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

			int paramCount = 3;

			// String[] paramNames = new String[params.size()];
			// String[] alphaParamNames = new String[order];
			// String[] betaParamNames = new String[order];
			// params.setTableModelColumnNames( paramNames );
			// alpha.setTableModelColumnNames( alphaParamNames );
			// beta.setTableModelColumnNames( betaParamNames );
			// paramNames[0] = "κ";
			// for ( int i = 1; i <= order; i++ )
			// {
			// paramNames[i] = alphaParamNames[i - 1] = "α" + ( order > 1 ? i : "" );
			// paramNames[i + order] = betaParamNames[i - 1] = "β" + ( order > 1 ? i :
			// "" );
			// }
		}
		return params;
	}

	public Vector getDurations()
	{
		return intereventTimes;
	}

	public void setDurations(Vector intereventTimes)
	{
		this.intereventTimes = intereventTimes;
	}

}
