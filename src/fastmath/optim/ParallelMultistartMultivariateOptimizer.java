package fastmath.optim;

import static java.util.stream.IntStream.range;

import java.lang.reflect.Field;
import java.util.ArrayList;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.BaseMultivariateOptimizer;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.random.RandomVectorGenerator;

/**
 * Multi-start optimizer.
 *
 * This class wraps an optimizer in order to use it several times in turn with
 * different starting points (trying to avoid being trapped in a local extremum
 * when looking for a global one).
 *
 * @since 3.0
 */
public class ParallelMultistartMultivariateOptimizer extends BaseMultivariateOptimizer<PointValuePair>
{

  public ParallelMultistartMultivariateOptimizer(final Supplier<MultivariateOptimizer> optimizerSupplier,
      final int starts, final RandomVectorGenerator generator)
  {
    super(null);

    if (starts < 1) { throw new NotStrictlyPositiveException(starts); }

    this.optimizerSupplier = optimizerSupplier;
    this.starts = starts;
    this.generator = generator;
    
  }

  /** Number of evaluations already performed for all starts. */
  private int totalEvaluations;
  /** Number of starts to go. */
  private int starts;
  /** Random generator for multi-start. */
  private RandomVectorGenerator generator;
  /** Optimization data. */
  private OptimizationData[] optimData;
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
    return totalEvaluations;
  }

  /**
   * {@inheritDoc}
   *
   * @throws MathIllegalStateException
   *           if {@code optData} does not contain an instance of {@link MaxEval}
   *           or {@link InitialGuess}.
   */
  @Override
  public PointValuePair optimize(OptimizationData... optData)
  {
    // Store arguments in order to pass them to the internal optimizer.
    optimData = optData;
    // Set up base class and perform computations.
    return super.optimize(optData);
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

  @Override
  protected PointValuePair doOptimize()
  {
    // Remove all instances of "MaxEval" and "InitialGuess" from the
    // array that will be passed to the internal optimizer.
    // The former is to enforce smaller numbers of allowed evaluations
    // (according to how many have been used up already), and the latter
    // to impose a different start value for each start.
    int maxEvalIndex = -1;
    int initialGuessIndex = -1;
    for (int i = 0; i < optimData.length; i++)
    {
      if (optimData[i] instanceof MaxEval)
      {
        optimData[i] = null;
        maxEvalIndex = i;
      }
      if (optimData[i] instanceof InitialGuess)
      {
        optimData[i] = null;
        initialGuessIndex = i;
        continue;
      }
    }
    if (maxEvalIndex == -1) { throw new MathIllegalStateException(); }
    if (initialGuessIndex == -1) { throw new MathIllegalStateException(); }

    RuntimeException lastException = null;
    AtomicInteger totalEvaluations = new AtomicInteger();
    clear();

    final int maxEval = getMaxEvaluations();
    final double[] min = getLowerBound();
    final double[] max = getUpperBound();
    final double[] startPoint = getStartPoint();

    final int _maxEvalIndex = maxEvalIndex;
    final int _initialGuessIndex = initialGuessIndex;

    // Multi-start loop.
    // for (int i = 0; i < starts; i++)
    range(0, starts).parallel().forEach(i -> {
      BaseMultivariateOptimizer<PointValuePair> optimizer = optimizerSupplier.get();
      // CHECKSTYLE: stop IllegalCatch
      // Decrease number of allowed evaluations.
      optimData[_maxEvalIndex] = new MaxEval(maxEval - totalEvaluations.get());
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
      optimData[_initialGuessIndex] = new InitialGuess(s);
      // Optimize.
      final PointValuePair result = optimizer.optimize(optimData);
      store(result);
      // CHECKSTYLE: resume IllegalCatch

      totalEvaluations.addAndGet(optimizer.getEvaluations());
    });

    final PointValuePair[] optima = getOptima();

    // Return the best optimum.
    return optima[0];

  }

  public PointValuePair[] getOptima()
  {
    Collections.sort(optima, getPairComparator());
    return optima.toArray(new PointValuePair[0]);
  }

  /** Underlying optimizer. */
  private final Supplier<MultivariateOptimizer> optimizerSupplier;
  /** Found optima. */
  private final List<PointValuePair> optima = new ArrayList<PointValuePair>();

  /**
   * @return a comparator for sorting the optima.
   */
  private Comparator<PointValuePair> getPairComparator()
  {
    BaseMultivariateOptimizer<PointValuePair> optimizer = optimizerSupplier.get();

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
        return (goalType == GoalType.MINIMIZE) ? Double.compare(v1, v2) : Double.compare(v2, v1);
      }
    };
  }

  GoalType goalType = GoalType.MAXIMIZE;
}
