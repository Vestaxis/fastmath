package stochastic.processes.selfexciting;

import static fastmath.Functions.product;
import static fastmath.Functions.sum;
import static java.lang.Math.log;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.SimpleBounds;

import fastmath.DoubleMatrix;
import fastmath.Vector;
import fastmath.optim.ParallelMultistartMultivariateOptimizer;
import stochastic.processes.selfexciting.multivariate.MultivariateSelfExcitingProcess;

public abstract class AbstractSelfExcitingProcess implements MultivariateFunction, SelfExcitingProcess
{

  public abstract SelfExcitingProcessFactory.Type
         getType();

  public Vector T;

  public boolean verbose = false;

  /**
   * constant deterministic intensity
   */
  public double κ = 0;

  /*
   * The first column of this matrix is identical with T, the remaining columns,
   * if any, are the marks associated with the timestamps in column 0
   */
  public DoubleMatrix X;

  @Override
  public Object
         clone()
  {
    try
    {
      AbstractSelfExcitingProcess spawn = getClass().getDeclaredConstructor().newInstance();
      spawn.assignParameters(getParameters().toDoubleArray());
      spawn.T = T;
      spawn.X = X;
      if (spawn instanceof MultivariateSelfExcitingProcess)
      {
        MultivariateSelfExcitingProcess multivariateSpawn = (MultivariateSelfExcitingProcess) spawn;
        MultivariateSelfExcitingProcess multivariateThis = (MultivariateSelfExcitingProcess) this;
        multivariateSpawn.K = multivariateThis.K;
      }
      return spawn;
    }
    catch (Exception e)
    {
      if (e instanceof RuntimeException)
      {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e.getMessage(), e);
    }

  }

  @SuppressWarnings("unchecked")
  public final <E extends AbstractSelfExcitingProcess>
         E
         copy()
  {
    return (E) clone();
  }

  Field[] parameterFields = null;

  public SimpleBounds
         getParameterBounds()
  {
    BoundedParameter[] bounds = getBoundedParameters();
    final int paramCount = bounds.length;
    double[] lowerBounds = range(0, paramCount).mapToDouble(i -> bounds[i].getMin()).toArray();
    double[] upperBounds = range(0, paramCount).mapToDouble(i -> bounds[i].getMax()).toArray();
    return new SimpleBounds(lowerBounds, upperBounds);
  }

  public final Field
         getField(String name)
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
      if (field == null)
      {
        throw new RuntimeException(nsfe.getMessage(), nsfe);
      }
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

  public Vector
         getParameters()
  {
    return new Vector(stream(getParameterFields()).mapToDouble(field -> {
      try
      {
        return field.getDouble(this);
      }
      catch (Exception e)
      {
        throw new RuntimeException(e.getMessage(), e);
      }
    }));
  }

  public abstract Object[]
         evaluateParameterStatistics(double[] point);

  public abstract Vector
         Λ();

  public abstract double
         mean();

  public abstract double
         getΛmomentMeasure();

  public abstract double
         getΛmomentLjungBoxMeasure();

  public abstract double
         getLjungBoxMeasure();

  public abstract double
         getΛKolmogorovSmirnovStatistic();

  public abstract AbstractSelfExcitingProcess
         newProcess(double[] point);

  public final synchronized Field[]
         getParameterFields()
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
  public void
         assignParameters(double[] point)
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

  public abstract BoundedParameter[]
         getBoundedParameters();

  public int
         getParamCount()
  {
    return getBoundedParameters().length;
  }

  public String
         getParamString()
  {
    return "{" + asList(getParameterFields()).stream().map(param -> {
      try
      {
        return param.getName() + "=" + param.getDouble(this);
      }
      catch (IllegalArgumentException | IllegalAccessException e)
      {
        throw new RuntimeException(e.getMessage(), e);
      }
    }).collect(joining(",")) + "}";
  }

  public double
         getFieldValue(Field param)
  {
    try
    {
      return param.getDouble(this);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public abstract double
         logLik();

  public abstract ParallelMultistartMultivariateOptimizer
         estimateParameters(int numStarts);

  public abstract String[]
         getColumnHeaders();

  public abstract void
         loadParameters(File modelFile) throws IOException;

  public abstract void
         storeParameters(File modelFile) throws IOException;

  /**
   * kernel function
   * 
   * @param t
   * @return
   */
  public abstract double
         ν(double t);

  /**
   * integrated kernel function, regarded as a cumulative distribution function
   * when this{@link #ρ()}=1, it gives the probability that an event happens
   * before time t
   * 
   * @param t
   * @return ∫ν(s)ds(0,t) integral of this{@link #ν(double)} over s=0..t
   */
  public abstract double
         iν(double t);

  /**
   * the survivor/reliability function is the complementary cumulative
   * distribution function, it gives the probability of an event NOT happening
   * before t
   * 
   * @param t
   * @return 1-this{@link #iν(double)}
   */
  public double
         s(double t)
  {
    return 1 - iν(t);
  }

  /**
   * the hazard function at time t is an instantaneous rate that an event will
   * occur at time t, the probability that an event will occur in a small interval
   * of length dt around time t is h(t)*dt. If h(t)<=1 then h(t) is also an
   * instantaneous probability. In the case of an
   * {@link ExponentialSelfExcitingProcess} the limit of h(t) as t->∞ is equal to
   * min({@link ExponentialSelfExcitingProcess#β(int)}) and the limit as t->-∞ is
   * equal to max({@link ExponentialSelfExcitingProcess#β(int)})
   * 
   * @param t
   * @return
   */
  public double
         h(double t)
  {
    return ν(t) / s(t);
  }

  /**
   * integrated hazard function
   * 
   * @param t
   * @return -log(this{@link #s(double)}
   */
  public double
         ih(double t)
  {
    return -log(s(t));
  }

  /**
   * inverse integrated hazard
   * 
   * @param y
   * @return the value {t:ih(t)=y}
   */
  public double
         invih(double y)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * normalization factor which ensures the integral of this{@link #ν(double)}
   * over [0,∞] is equal to this#ρ()
   * 
   * @return
   */
  public abstract double
         Z();

  /**
   * 
   * @return ∫ν(s)ds(0,∞) the integral of this{@link #ν(double)} over [0,∞]
   */
  public abstract double
         ρ();

}