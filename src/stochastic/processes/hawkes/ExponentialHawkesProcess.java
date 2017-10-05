package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static fastmath.Functions.uniformRandom;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;

import java.util.Arrays;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.concurrent.atomic.DoubleAdder;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.knowm.xchart.XYChart;

import fastmath.Pair;
import fastmath.Vector;
import fastmath.optim.ObjectiveFunctionSupplier;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;

public abstract class ExponentialHawkesProcess implements MultivariateFunction, Cloneable
{

  @Override
  public abstract Object clone();

  protected abstract double α(int j);

  protected abstract double β(int j);

  public Vector T;

  protected boolean recursive = true;

  private double λ0;

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
      out.println("NaN for LL ");
    }
    out.println(Thread.currentThread().getName() + " LL{" + getParamString() + "}=" + ll);
    return ll;

  }

  public abstract String getParamString();

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

    return (tn * λ0
        + sum(i -> sum(j -> (α(j) / β(j)) * (1 - exp(-β(j) * (tn - T.get(i)))), 0, order() - 1), 0, T.size() - 1))
        / Z();
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
      Vector compensator = new Vector(n + 1);
      for (int i = 0; i < n + 1; i++)
      {
        compensator.set(i, Λ(i));
      }
      return compensator.diff();
    }

  }

  @Override
  public double value(double[] point)
  {
    assignParameters(point);

    double ll = logLik();

    if (Double.isNaN(ll)) { return Double.NEGATIVE_INFINITY; }

    // if (Double.isNaN(ll)) { throw new RuntimeException(new
    // NotANumberException("(log)likelihood is NaN")); }

    return ll;
  }
  
  public final int estimateParameters(int digits) throws CloneNotSupportedException
  {

    int maxIters = Integer.MAX_VALUE;
    double[] start = calculateInitialGuess(T).toArray();
    InitialGuess initialGuess = new InitialGuess(start);
    out.println("initialGuess=" + Arrays.toString(start));
    ObjectiveFunctionSupplier objectiveFunctionSupplier = () -> {
      ObjectiveFunction objectiveFunction = new ObjectiveFunction((MultivariateFunction) this.clone());
      out.println( Thread.currentThread().getName() + " cloned objectiveFunction " + objectiveFunction);
      return objectiveFunction;
    };

    MaxEval maxEval = new MaxEval(maxIters);
    SimpleBounds simpleBounds = getParameterBounds();

    ParallelMultistartMultivariateOptimizer multiopt = new ParallelMultistartMultivariateOptimizer(
        () -> new BOBYQAOptimizer(getParamCount() * 2 + 1), Runtime.getRuntime().availableProcessors(),
        getRandomVectorGenerator(simpleBounds));

    PointValuePair result = multiopt.optimize(GoalType.MAXIMIZE, maxEval, initialGuess, objectiveFunctionSupplier,
        simpleBounds);

    for (PointValuePair point : multiopt.getOptima())
    {
      out.println("tried " + Arrays.toString(point.getKey()) + " LL " + point.getValue());
    }
    assignParameters(result.getKey());
    out.println("parameter estimates=" + getParamString() + " LL of " + result.getValue());

    return multiopt.getEvaluations();

  }

  protected RandomVectorGenerator getRandomVectorGenerator(SimpleBounds bounds)
  {
    return () -> {
      try
      {
        double[] point = rangeClosed(0, bounds.getLower().length - 1)
            .mapToDouble(dim -> uniformRandom(new Pair<>(bounds.getLower()[dim], bounds.getUpper()[dim]))).toArray();
        out.println(Thread.currentThread().getName() + " starting from " + Arrays.toString(point));
        return point;
      }
      catch (Exception e)
      {
        e.printStackTrace(System.err);
        return null;
      }
    };
  }

  public abstract SimpleBounds getParameterBounds();

  private Vector calculateInitialGuess(Vector durations)
  {
    final Vector vec = getParameters();

    return vec;
    // return null;
  }

  public abstract Vector getParameters();

  public abstract int getParamCount();

  public static void addSeriesToChart(XYChart chart, String name, Vector X, Vector Y)
  {
    chart.addSeries(name, X.toArray(), Y.toArray());
  }

  public abstract void assignParameters(double[] point);

}