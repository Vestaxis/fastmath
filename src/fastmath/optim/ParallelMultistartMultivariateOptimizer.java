package fastmath.optim;

import static java.lang.System.out;
import static java.util.stream.IntStream.range;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.BaseMultivariateOptimizer;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.random.RandomVectorGenerator;

/**
 * Multi-start optimizer.
 *
 * This class wraps an optimizer in order to use it several times in turn with
 * different starting points (trying to avoid being trapped in a local extremum
 * when looking for a global one).
 *
 */
public class ParallelMultistartMultivariateOptimizer extends BaseMultivariateOptimizer<PointValuePair>
{

  private OptimizationData[] optimData;

  @Override
  public PointValuePair optimize(OptimizationData... optData)
  {
    this.optimData = optData.clone();
    return super.optimize(optData);
  }

  public ParallelMultistartMultivariateOptimizer(final Supplier<MultivariateOptimizer> optimizerSupplier,
                                                 final int starts,
                                                 final RandomVectorGenerator generator)
  {
    super(null);

    if (starts < 1) { throw new NotStrictlyPositiveException(starts); }

    this.optimizerSupplier = optimizerSupplier;
    this.starts = starts;
    this.generator = generator;

  }

  /** Number of evaluations already performed for all starts. */

  /** Number of starts to go. */
  private int starts;
  /** Random generator for multi-start. */
  private RandomVectorGenerator generator;
  /** Optimization data. */
  // private OptimizationData[] optimData;

  /**
   * Location in {@link #optimData} where the updated maximum number of
   * evaluations will be stored.
   */
  private int maxEvalIndex = -1;
  /**
   * Location in {@link #optimData} where the updated start value will be stored.
   */
  private int initialGuessIndex = -1;

  /** {@inheritDoc} */
  @Override
  public int getEvaluations()
  {
    return totalEvaluations.get();
  }

  /**
   * {@inheritDoc}
   */
  protected void store(PointValuePair optimum)
  {
    optima.add(optimum);
  }

  /**
   * {@inheritDoc}
   */
  protected void clear()
  {
    optima.clear();
  }

  @SuppressWarnings("unchecked")
  protected PointValuePair doOptimize()
  {
    // Remove all instances of "MaxEval" and "InitialGuess" from the
    // array that will be passed to the internal optimizer.
    // The former is to enforce smaller numbers of allowed evaluations
    // (according to how many have been used up already), and the latter
    // to impose a different start value for each start.
    int maxEvalIndex = -1;
    int initialGuessIndex = -1;
    int objectiveFunctionIndex = -1;
    ObjectiveFunctionSupplier objectiveFunctionSupplier = null;
    SolutionValidator validator = null;

    for (int i = 0; i < optimData.length; i++)
    {
      if (optimData[i] instanceof SolutionValidator)
      {
        validator = (SolutionValidator) optimData[i];
      }
      else if (optimData[i] instanceof MaxEval)
      {
        optimData[i] = null;
        maxEvalIndex = i;
      }
      else if (optimData[i] instanceof InitialGuess)
      {
        optimData[i] = null;
        initialGuessIndex = i;
      }
      else if (optimData[i] instanceof ObjectiveFunctionSupplier)
      {
        objectiveFunctionIndex = i;
        objectiveFunctionSupplier = (ObjectiveFunctionSupplier) optimData[i];
      }
    }
    if (maxEvalIndex == -1) { throw new IllegalArgumentException("MaxEval not specified"); }
    if (initialGuessIndex == -1) { throw new IllegalArgumentException("InitialGuess not specified"); }
    if (objectiveFunctionIndex == -1) { throw new IllegalArgumentException("ObjectiveFunctionSupplier not specified"); }

    RuntimeException lastException = null;
    clear();

    final int maxEval = getMaxEvaluations();
    final double[] min = getLowerBound();
    final double[] max = getUpperBound();
    final double[] startPoint = getStartPoint();

    final int _maxEvalIndex = maxEvalIndex;
    final int _initialGuessIndex = initialGuessIndex;
    final int _objectiveFunctionIndex = objectiveFunctionIndex;
    final ObjectiveFunctionSupplier _objectiveFunctionSupplier = objectiveFunctionSupplier;
    final SolutionValidator _validator = validator;

    // Multi-start loop.
    range(0, starts).parallel().forEach(i -> {
      OptimizationData[] instanceOptimData = optimData.clone();
      BaseMultivariateOptimizer<PointValuePair> optimizer = optimizerSupplier.get();
      // Decrease number of allowed evaluations.
      instanceOptimData[_maxEvalIndex] = new MaxEval(maxEval - totalEvaluations.get());
      double[] s = getStartingPoint(i, min, max, startPoint);
      instanceOptimData[_initialGuessIndex] = new InitialGuess(s);
      instanceOptimData[_objectiveFunctionIndex] = _objectiveFunctionSupplier.get();

      // Optimize.
      final PointValuePair result = optimizer.optimize(instanceOptimData);
      synchronized (optima)
      {
        boolean valid = _validator == null || _validator.apply(result);
        if (valid)
        {
          store(result);
        }
        else
        {
          out.println("Rejecting " + Arrays.toString(result.getKey()));
        }
      }

      totalEvaluations.addAndGet(optimizer.getEvaluations());
    });

    final TreeSet<PointValuePair> optima = getOptima();

    // Return the best optimum.
    return optima.first();

  }

  private double[] getStartingPoint(int i, final double[] min, final double[] max, final double[] startPoint)
  {
    // New start value.
    double[] s = null;
    if (i == 0)
    {
      s = startPoint;
    }
    else
    {
      int attempts = 0;
      while (s == null)
      {
        if (attempts++ >= getMaxEvaluations()) { throw new TooManyEvaluationsException(getMaxEvaluations()); }
        s = generator.nextVector();
        for (int k = 0; s != null && k < s.length; ++k)
        {
          if ((min != null && s[k] < min[k]) || (max != null && s[k] > max[k]))
          {
            // reject the vector
            s = null;
          }
        }
      }
    }
    return s;
  }

  public TreeSet<PointValuePair> getOptima()
  {
    return optima;
  }

  /** suppplier of Underlying optimizer. */
  private final Supplier<MultivariateOptimizer> optimizerSupplier;

  /** Found optima. */
  private final TreeSet<PointValuePair> optima = new TreeSet<PointValuePair>(getPairComparator());

  /**
   * @return a comparator for sorting the optima.
   */
  private Comparator<PointValuePair> getPairComparator()
  {

    return new Comparator<PointValuePair>()
    {
      /** {@inheritDoc} */
      public int compare(final PointValuePair o1, final PointValuePair o2)
      {
        if (o1 == null)
        {
          return (o2 == null) ? 0 : 1;
        }
        else if (o2 == null) { return -1; }
        final double v1 = o1.getValue();
        final double v2 = o2.getValue();
        return compareScore(v1, v2);
      }

      public int compareScore(final double v1, final double v2)
      {
        return (goalType == GoalType.MINIMIZE) ? Double.compare(v1, v2) : Double.compare(v2, v1);
      }
    };
  }

  GoalType goalType = GoalType.MAXIMIZE;

  private final AtomicInteger totalEvaluations = new AtomicInteger();
}
