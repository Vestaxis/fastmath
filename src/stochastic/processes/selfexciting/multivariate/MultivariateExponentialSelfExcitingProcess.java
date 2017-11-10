
package stochastic.processes.selfexciting.multivariate;

import static fastmath.Functions.eye;
import static fastmath.Functions.uniformRandom;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.err;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static java.util.stream.Stream.concat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.LegendreGaussIntegrator;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.random.RandomVectorGenerator;

import fastmath.AbstractMatrix;
import fastmath.DoubleColMatrix;
import fastmath.EigenDecomposition;
import fastmath.IntVector;
import fastmath.Pair;
import fastmath.Vector;
import fastmath.VectorContainer;
import fastmath.exceptions.FastMathException;
import fastmath.exceptions.IllegalValueError;
import fastmath.exceptions.SingularFactorException;
import fastmath.optim.ObjectiveFunctionSupplier;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import fastmath.optim.PointValuePairComparator;
import fastmath.optim.SolutionValidator;

public abstract class MultivariateExponentialSelfExcitingProcess extends MultivariateSelfExcitingProcess
{


  public abstract int order();

  // baseline intensity parameters
  Vector κ;

  public MultivariateExponentialSelfExcitingProcess()
  {

  }

  private Entry<Double, Integer>[][][] lowerEntries;

  private Entry<Double, Integer>[][][] upperEntries;

  private final ObjectiveFunctionSupplier objectiveFunctionSupplier = () -> new ObjectiveFunction(copy());

  /**
   * 
   * @param T
   * @param deterministicIntensity
   * @param lambda
   * @param alpha
   * @param bη
   * @return Pair<logLik,E[Lambda]>
   */
  public final double logLik()
  {
    assert T != null : "T cannot be null";
    double tn = T.getRightmostValue();
    double ll = tn - T.getLeftmostValue();
    final int n = T.size();

    double A[] = new double[order()];
    double S[] = new double[order()];
    for (int i = 1; i < n; i++)
    {
      double t = T.get(i);
      double prevdt = i == 1 ? 0 : (T.get(i - 1) - T.get(i - 2));
      double dt = t - T.get(i - 1);
      double λ = evolveλ(dt, T.get(i), S);
      double Λ = evolveΛ(prevdt, dt, T.get(i), A);

      // double Λ = sum(j -> ( α(j) / β(j) ) * (exp(-β(j) * (tn - t)) - 1), 0, M);

      if (λ > 0)
      {
        ll += log(λ);
      }

      ll -= Λ;

    }

    if (Double.isNaN(ll))
    {
      if (verbose)
      {
        out.println(Thread.currentThread().getName() + " NaN for LL ");
      }
      ll = Double.NEGATIVE_INFINITY;
    }

    return ll;

  }

  protected boolean verbose = false;

