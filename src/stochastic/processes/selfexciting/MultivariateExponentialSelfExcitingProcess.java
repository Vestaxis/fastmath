
package stochastic.processes.selfexciting;

import static fastmath.Functions.eye;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.LegendreGaussIntegrator;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
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
import stochastic.processes.point.MarkedPointProcess;
import stochastic.processes.pointprocesses.finance.Side;

/**
 * TODO: same thing I did with univariate exponential self exciting process
 * 
 *
 */
@SuppressWarnings(
{ "deprecation", "unused", "unchecked" })
public class MultivariateExponentialSelfExcitingProcess
{

  private final int dim; // dimension

  private final int order; // P

  // baseline intensity params
  Vector κ;

  // scale parameters
  DoubleMatrix[] α;

  // time decay parameters
  DoubleMatrix[] β;

  public static void main(String[] args) throws IOException
  {
    int typeOffset = 0;
    final int numArgs = args.length;
    boolean univariate = false;
    int order = 1;
    String saveFile = null;
    String firstFile = null;
    int pennies = 1;
    boolean splitUpDown = false;
    int limit = Integer.MAX_VALUE;

    final int dim = univariate ? 1 : typeOffset;
    System.out.println("dim=" + dim + " order=" + order);
    // Vector times = new Vector(timePointMap.size());
    // Vector types = new Vector(timePointMap.size());
    // DoubleColMatrix marks = new DoubleColMatrix(timePointMap.size(), 1);
    //
    // double t0 = timePointMap.firstKey();
    // int i = 0;
    // for (Map.Entry<Double, MarkedPoint> entry : timePointMap.entrySet())
    // {
    // times.set(i, entry.getKey() - t0); // seconds
    // types.set(i, entry.getValue().getType());
    // marks.row(i).assign(entry.getValue().getMarks());
    // i++;
    // }

    MultivariateExponentialSelfExcitingProcess estimator = new MultivariateExponentialSelfExcitingProcess(dim, order);

    // int iters = estimator.estimateParameters(times, types, 100000);
    // System.out.println(format("Converged in %d iterations", iters));
    System.out.println("unconditional mean intensity is " + estimator.calculateMean());
    System.out.println("Hawkes process params " + estimator.getParamString());
    // LinkedList<MiWritableElement> writables = new
    // LinkedList<MiWritableElement>();
    // if ( univariate && order == 1 )
    // {
    // System.out.println( "Calculating one-step ahead predictions..." );
    // Vector oneStepPredictions = estimator.predictUnivarMPF( times );
    // // System.out.println("times=" + times);
    // // System.out.println("nextPoint predictions = " + oneStepPredictions);
    // writables.add( oneStepPredictions.asMatrix().setName( "onestep" ) );
    // }
    // DoubleMatrix params = (DoubleMatrix) new Vector(
    // estimator.params.getPoint() ).abs().asMatrix().setName( "params" );
    // Pair<Vector[], TreeMap<Double, Integer>[]> subTimes =
    // estimator.getSubTimes( times, types );
    // for ( i = 0; i < dim; i++ )
    // {
    // Vector compensator = estimator.calculateCompensator( subTimes, i );
    // DoubleMatrix itimes = subTimes.first[i].asMatrix().setName( "times" + ( i
    // + 1 ) );
    // writables.add( itimes );
    // writables.add( compensator.asMatrix().setName( "Lambda" + ( i + 1 ) ) );
    // // double nextTime = estimator.predict(subTimes, i);
    // // System.out.println("Next time for dim " + i + ": " + nextTime);
    // }
    // final String outfile = saveFile != null ? saveFile : ( firstFile +
    // "-hawkes-" + estimator.getOrder() + "-dim" + estimator.getDim() + ".mat"
    // );
    // System.out.println( "Writing " + outfile );
    // DoubleMatrix ll = new DoubleColMatrix( 1, 1 ).setName( "loglik" );
    // ll.set( 0, 0, estimator.params.getValue() );
    // writables.add( params );
    // writables.add( ll );
    // writables.add( (DoubleMatrix) times.asMatrix().setName( "times" ) );
    // writables.add( (DoubleMatrix) types.asMatrix().setName( "types" ) );
    // writables.add( marks.setName( "marks" ) );
    // MatFile.write( outfile, writables.toArray( new
    // MiWritableElement[writables.size()] ) );
    // System.out.println( "Wrote " + outfile );
  }

