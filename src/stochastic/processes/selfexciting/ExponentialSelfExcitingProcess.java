package stochastic.processes.selfexciting;

import static fastmath.Functions.product;
import static fastmath.Functions.realSum;
import static fastmath.Functions.seq;
import static fastmath.Functions.sum;
import static fastmath.Functions.uniformRandom;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.String.format;
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
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;

import javax.swing.JProgressBar;

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
import org.arblib.Real;

import fastmath.Pair;
import fastmath.Vector;
import fastmath.optim.ObjectiveFunctionSupplier;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import fastmath.optim.PointValuePairComparator;
import fastmath.optim.SolutionValidator;
import junit.framework.TestCase;
import util.AutoArrayList;

public abstract class ExponentialSelfExcitingProcess extends AbstractSelfExcitingProcess implements MultivariateFunction, Cloneable, SelfExcitingProcess
{
  private static final int MAX_ITERS = 1000;

  public String
         getαβString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("{Z=" + ZReal());
    for (int i = 0; i < order(); i++)
    {
      sb.append(format(", alpha[%d]=%s, beta[%d]=%s", i + 1, αReal(i), i + 1, βReal(i)));
    }
    sb.append("}");
    return sb.toString();
  }

  @Override
  public double
         minh()
  {
    return getβVector().fmin();
  }

  @Override
  public double
         maxh()
  {
    return getβVector().fmax();
  }

  /**
   *
   * @param y
   *          exponentially distributed random variable
   *
   */
  public double
         Hphase(double y,
                double t)
  {
    return sum(k -> exp(y - γ(k) * (t * β(k) - 1)), 0, order() - 1);
  }

  public double
         HphaseTimeDifferential(double y,
                                double t)
  {
    return sum(k -> -β(k) * γ(k) * exp(y - t * β(k)), 0, order() - 1);
  }

  public double
         Fphase(double u,
                double t)
  {
    return sum(k -> γ(k) * (exp(-β(k) * t) + u - 1), 0, order() - 1);
  }

  public double
         FphaseTimeDifferential(double u,
                                double t)
  {
    return sum(k -> -γ(k) * β(k) * exp(-t * β(k)), 0, order() - 1);
  }

  /**
   * TODO: see about using some fancier numerical root finding algo... convergence is very slow past a certain point
   * 
   * @param y
   *          exponentially distributed random variable
   * 
   * @return the value of T[n+1] that will cause the compensator to be equal to
   *         Λ(T[n],T[N+1])=y
   */
  public double
         invΛ(double y,
              int tk)
  {
    double dt = 0;

    for (int i = 0;; i++)
    {
      double p = 0;
      if (trace)
      {
        out.println("dt[" + i + "]=" + dt);
      }
      p = Λphase(dt, y, tk);

      double pd = ΛphaseTimeDifferential(dt, tk);

      double ratio = p / pd;
      if (trace)
      {
        out.println("Λphase/ΛPhaseTimeDiff=" + ratio);
      }
      if (abs(ratio) < 1E-12)
      {
        break;
      }
      dt = dt - ratio;

    }
    return -dt;
  }

  private Real tolerance = new Real("1E-30");

  /**
   * 
   * @param y
   *          exponentially distributed random variable
   * 
   * @return inverse of the compensator for a given value of y
   */
  public Real
         invΛReal(double y,
                  int tk)
  {
    Real dt = Real.ZERO;

    for (int i = 0;; i++)
    {
      Real δ = ΛphaseδReal(dt, y, tk);

      if (trace)
      {
        out.println("dt[" + i + "]=" + dt + " δ=" + δ + " bits=" + δ.getRelativeAccuracyBits());
      }
      if (δ.getRelativeAccuracyBits() < 0)
      {
        break;
      }
      dt = dt.sub(δ);
    }
    return dt.neg();
  }

  /*
   * @param u unit uniformly distributed random variable
   * 
   * @see this{@link #Fphase(double, double)} and this{@link
   * #FphaseTimeDifferential(double, double)}
   * 
   * @return {t:Fphase(y,t)=0}
   */
  public double
         invF(double u)
  {
    double t = 0;
    double prevdt = 0;
    double dt = 0;
    for (int i = 0; i < MAX_ITERS; i++)
    {
      double phase = Fphase(u, t);
      double phasedt = FphaseTimeDifferential(u, t);
      prevdt = dt;
      dt = phase / phasedt;
      if (!Double.isFinite(dt))
      {
        return Double.POSITIVE_INFINITY;
      }
      double newt = t - dt;
      double relativeDifference = abs(abs(dt) - abs(prevdt));
      if (trace)
      {
        out.format("invF[%d] u=%f t=%f dt=%+30.30f newt=%f relativeDifference=%f\n", i, u, t, dt, newt, relativeDifference);
      }
      t = newt;
      if (relativeDifference < 1E-14)
      {
        if (trace)
        {
          out.println("Converged in " + i + " iterations");
        }
        break;
      }
    }
    return t;
  }

  /**
   * @param y
   *          unit exponentially distributed random variable
   * 
   * @see this{@link #Hphase(double, double)} and
   *      this{@link #HphaseTimeDifferential(double, double)}
   * 
   * @return {t:Hphase(y,t)=0}
   */
  @Override
  public double
         invH(double y)
  {
    if (true)
    {
      throw new UnsupportedOperationException("TODO: investigate numerical problems with this method");
    }
    double t = 0;
    double prevdt = 0;
    double dt = 0;
    for (int i = 0; i < MAX_ITERS; i++)
    {
      double phase = Hphase(y, t);
      double phasedt = HphaseTimeDifferential(y, t);
      prevdt = dt;
      dt = phase / phasedt;
      if (!Double.isFinite(dt))
      {
        return Double.POSITIVE_INFINITY;
      }
      double newt = t - dt;
      double relativeDifference = abs(abs(dt) - abs(prevdt));
      out.format("invH[%d] y=%f t=%f dt=%+30.30f newt=%f relativeDifference=%f\n", i, y, t, dt, newt, relativeDifference);
      t = newt;
      if (relativeDifference < 1E-14)
      {
        out.println("Converged in " + i + " iterations");
        break;
      }
    }
    return t;
  }

  public Vector
         getαVector()
  {
    return new Vector(seq(this::α, 0, order() - 1));
  }

  public Vector
         getβVector()
  {
    return new Vector(seq(this::β, 0, order() - 1));
  }

  /**
   * product(j -> j == k ? α(j) : β(j), 0, order() - 1)
   * 
   * @param k
   * @return
   */
  public double
         γ(int k)
  {
    return γsfp.getOrCreate(k);
  }

  /**
   * product(j -> j == k ? α(j) : β(j), 0, order() - 1)
   * 
   * @param k
   * @return
   */
  public Real
         γReal(int k)
  {
    return γs.getOrCreate(k);
  }

  public Real
         γproduct(int k)
  {
    IntFunction<Real> a = j -> j == k ? αReal(j) : βReal(j);

    Real product = product(a, 0, order() - 1);

    return product;
  };

  public double
         γproductfp(int k)
  {
    IntToDoubleFunction a = j -> j == k ? α(j) : β(j);

    return product(a, 0, order() - 1);
  };

  private AutoArrayList<Real> γs = new AutoArrayList<>(order(), this::γproduct);

  private AutoArrayList<Double> γsfp = new AutoArrayList<>(order(), this::γproductfp);

  /**
   * The mean lifetime can be looked at as a "scaling time", because the
   * exponential decay equation can be written in terms of the mean lifetime, τ,
   * instead of the decay constant, λ
   * 
   * @param k
   * @return
   */
  public double
         getScalingTime(int k)
  {
    return 1 / (γ(k));
  }

  public double
         βproduct()
  {
    return product(this::β, 0, order() - 1);
  }

  public Real
         βproductReal()
  {
    return product((IntFunction<Real>) k -> βReal(k), 0, order() - 1);
  }

  final static ExponentialDistribution expDist = new ExponentialDistribution(1);

  final static KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();

  private final ObjectiveFunctionSupplier objectiveFunctionSupplier = () -> new ObjectiveFunction(copy());

  protected boolean recursive = true;

  public boolean trace = false;

  public boolean verbose = false;

  /**
   * functions which takes its minimum when the mean and the variance of the
   * compensator is closer to 1
   * 
   * @return measure which is greater the closer the first two moments of the
   *         compensator are to unity
   */
  public double
         getΛmomentMeasure()
  {
    Vector dT = Λ();
    Vector moments = dT.normalizedMoments(2);
    Vector normalizedSampleMoments = (moments.copy().subtract(1)).abs();
    return normalizedSampleMoments.sum();
  }

  /**
   * functions which takes its minimum when the mean and the variance of the
   * compensator is closer to 1
   * 
   * @return this{@link #getΛmomentMeasure()} * log( 1 +
   *         this{@link #getLjungBoxMeasure()} )
   */
  public double
         getΛmomentLjungBoxMeasure()
  {
    return getΛmomentMeasure() * log(1 + getLjungBoxMeasure());
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

  public ParallelMultistartMultivariateOptimizer
         estimateParameters(int numStarts)
  {
    return estimateParameters(numStarts, (IntConsumer) null);
  }

  public ParallelMultistartMultivariateOptimizer
         estimateParameters(int numStarts,
                            JProgressBar progressMonitor)
  {
    return estimateParameters(numStarts, j -> progressMonitor.setValue(j));
  }

  public final ParallelMultistartMultivariateOptimizer
         estimateParameters(int numStarts,
                            IntConsumer progressNotifier)
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

  protected final double
            evolveAandΛ(double dt,
                        int tk)
  {
    double Λ = dt * κ;
    for (int j = 0; j < order(); j++)
    {
      double α = α(j);
      double β = β(j);
      Λ += (α / β) * (1 - exp(-β * dt)) * A(tk, j);
    }
    if (trace)
    {
      out.println("A[" + tk + "]=" + Arrays.toString(A[tk]));
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

  public Vector
         getSpectrum(int n)
  {
    throw new UnsupportedOperationException("TODO: implement simulation then generate sample autocorrelation from simulated samples since the combinatorial complexixity of the analytic expression would require about 17 years to evaluate on even a really fast machine.");
  }

  @Override
  public double
         getStationaryλ()
  {
    return 1 / mean();
  }

  /**
   * integrated kernel function
   * 
   * @param t
   * @return
   */
  @Override
  public double
         F(double t)
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
    double ll = tn - T.getLeftmostValue() - totalΛ();
    final int n = T.size();

    if (recursive)
    {
      A = new double[n][order()];
      AReal = new Real[n][order()];

      double S[] = new double[order()];
      for (int tk = 1; tk < n; tk++)
      {
        double t = T.get(tk);
        double prevdt = tk == 1 ? 0 : (T.get(tk - 1) - T.get(tk - 2));
        double dt = t - T.get(tk - 1);
        double λ = evolveλ(dt, t, S);


        if (λ > 0)
        {
          ll += log(λ);
        }

        // ll -= Λ;

      }
    }
    else
    {
      for (int i = 1; i < n; i++)
      {
        double thist = T.get(i);
        ll += log(λ(thist));
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

  protected Vector
            recursiveΛ(final int n)
  {
    Vector durations = dT();

    Vector compensator = new Vector(n);
    for (int tk = 0; tk < n; tk++)
    {
      double dt = durations.get(tk);
      compensator.set(tk, evolveAandΛ(dt, tk));
    }
    return compensator.setName("dΛ");
  }

  protected double
            Λ(int i)
  {
    return sum(j -> (α(j) / β(j)) * (1 - (exp(-β(j) * (T.get(i + 1) - T.get(i))))) * A(i, j), 0, order() - 1) / Z();
  }

  Vector dT;

  Vector dT()
  {
    if (dT != null)
    {
      return dT;
    }
    else
    {
      return dT = T.diff();
    }
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

    return (tn * κ + sum(i -> sum(j -> (α(j) / β(j)) * (1 - exp(-β(j) * (tn - T.get(i)))), 0, order() - 1), 0, T.size() - 1)) / Z();
  }

  @Override
  public final double
         value(double[] point)
  {
    assignParameters(point);
    return logLik();

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

  public abstract Real
         ZReal();

  /**
   * 
   * @param j
   *          index in [0,order()-1]
   * @return the j-th α parameter
   */
  public abstract double
         α(int j);

  /**
   * 
   * @param j
   *          index in [0,order()-1]
   * @return the j-th α parameter
   */
  public abstract Real
         αReal(int j);

  /**
   * 
   * @param j
   *          index in [0,order()-1]
   * @return the j-th β parameter
   */
  public abstract double
         β(int j);

  /**
   * 
   * @param j
   *          index in [0,order()-1]
   * @return the j-th β parameter
   */
  public abstract Real
         βReal(int j);

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
    sum.add(κ);
    double s;
    for (int i = 0; i < T.size() && (s = T.get(i)) <= t; i++)
    {
      double dt = t - s;
      sum.add(f(dt));
    }
    return sum.doubleValue();
  }

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
      Vector compensator = new Vector(n);
      for (int i = 0; i < n; i++)
      {
        compensator.set(i, Λ(i));
      }
      return compensator.setName("dΛ");
    }

  }

  public Vector
         iΛ()
  {

    final int n = T.size() - 1;

    // return Λ(n);
    Vector integratedCompensator = new Vector(n);
    for (int i = 0; i < n; i++)
    {
      integratedCompensator.set(i, iΛ(i + 1));
    }
    return integratedCompensator.setName("iΛ");

  }

  /**
   * n-th integrated compensator, expensive non-recursive O(n^2) runtime version
   * 
   * @param i
   *          >= 1 and <= n
   * @return sum(k -> iψ(T.get(i + 1) - T.get(k)) - iψ(T.get(i) - T.get(k)), 0,
   *         i-1)
   */
  protected double
            iΛ(int i)
  {
    return sum(k -> sum(j -> (α(j) / β(j)) * (1 - (exp(-β(j) * (T.get(i) - T.get(k))))), 0, order() - 1), 0, i - 1) / Z();
  }

  /**
   * n-th compensated point, expensive non-recursive O(n^2) runtime version
   * 
   * @param i
   *          >= 1 and <= n
   * @return sum(k -> iψ(T.get(i + 1) - T.get(k)) - iψ(T.get(i) - T.get(k)), 0,
   *         i-1)
   */
  public Real
         ΛReal(int i)
  {
    final Real Ti = new Real(T.get(i));
    return realSum(k -> realSum(j -> αReal(j).div(βReal(j).mult(Real.ONE.sub(βReal(j).neg().mult(Ti.sub(T.get(k))).exp()))), 0, order() - 1),
                   0,
                   i - 1).div(ZReal());
  }

  public double
         Λphase(double dt,
                double y,
                int tk)
  {
    assert A != null;
    assert tk < A.length : format("tk=%d >= A.length=%d", tk, A.length);
    return sum(j -> γ(j) * A(tk, j) * (exp(dt * β(j)) - 1), 0, order() - 1) + y * βproduct() * Z();
  }

  public double
         ΛphaseTimeDifferential(double dt,
                                int tk)
  {
    return sum(j -> γ(j) * A(tk, j) * exp(dt * β(j)), 0, order() - 1);
  }

  public Real
         ΛphaseReal(Real dt,
                    double y,
                    int tk)
  {
    assert A != null;
    assert tk < A.length : format("tk=%d >= A.length=%d", tk, A.length);
    return realSum(j -> γReal(j).mult(AReal(tk, j)).mult(dt.mult(βReal(j)).exp().sub(Real.ONE)), 0, order() - 1).add(βproductReal().mult(y).mult(ZReal()));
  }

  public Real
         ΛphaseδReal(Real t,
                     double y,
                     int tk)
  {
    return ΛphaseReal(t, y, tk).div(ΛphaseTimeDifferentialReal(t, tk));
  }

  public double
         Λphaseδ(double t,
                 double y,
                 int tk)
  {
    return Λphase(t, y, tk) / ΛphaseTimeDifferential(t, tk);
  }

  public Real
         ΛphaseTimeDifferentialReal(Real t,
                                    int tk)
  {
    return realSum(j -> γReal(j).mult(AReal(tk, j)).mult(t.mult(βReal(j)).exp()), 0, order() - 1);
  }

  /**
   * n-th compensated point, expensive non-recursive O(n^2) runtime version
   * 
   * @param i
   *          >= 1 and <= n
   * @return sum(k -> iψ(T.get(i + 1) - T.get(k)) - iψ(T.get(i) - T.get(k)), 0,
   *         i-1)
   */
  protected double
            Λ(int i,
              double dt)
  {
    final double Ti = T.get(i - 1) + dt;
    return sum(k -> sum(j -> (α(j) / β(j)) * (1 - (exp(-β(j) * (Ti - T.get(k))))), 0, order() - 1), 0, i - 1) / Z();
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
        double prevdt = i == 1 ? κ : (T.get(i - 1) - T.get(i - 2));
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
         f(double t)
  {
    return sum(i -> α(i) * exp(-β(i) * t), 0, order() - 1) / Z();
  }

  public static String[] statisticNames =
  { "Log-Lik", "KS(Λ)", "mean(Λ)", "var(Λ)", "MM(Λ)", "LB(Λ)", "MMLB(Λ)" };

  double A[][];

  Real AReal[][];

  private double[][] B;

  /**
   * @return an array whose elements correspond to this{@link #statisticNames}
   */
  public Object[]
         evaluateParameterStatistics(double[] point)
  {
    ExponentialSelfExcitingProcess process = newProcess(point);
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

    return addAll(stream(getParameterFields()).map(param -> process.getFieldValue(param)).toArray(), statisticsVector);
  }

  public double
         Asum(int tk,
              int j)
  {
    if (tk < 0)
    {
      return 0;
    }
    double Ti = T.get(tk);
    return 1 + sum(k -> exp(-β(j) * (Ti - T.get(k))), 0, tk - 1);
  }

  public double
         A(int tk,
           int j)
  {
    if (A == null)
    {
      A = new double[T.size()][order()];
    }
    double val = A[tk][j];
    if (val == 0)
    {
      val = tk == 0 ? 1 : (1 + (exp(-β(j) * (T.get(tk) - T.get(tk - 1))) * A(tk - 1, j)));
      A[tk][j] = val;
    }
    return val;
  }

  /**
   * 
   * @param tk
   * @param j
   * @return this{@link #A(int, int)}(tk,j)-1
   */
  public double
         B(int tk,
           int j)
  {
    return A(tk, j) - 1;
  }

  protected final double
            evolveλ(double dt,
                    double t,
                    double[] S)
  {
    double λ = κ;
    for (int j = 0; j < order(); j++)
    {
      S[j] = exp(-β(j) * dt) * (1 + S[j]);
      λ += α(j) * S[j];
    }
    return λ / Z();
  }

  public Real
         AReal(int tk,
               int j)
  {
    if (AReal == null)
    {
      AReal = new Real[T.size()][order()];
    }
    Real val = AReal[tk][j];
    if (val == null)
    {
      val = tk == 0 ? Real.ONE : Real.ONE.add(βReal(j).neg().mult(T.get(tk) - T.get(tk - 1)).exp().mult(AReal(tk - 1, j)));
      AReal[tk][j] = val;
    }
    return val;
  }

  /**
   * time it takes the impulse to decay to half its initial value
   * 
   * @param i
   * @return log(2) / β(i) / Z
   */
  public double
         getHalfDuration(int i)
  {
    return log(2) / (β(i) / Z());
  }

}