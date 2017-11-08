package stochastic.processes.selfexciting.multivariate;

import java.lang.reflect.Field;

import fastmath.IntVector;
import fastmath.Vector;
import stochastic.processes.selfexciting.AbstractSelfExcitingProcess;
import stochastic.processes.selfexciting.BoundedParameter;

public abstract class MultivariateSelfExcitingProcess extends AbstractSelfExcitingProcess
{

  protected int dim;

  /**
   * integer-array indicating which dimension to which each point in
   * this{@link #T} corresponds
   */
  public IntVector K;

  public MultivariateSelfExcitingProcess()
  {
    super();
  }

  @Override
  public Object clone()
  {
    assert T != null : "T is null";
    assert K != null : "K is null";
    assert X != null : "X is null";

    try
    {
      MultivariateSelfExcitingProcess spawn = getClass().getDeclaredConstructor(int.class).newInstance(dim);
      spawn.assignParameters(getParameters().toArray());
      spawn.T = T;
      spawn.X = X;
      spawn.K = K;
      return spawn;
    }
    catch (Exception e)
    {
      if (e instanceof RuntimeException) { throw (RuntimeException) e; }
      throw new RuntimeException(e.getMessage(), e);
    }

  }

  @Override
  public Vector getParameters()
  {
    Vector params = new Vector(getParamCount() * dim);
    for (int i = 0; i < getParamCount(); i++)
    {
      Vector fieldArray = getField(i);
      if (fieldArray == null) { throw new IllegalArgumentException("Vector field '" + getParameterFields()[i].getName()
                                                                   + "' not found in "
                                                                   + getClass().getSimpleName()); }
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
        vector = new Vector(dim).setName(field.getName());
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
   * @param i index of the parameter to return
   * 
   * @return the {@link Vector} corresponding to the i-th parameter as determined
   *         by this{@link #getParameterFields()}
   */
  public Vector getField(int i)
  {
    return getField(getParameterFields()[i]);
  }

  /**
   * 
   * @param field
   * @param j
   * @return the n-th element of the {@link Vector} referenced by field
   */
  public double getFieldValue(Field field, int j)
  {
    return getField(field).get(j);
  }

}