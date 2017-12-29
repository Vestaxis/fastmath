
package stochastic.processes.selfexciting.multivariate;

import static fastmath.Functions.eye;
import static fastmath.Functions.product;
import static fastmath.Functions.sum;
import static fastmath.Functions.uniformRandom;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.err;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static java.util.stream.Stream.concat;
import static org.apache.commons.lang.ArrayUtils.addAll;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.IntConsumer;
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;

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
import org.arblib.Real;

import fastmath.DoubleColMatrix;
import fastmath.EigenDecomposition;
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
import stochastic.processes.pointprocesses.finance.TradingFiltration;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory;

public abstract class ExponentialMutuallyExcitingProcess extends MutuallyExcitingProcess
{

  public abstract int
         order();

  // baseline intensity parameters
  Vector κ;

  public ExponentialMutuallyExcitingProcess()
  {

  }

  @Override
  public double
         Φδ(double t,
            double y)
  {
    int tk = T.size() - 1;
    throw new UnsupportedOperationException("TODO");
    // return Φ(t, y, tk) / Φdt(t, tk);
  }

  public double
         Φ(int m,
           double dt,
           double y,
           int tk)
  {
    double τ = τ(m, dt, y);
    out.println("τ=" + τ);
    return τ + sum(l -> sum(i -> Φ(m, i, l) * sum(k -> σ(m, i, l, k, dt, dt) - σ(m, i, l, k, dt, lastT(m)), 0, tk), 0, dim() - 1), 0, order() - 1);
  }

  /**
   * 
   * @param m
   * @param dt
   * @param y
   * @return -y * this{@link #v(int)}(m)*{@link #η(int, double)}(m,dt)
   */
  public double
         τ(int m,
           double dt,
           double y)
  {
    double v = v(m);
    double η = η(m, dt);
    out.println("v=" + v + " η=" + η);
    return -y * v * η;
  }

  /**
   * 
   * @param m
   *          dimension/index of the process, an integer in [0,dim)
   * 
   * @return ∏(∏(β(m,n,j),j=1..P),n=1..M)
   */
  public double
         v(int m)
  {
    return product((IntToDoubleFunction) j -> product((IntToDoubleFunction) n -> β(m, n, j), 0, dim() - 1), 0, order() - 1);
  }

  public double
         Φ(int m,
           int i,
           int l)
  {
    return product((IntToDoubleFunction) j -> product((IntToDoubleFunction) n -> n == i && j == l ? α(m, n, j) : β(m, n, j), 0, dim() - 1), 0, order() - 1);
  }

  public double
         σ(int m,
           int i,
           int l,
           int k,
           double ds,
           double dt)
  {
    assert m < dim() : "m=" + m + " >= dim";
    double Tm = lastT(m);

    return exp(sum(j -> sum(n -> β(m, n, j) * ((n == i && j == l) ? (ds + t(n, k)) : (dt + lastT(n))), 0, dim() - 1), 0, order() - 1));
  }

  /**
   * get time of i-th point of the m-th proces
   * 
   * @param m
   *          ordinal, integer in [0,dim)
   * @param i
   *          time index, starts at 0
   * @return
   */
  public double
         t(int m,
           int i)
  {
    assert m < dim() : "m=" + m + " >= dim";

    return getSubTimes().left[m].get(i);
  }

  public double
         η(int m,
           double dt)
  {
    double Tm = lastT(m);

    double vol = sum(j -> sum(n -> {
      double β = β(m, n, j);
      out.println(format("β(m=%d,n=%d,j=%d)=%f", m, n, j, β));
      return β;
    }, 0, dim() - 1), 0, order() - 1);

    double measure = (dt + Tm) * vol;
    out.println("vol=" + vol + " dt=" + dt + " Tm=" + Tm + " measure=" + measure);
    return exp(measure);
  }

  public Double
         lastT(int m)
  {
    assert m < dim() : "m=" + m + " >= dim";

    return getSubTimes().right[m].lastKey();
  }

  public double
         γ(int type,
           int k)
  {
    IntToDoubleFunction a = j -> j == k ? α(j, type, type) : β(j, type, type);

    return product(a, 0, order() - 1);
  }

  @Override
  public double
         Φδ(double t,
            double y,
            int tk)
  {
    return sum(m -> Φ(m, t, y, tk) / Φdt(t, tk), 0, dim() - 1);
  }

  public double
         Φdt(double t,
             int tk)
  {
    throw new UnsupportedOperationException("TODO");
  }

