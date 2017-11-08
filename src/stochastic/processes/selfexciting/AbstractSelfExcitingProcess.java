package stochastic.processes.selfexciting;

import static java.lang.Math.pow;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang.ArrayUtils.addAll;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.SimpleBounds;

import fastmath.DoubleMatrix;
import fastmath.Vector;

public abstract class AbstractSelfExcitingProcess implements MultivariateFunction
{

  public Vector T;

  public boolean verbose = false;

  /*
   * The first column of this matrix is identical with T, the remaining columns,
   * if any, are the marks associated with the timestamps in column 0
   */
  public DoubleMatrix X;

  @Override
  public Object clone()
  {
    try
    {
      AbstractSelfExcitingProcess spawn = getClass().newInstance();
      spawn.assignParameters(getParameters().toArray());
      spawn.T = T;
      spawn.X = X;
      return spawn;
    }
    catch (InstantiationException | IllegalAccessException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }

  }

  @SuppressWarnings("unchecked")
  public final <E extends AbstractSelfExcitingProcess> E copy()
  {
    return (E) clone();
  }

  Field[] parameterFields = null;

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

  public AbstractSelfExcitingProcess()
  {
    super();
  }

  public Vector getParameters()
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

  public abstract Object[] evaluateParameterStatistics(double[] point);

  public abstract Vector Λ();

  public abstract double mean();

  public abstract double compensatorMomentMeasure();

  public abstract double getΛKolmogorovSmirnovStatistic();

  private AbstractSelfExcitingProcess newProcess(double[] point)
  {
    throw new UnsupportedOperationException("TODO");
  }

  public final synchronized Field[] getParameterFields()
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

  /**
   * Uses this{@link #getParameterFields()} to assign values from an array to the
   * specified Java fields
   * 
   * @param array
   *          of values ordered according to this{@link #getBoundedParameters()}
   */
  public void assignParameters(double[] point)
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
    cachedρ = Double.NaN;
  }

  protected double cachedρ = Double.NaN;

  public abstract BoundedParameter[] getBoundedParameters();

  public final int getParamCount()
  {
    return getBoundedParameters().length;
  }

  public String getParamString()
  {
    return "[" + asList(getParameterFields()).stream().map(param -> {
      try
      {
        return param.getName() + "=" + param.getDouble(this);
      }
      catch (IllegalArgumentException | IllegalAccessException e)
      {
        throw new RuntimeException(e.getMessage(), e);
      }
    }).collect(joining(",")) + "]";
  }

  public double getFieldValue(Field param)
  {
    try
    {
      return param.getDouble(this);
    }
    catch (IllegalArgumentException | IllegalAccessException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public abstract double logLik();

}