  public MultivariateExponentialSelfExcitingProcess(int dim, int order)
  {
    this.dim = dim;
    this.order = order;
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

  public int estimateParameters(final Vector times, final Vector types, int maxIters)
  {
    assert times.size() == types.size() : "times and types should be of same dimension";

    throw new UnsupportedOperationException("TODO: implement Newton iteration to estimate parameters");
    // final MultivariateFunction logLikelihoodFunction = new
    // MultivariateFunction()
    // {
    //
    // @Override
    // public double value( double[] paramArray )
    // {
    // Vector params = tmp0.getVector( paramArray.length ).assign( paramArray
    // ).abs();
    //
    // Vector mean = calculateMean();
    // double ll = 0;
    // Double compMeans[] = new Double[getDim()];
    //
    // Double compVars[] = new Double[getDim()];
    // for ( int i = 0; i < getDim(); i++ )
    // {
    // final Vector intensity = calculateIntensity( timesSubPair, i );
    //
    // // final Vector compensator =
    // // calculateCompensatorSlow(timesSubPair,
    // // i);
    // final Vector compensator = calculateCompensator( timesSubPair, i );
    // compMeans[i] = compensator.mean();
    // compVars[i] = compensator.getVariance();
    // // double fastMean = compensatorFast.mean();
    // // System.out.println("fastMean " + fastMean + " should equal " +
    // // compMeans[i]);
    //
    // assert intensity.size() == compensator.size();
    // for ( int j = 0; j < intensity.size(); j++ )
    // {
    // ll += Math.log( intensity.get( j ) ) - compensator.get( j );
    // }
    // }
    //
    // System.out.println( "ll[" + params.toString().replace( "\n", "" ) + "]="
    // + ll + " compMeans=" + Arrays.asList( compMeans ) + " compVars="
    // + Arrays.asList( compVars ) + " mean=" + mean );
    // return ll;
    // }
    //
    // };
    // try
    // {
    //
    // params = optimizer.optimize( maxIters, logLikelihoodFunction,
    // GoalType.MAXIMIZE, calculateInitialGuess( times, types ) );
    // tmp0.getVector( getDim() + ( matrixSize * getOrder() * 2 ) ).assign(
    // params.getPoint() ).abs();
    //
    // }
    // catch( TooManyEvaluationsException tooManyE )
    // {
    // System.err.println( tooManyE.getMessage() );
    // double[] point = tmp0.getVector( getDim() + ( matrixSize * getOrder() * 2
    // ) ).toPrimitiveArray();
    // params = new PointValuePair( point, logLikelihoodFunction.value( point )
    // );
    // }
    // return optimizer.getEvaluations();
  }

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
    final Vector params = tmp0.getVector(getDim() + (matrixSize * getOrder() * 2)).assign(0.0);
    // baseline intensity params

    final Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair = getSubTimes(times, types);
    final Vector[] timesSub = timesSubPair.left;

    for (int i = 0; i < getDim(); i++)
    {
      System.out.println("mean[" + i + "]=" + 1.0 / (timesSub[i].diff().mean()));
    }

    κ = params.slice(0, getDim());
    α = new DoubleMatrix[getOrder()];
    β = new DoubleMatrix[getOrder()];
    for (int j = 0; j < getDim(); j++)
    {
      Vector theseTimes = new Vector(timesSub[j]);
      κ.set(j, 0.5 / theseTimes.diff().mean());
    }

    for (int j = 0; j < getOrder(); j++)
    {
      α[j] = params.slice(getDim() + (j * matrixSize), getDim() + ((j + 1) * matrixSize)).reshape(getDim(), getDim());
      β[j] = params.slice(getDim() + (getOrder() * matrixSize) + (j * matrixSize), getDim() + (getOrder() * matrixSize) + ((j + 1) * matrixSize))
                   .reshape(getDim(), getDim());
      α[j].diag().assign(1.0 / getOrder());
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
   * @return sum(alpha_j[m][n]/beta_j[m][n],j=1..order)
   */
  public DoubleColMatrix calculateBranchingMatrix()
  {
    DoubleColMatrix ab = new DoubleColMatrix(getDim(), getDim());
    for (int j = 0; j < getOrder(); j++)
    {
      DoubleMatrix alphaj = α[j];
      DoubleMatrix betaj = β[j];
      for (int m = 0; m < getDim(); m++)
      {
        for (int n = 0; n < getDim(); n++)
        {
          ab.add(m, n, alphaj.get(m, n) / betaj.get(m, n));
        }
      }
    }
    return ab;
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
      System.err.println(e.getMessage());
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
        for (int j = 0; j < getOrder(); j++)
        {
          final double alphajmn = α[j].get(m, n);
          final double betajmn = β[j].get(m, n);
          double ntime;
          for (int k = 0; k < Nn && (ntime = ntimes.get(k)) < mtime; k++)
          {
            lambda += alphajmn * exp(-betajmn * (mtime - ntime));
          }
        }
      }

      intensity.set(i - 1, lambda);
    }
    return intensity;
  }