  private Entry<Double, Integer>[][][] lowerEntries;

  private Entry<Double, Integer>[][][] upperEntries;

  private final ObjectiveFunctionSupplier objectiveFunctionSupplier = () -> new ObjectiveFunction(copy());

  private double[][][] A;

  private Real[][][] AReal;

  public final double
         logLik()
  {
    double tn = T.getRightmostValue();
    double ll = tn - T.getLeftmostValue() - totalΛ();
    final int n = T.size();

    double S[][] = new double[order()][dim()];
    for (int type = 0; type < dim(); type++)

    {
      for (int tk = 1; tk < n; tk++)
      {
        double t = T.get(tk);
        double prevdt = tk == 1 ? 0 : (T.get(tk - 1) - T.get(tk - 2));
        double dt = t - T.get(tk - 1);
        double λ = evolveλ(type, dt, S);

        if (λ > 0)
        {
          ll += log(λ);
        }

        // ll -= Λ;

      }

      if (Double.isNaN(ll))

      {
        if (verbose)
        {
          out.println(Thread.currentThread().getName() + " NaN for LL ");
        }
        ll = Double.NEGATIVE_INFINITY;
      }
    }

    return ll;

  }

  protected boolean verbose = false;

  public ParallelMultistartMultivariateOptimizer
         estimateParameters(int numStarts,
                            IntConsumer progressNotifier)
  {
    int digits = 15;
    int maxIters = Integer.MAX_VALUE;

    MaxEval maxEval = new MaxEval(maxIters);
    SimpleBounds simpleBounds = getParameterBounds();

    SolutionValidator validator = point -> {
      ExponentialMutuallyExcitingProcess process = newProcess(point.getPoint());
      double mean = process.Λ().mean();
      out.println("process mean " + mean);
      return mean > 0;
    };

    Supplier<MultivariateOptimizer> optimizerSupplier = () -> new BOBYQAOptimizer(getParamCount() * dim() * 2 + 1);

    ParallelMultistartMultivariateOptimizer multiopt = new ParallelMultistartMultivariateOptimizer(optimizerSupplier,
                                                                                                   numStarts,
                                                                                                   getRandomVectorGenerator(simpleBounds));

    PointValuePairComparator momentMatchingAutocorrelationComparator = (a,
                                                                        b) -> {
      ExponentialMutuallyExcitingProcess processA = newProcess(a.getPoint());
      ExponentialMutuallyExcitingProcess processB = newProcess(b.getPoint());
      double mma = processA.getΛmomentLjungBoxMeasure();
      double mmb = processB.getΛmomentLjungBoxMeasure();
      return Double.compare(mma, mmb);
    };

    double startTime = currentTimeMillis();
    PointValuePair optimum = multiopt.optimize(progressNotifier,
                                               GoalType.MAXIMIZE,
                                               momentMatchingAutocorrelationComparator,
                                               validator,
                                               maxEval,
                                               objectiveFunctionSupplier,
                                               simpleBounds);
    double stopTime = currentTimeMillis();
    double secondsElapsed = (stopTime - startTime) / 1000;
    double evaluationsPerSecond = multiopt.getEvaluations() / secondsElapsed;
    double minutesElapsed = secondsElapsed / 60;

    assignParameters(optimum.getKey());

    out.format("estimation completed in %f minutes at %f evals/sec\n", minutesElapsed, evaluationsPerSecond);

    // plot("λ(t)", this::λ, T.fmin(), T.fmax(), 5000 );

    return multiopt;
  }

  public double
         getΛmomentMeasure()
  {
    Vector dT = Λ();
    Vector moments = dT().normalizedMoments(2);
    Vector normalizedSampleMoments = (moments.copy().subtract(1)).abs();
    return normalizedSampleMoments.sum();
  }

  public Vector
         dT()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         getΛmomentLjungBoxMeasure()
  {
    throw new UnsupportedOperationException("TODO");
  }

  public ExponentialMutuallyExcitingProcess
         newProcess(double[] point)
  {
    ExponentialMutuallyExcitingProcess process = (ExponentialMutuallyExcitingProcess) this.clone();
    process.assignParameters(point);
    return process;
  }

  /**
   * 
   * @return Λ(0,t[n])
   */
  public abstract double
         totalΛ();

