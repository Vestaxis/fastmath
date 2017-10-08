package stochastic.processes.timeseries;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.String.format;
import static org.apache.commons.math3.util.CombinatoricsUtils.binomialCoefficient;
import static org.apache.commons.math3.util.CombinatoricsUtils.factorial;

import org.apache.commons.math3.exception.MathArithmeticException;

import fastmath.DoubleColMatrix;
import fastmath.Vector;
import fastmath.exceptions.NotInvertableException;

/**
 * The Zero-Inflated Poisson Integer-valued AutoRegressive process
 * 
 * {@link https://github.com/crowlogic/back2thefuture/issues/5}
 */
public class ZIPINARProcess
{
	@Override
	public String toString()
	{
		return String.format("ZIPINARProcess[p=%s, λ=%s, α=%s, mean=%s, variance=%s, expectedZeroRunLength=%s]", p, λ,
				α, getMean(), getVariance(), getExpectedLengthOfZeroRuns());
	}

	public ZIPINARProcess(double p, double λ, double α)
	{
		this.p = p;
		this.λ = λ;
		this.α = α;
	}

	public ZIPINARProcess()
	{
		// TODO Auto-generated constructor stub
	}

	private double p;

	private double λ;

	private double α;

	private int M = -1; // maximum of avaliable integeter. -------nansui insert

	/**
	 * This is tested by ZIPInarProcessTest#testP
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	double getTransitionProbability(int i, int j)// ok
	{
		try
		{
		// this is the probability transition function going to state i leaving state j

		if (i == 0 && j == 0)
		{
			return 0.1e1 - (double) p + (double) p * (double) exp(-λ) / (double) (1 - p + p * exp(-λ))
					* (0.1e1 - (double) p + (double) p * exp(-(double) λ * (0.1e1 - α)));
		} else if (i != 0 && j == 0)
		{
			return (double) p / (double) (1 - p + p * exp(-λ))
					* ((double) ((1 - p) * exp(-λ) * pow(λ, i) / factorial(i))
							+ (double) p * (double) exp(-λ) * exp(-(double) λ * (0.1e1 - α))
									* pow((double) λ * (0.1e1 - α), (double) i) / (double) factorial(i));
		} else if (i == 0 && j != 0)
		{
			return 0.1e1 - (double) p + (double) p * pow(0.1e1 - α, (double) j) * exp(-λ * (0.1e1 - α));
		} else
		{
			double r;
			r = 0;
			for (int s = 0; s <= (i <= j ? i : j); s++)
				r = ((double) r + 0.1e1 / factorial(i - s) * binomialCoefficient(j, s) * pow(α, (double) s)
						* pow(0.1e1 - α, (double) (j - s)) * exp(-(double) λ * (0.1e1 - α))
						* pow(λ * (0.1e1 - α), (double) (i - s)));
			return p * r;
		}
		}
		catch(MathArithmeticException e)
		{
			throw new RuntimeException(format("i=%d j=%d\n", i, j ), e);
		}
	}

	public double P2(int i, int j)
	{
		if (i == 0 && j == 0)
		{
			return ((1 - p) + (p * exp(-λ) / ((1 - p) + p * exp(-λ))) * ((1 - p) + p * exp(-λ * (1 - α))));
		} else if (i != 0 && j == 0)
		{
			return (p / (1 - p + p * exp(-λ)) * (((1 - p) * exp(-λ) * pow(λ, i) / factorial(i))
					+ p * exp(-λ) * exp(-λ * (1 - α)) * pow(λ * (1 - α), i) / factorial(i)));
		} else if (i == 0 && j != 0)
		{
			return (1 - p + p * pow(1 - α, j) * exp(-λ * (1 - α)));
		} else
		{
			double r = 0;

			for (int s = 0; s <= Math.min(i, j); s++)
			{
				double summand1 = 1 / factorial(i - s) * binomialCoefficient(j, s) * pow(α, s) * pow(1 - α, (j - s))
						* exp(-λ * (1 - α)) * pow(λ * (1 - α), (i - s));
				double summand = binomialCoefficient(j, s) * pow(α, s) * pow(1 - α, j - s) * exp(-λ * (1 - α))
						* pow(λ * (1 - α), i - s) / factorial(i - s);
				double bah = ((double) r + 0.1e1 / factorial(i - s) * binomialCoefficient(j, s) * pow(α, (double) s)
						* pow(0.1e1 - α, (double) (j - s)) * exp(-(double) λ * (0.1e1 - α))
						* pow(λ * (0.1e1 - α), (double) (i - s)));
				r = r + bah;
			}
			return p * r;
		}

	}

	/**
	 * 
	 * @return the ergodic mean of the process
	 */
	public double getMean()// ok
	{
		return p * λ;
	}

	/**
	 * 
	 * @return the ergodic variance of the process
	 */
	public double getVariance()// ok
	{
		return p * λ + p * (1 - p) * pow(λ, 2);
	}

	public int getMode(int j)// ok
	{
		double maxp = 0;
		int mode = -1;

		if (M == -1)
			M = 20; // nansui insert

		for (int i = 0; i < M; i++) // nansui 20 ----> M
		{
			double p = getTransitionProbability(j, i);
			if (p > maxp)
			{
				maxp = p;
				mode = i;
			}
		}

		return mode;
	}

