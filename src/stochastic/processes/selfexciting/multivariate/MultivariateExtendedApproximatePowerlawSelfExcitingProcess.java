package stochastic.processes.selfexciting.multivariate;

import static java.lang.System.out;

import java.lang.reflect.Field;

import fastmath.Vector;
import stochastic.processes.selfexciting.BoundedParameter;
import stochastic.processes.selfexciting.ExtendedApproximatePowerlawSelfExcitingProcess;

public class MultivariateExtendedApproximatePowerlawSelfExcitingProcess extends MultivariateExponentialSelfExcitingProcess
{
  /**
   * Uses this{@link #getParameterFields()} to assign values from an array to the
   * specified Java fields, there should dim*this{@link #getParamCount()}
   * elements, arranged in order [α1,β1,ε1,α.,β.,ε.,αD,βD,εD] where
   * D=this{@link #getDim()} and for instance getBoundedParms() has 3 elements
   * called [α,β,ε]
   * 
   * @param array
   *          of values ordered according to this{@link #getBoundedParameters()}
   */
  @Override
  public void assignParameters(double[] point)
  {

    BoundedParameter[] params = getBoundedParameters();
    Field[] fields = getParameterFields();
    assert fields.length == params.length;

    for (int i = 0; i < fields.length; i++)
    {
      for (int j = 0; j < dim; j++)
      {
        {
          try
          {
            int offset = params[i].getOrdinal() * (j + 1);
            Vector fieldArray = (Vector) fields[i].get(this);
            fieldArray.set(j, point[offset]);
          }
          catch (IllegalArgumentException | IllegalAccessException e)
          {
            throw new RuntimeException(e.getMessage(), e);
          }
        }
      }
    }
    cachedρ = Double.NaN;
  }

  public MultivariateExtendedApproximatePowerlawSelfExcitingProcess(int dim)
  {
    super(dim);
  }

  public Vector κ;

  public Vector η;

  public Vector b;

  public Vector ε;

  public Vector τ0;

  @Override
  public final double value(double[] point)
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

  @Override
  public int order()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public BoundedParameter[] getBoundedParameters()
  {
    return ExtendedApproximatePowerlawSelfExcitingProcess.Parameter.values();
  }

  @Override
  public Object[] evaluateParameterStatistics(double[] point)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double mean()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getΛKolmogorovSmirnovStatistic()
  {
    // TODO Auto-generated method stub
    return 0;
  }

}
