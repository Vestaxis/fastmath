package stochastic.processes.selfexciting.multivariate;

import static java.lang.System.out;

import java.lang.reflect.Field;

import fastmath.Vector;
import stochastic.processes.selfexciting.BoundedParameter;
import stochastic.processes.selfexciting.ExtendedApproximatePowerlawSelfExcitingProcess;

public class MultivariateExtendedApproximatePowerlawSelfExcitingProcess extends MultivariateExponentialSelfExcitingProcess
{
  @Override
  public Vector getParameters()
  {
    Vector params = new Vector(getParamCount() * dim);
    for (int i = 0; i < getParamCount(); i++)
    {
      Field field = getParameterFields()[i];
      Vector fieldArray = getField(field);
      if (fieldArray == null) { throw new IllegalArgumentException("Vector field '" + field.getName() + "' not found in " + getClass().getSimpleName()); }
      for (int j = 0; j < dim; j++)
      {
        int offset = getBoundedParameters()[i].getOrdinal() * (j + 1);
        params.set(offset, fieldArray.get(j));
      }
    }
    return params;
  }

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
      Vector fieldArray = getField(fields[i]);
      for (int j = 0; j < dim; j++)
      {
        {
          int offset = params[i].getOrdinal() * (j + 1);
          fieldArray.set(j, point[offset]);
        }
      }
    }
  }

  public MultivariateExtendedApproximatePowerlawSelfExcitingProcess()
  {

  }

  public MultivariateExtendedApproximatePowerlawSelfExcitingProcess(int dim)
  {
    this.dim = dim;
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
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public double getΛKolmogorovSmirnovStatistic()
  {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * 
   * @param field
   * @return a Vector of dimension this{@link #getDim()}, one is constructed if it
   *         does not already exist
   */
  public Vector getField(Field field)
  {
    try
    {
      Vector vector = (Vector) field.get(this);
      if (vector == null)
      {
        vector = new Vector(dim);
        field.set(this, vector);
      }
      return vector;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * 
   * @param field
   * @param j
   * @return the n-th element of the {@link Vector} referenced by field
   */
  public double getFieldValue(Field field, int j)
  {
    Vector fieldArray;
    try
    {
      fieldArray = (Vector) field.get(this);
      return fieldArray.get(j);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

}