	public double getAutocorrelation(double h)// ok
	{
		return (pow(p, 2) * λ) / (p * λ + p * (1 - p) * pow(λ, 2)) * pow(α, h);

	}

	public double getExpectedLengthOfZeroRuns()// ok
	{
		double expnegλ = exp(-λ);
		double pexpnegλ = p * expnegλ;
		double numer = (1 - p + pexpnegλ);
		double expnegλ1minusα = exp(-λ * (1 - α));
		double denom = (p - (pexpnegλ / (1 - p + pexpnegλ))) * (1 - p + p * expnegλ1minusα);
		return numer / denom;
	}

	public void performYuleWalkerEstimation(Vector z)
	{
		final int n = z.size();
		final double mean = z.mean();

		double δ = sum(t -> z.get(t)*z.get(t) - mean, 0, n - 1 ) / n;
		double γ1 = sum(t -> ((z.get(t) - mean) * (z.get(t + 1) - mean)), 0, n - 2) / n;

		λ = δ / mean;
		p = pow(mean, 2) / δ;
		α = (γ1 * δ) / pow(mean, 3);
	}

	/**
	 * using newton-
	 * 
	 * @param z:
	 *            input
	 * @throws NotInvertableException
	 */
	public void performConditionalLeastSquaresEstimation(Vector z) throws NotInvertableException
	{
		final int n = z.size();

		double p0, λ0, α0;
		p0 = λ0 = α0 = 0;
		double p1, λ1, α1;
		p1 = λ1 = α1 = 0;

		int repeatingcount = 0;

		DoubleColMatrix mat1 = new DoubleColMatrix(3, 3);
		DoubleColMatrix mat2 = new DoubleColMatrix(3, 1);

		double parameterError;
		do
		{

			mat1.resize(3, 3);
			mat2.resize(3, 1);

			for (int i = 0; i < 3; i++)
			{
				for (int j = 0; j < 3; j++)
				{
					for (int k = 0; i < n - 1; i++)
						mat1.set(i, j, diffConditionalMean_CLS(i + 1, p0, λ0, α0, z.get(k))
								* diffConditionalMean_CLS(j + 1, p0, λ0, α0, z.get(k)));
				}
			}

			for (int i = 0; i < 3; i++)
				for (int k = 0; i < n - 1; i++)
					mat1.set(i, 0, z.get(k + 1) - conditionalMean_CLS(p0, λ0, α0, z.get(k)));

			mat1 = mat1.invert();

			mat2 = mat1.prod(mat2);

			p1 = p0 - mat2.get(0, 0);
			λ1 = λ0 - mat2.get(1, 0);
			α1 = α0 - mat2.get(2, 0);

			parameterError = Math.sqrt(pow(p1 - p0, 2) + pow(λ1 - λ0, 2) + pow(α1 - α0, 2));
			p1 = p0;
			λ1 = λ0;
			α1 = α0;

			repeatingcount++;
			if (repeatingcount > 10000)
				break;

		} while (parameterError > 0.001); // end loop if parameter error is smaller than 0.001;

		p = p0;
		λ = λ0;
		α = α0;

	}

	/**
	 * 
	 * @param mode
	 *            : 1: derivative by p, 2: derivative by λ, 3: derivative by α
	 * @param p
	 * @param λ
	 * @param α
	 * @param pz
	 * @return
	 */
	private double diffConditionalMean_CLS(int mode, double p, double λ, double α, double pz)
	{
		double ret = 0;
		if (pz != 0)
		{
			if (mode == 1)
				ret = α * pz + λ * (1 - α);
			else if (mode == 2)
				ret = p * (1 - α);
			else
				ret = p * pz - p * λ;
		} else
		{
			if (mode == 1)
				ret = λ * (1 - p * α * exp(-λ) / (1 - p + p * α * exp(-λ)))
						- p * λ * α * exp(-λ) / pow(1 - p + p * exp(-λ), 2);
			else if (mode == 2)
				ret = p * (1 - p * α * exp(-λ) / (1 - p + p * α * exp(-λ)))
						+ pow(p, 2) * λ * α * (1 - p) * exp(-λ) / pow(1 - p + p * exp(-λ), 2);
			else
				ret = -pow(p, 2) * λ * exp(-λ) / (1 - p + p * α * exp(-λ));

		}
		return ret;

	}

	private double conditionalMean_CLS(double p, double λ, double α, double pz)
	{

		if (pz == 0)
			return p * λ * (1 - p * α * exp(-λ) / (1 - p + p * exp(-λ)));

		return p * α * pz + p * λ * (1 - α);
	}

	/**
	 * 
	 * @param z
	 *            present state
	 * @param h
	 *            number of time-steps ahead to forecast
	 * @return mean of expected state h time-steps ahead from now
	 */
	public double forecastExpectedValue(int z, int h)
	{
		switch (z)
		{
		case 0:
			double numer = p * exp(-λ) * pow(α, h);
			double denom = 1 - p + p * exp(-λ);
			return getMean() * (1 - numer / denom);
		default:
			double first = 1 - pow(α, h);
			double second = p * z * pow(α, h);
			return getMean() * first + second;
		}
	}

}
