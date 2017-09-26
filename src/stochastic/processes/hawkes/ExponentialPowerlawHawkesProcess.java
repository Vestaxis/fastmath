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
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.direct.NelderMeadSimplex;
import org.apache.commons.math3.optimization.direct.SimplexOptimizer;
import org.apache.commons.math3.util.FastMath;

import fastmath.Vector;
import fastmath.exceptions.NotANumberException;

@SuppressWarnings( { "deprecation", "unused", "unchecked" } )
public class ExponentialPowerlawHawkesProcess extends ExponentialHawkesProcess
		implements MultivariateFunction, Serializable
{

	/**
	 * 
	 * @param η
	 *            scale
	 * 
	 * @param ε
	 *            degree of fractional integration
	 * 
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

	private boolean normalize = false;

	@Override
	public double Z()
	{
		if (!normalize)
		{
			return 1;
		}
		final double eta = exp(η);
		double eps = 0.25 * tanh(ε) + 0.25;
		double a = pow(m, (-eps * M + eps + 1)) - pow(m, (1 + eps));
		double b = pow(m, eps) - 1;
		double c = pow(eta, -1 - eps);
		return -eta * a / m / b * c - αS() * eta / m;
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
		return (pow(eta, -1 - eps) * (pow(m, -(1 + eps) * (M - 1)) - pow(m, (1 + eps)))) / (pow(m, 1 + eps) - 1);

	}

	@Override
	public double β(int i)
	{
		if (i == M)
		{
			return βS();
		}
		final double eta = exp(η);
		return 1 / eta / pow(m, i);
	}

	@Override
	public double α(int i)
	{
		if (i == M)
		{
			return αS();
		}
		final double eta = exp(η);
		double eps = 0.25 * tanh(ε) + 0.25;
		return pow(1 / (eta * pow(m, i)), 1 + eps);
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
		tparams.set(Parameter.η.ordinal(), exp(η));
		return tparams;
	}

	public double getε()
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

	/**
	 * integrated kernel function which is the anti-derivative/indefinite integral
	 * of this{@link #ρ}
	 * 
	 * @param t
	 * @return ∫this{@link #ψ}(t)dt
	 */
	@Override
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

	@Override
	public int getOrder()
	{
		return M + 1;
	}

}
