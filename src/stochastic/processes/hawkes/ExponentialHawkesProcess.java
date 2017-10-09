package stochastic.processes.hawkes;

import static fastmath.Functions.sum;
import static fastmath.Functions.uniformRandom;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.apache.commons.math3.util.CombinatoricsUtils.factorialDouble;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Supplier;

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

import dnl.utils.text.table.TextTable;
import fastmath.Pair;
import fastmath.Vector;
import fastmath.optim.ObjectiveFunctionSupplier;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import fastmath.optim.PointValuePairComparator;
import fastmath.optim.SolutionValidator;
import stochastic.processes.hawkes.ExponentialPowerlawHawkesProcess.Parameter;

public abstract class ExponentialHawkesProcess implements MultivariateFunction, Cloneable, HawkesProcess
{

  /**
   * 
   * @return number of trajectories do generate during search for optimal
   *         parameters
   * 
   */
  public int getTrajectoryCount()
  {
    return Runtime.getRuntime().availableProcessors();
  }

  @Override
  public double logLikelihood(Vector t)
  {
    ExponentialHawkesProcess spawn = copy();
    spawn.T = t;
    return spawn.logLik();
  }

  @Override
  public final Object clone()
  {
    try
    {
      ExponentialHawkesProcess spawn = getClass().newInstance();
      spawn.assignParameters(getParameters().toArray());
      spawn.T = T;
      return spawn;
    }
    catch (InstantiationException | IllegalAccessException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }

  }

  @SuppressWarnings("unchecked")
  public final <E extends ExponentialHawkesProcess> E copy()
  {
    return (E) clone();
  }

  protected abstract double α(int j);

  protected abstract double β(int j);

  public Vector T;

  protected boolean recursive = true;

  private double λ0;

  private boolean verbose = false;

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

  private ScoringMethod scoringMethod = ScoringMethod.MomentMatching;

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
      score = σ();
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

  public final int estimateParameters()
  {
    int digits = 15;
    int maxIters = Integer.MAX_VALUE;

    InitialGuess initialGuess = getInitialGuess();

    ObjectiveFunctionSupplier objectiveFunctionSupplier = () -> new ObjectiveFunction(copy());

    MaxEval maxEval = new MaxEval(maxIters);
    SimpleBounds simpleBounds = getParameterBounds();

    int numStarts = getTrajectoryCount();

    SolutionValidator validator = point -> {
      ExponentialHawkesProcess process = newProcess(point);
      return process.Λ().mean() > 0;
    };

    Supplier<MultivariateOptimizer> optimizerSupplier = () -> new BOBYQAOptimizer(getParamCount() * 2 + 1);

    ParallelMultistartMultivariateOptimizer multiopt = new ParallelMultistartMultivariateOptimizer(optimizerSupplier,
                                                                                                   numStarts,
                                                                                                   getRandomVectorGenerator(simpleBounds));

    PointValuePairComparator momentMatchingComparator = (a, b) -> {
      ExponentialHawkesProcess processA = newProcess(a);
      ExponentialHawkesProcess processB = newProcess(b);
      double σa = processA.σ();
      double σb = processB.σ();
      return Double.compare(σb, σa);
    };

    double startTime = currentTimeMillis();
    PointValuePair optimum = multiopt.optimize(GoalType.MAXIMIZE,
                                               momentMatchingComparator,
                                               validator,
                                               maxEval,
                                               initialGuess,
                                               objectiveFunctionSupplier,
                                               simpleBounds);
    double stopTime = currentTimeMillis();
    double secondsElapsed = (stopTime - startTime) / 1000;
    double evaluationsPerSecond = multiopt.getEvaluations() / secondsElapsed;
    double minutesElapsed = secondsElapsed / 60;

    assignParameters(optimum.getKey());

    String[] columnNames =
    { "First Name", "Last Name", "Sport", "# of Years", "Vegetarian" };

    Object[][] data =
    {
      { "Kathy", "Smith", "Snowboarding", new Integer(5), new Boolean(false) },
      { "John", "Doe", "Rowing", new Integer(3), new Boolean(true) },
      { "Sue", "Black", "Knitting", new Integer(2), new Boolean(false) },
      { "Jane", "White", "Speed reading", new Integer(20), new Boolean(true) },
      { "Joe", "Brown", "Pool", new Integer(10), new Boolean(false) } };

    TextTable tt = new TextTable(columnNames, data);
    
    // this adds the numbering on the left
    tt.setAddRowNumbering(true);
    // sort by the first column
    tt.setSort(0);
    tt.printTable();

    for (PointValuePair point : multiopt.getOptima())
    {
      evaluateParameters(point);
    }

    out.format("estimated parameters: %s in %f minutes at %f evals/sec\n", getParamString(), minutesElapsed, evaluationsPerSecond);

    return multiopt.getEvaluations();

  }