  @Override
  public Vector
         Λ()
  {
    final int n = T.size() - 1;

    Vector compensator = new Vector(n);
    for (int i = 0; i < n; i++)
    {
      for (int type = 0; type < dim(); type++)
      {
        compensator.add(Λ(type, i));
      }
    }
    return compensator;
  }

  public Vector
         Λ(int type)
  {

    final int n = T.size() - 1;

    Vector compensator = new Vector(n);
    for (int i = 0; i < n; i++)
    {
      compensator.set(i, Λ(type, i));
    }

    return compensator.setName("Λ" + type);

  }

  /**
   * 
   * @param m
   *          ordinal ranging from 0 to this{@link #dim()}-1
   * @param tk
   * @return ∫λ(t)dt where t ranges from T[tk] to T[tk+1]
   */
  public double
         Λ(int m,
           int tk)
  {
    assert 0 <= m;
    assert m < dim();
    return sum(n -> sum(j -> V(m, n, j, tk), 0, order() - 1), 0, dim() - 1);
  }

  /**
   * 
   * @param m
   * @param n
   * @param j
   * @param i
   * @return α(j, m, n) / β(j, m, n) * (1 - e^(-β * (T(m,i) - T(m,i-1)))) * A(m,
   *         i, j) + sum(k -> 1 - e^(-β * (T(m, i) - T(n, k))), N(T(m,i-1)),
   *         N(T(m,i)))
   */
  public double
         V(int m,
           int n,
           int j,
           int i)
  {
    final double β = β(m, n, j);
    double lowerTime = t(m, i - 1);
    double upperTime = t(m, i);
    Integer lowerTimeIndex = getSubTimes().right[m].get(lowerTime);
    Integer upperTimeIndex = getSubTimes().right[m].get(upperTime);
    assert lowerTimeIndex != null;
    assert upperTimeIndex != null;
    double interim = sum(k -> 1 - exp(-β * (t(m, i) - t(n, k))), lowerTimeIndex, upperTimeIndex);
    return α(j, m, n) / β(j, m, n) * (1 - exp(-β * (upperTime - lowerTime))) * A(m, i, j) + interim;
  }

  public double
         B(int type,
           int tk,
           int j)
  {
    return A(type, tk, j) - 1;
  }

  public double
         Asum(int type,
              int tk,
              int j)
  {
    if (tk < 0)
    {
      return 0;
    }
    double Ti = T.get(tk);
    return 1 + sum(k -> exp(-β(j, type, type) * (Ti - T.get(k))), 0, tk - 1);
  }

  public double
         A(int m,
           int tk,
           int j)
  {
    assert 0 <= m && m < dim() : format("type=%d tk=%d j=%d dim=%d order=%d\n", m, tk, j, dim(), order());
    if (A == null)
    {
      A = new double[dim()][T.size()][order()];
    }
    double val = A[m][tk][j];
    if (val == 0)
    {
      val = tk == 0 ? 1 : (1 + (exp(-β(j, m, m) * (T.get(tk) - T.get(tk - 1))) * A(m, tk - 1, j)));
      A[m][tk][j] = val;
    }
    return val;
  }

  public Real
         AReal(int type,
               int tk,
               int j)
  {
    assert 0 <= type && type < dim() : format("type=%d tk=%d j=%d val=%d dim=%d order=%d\n", type, tk, j, dim(), order());
    if (AReal == null)
    {
      AReal = new Real[dim()][T.size()][order()];
    }
    Real val = AReal[type][tk][j];
    if (val == null)
    {
      val = tk == 0 ? Real.ONE : (Real.ONE.add((exp(-β(j, type, type) * (T.get(tk) - T.get(tk - 1))) * A(type, tk - 1, j))));
      AReal[type][tk][j] = val;
    }
    return val;
  }

  /**
   * @return an array whose elements correspond to this{@link #statisticNames}
   */
  public Object[]
         evaluateParameterStatistics(double[] point)
  {
    ExponentialMutuallyExcitingProcess process = newProcess(point);
    double ksStatistic = process.getΛKolmogorovSmirnovStatistic();

    Vector compensated = process.Λ();

    // out.println(compensated.autocor(30));

    Object[] statisticsVector = new Object[]
    { process.logLik(),
      ksStatistic,
      compensated.mean(),
      compensated.variance(),
      process.getΛmomentMeasure(),
      process.getLjungBoxMeasure(),
      process.getΛmomentLjungBoxMeasure() };

    /**
     * Exception in thread "main" java.lang.RuntimeException: Attempt to get
     * fastmath.Vector field
     * "stochastic.processes.selfexciting.multivariate.ExtendedApproximatePowerlawMututallyExcitingProcess.τ"
     * with illegal data type conversion to double at
     * stochastic.processes.selfexciting.AbstractSelfExcitingProcess.getFieldValue(AbstractSelfExcitingProcess.java:260)
     * at
     * stochastic.processes.selfexciting.multivariate.ExponentialMutuallyExcitingProcess.lambda$6(ExponentialMutuallyExcitingProcess.java:346)
     */
    return addAll(stream(getParameterFields()).map(param -> {
      Vector value = process.getVectorField(param);
      return value;
    }).toArray(), statisticsVector);
  }