  public Vector calculateIntensity(Pair<Vector[], TreeMap<Double, Integer>[]> timesSubPair, int m)
  {
    double R[][][] = new double[getOrder()][getDim()][getDim()];
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

        for (int j = 0; j < getOrder(); j++)
        {
          double r;
          double betajmn = β[j].get(m, n);
          if (m != n)
          {
            r = exp(-betajmn * mtimeDiff) * R[j][m][n];
            int initialk = floorEntry != null ? floorEntry.getValue() : 0;
            int finalk = ceilEntry != null ? ceilEntry.getValue() : ntimes.size();
            for (int k = initialk; k < finalk; k++)
            {
              final Double ktime = ntimes.get(k);
              if (ktime >= lowerTime && ktime < upperTime)
              {
                r += exp(-betajmn * (upperTime - ktime));
              }
            }

          }
          else
          {
            r = exp(-betajmn * mtimeDiff) * (1 + R[j][m][n]);
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

  /**
   * Cached {@link TreeMap#ceilingEntry(Object)}
   *
   * @param subTimeIndex
   * @param upperTime
   * @param n
   * @param m
   * @param i
   * @return
   */
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

  /**
   * Cacached {@link TreeMap#ceilingEntry(Object)}
   *
   * @param subTimeIndex
   * @param lowerTime
   * @param n
   * @param m
   * @param i
   * @return
   */
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

        for (int j = 0; j < getOrder(); j++)
        {
          final double alphajmn = α[j].get(m, n);
          final double betajmn = β[j].get(m, n);
          double ktime;
          int k = 0;
          // for (k = 0; k < Nn && (ktime = ntimes.get(k)) <
          // lowerTime; k++)
          for (k = 0; k < Nn && (ktime = ntimes.get(k)) < upperTime; k++)
          {
            sum += (alphajmn / betajmn) * (exp(-betajmn * (lowerTime - ktime)) - exp(-betajmn * (upperTime - ktime)));
          }

          // for (; k < Nn && (ktime = ntimes.get(k)) < upperTime;
          // k++)
          // {
          // if (lowerTime <= ktime && ktime < upperTime)
          // {
          // sum += (alphajmn / betajmn) * (1 - exp(-betajmn *
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
    double A[][][] = new double[getOrder()][getDim()][getDim()];

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

        for (int j = 0; j < getOrder(); j++)
        {
          final double alphajmn = α[j].get(m, n);
          final double betajmn = β[j].get(m, n);
          double ktime;
          int k;
          double subsum = exp(-betajmn * (lowerTime - lowerTimeBeforeLast)) * A[j][m][n];
          for (k = lowerkBeforeLast; k < lowerk; k++)
          {
            ktime = ntimes.get(k);
            if (ktime < lowerTime && ktime >= lowerTimeBeforeLast)
            {
              subsum += exp(-betajmn * (lowerTime - ktime));
            }
          }
          A[j][m][n] = subsum;

          double innerSum = subsum * (1 - exp(-betajmn * (upperTime - lowerTime)));
          for (k = lowerk; k < upperk; k++)
          {
            ktime = ntimes.get(k);
            if (ktime >= lowerTime && ktime < upperTime)
            {
              innerSum += 1 - exp(-betajmn * (upperTime - ktime));
            }
          }

          sum += (alphajmn / betajmn) * innerSum;

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

  public int getOrder()
  {
    return order;
  }

  /**
   * Only for dim=1,order=2 using the GNU Real native arbitrary library
   *
   * @param times
   * @return
   */
  public Vector predictUnivarMPF2(final Vector times)
  {
    Vector predictions = new Vector(times.size());
    assert dim == 1;
    assert order == 2;
    // Equation (74) in Point Processes
    for (int k = 0; k < times.size(); k++)
    {
      final Vector timeSlice = times.slice(0, k + 1);
      try
      {
        predictions.set(k, integrator.integrate(10000, new UnivariateFunction()
        {
          @Override
          public double value(double eps)
          {
            // System.out.println("eps=" + eps);
            final double value = inverseLambda2(timeSlice, eps) * exp(-eps);
            // System.out.println("f("+eps+")="+value);
            return value;
          }
        }, 0, predictionIntegrationLimit));
      }
      catch (TooManyEvaluationsException e)
      {
        System.err.println("STUCK!");
      }
      System.out.println("step " + k + "/" + times.size() + " next = " + predictions.get(k));
    }

    return predictions;
  }

  private double lastInitialLambda2Guess = 0.0;

  /**
   * The "Inverse Lambda" prediction function, integrate this with respect to
   * exp(-eps) to get the expected value of the next arrival time. ONLY for
   * order=1 P=2.
   *
   * @param times
   * @param eps
   * @return
   */
  public double inverseLambda2(Vector times, final double eps)
  {
    throw new UnsupportedOperationException("TODO: port code");
    // assert getOrder() == 2;
    // double beta1 = beta[0].get(0, 0);
    // double beta2 = beta[1].get(0, 0);
    // double last = -1;
    // double initialGuess = lastInitialLambda2Guess == 0.0 ?
    // beta1*beta2*times.sum() : lastInitialLambda2Guess;
    // double xin = initialGuess;
    // double alpha1 = alpha[0].get(0, 0);
    // double alpha2 = alpha[1].get(0, 0);
    // double kappa = this.kappa.get(0);
    // double tn = times.get(times.size() - 1);
    // Real xout = new Real();
    // Real dxout = new Real();
    // Real a = new Real();
    // Real b = new Real();
    // Real c = new Real();
    // Real d = new Real();
    // Real dm = new Real();
    // Real e = new Real();
    // Real f = new Real();
    // Real df = new Real();
    // Real g = new Real();
    // Real h = new Real();
    // Real i = new Real();
    // Real j = new Real();
    // Real l = new Real();
    // Real m = new Real();
    // //Real n = new Real();
    // a.set(kappa).mul(tn).add(eps).mul(beta2); // a=(kappa*tn+eps)*beta2
    // while (xin != last)
    // {
    // last = xin;
    // b.set(kappa).mul(xin); // b=kappa*xin
    // xout.set(b.sub(a).mul(beta1)); // xout = (b-a)*beta1
    // c.set(xout).mul((beta1 / beta2) + 1); // c = xout*(beta1/beta2)
    // dxout.set(c.add(kappa * beta1)); // dxout =c+kappa*beta1
    // b.set(beta1 / beta2).mul(xin); // b = (beta1/beta2)*xin
    // j.set((beta1 + beta2) * tn).add(b).add(xin).exp(); //
    // j=exp((beta1+beta2)*tn+b+xin)
    // xout.mul(j); // xout *= j
    // dxout.mul(j); // dxout *= j
    // e.set(beta1).mul(tn).add(b).add(xin); // e=(beta1*tn)+b+xin
    // i.set(beta2).mul(tn).add(b).add(xin); // i = (beta2*tn)+b+xin
    // h.set(beta1 + beta2).mul(tn); // h=(beta1+beta2)*tn
    // for (int k = 0; k < times.size(); k++)
    // {
    // double tk = times.get(k);
    // d.set(beta2).mul(tk); // d=beta2*tk
    // l.set(d).add(e).exp(); // l=exp(d+e)
    // dm.set(l).mul((beta1 / beta2) + 1); // dl=l*((beta1/beta2)+1)
    // f.set(h).add(d).add(b).exp(); // f=exp(h+d+b)
    // l.sub(f).mul(alpha2 * beta1); // l=(l-f)*(alpha2*beta1)
    // xout.add(l); // xout += l
    // df.set(f).mul(beta1 / beta2); // df=f*(beta1/beta2)
    // dm.sub(df).mul(alpha2 * beta1); // da=(da-df)*(alpha2*beta1)
    // dxout.add(dm); // dxout += da
    // d.set(beta1).mul(tk); // d = beta1*tk
    // m.set(d).add(i).exp(); // m=exp(d+i)
    // dm.set(m).mul((beta1 / beta2) + 1); // dl=m*((beta1/beta2)+1)
    // g.set(xin).add(d).add(h).exp(); // g=exp(xin+d+h)
    // m.sub(g).mul(alpha1 * beta2); // m=(m-g)*(alpha1*beta2)
    // xout.add(m); // xout += m
    // dm.sub(g).mul(alpha1 * beta2); // da=(da - g)*(alpha1*beta2)
    // dxout.add(dm); // dxout += da
    // }
    // xin = xin - xout.div(dxout).get_d();
    // if ( xin < 0 )
    // {
    // // stuck in the valley, double the initial guess and restart
    // xin = ( initialGuess *= 2 );
    // lastInitialLambda2Guess = xin;
    // }
    // // System.out.println("next=" + next + " initialGuess=" + initialGuess +
    // " eps=" + eps + " restarts=" + restarts );
    //
    // // TODO: better initial guess without all the restarts, and more
    // efficient"remembering" of initial guesses
    //// if ( restarts != 0 )
    //// {
    //// System.out.println("next=" + next + " initialGuess=" + initialGuess + "
    // eps=" + eps +
    // " restarts=" + restarts + " wastedIters=" + wastedIters );
    ////
    // lastInitialLambda2Guess=xin;
    //
    // double value = xin / beta2;
    //
    // return value;
  }

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
      double nextTime = solver.solve(100000, new UnivariateFunction()
      {
        @Override
        public double value(double t)
        {
          final double A[][][] = new double[getOrder()][getDim()][getDim()];
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
              Entry<Double, Integer> lowerEntryBeforeLast = Double.isInfinite(lowerTimeBeforeLast) ? null
                                                                                                   : getLowerEntry(subTimeIndex,
                                                                                                                   lowerTimeBeforeLast,
                                                                                                                   n,
                                                                                                                   m,
                                                                                                                   i - 1);
              Entry<Double, Integer> lowerEntry = getLowerEntry(subTimeIndex, lowerTime, n, m, i);
              Entry<Double, Integer> upperEntry = getUpperEntry(subTimeIndex, upperTime, n, m, i);
              int lowerkBeforeLast = lowerEntryBeforeLast != null ? lowerEntryBeforeLast.getValue() : 0;
              int lowerk = lowerEntry != null ? lowerEntry.getValue() : 0;
              int upperk = upperEntry != null ? upperEntry.getValue() : mtimes.size();

              for (int j = 0; j < getOrder(); j++)
              {
                final double alphajmn = α[j].get(m, n);
                final double betajmn = β[j].get(m, n);
                double ktime;
                int k;
                double subsum = exp(-betajmn * (lowerTime - lowerTimeBeforeLast)) * A[j][m][n];
                for (k = lowerkBeforeLast; k < lowerk; k++)
                {
                  ktime = ntimes.get(k);
                  if (ktime < lowerTime && ktime >= lowerTimeBeforeLast)
                  {
                    subsum += exp(-betajmn * (lowerTime - ktime));
                  }
                }
                A[j][m][n] = subsum;

                double innerSum = subsum * (1 - exp(-betajmn * (upperTime - lowerTime)));
                for (k = lowerk; k < upperk; k++)
                {
                  ktime = ntimes.get(k);
                  if (ktime >= lowerTime && ktime < upperTime)
                  {
                    innerSum += 1 - exp(-betajmn * (upperTime - ktime));
                  }
                }

                sum += (alphajmn / betajmn) * innerSum;

              }
            }
          }

          System.out.println("t=" + t + " eta=" + eta + " sum=" + sum);

          return sum;
        }
      }, lastTime - 0.0001, lastTime + 100);
      inverses.set(l, nextTime);
    }
    return inverses.mean();
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
