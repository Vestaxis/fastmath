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

@SuppressWarnings("deprecation")
public class ExponentialPowerlawHawkesProcess implements MultivariateFunction, Serializable
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

	public Vector T;

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
		return sum(i -> α(i) * exp(-β(i) * t), 0, M) / Z();
	}

	public double Z()
	{
		final double eta = exp(η);
		double eps = 0.25 * tanh(ε) + 0.25;
		double a = pow(m, (-eps * M + eps + 1)) - pow(m, (1 + eps));
		double b = pow(m, eps) - 1;
		double c = pow(eta, -1 - eps);
		return -eta * a / m / b * c - αS() * eta / m;
	}

	/**
	 * intensity function
	 * 
	 * @param t
	 * 
	 * @return intensity at time t
	 */
	public double λ(double t)
	{
		return T.stream().filter(s -> s < t).map(s -> ψ(t - s)).sum();
	}

	double evolveλ(double dt, double[] S)
	{
		double λ = 0;
		for (int j = 0; j <= M; j++)
		{
			S[j] = exp(-β(j) * dt) * (1 + S[j]);
			λ += α(j) * S[j];
		}
		return λ / Z();
	}

	double evolveΛ(double dt, double[] A)
	{
		double Λ = 0;
		for (int j = 0; j <= M; j++)
		{
			A[j] = 1 + exp(-β(j) * dt) * A[j];
			Λ += (α(j) / β(j)) * (1 - exp(-β(j) * dt)) * A[j];
		}
		return Λ;
	}

	boolean recursive = false;

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
		double tn = T.getRightmostValue();
		double ll = tn - T.getLeftmostValue();
		final int n = T.size();

		if (recursive)
		{
			double A[] = new double[M + 1];
			double S[] = new double[M + 1];
			for (int i = 1; i < n; i++)
			{
				double t = T.get(i);
				double dt = t - T.get(i - 1);
				double λ = evolveλ(dt, S);
				double Λ = evolveΛ(dt, A);

				// double Λ = sum(j -> α(j) / β(j) * (1 - exp(-β(j) * (tn - t))), 0, M);

				if (λ > 0)
				{
					ll += log(λ);
				}

				ll -= Λ / (Z() * dt);

			}
		} else
		{
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
		}
		return ll;

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

		// for (int i = 0; i < n - 1; i++)
		// {
		// compensator.set(i, Λ(i));
		// }
		// compensator = compensator.cumsum();
		return calculateCompensator(n);
	}

	private Vector calculateCompensator(final int n)
	{
		Vector durations = T.diff();
		double lambda = getKappa();
		final int order = M + 1;

		double A[] = new double[order];
		Vector compensator = new Vector(n);		
		for (int i = 0; i < n - 1; i++)
		{
			double x = durations.get(i);
			double secondSum = x * lambda;
			for (int j = 0; j < order; j++)
			{
				final double a = α(j);
				final double b = β(j);
				double exphi = exp(-b * x);
				A[j] = 1 + (exphi * A[j]);
				secondSum += (a / b) * (1 - exphi) * A[j];
			}
			compensator.set(i, secondSum);
		}
		return compensator;
	}

	private double getKappa()
	{
		return 0;
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

	public double β(int i)
	{
		if (i == M)
		{
			return βS();
		}
		final double eta = exp(η);
		return 1 / eta / pow(m, i);
	}

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

}