  /**
   * Given two Vectors (of times and types), calculate indices and partition
   * subsets of different types
   *
   * 
   * @return Pair<Vector times[dim],Map<time,type>[dim]>
   */
  public Pair<Vector[], TreeMap<Double, Integer>[]>
         getSubTimes()
  {
    if (cachedSubTimes != null)
    {
      return cachedSubTimes;
    }
    final ArrayList<Double>[] timesSub = new ArrayList[dim()];
    final Vector[] timeVectors = new Vector[dim()];
    TreeMap<Double, Integer>[] timeIndices = new TreeMap[dim()];

    for (int i = 0; i < dim(); i++)
    {
      timesSub[i] = new ArrayList<Double>();
      timeIndices[i] = new TreeMap<Double, Integer>();
    }
    for (int i = 0; i < T.size(); i++)
    {
      timesSub[K.get(i)].add(T.get(i));
    }
    for (int i = 0; i < dim(); i++)
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
  public Vector
         calculateMean()
  {
    try
    {
      return eye(dim()).subtract(calculateBranchingMatrix()).ldivide(κ.asMatrix().trans().copy(false)).asVector();
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
   *          from type in [0,dim-1]
   * @param n
   *          to type in [0,dim-1]
   * @return the j-th element of the Vector of parameters corresponding to the
   *         k-th type
   */
  protected abstract double
            α(int j,
              int m,
              int n);

  /**
   * 
   * @param j
   *          index in [0,order()-1]
   * @param m
   *          from type in [0,dim-1]
   * @param n
   *          to type in [0,dim-1]
   * @return the j-th element of the Vector of parameters corresponding to the
   *         k-th type
   */
  protected abstract double
            β(int j,
              int m,
              int n);

  /**
   * Calculate sum(α(j,m,n)/β(j,m,n),j=1..order)
   * 
   * @return branching matrix of dimsion this{@link #dim} x this{@link #dim}
   */
  public DoubleColMatrix
         calculateBranchingMatrix()
  {
    DoubleColMatrix αβ = new DoubleColMatrix(dim(), dim());
    for (int j = 0; j < order(); j++)
    {
      for (int m = 0; m < dim(); m++)
      {
        for (int n = 0; n < dim(); n++)
        {
          αβ.add(n, n, α(j, m, n) / β(j, m, n));
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
  public boolean
         isStationary()
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
    for (int i = 0; i < dim(); i++)
    {
      double d = sqrt(pow(eig.getRealEigenvalues().get(i), 2) + pow(eig.getImaginaryEigenvalues().get(i), 2));
      if (d >= 1)
      {
        return false;
      }
    }
    return true;
  }

  public Vector
         calculateIntensitySlow(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair,
                                int m)
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

      for (int n = 0; n < dim(); n++)
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

  public Vector
         calculateIntensity(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair,
                            int m)
  {
    double R[][][] = new double[order()][dim()][dim()];
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

      for (int n = 0; n < dim(); n++)
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

  protected abstract double
            evolveλ(int type,
                    double dt,
                    double[][] S);

  protected double
            getDeterministicIntensity(int m,
                                      double upperTime,
                                      int i)
  {
    return κ.get(m);
  }

  protected double
            getDeterministicCompensator(int m,
                                        double upperTime,
                                        double lowerTime,
                                        int i)
  {
    return (upperTime - lowerTime) * getDeterministicIntensity(m, upperTime, i);
  }

  private Entry<Double, Integer>
          getUpperEntry(TreeMap<Double, Integer>[] subTimeIndex,
                        final double upperTime,
                        int n,
                        int m,
                        int i)
  {
    Entry<Double, Integer> upperEntry = upperEntries[n][m][i];
    if (upperEntry == null)
    {
      upperEntry = subTimeIndex[n].ceilingEntry(upperTime);
      upperEntries[n][m][i] = upperEntry;
    }
    return upperEntry;
  }

  private Entry<Double, Integer>
          getLowerEntry(TreeMap<Double, Integer>[] subTimeIndex,
                        final double lowerTime,
                        int n,
                        int m,
                        int i)
  {
    Entry<Double, Integer> lowerEntry = lowerEntries[n][m][i];
    if (lowerEntry == null)
    {
      lowerEntry = subTimeIndex[n].ceilingEntry(lowerTime);
      lowerEntries[n][m][i] = lowerEntry;
    }
    return lowerEntry;
  }

  public Vector
         calculateCompensatorSlow(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair,
                                  int m)
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
      for (int n = 0; n < dim(); n++)
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
  protected double
            evolveΛ(double prevdt,
                    double dt,
                    double d,
                    double[] a)
  {
    throw new UnsupportedOperationException("TODO: do as in calculateCompensator");
  }

  public Vector
         calculateCompensator(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair,
                              int m)
  {
    final Vector[] timesSub = timesSubPair.left;
    final TreeMap<Double, Integer>[] subTimeIndex = timesSubPair.right;
    double kappa = this.κ.get(m);
    final Vector mtimes = timesSub[m];
    final int N = mtimes.size();
    Vector compensator = new Vector(N - 1);
    double A[][][] = new double[order()][dim()][dim()];

    for (int i = 1; i < N; i++)
    {
      double upperTime = mtimes.get(i);
      double lowerTime = mtimes.get(i - 1);
      double lowerTimeBeforeLast = i > 2 ? mtimes.get(i - 2) : Double.NEGATIVE_INFINITY;

      double sum = getDeterministicCompensator(m, upperTime, lowerTime, i) * kappa;
      for (int n = 0; n < dim(); n++)
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

  public int
         dim()
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
  public double
         predict(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair,
                 final int m)
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

  public UnivariateFunction
         getPredictiveDensity(final int m,
                              final Vector[] timesSub,
                              final TreeMap<Double, Integer>[] subTimeIndex,
                              final double kappa,
                              final Vector mtimes,
                              final int N,
                              final double eta)
  {
    return new UnivariateFunction()
    {
      @Override
      public double
             value(double t)
      {
        final double A[][][] = new double[order()][dim()][dim()];
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
          for (int n = 0; n < dim(); n++)
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

  public int
         getPredictionIntegrationLimit()
  {
    return predictionIntegrationLimit;
  }

  public void
         setPredictionIntegrationLimit(int predictionIntegrationLimit)
  {
    this.predictionIntegrationLimit = predictionIntegrationLimit;
  }

  protected RandomVectorGenerator
            getRandomVectorGenerator(SimpleBounds bounds)
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

  boolean trace = false;

  public static String[] statisticNames =
  { "Log-Lik", "1-KS(Λ,exp)", "mean(Λ)", "var(Λ)", "MM(Λ)", "(LjungBox(Λ,10)-8)^2", "E[dt]" };

  /**
   * 
   * @return a list formed by concatenating the names of the parameters enumerated
   *         by this{@link #getBoundedParameters()} and the names of the
   *         statistics enumerated by this{@link #statisticNames}
   */
  public String[]
         getColumnHeaders()
  {
    return concat(stream(getBoundedParameters()).map(param -> param.getName()), asList(statisticNames).stream()).collect(toList()).toArray(new String[0]);
  }

  public void
         storeParameters(File modelFile)
  {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * 
   * @param type
   *          type of process to spawn
   * @param filtration
   * @return
   */
  public static ExponentialMutuallyExcitingProcess
         spawnNewProcess(SelfExcitingProcessFactory.Type type,
                         TradingFiltration filtration)
  {
    assert filtration.times != null : "tradingProcess.times is null";
    assert filtration.types != null : "tradingProcess.types is null";
    assert filtration.markedPoints != null : "tradingProcess.markedPoints is null";

    if (type == SelfExcitingProcessFactory.Type.MultivariateExtendedApproximatePowerlaw)
    {
      ExtendedApproximatePowerlawMututallyExcitingProcess process = new ExtendedApproximatePowerlawMututallyExcitingProcess(2);
      process.T = filtration.times;
      process.K = filtration.types;
      process.X = filtration.markedPoints;
      return process;
    }
    else
    {
      throw new UnsupportedOperationException("TODO: " + type);
    }
  }
}
