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

@SuppressWarnings( { "deprecation", "unused", "unchecked" } )
public class ExtendedExponentialPowerlawHawkesProcess extends ExponentialPowerlawHawkesProcess implements MultivariateFunction, Serializable
{


  /**
	 * 
	 * @param η
	 *            exponential multiplier
   * @param ε
	 *            degree of fractional integration
   * @param b
	 *            amplitude of short-term exponential
   * @param τ
	 *            power multiplier
	 */
	public ExtendedExponentialPowerlawHawkesProcess(double η, double ε, double b, double τ)
	{
		super();
		this.η = log(η);
		this.τ = log(τ);
		this.ε = FastMath.atanh(-1 + 4 * ε);
		this.b = b;
	}

	private static final long serialVersionUID = 1L;

	public ExtendedExponentialPowerlawHawkesProcess()
	{
		initializeParameterVectors();
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


	/**
	 * powerlaw scale
	 */
	private double τ;


	/**
	 * precision of approximation
	 */
	private double m = 5;

	private double b;

	/**
	 * normalization factor such that the branching rate is equal to this{@link #ρ}
	 * 
	 * @return M * b * τ + 1 / (pow(m, -ε) - 1) * pow(η, -ε) * (pow(m, -ε * M) - 1)
	 */
	public double Z()
	{
		double eps = 0.25 * tanh(ε) + 0.25;
		return M * exp(b) * exp(τ) + 1.0 / (pow(m, -eps) - 1) * pow(exp(η), -eps) * (pow(m, -eps * M) - 1);
	}


	/**
	 * integrated kernel function which is the anti-derivative/indefinite integral
	 * of this{@link #ρ}
	 * 
	 * @param t
	 * @return ∫this{@link #ψ(double)}(t)dt
	 */
	public double iψ(double t)
	{
		double tau = exp(τ);
		double bee = exp(b);
		double eta = exp(η);
		double eps = 0.25 * tanh(ε) + 0.25;
		double x = sum(i -> -pow(eta, -eps) * pow(m, i) * pow(pow(m, -i), 1 + eps) * exp(-t / eta * pow(m, -i)), 0,
				M - 1);
		return t * (M * bee * tau - tau * M * bee * exp(-t / tau)
				+ 1 / (-1 + pow(m, eps)) * pow(eta, -eps) * (pow(m, eps) - pow(m, -eps * (M - 1))) + x)
				/ (M * bee * tau * t + 1 / (pow(m, -eps) - 1) * pow(eta, -eps) * (pow(m, -eps * M) - 1) * t);
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
			params = new Vector(paramCount);
		}
		params.set(Parameter.b.ordinal(), b);
		params.set(Parameter.ε.ordinal(), ε);
		params.set(Parameter.τ.ordinal(), τ);
		params.set(Parameter.η.ordinal(), η);
		return params;
	}

	public Vector getTransformedParameters()
	{
		Vector tparams = new Vector(Parameter.values().length);
		tparams.set(Parameter.b.ordinal(), exp(b));
		tparams.set(Parameter.ε.ordinal(), 0.25 * tanh(ε) + 0.25);
		tparams.set(Parameter.τ.ordinal(), exp(τ));
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



  @Override
  public int order()
  {
   return M+1;
  }


  @Override
  public double βS()
  {
    double tau = exp(τ);
    return 1/tau;
  }


  @Override
  public double αS()
  {
    return b;
  }

}
