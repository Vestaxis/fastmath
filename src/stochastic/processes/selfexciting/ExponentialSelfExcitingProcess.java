package stochastic.processes.selfexciting;

import static fastmath.Functions.prod;
import static fastmath.Functions.sum;
import static fastmath.Functions.sumExcluding;
import static fastmath.Functions.uniformRandom;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static java.util.stream.Stream.concat;
import static org.apache.commons.lang.ArrayUtils.addAll;
import static org.apache.commons.math3.util.CombinatoricsUtils.factorial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.knowm.xchart.XYChart;

import fastmath.Pair;
import fastmath.Vector;
import fastmath.functions.QuadvariateFunction;
import fastmath.optim.ObjectiveFunctionSupplier;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import fastmath.optim.PointValuePairComparator;
import fastmath.optim.SolutionValidator;
import stochastic.processes.pointprocesses.finance.TradingFiltration;
import stochastic.processes.selfexciting.multivariate.MultivariateExponentialSelfExcitingProcess;
import stochastic.processes.selfexciting.multivariate.MultivariateExtendedApproximatePowerlawSelfExcitingProcess;

public abstract class ExponentialSelfExcitingProcess extends AbstractSelfExcitingProcess implements MultivariateFunction, Cloneable, SelfExcitingProcess
{

  public static enum ScoringMethod
  {
    LikelihoodMaximization, MomentMatching
  }

  final static ExponentialDistribution expDist = new ExponentialDistribution(1);

  final static KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();

  public static void
         addSeriesToChart(XYChart chart,
                          String name,
                          Vector X,
                          Vector Y)
  {
    chart.addSeries(name, X.toDoubleArray(), Y.toDoubleArray());
  }

  private final ObjectiveFunctionSupplier objectiveFunctionSupplier = () -> new ObjectiveFunction(copy());

  protected boolean recursive = true;

  private ScoringMethod scoringMethod = ScoringMethod.LikelihoodMaximization;

  public boolean trace = false;

  public boolean verbose = false;

  /**
   * background rate is just a fixed-constant per time-interval, for now
   * 
   */
  private UnivariateFunction λ0 = t -> κ;

  /**
   * functions which takes its minimum when the mean and the variance of the
   * compensator is closer to 1
   * 
   * @return measure which is greater the closer the first two moments of the
   *         compensator are to unity
   */
  public double
         ΛmomentMeasure()
  {
    Vector dT = Λ();
    Vector moments = dT.normalizedMoments(2);
    Vector normalizedSampleMoments = (moments.copy().subtract(1)).pow(2);
    return normalizedSampleMoments.sum();
  }

  /**
   * functions which takes its minimum when the mean and the variance of the
   * compensator is closer to 1
   * 
   * @return this{@link #ΛmomentMeasure()} * log( 1 +
   *         this{@link #getLjungBoxMeasure()} )
   */
  public double
         getΛmomentLjungBoxMeasure()
  {
    return ΛmomentMeasure() * log(1 + getLjungBoxMeasure());
  }

  /**
   * return a function of the Ljung-Box statistic which measures the amount of
   * autocorrelation remaining in the compensator up to lags of
   * this{@link #LJUNG_BOX_ORDER}
   * 
   * @return (Λ().getLjungBoxStatistic( this{@link #LJUNG_BOX_ORDER} ) - (
   *         this{@link #LJUNG_BOX_ORDER} - 2 ))^2
   */
  public double
         getLjungBoxMeasure()
  {
    return pow(Λ().getLjungBoxStatistic(LJUNG_BOX_ORDER) - (LJUNG_BOX_ORDER - 2), 2);
  }

  public static final int LJUNG_BOX_ORDER = 10;