  public ParallelMultistartMultivariateOptimizer estimateParameters(int numStarts)
  {
    int digits = 15;
    int maxIters = Integer.MAX_VALUE;
    assert T != null : "T cannot be null";
    assert K != null : "K cannot be null";

    assert T.size() == K.size() : "times and types should be of same dimension";

    final MultivariateFunction logLikelihoodFunction = new MultivariateFunction()
    {

      @Override
      public double value(double[] paramArray)
      {
        Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair = getSubTimes(T, K);

        Vector mean = calculateMean();
        double ll = 0;
        Double compMeans[] = new Double[getDim()];

        Double compVars[] = new Double[getDim()];
        for (int i = 0; i < getDim(); i++)
        {
          final Vector intensity = calculateIntensity(timesSubPair, i);

          // final Vector compensator =
          // calculateCompensatorSlow(timesSubPair,
          // i);
          final Vector compensator = calculateCompensator(timesSubPair, i);
          compMeans[i] = compensator.mean();
          compVars[i] = compensator.variance();
          // double fastMean = compensatorFast.mean();
          // System.out.println("fastMean " + fastMean + " should equal " +
          // compMeans[i]);

          assert intensity.size() == compensator.size();
          for (int j = 0; j < intensity.size(); j++)
          {
            ll += Math.log(intensity.get(j)) - compensator.get(j);
          }
        }

        return ll;
      }

    };

    MaxEval maxEval = new MaxEval(maxIters);
    SimpleBounds simpleBounds = getParameterBounds();

    SolutionValidator validator = point -> {
      MultivariateExponentialSelfExcitingProcess process = newProcess(point.getPoint());
      return process.Λ().mean() > 0;
    };

    Supplier<MultivariateOptimizer> optimizerSupplier = () -> new BOBYQAOptimizer(getParamCount() * dim * 2 + 1);

    ParallelMultistartMultivariateOptimizer optimizer = new ParallelMultistartMultivariateOptimizer(optimizerSupplier,
                                                                                                    numStarts,
                                                                                                    getRandomVectorGenerator(simpleBounds));

    PointValuePairComparator momentMatchingComparator = (a, b) -> {
      MultivariateExponentialSelfExcitingProcess processA = newProcess(a.getPoint());
      MultivariateExponentialSelfExcitingProcess processB = newProcess(b.getPoint());
      double mma = processA.ΛmomentMeasure();
      double mmb = processB.ΛmomentMeasure();
      return Double.compare(mma, mmb);
    };

    double startTime = currentTimeMillis();
    PointValuePair optimum = optimizer.optimize(GoalType.MAXIMIZE, momentMatchingComparator, validator, maxEval, objectiveFunctionSupplier, simpleBounds);
    double stopTime = currentTimeMillis();
    double secondsElapsed = (stopTime - startTime) / 1000;
    double evaluationsPerSecond = optimizer.getEvaluations() / secondsElapsed;
    double minutesElapsed = secondsElapsed / 60;

    assignParameters(optimum.getKey());

    out.format("estimation completed in %f minutes at %f evals/sec\n", minutesElapsed, evaluationsPerSecond);

    return optimizer;
  }

  public double ΛmomentMeasure()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double ΛmomentLjungBoxMeasure()
  {
    throw new UnsupportedOperationException("TODO");
  }

  public MultivariateExponentialSelfExcitingProcess newProcess(double[] point)
  {
    throw new UnsupportedOperationException("TODO");
  }

