package stochastic.processes.selfexciting.multivariate;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
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
public class MultivariateExtendedApproximatePowerlawSelfExcitingProcess extends MultivariateExponentialSelfExcitingProcess
{

  public MultivariateExtendedApproximatePowerlawSelfExcitingProcess(int dim)
  {
    this.dim = dim;
    κ = new Vector(dim).setName("κ");
    η = new Vector(dim).setName("η");
    b = new Vector(dim).setName("b");
    ε = new Vector(dim).setName("ε");
    τ0 = new Vector(dim).setName("τ0");
  }

  public Vector κ;

  public Vector η;

  public Vector b;

  public Vector ε;

  public Vector τ0;

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
  public Object[]
         evaluateParameterStatistics(double[] point)
  {
    throw new UnsupportedOperationException("TODO");
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

  @Override
  protected double
            α(int i,
              int j,
              int k)
  {
    if (j != k) { return 0; }
    return i < M ? pow(1 / (τ0.get(j) * pow(m, i)), 1 + ε.get(j)) : αS(j);

  }

  @Override
  protected double
            β(int i,
              int j,
              int k)
  {
    if (j != k) { return 0; }
    return i < M ? 1 / (τ0.get(j) * pow(m, i)) : βS(j);

  }

  public double
         αS(int j)
  {
    return b.get(j);
  }

  public double
         βS(int j)
  {
    return 1 / τ0.get(j);
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
    //return Type.MultivariateExtendedApproximatePowerlaw;
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
         ν(double t)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         Z()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         iν(double t)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double
         Hphase(double H,
                double t)
  {
    throw new UnsupportedOperationException("TODO");
  }

}
