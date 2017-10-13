package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static fastmath.Functions.uniformRandom;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;
import static org.apache.commons.math3.util.CombinatoricsUtils.factorial;

import java.util.Arrays;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Supplier;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.optim.InitialGuess;
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
import fastmath.optim.ObjectiveFunctionSupplier;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import fastmath.optim.PointValuePairComparator;
import fastmath.optim.SolutionValidator;

public abstract class ExponentialHawkesProcess extends AbstractHawkesProcess implements MultivariateFunction, Cloneable, HawkesProcess
{

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + getParamString();
  }

  @Override
  public double logLikelihood(Vector t)
  {
    ExponentialHawkesProcess spawn = copy();
    spawn.T = t;
    return spawn.logLik();
  }

  protected abstract double α(int j);

  protected abstract double β(int j);

  protected boolean recursive = true;

  private double λ0;

  public boolean verbose = false;

  public ExponentialHawkesProcess()
  {
    super();
  }

  public final double getBranchingRatio()
  {
    return sum(i -> α(i) / β(i), 0, order() - 1) / Z();
  }

  public abstract int order();

  public abstract double Z();

  /**
   * intensity function
   * 
   * @param t
   * 
   * @return intensity at time t
   */
  public final double λ(double t)
  {
    DoubleAdder sum = new DoubleAdder();
    double s;
    for (int i = 0; i < T.size() && (s = T.get(i)) < t; i++)
    {
      sum.add(ψ(t - s));
    }
    return sum.doubleValue();
  }

  /**
   * kernel function
   * 
   * @param t
   * @return
   */
  public final double ψ(double t)
  {
    return sum(i -> α(i) * exp(-β(i) * t), 0, order() - 1) / Z();
  }

  /**
   * integrated kernel function
   * 
   * @param t
   * @return
   */
  public double iψ(double t)
  {
    return sum(i -> (α(i) / β(i)) * (1 - exp(-β(i) * t)), 0, order() - 1) / Z();
  }

  protected final double evolveλ(double dt, double[] S)
  {
    double λ = λ0;
    for (int j = 0; j < order(); j++)
    {
      S[j] = exp(-β(j) * dt) * (1 + S[j]);
      λ += α(j) * S[j];
    }
    return λ / Z();
  }

  protected final double evolveΛ(double prevdt, double dt, double[] A)
  {
    double Λ = dt * λ0;
    for (int j = 0; j < order(); j++)
    {
      double a = α(j);
      double b = β(j);
      A[j] = 1 + exp(-b * prevdt) * A[j];
      Λ += (a / b) * (1 - exp(-b * dt)) * A[j];
    }
    return Λ / Z();
  }

  protected Vector recursiveΛ(final int n)
  {
    Vector durations = T.diff();

    double A[] = new double[order()];
    Vector compensator = new Vector(n);
    for (int i = 0; i < n; i++)
    {
      double dtprev = i == 0 ? 0 : durations.get(i - 1);
      double dt = durations.get(i);
      compensator.set(i, evolveΛ(dtprev, dt, A));
    }
    return compensator;
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
  public final double logLik()
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
        double λ = evolveλ(dt, S);
        double Λ = evolveΛ(prevdt, dt, A);

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

  /**
   * n-th compensated point
   * 
   * @param i
   *          >= 1 and <= n
   * @return sum(k -> iψ(T.get(i + 1) - T.get(k)) - iψ(T.get(i) - T.get(k)), 0,
   *         i-1)
   */
  protected double Λ(int i)
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

  public double totalΛ()
  {
    double tn = T.getRightmostValue();

    return (tn * λ0 + sum(i -> sum(j -> (α(j) / β(j)) * (1 - exp(-β(j) * (tn - T.get(i)))), 0, order() - 1), 0, T.size() - 1)) / Z();
  }

  public Vector λvector()
  {
    final int n = T.size();
    Vector intensity = new Vector(n);

    if (recursive)
    {
      double S[] = new double[order()];
      for (int i = 1; i < n; i++)
      {
        double t = T.get(i);
        double prevdt = i == 1 ? 0 : (T.get(i - 1) - T.get(i - 2));
        double dt = t - T.get(i - 1);
        double λ = evolveλ(dt, S);
        intensity.set(i, λ);

      }
    }
    else
    {
      for (int i = 1; i < n; i++)
      {
        double thist = T.get(i);
        intensity.set(i - 1, λ(thist));
      }

    }

    return intensity;
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

  public static enum ScoringMethod
  {
    LikelihoodMaximization, MomentMatching
  }

  private ScoringMethod scoringMethod = ScoringMethod.LikelihoodMaximization;

  @Override
  public final double value(double[] point)
  {
    assignParameters(point);

    double score = Double.NaN;

    if (scoringMethod == ScoringMethod.LikelihoodMaximization)
    {
      score = logLik();
    }
    else if (scoringMethod == ScoringMethod.MomentMatching)
    {
      score = compensatorMomentMeasure();
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

  final static ExponentialDistribution expDist = new ExponentialDistribution(1);

  final static KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();

  public final ParallelMultistartMultivariateOptimizer estimateParameters(int numStarts)
  {
    int digits = 15;
    int maxIters = Integer.MAX_VALUE;

    MaxEval maxEval = new MaxEval(maxIters);
    SimpleBounds simpleBounds = getParameterBounds();

    SolutionValidator validator = point -> {
      ExponentialHawkesProcess process = newProcess(point.getPoint());
      return process.Λ().mean() > 0;
    };

    Supplier<MultivariateOptimizer> optimizerSupplier = () -> new BOBYQAOptimizer(getParamCount() * 2 + 1);

    ParallelMultistartMultivariateOptimizer multiopt = new ParallelMultistartMultivariateOptimizer(optimizerSupplier,
                                                                                                   numStarts,
                                                                                                   getRandomVectorGenerator(simpleBounds));

    PointValuePairComparator momentMatchingComparator = (a, b) -> {
      ExponentialHawkesProcess processA = newProcess(a.getPoint());
      ExponentialHawkesProcess processB = newProcess(b.getPoint());
      double σa = pow( processA.Λ().getLjungBoxStatistic(10) - 8, 2 );
      double σb = pow( processB.Λ().getLjungBoxStatistic(10) - 8, 2 );
      return Double.compare(σa, σb);
    };

    double startTime = currentTimeMillis();
    PointValuePair optimum = multiopt.optimize(GoalType.MAXIMIZE,
                                               momentMatchingComparator,
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

    return multiopt;
  }

  public static String[] statisticNames =
  { "Log-Lik", "1-KS(Λ,exp)", "mean(Λ)", "var(Λ)", "MM(Λ)", "(LjungBox(Λ,10)-8)^2" };

  public Object[] evaluateParameterStatistics(double[] point)
  {
    ExponentialHawkesProcess process = newProcess(point);
    double ksStatistic = process.getCompensatorKolmogorovSmirnovStatistic();

    Vector compensated = process.Λ();

    // out.println(compensated.autocor(30));

    Object[] statisticsVector = new Object[]
    { process.logLik(),
      ksStatistic,
      compensated.mean(),
      compensated.variance(),
      process.compensatorMomentMeasure(),
      pow( compensated.getLjungBoxStatistic(10) - 8, 2 ) };

    return ArrayUtils.addAll(Arrays.stream(getParameterFields()).map(param -> process.getFieldValue(param)).toArray(), statisticsVector);
  }

  public double getCompensatorKolmogorovSmirnovStatistic()
  {
    Vector sortedCompensator = new Vector(Λ().stream().sorted()).reverse();
    double ksStatistic = ksTest.kolmogorovSmirnovStatistic(expDist, sortedCompensator.toArray());
    return 1 - ksStatistic;
  }

  /**
   * functions which takes its minimum when the mean and the variance of the
   * compensator is closer to 1
   * 
   * @return measure which is greater the closer the first two moments of the compensator are to unity
   */
  public double compensatorMomentMeasure()
  {
    Vector dT = Λ();
    Vector normalizedSampleMoments = dT.normalizedMoments(2);
    Vector parametricMoments = normalizedMoments(2);
    return -(parametricMoments.subtract(normalizedSampleMoments).pow(2).sum());
    
  }

  private Vector normalizedMoments(int n)
  {
    return new Vector(rangeClosed(1, n).mapToDouble(i -> nthNormalizedMoment(i)));
  }

  public ExponentialHawkesProcess newProcess(double[] point)
  {
    ExponentialHawkesProcess process = (ExponentialHawkesProcess) this.clone();
    process.assignParameters(point);
    return process;
  }

  public InitialGuess getInitialGuess()
  {
    double[] start = calculateInitialGuess(T).toArray();
    InitialGuess initialGuess = new InitialGuess(start);
    out.println("initialGuess=" + Arrays.toString(start));
    return initialGuess;
  }

  public boolean trace = false;

  protected RandomVectorGenerator getRandomVectorGenerator(SimpleBounds bounds)
  {
    return () -> {
      try
      {
        double[] point = rangeClosed(0, bounds.getLower().length - 1).mapToDouble(dim -> uniformRandom(new Pair<>(bounds.getLower()[dim],
                                                                                                                  bounds.getUpper()[dim])))
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

  private final ObjectiveFunctionSupplier objectiveFunctionSupplier = () -> new ObjectiveFunction(copy());

  private Vector calculateInitialGuess(Vector durations)
  {
    final Vector vec = getParameters();

    return vec;
    // return null;
  }

  public static void addSeriesToChart(XYChart chart, String name, Vector X, Vector Y)
  {
    chart.addSeries(name, X.toArray(), Y.toArray());
  }

  public ScoringMethod getScoringMethod()
  {
    return scoringMethod;
  }

  public void setScoringMethod(ScoringMethod scoringMethod)
  {
    this.scoringMethod = scoringMethod;
  }

  /**
   * 
   * @return n'th (raw) moment E[X^n]
   */
  public double nthMoment(int n)
  {
    return nthNormalizedMoment(n) * factorial(n);
  }

  /**
   * FIXME: I think this is something other than the factorial moment measure
   * 
   * @return n'th (raw) factorial moment E[X^n]/n!
   */
  public double nthNormalizedMoment(int n)
  {
    return sum(i -> (α(i) / pow(β(i), n + 1)), 0, order() - 1) / Z();
  }

  /**
   * 
   * @return theoretical mean
   */
  public final double mean()
  {
    return nthNormalizedMoment(1);
  }

  /**
   * 
   * @return theoretical mean
   */
  public final double variance()
  {
    return sum(i -> (2 * α(i)) / pow(β(i), 3), 0, order() - 1) / Z();
  }

}