  public void evaluateParameters(PointValuePair point)
  {
    ExponentialHawkesProcess process = newProcess(point);
    double ksStatistic = process.getCompensatorKolmogorovSmirnovStatistic();

    Vector compensated = process.Λ();
    out.format("tried %s LL=%f 1-KS=%f mean(Λ)=%f var(Λ)=%f σ=%f\n",
               Arrays.toString(point.getKey()),
               process.logLik(),
               ksStatistic,
               compensated.mean(),
               compensated.variance(),
               process.σ());
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
   * @return -sum((1-(Λ^i)/i!)^2,i=1..n)/n
   */
  public double σ()
  {
    Vector compensator = Λ();
    DoubleAdder measure = new DoubleAdder();
    int n = getParamCount();
    for (int i = 1; i <= n; i++)
    {
      double sampleMoment = compensator.copy().pow(i).mean();
      double desiredMoment = factorialDouble(i);
      double ratio = sampleMoment / desiredMoment;
      measure.add(pow(1 - ratio, 2));
    }
    return -(measure.doubleValue()) / n;
  }

  public ExponentialHawkesProcess newProcess(PointValuePair point)
  {
    ExponentialHawkesProcess process = (ExponentialHawkesProcess) this.clone();
    process.assignParameters(point.getKey());
    return process;
  }

  public InitialGuess getInitialGuess()
  {
    double[] start = calculateInitialGuess(T).toArray();
    InitialGuess initialGuess = new InitialGuess(start);
    out.println("initialGuess=" + Arrays.toString(start));
    return initialGuess;
  }

  protected RandomVectorGenerator getRandomVectorGenerator(SimpleBounds bounds)
  {
    return () -> {
      try
      {
        double[] point = rangeClosed(0, bounds.getLower().length - 1)
                                                                     .mapToDouble(dim -> uniformRandom(new Pair<>(bounds.getLower()[dim],
                                                                                                                  bounds.getUpper()[dim])))
                                                                     .toArray();
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

  public final SimpleBounds getParameterBounds()
  {
    BoundedParameter[] bounds = getBoundedParameters();
    final int paramCount = bounds.length;
    return new SimpleBounds(range(0, paramCount).mapToDouble(i -> bounds[i].getMin()).toArray(),
                            range(0, paramCount).mapToDouble(i -> bounds[i].getMax()).toArray());
  }

  public final Field getField(String name)
  {
    Class<? extends Object> oClass = getClass();
    NoSuchFieldException nsfe = null;
    try
    {
      Field field = null;
      while (field == null && oClass != null)
      {
        try
        {
          field = oClass.getDeclaredField(name);
        }
        catch (NoSuchFieldException e)
        {
          oClass = oClass.getSuperclass();
          nsfe = e;
        }
      }
      if (field == null) { throw new RuntimeException(nsfe.getMessage(), nsfe); }
      field.setAccessible(true);
      return field;
    }
    catch (SecurityException e)
    {
      throw new RuntimeException(oClass.getName() + ": " + e.getMessage(), e);
    }
  }

  Field[] parameterFields = null;

  public final Vector getParameters()
  {
    return new Vector(Arrays.stream(getParameterFields()).mapToDouble(field -> {
      try
      {
        return field.getDouble(this);
      }
      catch (IllegalArgumentException | IllegalAccessException e)
      {
        throw new RuntimeException(e.getMessage(), e);
      }
    }));
  }

  public final Field[] getParameterFields()
  {
    if (parameterFields == null)
    {
      BoundedParameter[] parameters = getBoundedParameters();
      parameterFields = new Field[parameters.length];
      int i = 0;
      for (BoundedParameter param : parameters)
      {
        parameterFields[i++] = getField(param.getName());
      }
    }
    return parameterFields;
  }

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

  /**
   * Uses this{@link #getParameterFields()} to assign values from an array to the
   * specified Java fields
   * 
   * @param array
   *          of values ordered according to this{@link #getBoundedParameters()}
   */
  public final void assignParameters(double[] point)
  {
    BoundedParameter[] params = getBoundedParameters();
    Field[] fields = getParameterFields();
    assert fields.length == params.length;

    for (int i = 0; i < fields.length; i++)
    {
      try
      {
        fields[i].setDouble(this, point[params[i].getOrdinal()]);
      }
      catch (IllegalArgumentException | IllegalAccessException e)
      {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }

  public abstract BoundedParameter[] getBoundedParameters();

  public ScoringMethod getScoringMethod()
  {
    return scoringMethod;
  }

  public void setScoringMethod(ScoringMethod scoringMethod)
  {
    this.scoringMethod = scoringMethod;
  }

  public final int getParamCount()
  {
    return getBoundedParameters().length;
  }

  public String getParamString()
  {
    return Arrays.asList(Parameter.values()).toString() + "=" + getParameters().toString();

  }
}