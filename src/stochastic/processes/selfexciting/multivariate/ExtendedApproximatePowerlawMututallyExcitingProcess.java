package stochastic.processes.selfexciting.multivariate;

import static fastmath.Functions.sum;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.String.format;
import static java.lang.System.out;

import java.io.File;
import java.io.IOException;

import fastmath.Vector;
import stochastic.processes.selfexciting.BoundedParameter;
import stochastic.processes.selfexciting.ExtendedApproximatePowerlawSelfExcitingProcess;
import stochastic.processes.selfexciting.SelfExcitingProcessFactory.Type;

/**
 * a multivariate version of
 * {@link ExtendedApproximatePowerlawSelfExcitingProcess} with null
 * cross-terms.. that is, the branching matrix is a diagonal vector
 */
public class ExtendedApproximatePowerlawMututallyExcitingProcess extends DiagonalExponentialMututallyExcitingProcess
{

  public ExtendedApproximatePowerlawMututallyExcitingProcess(int dim)
  {
    this.dim = dim;
    κ = new Vector(dim).setName("κ");
    η = new Vector(dim).setName("η");
    b = new Vector(dim).setName("b");
    ε = new Vector(dim).setName("ε");
    τ = new Vector(dim).setName("τ");
  }

  public Vector κ;

  public Vector η;

  public Vector b;

  public Vector ε;

  public Vector τ;

  @Override
  public final double
         value(double[] point)
  {
    assignParameters(point);

    double score = Double.NaN;

    score = logLik();

    if (verbose)
    {
      out.println(Thread.currentThread().getName() + " score{" + getParamString() + "}=" + score);
    }

    return score;

  }

  public int M = 15;

  /**
   * choose m such that m^M=1 minute, in milliseconds
   */
  public double m = exp(log(60000) / M);

  public Vector dT;

  @Override
  public int
         order()
  {
    return M + 1;
  }

  @Override
  public BoundedParameter[]
         getBoundedParameters()
  {
    return ExtendedApproximatePowerlawSelfExcitingProcess.Parameter.values();
  }


  @Override
  public double
         mean()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         getΛKolmogorovSmirnovStatistic()
  {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * 
   * @param j
   *          index in [0,order()-1]
   * @param m
   *          from type in [0,dim-1]
   * @param n
   *          to type in [0,dim-1]
   * @return the j-th element of the Vector of parameters corresponding to the k-th type
   */
  @Override
  protected double
            α(int i,
              int j,
              int k)
  {
    assert j < dim() : format("i=%d j=%d k=%d order=%d dim=%d\n", i, j, k, order(), dim() );
    
    if (j != k)
    {
      return 0;
    }
    if ( i == M )
    {
      return αS(j);
    }
    double τj = τ.get(j);
    double εj = ε.get(j);
    return pow(1 / (τj * pow(m, i)), 1 + εj);
  }

  @Override
  protected double
            β(int i,
              int j,
              int k)
  {
    if (j != k)
    {
      return 0;
    }
    return i < M ? 1 / (τ.get(j) * pow(m, i)) : βS(j);

  }

  public double
         αS(int j)
  {
    return b.get(j);
  }

  public double
         βS(int j)
  {
    return 1 / τ.get(j);
  }

  @Override
  protected double
            evolveλ(double dt,
                    double[][] S)
  {
    return 0;
  }

  @Override
  public double
         getLjungBoxMeasure()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Type
         getType()
  {
    return null;
    // return Type.MultivariateExtendedApproximatePowerlaw;
  }

  @Override
  public double
         getBranchingRatio()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         logLikelihood(Vector t)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         getStationaryλ()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         λ(double t)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Vector
         λvector()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public void
         loadParameters(File modelFile) throws IOException
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         f(double t)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         Z()
  {
    return sum(j -> sum(m -> sum(n -> α(j, m, n) / β(j, m, n), 0, dim() - 1), 0, dim() - 1), 0, order() - 1);
  }

  @Override
  public double
         F(double t)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         Φδ(double t,
            double y)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         totalΛ()
  {
    double tn = T.getRightmostValue();

    return (sum(i -> sum(j -> sum(k -> (α(i, j, k) / β(i, j, k)) * (1 - exp(-β(i, j, k) * (tn - T.get(i)))), 0, order() - 1), 0, dim() - 1), 0, T.size() - 1))
           / Z();
  }

  public String
         getαβString()
  {
    return "TODO";
  }

  public Vector
         dT()
  {
    return (dT != null) ? dT : (dT = T.diff());
  }

  @Override
  public Vector
         getInnovationSequence()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         getRootMeanSquaredPredictionError()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         getMeanSquaredPredictionError()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         invΛ(int tk,
              double y)
  {
    throw new UnsupportedOperationException("TODO");
  }

}