  public Vector Λ()
  {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Given two Vectors (of times and types), calculate indices and partition
   * subsets of different types
   *
   * @param times
   * @param types
   * @return Pair<Vector times[dim],Map<time,type>[dim]>
   */
  public Pair<Vector[], TreeMap<Double, Integer>[]> getSubTimes(final Vector times, final IntVector types)
  {
    if (cachedSubTimes != null) { return cachedSubTimes; }
    final ArrayList<Double>[] timesSub = new ArrayList[getDim()];
    final Vector[] timeVectors = new Vector[getDim()];
    TreeMap<Double, Integer>[] timeIndices = new TreeMap[getDim()];

    for (int i = 0; i < getDim(); i++)
    {
      timesSub[i] = new ArrayList<Double>();
      timeIndices[i] = new TreeMap<Double, Integer>();
    }
    for (int i = 0; i < times.size(); i++)
    {
      timesSub[types.get(i)].add(times.get(i));
    }
    for (int i = 0; i < getDim(); i++)
    {
      ArrayList<Double> subTimes = timesSub[i];
      TreeMap<Double, Integer> subTimeIndices = timeIndices[i];
      for (int j = 0; j < subTimes.size(); j++)
      {
        subTimeIndices.put(subTimes.get(j), j);
      }
      timeVectors[i] = new Vector(timesSub[i]);
    }
    cachedSubTimes = new Pair<Vector[], TreeMap<Double, Integer>[]>(timeVectors, timeIndices);

    return cachedSubTimes;
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
   * 
   * @param j
   *          index in [0,order()-1]
   * @param m
   *          dimension in [0,dim-1]
   * @param n
   *          dimension in [0,dim-1]
   * 
   * @return the j-th α parameter corresponding to the k-th dimension
   */
  protected abstract double α(int j, int m, int n);

  /**
   * 
   * @param j
   *          index in [0,order()-1]
   * @param m
   *          dimension in [0,dim-1]
   * @param n
   *          dimension in [0,dim-1]
   * @return the j-th β parameter corresponding to the k-th dimension
   */
  protected abstract double β(int j, int m, int n);

  /**
   * Calculate sum(α(j,m,n)/β(j,m,n),j=1..order)
   * 
   * @return branching matrix of dimsion this{@link #dim} x this{@link #dim}
   */
  public DoubleColMatrix calculateBranchingMatrix()
  {
    DoubleColMatrix αβ = new DoubleColMatrix(getDim(), getDim());
    for (int j = 0; j < order(); j++)
    {
      for (int m = 0; m < getDim(); m++)
      {
        for (int n = 0; n < getDim(); n++)
        {
          αβ.add(m, n, α(j, m, n) / β(j, m, n));
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
    Vector intensity = new Vector(Nm - 1);
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
          final double αjmn = α(j, m, n);
          final double βjmn = β(j, m, n);
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
    Vector intensity = new Vector(mtimes.size() - 1);
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
          double βjmn = β(j, m, n);
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
          logsum += α(j, m, n) * r;
          R[j][m][n] = r;
        }
      }
      intensity.set(i - 1, logsum);
    }
    return intensity;
  }

  /**
   * @see this{@link #calculateIntensity(Pair, int)}
   * @param dt
   * @param d
   * @param s
   * @return
   */
  protected double evolveλ(double dt, double d, double[] s)
  {
    throw new UnsupportedOperationException("TODO: do as in calculateIntensity");
  }

  protected double getDeterministicIntensity(int m, double upperTime, int i)
  {
    return κ.get(m);
  }

  protected double getDeterministicCompensator(int m, double upperTime, double lowerTime, int i)
  {
    return (upperTime - lowerTime) * getDeterministicIntensity(m, upperTime, i);
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
    Vector compensator = new Vector(N - 1);
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
          final double αjmn = α(j, m, n);
          final double βjmn = β(j, m, n);
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

  /**
   * @see this{@link #calculateCompensator(Pair, int)}
   * @param prevdt
   * @param dt
   * @param d
   * @param a
   * @return
   */
  protected double evolveΛ(double prevdt, double dt, double d, double[] a)
  {
    throw new UnsupportedOperationException("TODO: do as in calculateCompensator");
  }

  public Vector calculateCompensator(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair, int m)
  {
    final Vector[] timesSub = timesSubPair.left;
    final TreeMap<Double, Integer>[] subTimeIndex = timesSubPair.right;
    double kappa = this.κ.get(m);
    final Vector mtimes = timesSub[m];
    final int N = mtimes.size();
    Vector compensator = new Vector(N - 1);
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
          final double αjmn = α(j, m, n);
          final double βjmn = β(j, m, n);
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

  public int getDim()
  {
    return dim;
  }

  private double lastInitialLambda2Guess = 0.0;

  private Pair<Vector[], TreeMap<Double, Integer>[]> cachedSubTimes;

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
              final double αjmn = α(j, m, n);
              final double βjmn = β(j, m, n);
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

  protected RandomVectorGenerator getRandomVectorGenerator(SimpleBounds bounds)
  {
    return () -> {
      try
      {
        double[] point = rangeClosed(0,
                                     bounds.getLower().length - 1).mapToDouble(dim -> uniformRandom(new Pair<>(bounds.getLower()[dim], bounds.getUpper()[dim])))
                                                                  .toArray();
        if (trace)
        {
          out.println(Thread.currentThread().getName() + " starting from " + Arrays.toString(point));
        }
        return point;
      }
      catch (Exception e)
      {
        e.printStackTrace(System.err);
        return null;
      }
    };
  }

  private boolean trace = false;

  public static String[] statisticNames =
  { "Log-Lik", "1-KS(Λ,exp)", "mean(Λ)", "var(Λ)", "MM(Λ)", "(LjungBox(Λ,10)-8)^2", "E[dt]" };

  /**
   * 
   * @return a list formed by concatenating the names of the parameters enumerated
   *         by this{@link #getBoundedParameters()} and the names of the
   *         statistics enumerated by this{@link #statisticNames}
   */
  public String[] getColumnHeaders()
  {
    return concat(stream(getBoundedParameters()).map(param -> param.getName()), asList(statisticNames).stream()).collect(toList()).toArray(new String[0]);
  }

  public AbstractMatrix conditionalλ()
  {
    throw new UnsupportedOperationException("TODO");
  }

  public void storeParameters(File modelFile)
  {
    throw new UnsupportedOperationException("TODO");
  }
}