  public final ParallelMultistartMultivariateOptimizer
         estimateParameters(int numStarts)
  {
    int digits = 15;
    int maxIters = Integer.MAX_VALUE;

    MaxEval maxEval = new MaxEval(maxIters);
    SimpleBounds simpleBounds = getParameterBounds();

    SolutionValidator validator = point -> {
      ExponentialSelfExcitingProcess process = newProcess(point.getPoint());
      return process.Λ().mean() > 0;
    };

    Supplier<MultivariateOptimizer> optimizerSupplier = () -> new BOBYQAOptimizer(getParamCount() * 2 + 1);

    ParallelMultistartMultivariateOptimizer multiopt = new ParallelMultistartMultivariateOptimizer(optimizerSupplier,
                                                                                                   numStarts,
                                                                                                   getRandomVectorGenerator(simpleBounds));

    PointValuePairComparator momentMatchingAutocorrelationComparator = (a,
                                                                        b) -> {
      ExponentialSelfExcitingProcess processA = newProcess(a.getPoint());
      ExponentialSelfExcitingProcess processB = newProcess(b.getPoint());
      double mma = processA.getΛmomentLjungBoxMeasure();
      double mmb = processB.getΛmomentLjungBoxMeasure();
      return Double.compare(mma, mmb);
    };

    double startTime = currentTimeMillis();
    PointValuePair optimum = multiopt.optimize(GoalType.MAXIMIZE,
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

  public static String[] statisticNames =
  { "Log-Lik", "1-KS(Λ,exp)", "mean(Λ)", "var(Λ)", "MM(Λ)", "(LjungBox(Λ,10)-8)^2", "MMLB(Λ)" };

  protected final double
            evolveλ(double dt,
                    double t,
                    double[] S)
  {
    double λ = λ0.value(t);
    for (int j = 0; j < order(); j++)
    {
      S[j] = exp(-β(j) * dt) * (1 + S[j]);
      λ += α(j) * S[j];
    }
    return λ / Z();
  }

  protected final double
            evolveΛ(double prevdt,
                    double dt,
                    double t,
                    double[] A)
  {
    double Λ = dt * λ0.value(t);
    for (int j = 0; j < order(); j++)
    {
      double a = α(j);
      double b = β(j);
      A[j] = 1 + exp(-b * prevdt) * A[j];
      Λ += (a / b) * (1 - exp(-b * dt)) * A[j];
    }
    return Λ / Z();
  }

  public final double
         getBranchingRatio()
  {
    return sum(i -> α(i) / β(i), 0, order() - 1) / Z();
  }

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

  public double
         getΛKolmogorovSmirnovStatistic()
  {
    Vector sortedCompensator = new Vector(Λ().doubleStream().sorted()).reverse();
    double ksStatistic = ksTest.kolmogorovSmirnovStatistic(expDist, sortedCompensator.toDoubleArray());
    return 1 - ksStatistic;
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

  public ScoringMethod
         getScoringMethod()
  {
    return scoringMethod;
  }

  public Vector
         getSpectrum(int n)
  {
    throw new UnsupportedOperationException("TODO: implement simulation then generate sample autocorrelation from simulated samples since the combinatorial complexixity of the analytic expression would require about 17 years to evaluate on even a really fast machine.");
  }

  /**
   * TODO: is the critical case when branchingRate == 1 correct?
   */
  @Override
  public double
         getStationaryλ()
  {
    double branchingRate = ρ();
    if (branchingRate > 1)
    {
      return Double.POSITIVE_INFINITY;
    }
    else if (branchingRate == 1) { return 1 / mean(); }
    return κ / (1 - branchingRate);
  }

  /**
   * branching rate
   */
  public abstract double
         ρ();

  /**
   * integrated kernel function
   * 
   * @param t
   * @return
   */
  public double
         iψ(double t)
  {
    return sum(i -> (α(i) / β(i)) * (1 - exp(-β(i) * t)), 0, order() - 1) / Z();
  }

  public void
         loadParameters(File file)
  {
    try
    {
      FileInputStream fileInputStream = new FileInputStream(file);
      DataInputStream dis = new DataInputStream(fileInputStream);
      Vector params = new Vector((int) (file.length() / Double.BYTES));
      for (int i = 0; i < params.size(); i++)
      {
        params.set(i, dis.readDouble());
      }
      dis.close();
      fileInputStream.close();
      assignParameters(params.toDoubleArray());
    }
    catch (Exception e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * 
   * @param T
   * @param deterministicIntensity
   * @param lambda
   * @param alpha
   * @param bη
   * @return Pair<logLik,E[Lambda]>
   */
  public final double
         logLik()
  {
    double tn = T.getRightmostValue();
    double ll = tn - T.getLeftmostValue();
    final int n = T.size();

    if (recursive)
    {
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
    }
    else
    {
      for (int i = 1; i < n; i++)
      {
        double thist = T.get(i);
        ll += log(λ(thist)) - Λ(i);
      }

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

  @Override
  public double
         logLikelihood(Vector t)
  {
    ExponentialSelfExcitingProcess spawn = copy();
    spawn.T = t;
    return spawn.logLik();
  }

  /**
   * 
   * @return theoretical mean
   */
  public final double
         mean()
  {
    return nthNormalizedMoment(1);
  }

  public ExponentialSelfExcitingProcess
         newProcess(double[] point)
  {
    ExponentialSelfExcitingProcess process = (ExponentialSelfExcitingProcess) this.clone();
    process.assignParameters(point);
    return process;
  }

  private Vector
          normalizedMoments(int n)
  {
    return new Vector(rangeClosed(1, n).mapToDouble(i -> nthNormalizedMoment(i)));
  }

  /**
   * 
   * @return n'th (raw) moment E[X^n]
   */
  public double
         nthMoment(int n)
  {
    return nthNormalizedMoment(n) * factorial(n);
  }

  /**
   * 
   * @return n'th (raw) factorial moment E[X^n]/n!
   */
  public double
         nthNormalizedMoment(int n)
  {
    return sum(i -> (α(i) / pow(β(i), n + 1)), 0, order() - 1) / Z();
  }

  public abstract int
         order();

  /**
   * @return predicted time of next point of the process given the list-history
   *         {@link #T}
   */
  public double
         predict()
  {
    final double v = prod(k -> β(k), 0, order() - 1);
    final double w = sum(k -> β(k), 0, order() - 1);
    double maxT = T.fmax();
    int N = T.size();

    out.println("v=" + v);
    out.println("w=" + w);

    UnivariateFunction η = t -> exp((t + maxT) * w);
    BivariateFunction τ = (t,
                           ε) -> ((t - maxT) * λ0.value(t) - ε) * v * η.value(t);
    IntFunction<Double> Φ = m -> prod(k -> k == m ? α(k) : β(k), 0, order() - 1);
    QuadvariateFunction σ = (m,
                             k,
                             t,
                             s) -> β(m) * (s + T.get(k)) + sumExcluding(j -> β(j) * (t + s), 0, order() - 1, m);
    BivariateFunction φ = (t,
                           ε) -> τ.value(t, ε) + sum(j -> Φ.apply(j) * sum(k -> σ.value(j, k, t, t) - σ.value(j, k, t, maxT), 0, N - 1), 0, order() - 1);

    ExponentialDistribution expDist = new ExponentialDistribution(1);
    double ε = expDist.sample();
    double fuck = τ.value(T.fmax(), ε);
    double shit = φ.value(T.fmax() + 1, ε);
    out.println("shit=" + shit + " fuck=" + fuck + " maxT=" + maxT);
    // plot( t-> φ.value(t,ε), T.fmax(), T.fmax() + 20 );
    return 0;

    // throw new UnsupportedOperationException("TODO: for each exponentially
    // distributed ε find the critical point t which maximizes the score φ(t,ε) ");
  }

  protected Vector
            recursiveΛ(final int n)
  {
    Vector durations = T.diff();

    double A[] = new double[order()];
    Vector compensator = new Vector(n);
    for (int i = 0; i < n; i++)
    {
      double dtprev = i == 0 ? 0 : durations.get(i - 1);
      double dt = durations.get(i);
      compensator.set(i, evolveΛ(dtprev, dt, T.get(i), A));
    }
    return compensator;
  }

  public void
         setScoringMethod(ScoringMethod scoringMethod)
  {
    this.scoringMethod = scoringMethod;
  }

  public void
         storeParameters(File file) throws IOException
  {
    FileOutputStream fileOutputStream = new FileOutputStream(file, false);
    DataOutputStream dos = new DataOutputStream(fileOutputStream);
    Vector params = getParameters();
    for (int i = 0; i < getParamCount(); i++)
    {
      dos.writeDouble(params.get(i));
    }
    dos.close();
    fileOutputStream.close();
  }

  @Override
  public String
         toString()
  {
    return getClass().getSimpleName() + getParamString();
  }

  public double
         totalΛ()
  {
    double tn = T.getRightmostValue();

    return (tn * λ0.value(tn) + sum(i -> sum(j -> (α(j) / β(j)) * (1 - exp(-β(j) * (tn - T.get(i)))), 0, order() - 1), 0, T.size() - 1)) / Z();
  }

  @Override
  public final double
         value(double[] point)
  {
    assignParameters(point);

    double score = Double.NaN;

    if (scoringMethod == ScoringMethod.LikelihoodMaximization)
    {
      score = logLik();
    }
    else if (scoringMethod == ScoringMethod.MomentMatching)
    {
      score = ΛmomentMeasure();
    }
    else
    {
      throw new IllegalStateException("unhandled scoringMethod");

    }

    if (verbose)
    {
      out.println(Thread.currentThread().getName() + " score{" + getParamString() + "}=" + score);
    }

    return score;

  }

  /**
   * 
   * @return theoretical variance
   */
  public final double
         variance()
  {
    return sum(i -> (2 * α(i)) / pow(β(i), 3), 0, order() - 1) / Z();
  }

  public abstract double
         Z();

  /**
   * 
   * @param j
   *          index in [0,order()-1]
   * @return the j-th α parameter
   */
  protected abstract double
            α(int j);

  /**
   * 
   * @param j
   *          index in [0,order()-1]
   * @return the j-th β parameter
   */
  protected abstract double
            β(int j);

  /**
   * intensity function
   * 
   * @param t
   * 
   * @return intensity at time t
   */
  public final double
         λ(double t)
  {
    DoubleAdder sum = new DoubleAdder();
    sum.add(λ0.value(t));
    double s;
    for (int i = 0; i < T.size() && (s = T.get(i)) < t; i++)
    {
      double dt = t - s;
      sum.add(ν(dt));
    }
    return sum.doubleValue();
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
  public Vector
         Λ()
  {

    final int n = T.size() - 1;

    if (recursive)
    {
      return recursiveΛ(n);
    }
    else
    {
      Vector integratedCompensator = new Vector(n + 1);
      for (int i = 0; i < n + 1; i++)
      {
        integratedCompensator.set(i, Λ(i));
      }
      return integratedCompensator.diff();
    }

  }

  /**
   * n-th compensated point, expensive O(n^2) runtime version
   * 
   * @param i
   *          >= 1 and <= n
   * @return sum(k -> iψ(T.get(i + 1) - T.get(k)) - iψ(T.get(i) - T.get(k)), 0,
   *         i-1)
   */
  protected double
            Λ(int i)
  {
    final double Ti = T.get(i);
    return sum(k -> {
      double Tk = T.get(k);
      return sum(j -> {
        double dt = Ti - Tk;
        return (α(j) / β(j)) * (1 - (exp(-β(j) * dt)));
      }, 0, order() - 1);
    }, 0, i - 1) / Z();
  }

  public Vector
         λvector()
  {
    final int n = T.size();
    Vector λ = new Vector(n);

    if (recursive)
    {
      double S[] = new double[order()];
      for (int i = 1; i < n; i++)
      {
        double t = T.get(i);
        double prevdt = i == 1 ? 0 : (T.get(i - 1) - T.get(i - 2));
        double dt = t - T.get(i - 1);
        λ.set(i, evolveλ(dt, T.get(i), S));
      }
    }
    else
    {
      for (int i = 1; i < n; i++)
      {
        double thist = T.get(i);
        λ.set(i - 1, λ(thist));
      }

    }

    return λ;
  }

  /**
   * kernel function
   * 
   * @param t
   * @return
   */
  public final double
         ν(double t)
  {
    return sum(i -> α(i) * exp(-β(i) * t), 0, order() - 1) / Z();
  }

  /**
   * @return an array whose elements correspond to this{@link #statisticNames}
   */
  public Object[]
         evaluateParameterStatistics(double[] point)
  {
    AbstractSelfExcitingProcess process = newProcess(point);
    double ksStatistic = process.getΛKolmogorovSmirnovStatistic();

    Vector compensated = process.Λ();

    // out.println(compensated.autocor(30));

    Object[] statisticsVector = new Object[]
    { process.logLik(),
      ksStatistic,
      compensated.mean(),
      compensated.variance(),
      process.ΛmomentMeasure(),
      process.getLjungBoxMeasure(),
      process.getΛmomentLjungBoxMeasure() };

    return addAll(stream(getParameterFields()).map(param -> process.getFieldValue(param)).toArray(), statisticsVector);
  }

  /**
   * 
   * @param type
   *          type of process to spawn
   * @param filtration
   * @return
   */
  public static MultivariateExponentialSelfExcitingProcess
         spawnNewProcess(SelfExcitingProcessFactory.Type type,
                         TradingFiltration filtration)
  {
    assert filtration.times != null : "tradingProcess.times is null";
    assert filtration.types != null : "tradingProcess.types is null";
    assert filtration.markedPoints != null : "tradingProcess.markedPoints is null";

    if (type == SelfExcitingProcessFactory.Type.MultivariateExtendedApproximatePowerlaw)
    {
      MultivariateExtendedApproximatePowerlawSelfExcitingProcess process = new MultivariateExtendedApproximatePowerlawSelfExcitingProcess(2);
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