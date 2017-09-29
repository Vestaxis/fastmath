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
import org.apache.commons.math3.optim.SimpleBounds;
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
		this.η = log(η);
		this.ε = FastMath.atanh(-1 + 4 * ε);
    initializeParameterVectors();
	}

	private static final long serialVersionUID = 1L;

	public ExponentialPowerlawHawkesProcess()
	{
	}

	static enum Parameter
	{
		 ε, η
	};


  @Override
  public SimpleBounds getParameterBounds()
  {
    return new SimpleBounds(new double[] { 0.0, 0 }, new double[] { 0.5, 100 } );
  }
  
	/**
	 * range of the approximation
	 */
	public int M = 15;

	/**
	 * smallest timescale
	 */
	protected double η;

	/**
	 * Tail exponent
	 */
	protected double ε;

	/**
	 * precision of approximation
	 */
	private double m = 5;

	boolean normalize = true;

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

	public double getη()
  {
	  return exp(η);
  }
	
	@Override
	public double value(double[] point)
	{
		getParameters().assign(point);
		this.ε = point[Parameter.ε.ordinal()];
		this.η = point[Parameter.η.ordinal()];
   // this.λ0 = point[Parameter.λ0.ordinal()];

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
			int paramCount = getParamCount();
			params = new Vector(paramCount);
		}
		params.set(Parameter.ε.ordinal(), ε);
		//params.set(Parameter.λ0.ordinal(), λ0 );
		params.set(Parameter.η.ordinal(), η);
		return params;
	}

	public Vector getTransformedParameters()
	{
		Vector tparams = new Vector(getParamCount());
		tparams.set(Parameter.ε.ordinal(), 0.25 * tanh(ε) + 0.25);
		tparams.set(Parameter.η.ordinal(), exp(η));
    //tparams.set(Parameter.λ0.ordinal(), exp(λ0));
		return tparams;
	}

	public double getε()
	{
		return 0.25 * tanh(ε) + 0.25;
	}

	public void setε(double ε)
	{
		this.ε = FastMath.atanh(-1 + 4 * ε);
	}

	public void setη(double η)
	{
		this.η = log(η);
	}



  public int getParamCount()
  {
    return Parameter.values().length;
  }

	private double[] getParameterArray()
	{
		return getParameters().toPrimitiveArray();
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
	public int order()
	{
		return M + 1;
	}


  

}
