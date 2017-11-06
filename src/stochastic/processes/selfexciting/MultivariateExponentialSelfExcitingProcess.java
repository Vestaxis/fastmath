
package stochastic.processes.selfexciting;

import static fastmath.Functions.eye;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.System.err;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.LegendreGaussIntegrator;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.optimization.PointValuePair;

import fastmath.DoubleColMatrix;
import fastmath.DoubleMatrix;
import fastmath.EigenDecomposition;
import fastmath.Pair;
import fastmath.Vector;
import fastmath.VectorContainer;
import fastmath.exceptions.FastMathException;
import fastmath.exceptions.IllegalValueError;
import fastmath.exceptions.SingularFactorException;

/**
 * TODO: same thing I did with univariate exponential self exciting process
 * 
 *
 */
@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public abstract class MultivariateExponentialSelfExcitingProcess
{

  private final int dim; // dimension

  public abstract int order();

  // baseline intensity parameters
  Vector κ;

  // scale parameters
  DoubleMatrix[] α;

  // time decay parameters
  DoubleMatrix[] β;

  public MultivariateExponentialSelfExcitingProcess(int dim)
  {
    this.dim = dim;
    tmp1 = new VectorContainer[dim];
    tmp2 = new VectorContainer[dim];
    tmp3 = new VectorContainer[dim];
    tmp4 = new VectorContainer[dim];
    for (int i = 0; i < dim; i++)
    {
      tmp1[i] = new VectorContainer();
      tmp2[i] = new VectorContainer();
      tmp3[i] = new VectorContainer();
      tmp4[i] = new VectorContainer();
    }
  }

  VectorContainer tmp0 = new VectorContainer();

  VectorContainer tmp1[];

  VectorContainer tmp2[];

  VectorContainer tmp3[];

  VectorContainer tmp4[];

  private PointValuePair params;

  private Entry<Double, Integer>[][][] lowerEntries;

  private Entry<Double, Integer>[][][] upperEntries;

  /**
   * Given two Vectors (of times and types), calculate indices and partition
   * subsets of different types
   *
   * @param times
   * @param types
   * @return Pair<Vector times[dim],Map<time,type>[dim]>
   */
  public Pair<Vector[], TreeMap<Double, Integer>[]> getSubTimes(final Vector times, final Vector types)
  {
    @SuppressWarnings("unchecked")
    final ArrayList<Double>[] timesSub = new ArrayList[getDim()];
    final Vector[] timeVectors = new Vector[getDim()];
    Vector uniqueTypes = types.unique();
    TreeMap<Double, Integer> typeMap = new TreeMap<Double, Integer>();
    @SuppressWarnings("unchecked")
    TreeMap<Double, Integer>[] timeIndices = new TreeMap[getDim()];

    int j = 0;
    for (Double type : uniqueTypes)
    {
      typeMap.put(type, j++);
    }
    for (int i = 0; i < getDim(); i++)
    {
      timesSub[i] = new ArrayList<Double>();
      timeIndices[i] = new TreeMap<Double, Integer>();
    }
    for (int i = 0; i < times.size(); i++)
    {
      timesSub[typeMap.get(types.get(i))].add(times.get(i));
    }
    for (int i = 0; i < getDim(); i++)
    {
      ArrayList<Double> subTimes = timesSub[i];
      TreeMap<Double, Integer> subTimeIndices = timeIndices[i];
      for (j = 0; j < subTimes.size(); j++)
      {
        subTimeIndices.put(subTimes.get(j), j);
      }
      timeVectors[i] = new Vector(timesSub[i]);
    }
    return new Pair<Vector[], TreeMap<Double, Integer>[]>(timeVectors, timeIndices);
  }

  @SuppressWarnings("unused")
  private Vector calculateInitialGuess(Vector times, Vector types)
  {
    final int matrixSize = getDim() * getDim();
    final Vector params = tmp0.getVector(getDim() + (matrixSize * order() * 2)).assign(0.0);
    // baseline intensity params

    final Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair = getSubTimes(times, types);
    final Vector[] timesSub = timesSubPair.left;

    for (int i = 0; i < getDim(); i++)
    {
      System.out.println("mean[" + i + "]=" + 1.0 / (timesSub[i].diff().mean()));
    }

    κ = params.slice(0, getDim());
    α = new DoubleMatrix[order()];
    β = new DoubleMatrix[order()];
    for (int j = 0; j < getDim(); j++)
    {
      Vector theseTimes = new Vector(timesSub[j]);
      κ.set(j, 0.5 / theseTimes.diff().mean());
    }

    for (int j = 0; j < order(); j++)
    {
      α[j] = params.slice(getDim() + (j * matrixSize), getDim() + ((j + 1) * matrixSize)).reshape(getDim(), getDim());
      β[j] = params.slice(getDim() + (order() * matrixSize) + (j * matrixSize), getDim() + (order() * matrixSize) + ((j + 1) * matrixSize)).reshape(getDim(),
                                                                                                                                                    getDim());
      α[j].diag().assign(1.0 / order());
      β[j].assign(2.0);
    }

    return params;
  }

  /**
   * @return null if mean could not be calculated
   */
  public Vector calculateMean()
  {
    try
    {
      return eye(getDim()).subtract(calculateBranchingMatrix()).ldivide(κ.asMatrix().trans().copy(false)).asVector();
    }
    catch (SingularFactorException e)
    {
      System.err.println(e.getMessage());
      return null;
    }
    catch (IllegalValueError e)
    {
      System.err.println(e.getMessage());
      return null;
    }
  }

  /**
   * @return sum(α[j][m][n]/β[j][m][n],j=1..order)
   */
  public DoubleColMatrix calculateBranchingMatrix()
  {
    DoubleColMatrix αβ = new DoubleColMatrix(getDim(), getDim());
    for (int j = 0; j < order(); j++)
    {
      DoubleMatrix αj = α[j];
      DoubleMatrix βj = β[j];
      for (int m = 0; m < getDim(); m++)
      {
        for (int n = 0; n < getDim(); n++)
        {
          αβ.add(m, n, αj.get(m, n) / βj.get(m, n));
        }
      }
    }
    return αβ;
  }

  /**
   * @return false if any of the absolute values of the eigenvalues of this
   *         {@link #calculateBranchingMatrix()} are >=1 or if the eigenvalue
   *         decomposition cannot be calculated
   */
  public boolean isStationary()
  {
    EigenDecomposition eig;
    try
    {
      eig = calculateBranchingMatrix().eig();
    }
    catch (FastMathException e)
    {
      e.printStackTrace(err);
      return false;
    }
    for (int i = 0; i < getDim(); i++)
    {
      double d = sqrt(pow(eig.getRealEigenvalues().get(i), 2) + pow(eig.getImaginaryEigenvalues().get(i), 2));
      if (d >= 1) { return false; }
    }
    return true;
  }

  public Vector calculateIntensitySlow(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair, int m)
  {
    final Vector[] timesSub = timesSubPair.left;

    final Vector mtimes = timesSub[m];
    final int Nm = mtimes.size();
    Vector intensity = tmp1[m].getVector(Nm - 1);
    double kappa = this.κ.get(m);
    for (int i = 1; i < Nm; i++)
    {
      double lambda = kappa;
      final double mtime = mtimes.get(i);

      for (int n = 0; n < getDim(); n++)
      {
        final Vector ntimes = timesSub[n];
        final int Nn = ntimes.size();
        for (int j = 0; j < order(); j++)
        {
          final double αjmn = α[j].get(m, n);
          final double βjmn = β[j].get(m, n);
          double ntime;
          for (int k = 0; k < Nn && (ntime = ntimes.get(k)) < mtime; k++)
          {
            lambda += αjmn * exp(-βjmn * (mtime - ntime));
          }
        }
      }

      intensity.set(i - 1, lambda);
    }
    return intensity;
  }

  public Vector calculateIntensity(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair, int m)
  {
    double R[][][] = new double[order()][getDim()][getDim()];
    Vector[] timesSub = timesSubPair.left;
    TreeMap<Double, Integer>[] subTimeIndex = timesSubPair.right;
    final Vector mtimes = timesSub[m];
    Vector intensity = tmp4[m].getVector(mtimes.size() - 1);
    final int Nm = mtimes.size();
    for (int i = 1; i < Nm; i++)
    {
      final double upperTime = mtimes.get(i);
      final double lowerTime = mtimes.get(i - 1);
      double mtimeDiff = upperTime - lowerTime;
      double logsum = κ.get(m) * getDeterministicIntensity(m, upperTime, i);

      for (int n = 0; n < getDim(); n++)
      {
        final Vector ntimes = timesSub[n];
        Entry<Double, Integer> floorEntry = getLowerEntry(subTimeIndex, lowerTime, n, m, i);
        Entry<Double, Integer> ceilEntry = getUpperEntry(subTimeIndex, upperTime, n, m, i);

        for (int j = 0; j < order(); j++)
        {
          double r;
          double βjmn = β[j].get(m, n);
          if (m != n)
          {
            r = exp(-βjmn * mtimeDiff) * R[j][m][n];
            int initialk = floorEntry != null ? floorEntry.getValue() : 0;
            int finalk = ceilEntry != null ? ceilEntry.getValue() : ntimes.size();
            for (int k = initialk; k < finalk; k++)
            {
              final Double ktime = ntimes.get(k);
              if (ktime >= lowerTime && ktime < upperTime)
              {
                r += exp(-βjmn * (upperTime - ktime));
              }
            }

          }
          else
          {
            r = exp(-βjmn * mtimeDiff) * (1 + R[j][m][n]);
          }
          logsum += α[j].get(m, n) * r;
          R[j][m][n] = r;
        }
      }
      intensity.set(i - 1, logsum);
    }
    return intensity;
  }

  protected double getDeterministicIntensity(int m, double upperTime, int i)
  {
    return 1;
  }

  protected double getDeterministicCompensator(int m, double upperTime, double lowerTime, int i)
  {
    return upperTime - lowerTime;
  }

  private Entry<Double, Integer> getUpperEntry(TreeMap<Double, Integer>[] subTimeIndex, final double upperTime, int n, int m, int i)
  {
    Entry<Double, Integer> upperEntry = upperEntries[n][m][i];
    if (upperEntry == null)
    {
      upperEntry = subTimeIndex[n].ceilingEntry(upperTime);
      upperEntries[n][m][i] = upperEntry;
    }
    return upperEntry;
  }

  private Entry<Double, Integer> getLowerEntry(TreeMap<Double, Integer>[] subTimeIndex, final double lowerTime, int n, int m, int i)
  {
    Entry<Double, Integer> lowerEntry = lowerEntries[n][m][i];
    if (lowerEntry == null)
    {
      lowerEntry = subTimeIndex[n].ceilingEntry(lowerTime);
      lowerEntries[n][m][i] = lowerEntry;
    }
    return lowerEntry;
  }

  public Vector calculateCompensatorSlow(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair, int m)
  {
    final Vector[] timesSub = timesSubPair.left;
    double kappa = this.κ.get(m);
    final Vector mtimes = timesSub[m];
    final int N = mtimes.size();
    Vector compensator = tmp2[m].getVector(N - 1);
    for (int i = 1; i < N; i++)
    {
      double upperTime = mtimes.get(i);
      double lowerTime = mtimes.get(i - 1);
      double sum = (upperTime - lowerTime) * kappa;
      for (int n = 0; n < getDim(); n++)
      {
        final Vector ntimes = timesSub[n];
        final int Nn = ntimes.size();

        for (int j = 0; j < order(); j++)
        {
          final double αjmn = α[j].get(m, n);
          final double βjmn = β[j].get(m, n);
          double ktime;
          int k = 0;
          // for (k = 0; k < Nn && (ktime = ntimes.get(k)) <
          // lowerTime; k++)
          for (k = 0; k < Nn && (ktime = ntimes.get(k)) < upperTime; k++)
          {
            sum += (αjmn / βjmn) * (exp(-βjmn * (lowerTime - ktime)) - exp(-βjmn * (upperTime - ktime)));
          }

          // for (; k < Nn && (ktime = ntimes.get(k)) < upperTime;
          // k++)
          // {
          // if (lowerTime <= ktime && ktime < upperTime)
          // {
          // sum += (αjmn / βjmn) * (1 - exp(-βjmn *
          // (upperTime -
          // ktime)));
          // }
          // }

        }
      }
      compensator.set(i - 1, sum);
    }
    return compensator;
  }

  public Vector calculateCompensator(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair, int m)
  {
    final Vector[] timesSub = timesSubPair.left;
    final TreeMap<Double, Integer>[] subTimeIndex = timesSubPair.right;
    double kappa = this.κ.get(m);
    final Vector mtimes = timesSub[m];
    final int N = mtimes.size();
    Vector compensator = tmp3[m].getVector(N - 1);
    double A[][][] = new double[order()][getDim()][getDim()];

    for (int i = 1; i < N; i++)
    {
      double upperTime = mtimes.get(i);
      double lowerTime = mtimes.get(i - 1);
      double lowerTimeBeforeLast = i > 2 ? mtimes.get(i - 2) : Double.NEGATIVE_INFINITY;

      double sum = getDeterministicCompensator(m, upperTime, lowerTime, i) * kappa;
      for (int n = 0; n < getDim(); n++)
      {
        final Vector ntimes = timesSub[n];
        Entry<Double, Integer> lowerEntryBeforeLast = Double.isInfinite(lowerTimeBeforeLast) ? null
                                                                                             : getLowerEntry(subTimeIndex, lowerTimeBeforeLast, n, m, i - 1);
        Entry<Double, Integer> lowerEntry = getLowerEntry(subTimeIndex, lowerTime, n, m, i);
        Entry<Double, Integer> upperEntry = getUpperEntry(subTimeIndex, upperTime, n, m, i);
        int lowerkBeforeLast = lowerEntryBeforeLast != null ? lowerEntryBeforeLast.getValue() : 0;
        int lowerk = lowerEntry != null ? lowerEntry.getValue() : 0;
        int upperk = upperEntry != null ? upperEntry.getValue() : ntimes.size();

        for (int j = 0; j < order(); j++)
        {
          final double αjmn = α[j].get(m, n);
          final double βjmn = β[j].get(m, n);
          double ktime;
          int k;
          double subsum = exp(-βjmn * (lowerTime - lowerTimeBeforeLast)) * A[j][m][n];
          for (k = lowerkBeforeLast; k < lowerk; k++)
          {
            ktime = ntimes.get(k);
            if (ktime < lowerTime && ktime >= lowerTimeBeforeLast)
            {
              subsum += exp(-βjmn * (lowerTime - ktime));
            }
          }
          A[j][m][n] = subsum;

          double innerSum = subsum * (1 - exp(-βjmn * (upperTime - lowerTime)));
          for (k = lowerk; k < upperk; k++)
          {
            ktime = ntimes.get(k);
            if (ktime >= lowerTime && ktime < upperTime)
            {
              innerSum += 1 - exp(-βjmn * (upperTime - ktime));
            }
          }

          sum += (αjmn / βjmn) * innerSum;

        }
      }
      compensator.set(i - 1, sum);
    }
    return compensator;

  }

  private VectorContainer tmp5 = new VectorContainer();

  private ExponentialDistribution expDist = new ExponentialDistribution(1);

  private final LegendreGaussIntegrator integrator = new LegendreGaussIntegrator(5, pow(10, -5), pow(10, -5));

  private int predictionIntegrationLimit = 25;

  public String getParamString()
  {
    return tmp5.getVector(params.getPoint().length).assign(params.getPoint()).toString();
  }

  public PointValuePair getParams()
  {
    return params;
  }

  public void setParams(PointValuePair params)
  {
    this.params = params;
  }

  public int getDim()
  {
    return dim;
  }

  private double lastInitialLambda2Guess = 0.0;

  /**
   *
   * @param timesSubPair
   * @param m
   *          dim
   * @param eta
   *          i.i.d. exponential random variable with mean 1
   * @return
   */
  public double predict(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair, final int m)
  {
    final Vector[] timesSub = timesSubPair.left;
    final TreeMap<Double, Integer>[] subTimeIndex = timesSubPair.right;
    final double kappa = this.κ.get(m);
    final Vector mtimes = timesSub[m].extend(1);
    final int N = mtimes.size();
    final double lastTime = mtimes.get(mtimes.size() - 2);

    BrentSolver solver = new BrentSolver(pow(10, -15));
    Vector inverses = new Vector(10000);
    for (int l = 0; l < inverses.size(); l++)
    {
      final double eta = expDist.sample();
      UnivariateFunction integrand = getPredictiveDensity(m, timesSub, subTimeIndex, kappa, mtimes, N, eta);
      double nextTime = solver.solve(100000, integrand, lastTime - 0.0001, lastTime + 100);
      inverses.set(l, nextTime);
    }
    return inverses.mean();
  }

  public UnivariateFunction getPredictiveDensity(final int m, final Vector[] timesSub, final TreeMap<Double, Integer>[] subTimeIndex, final double kappa,
      final Vector mtimes, final int N, final double eta)
  {
    return new UnivariateFunction()
    {
      @Override
      public double value(double t)
      {
        final double A[][][] = new double[order()][getDim()][getDim()];
        mtimes.set(N - 1, t);
        final int dummy = N - 1;

        double sum = -eta;
        for (int i = 1; i < N; i++)
        {
          double upperTime = mtimes.get(i);
          double lowerTime = mtimes.get(i - 1);
          double lowerTimeBeforeLast = i > 2 ? mtimes.get(i - 2) : Double.NEGATIVE_INFINITY;
          // assert upperTime >= lowerTime;

          sum = getDeterministicCompensator(m, upperTime, lowerTime, i) * kappa;
          for (int n = 0; n < getDim(); n++)
          {
            final Vector ntimes = timesSub[n];
            Entry<Double, Integer> lowerEntryBeforeLast =
                                                        Double.isInfinite(lowerTimeBeforeLast) ? null
                                                                                               : getLowerEntry(subTimeIndex, lowerTimeBeforeLast, n, m, i - 1);
            Entry<Double, Integer> lowerEntry = getLowerEntry(subTimeIndex, lowerTime, n, m, i);
            Entry<Double, Integer> upperEntry = getUpperEntry(subTimeIndex, upperTime, n, m, i);
            int lowerkBeforeLast = lowerEntryBeforeLast != null ? lowerEntryBeforeLast.getValue() : 0;
            int lowerk = lowerEntry != null ? lowerEntry.getValue() : 0;
            int upperk = upperEntry != null ? upperEntry.getValue() : mtimes.size();

            for (int j = 0; j < order(); j++)
            {
              final double αjmn = α[j].get(m, n);
              final double βjmn = β[j].get(m, n);
              double ktime;
              int k;
              double subsum = exp(-βjmn * (lowerTime - lowerTimeBeforeLast)) * A[j][m][n];
              for (k = lowerkBeforeLast; k < lowerk; k++)
              {
                ktime = ntimes.get(k);
                if (ktime < lowerTime && ktime >= lowerTimeBeforeLast)
                {
                  subsum += exp(-βjmn * (lowerTime - ktime));
                }
              }
              A[j][m][n] = subsum;

              double innerSum = subsum * (1 - exp(-βjmn * (upperTime - lowerTime)));
              for (k = lowerk; k < upperk; k++)
              {
                ktime = ntimes.get(k);
                if (ktime >= lowerTime && ktime < upperTime)
                {
                  innerSum += 1 - exp(-βjmn * (upperTime - ktime));
                }
              }

              sum += (αjmn / βjmn) * innerSum;

            }
          }
        }

        System.out.println("t=" + t + " eta=" + eta + " sum=" + sum);

        return sum;
      }
    };
  }

  public int getPredictionIntegrationLimit()
  {
    return predictionIntegrationLimit;
  }

  public void setPredictionIntegrationLimit(int predictionIntegrationLimit)
  {
    this.predictionIntegrationLimit = predictionIntegrationLimit;
  }

}
