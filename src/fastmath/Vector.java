package fastmath;

import static java.lang.String.format;
import static java.util.stream.IntStream.rangeClosed;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Collection;
import java.util.PrimitiveIterator.OfDouble;
import java.util.Spliterator;
import java.util.Spliterators.AbstractDoubleSpliterator;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.apache.commons.math3.util.CombinatoricsUtils;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import fastmath.exceptions.FastMathException;
import fastmath.matfile.MiDouble;
import fastmath.matfile.MiMatrix;
import fastmath.matfile.Writable;

public class Vector extends AbstractBufferedObject implements Writable, Iterable<Double>, Collection<Double>
{

  public Vector()
  {

  }

  static
  {
    NativeUtils.loadNativeFastmathLibrary();
  }

  public Pointer
         getPointer()
  {
    return Native.getDirectBufferPointer(buffer);
  }

  public Vector
         xor(double p)
  {
    return pow(p);
  }

  public Vector
         xor(int p)
  {
    return pow(p);
  }

  public Vector
         negate()
  {
    return copy().multiply(-1);
  }

  public final class VectorSpliterator extends AbstractDoubleSpliterator
  {
    int n = 0;

    public VectorSpliterator()
    {
      super(size, Spliterator.SIZED | Spliterator.SUBSIZED);
    }

    @Override
    public boolean
           tryAdvance(DoubleConsumer action)
    {
      if (n >= size)
      {
        return false;
      }
      action.accept(get(n++));
      return true;
    }

    @Override
    public boolean
           tryAdvance(Consumer<? super Double> action)
    {
      if (n >= size)
      {
        return false;
      }
      action.accept(get(n++));
      return true;
    }
  }

  public enum Condition
  {
    EQUAL, GT, GTE, LT, LTE
  }

  static class Sub extends Vector
  {

    private int baseOffset;

    private int increment;

    private int index = -1;

    public Sub(ByteBuffer buffer, int size, int offset, int increment)
    {
      super(size, buffer);

      this.baseOffset = offset;
      this.increment = increment;
    }

    public Sub(ByteBuffer buffer, int numRows, int offset2, int rowIncrement, int index)
    {
      this(buffer, numRows, offset2, rowIncrement);
      this.index = index;
    }

    @Override
    public int
           getIncrement()
    {
      return increment;
    }

    /**
     * Returns the index associated with this vector, used for row and column
     * iterators
     * 
     * @return
     */
    @Override
    public int
           getIndex()
    {
      return index;
    }

    @Override
    public int
           getOffset(int i)
    {
      return baseOffset + (increment * i * MiDouble.BYTES);
    }

  }

  private static final long serialVersionUID = 1L;

  /**
   * forms the dot product of two vectors.
   * 
   * @param n
   * @param x
   * @param offx
   * @param incx
   * @param y
   * @param offt
   * @param incy
   * 
   * @return
   */
  private static native double
          ddot(int n,
               ByteBuffer x,
               int offx,
               int incx,
               ByteBuffer y,
               int offy,
               int incy);

  public static native void
         swap(int n,
              ByteBuffer x,
              int xOff,
              int incx,
              ByteBuffer y,
              int yOff,
              int incy);

  private String name;

  protected int size;

  public Vector(ByteBuffer slice)
  {
    super(slice);
    assert slice != null : "slice is null";
    this.size = slice.capacity() / (Double.SIZE / Byte.SIZE);
  }

  public Vector(double[] x)
  {
    this(BufferUtils.newNativeBuffer(x.length * MiDouble.BYTES));
    size = x.length;

    for (int i = 0; i < x.length; i++)
    {
      set(i, x[i]);
    }
  }

  public Vector(Collection<Double> x)
  {
    super(BufferUtils.newNativeBuffer(x.size() * MiDouble.BYTES));
    size = x.size();

    int i = 0;
    for (Double num : x)
    {
      set(i++, num);
    }
  }

  public Vector(int m)
  {
    super(BufferUtils.newNativeBuffer(m * MiDouble.BYTES));
    size = m;
  }

  protected Vector(int m, ByteBuffer buffer)
  {
    super(buffer == null ? BufferUtils.newNativeBuffer(m * MiDouble.BYTES) : buffer);
    assert buffer != null : "slice is null";
    size = m;
  }

  /**
   * Copy
   * 
   * @param v
   */
  public Vector(Vector v)
  {
    this(v.toDoubleArray());
    setName(v.getName());
  }

  public Vector(String name)
  {
    this(0);
    setName(name);
  }

  public Vector(String name, int dim)
  {
    this(dim);
    setName(name);
  }

  public Vector(String string, double[] array)
  {
    this(array);
    setName(string);
  }

  public Vector(DoubleStream ds)
  {
    this(ds.toArray());
  }

  /**
   * Transform to absolute values
   * 
   * @return this
   */
  public Vector
         abs()
  {
    for (int i = 0; i < size(); i++)
    {
      set(i, Math.abs(get(i)));
    }

    return this;
  }

  /**
   * 
   * @param n
   * @return
   */
  public double
         moment(int n)
  {
    return doubleStream().map(x -> Math.pow(x, n)).average().getAsDouble();
  }

  public Vector
         moments(int n)
  {
    return new Vector(rangeClosed(1, n).mapToDouble(i -> moment(i)));
  }

  public Vector
         normalizedMoments(int n)
  {
    return new Vector(rangeClosed(1, n).mapToDouble(i -> normalizedMoment(i)));
  }

  private double
          normalizedMoment(int i)
  {
    return moment(i) / CombinatoricsUtils.factorial(i);
  }

  /**
   * 
   * @param n
   * @return
   */
  public double
         centralMoment(int n)
  {
    switch (n)
    {
    case 1:
      return mean();
    default:
      double m = mean();
      return doubleStream().map(x -> Math.pow(x - m, n)).average().getAsDouble();
    }
  }

  /**
   * this[i] += x
   * 
   * @param i
   * @param x
   * 
   * @return this
   */
  public double
         add(int i,
             double x)
  {
    assert i < size : format("%d >= %d", i, size);
    double updatedValue = get(i) + x;
    set(i, updatedValue);
    return updatedValue;
  }

  public Vector
         addVector(Vector x)
  {
    assert size() == x.size();
    return add(x);
  }

  /**
   * Adds a vector to this. See this{@link #addVector(Vector)} for a safer method
   * that does compatibility checking first, avoiding JNI crash if dimensions are
   * mismatched
   * 
   * 
   * @param x
   *          vector of same size
   * 
   * @return this
   * 
   * @throws FastMathException
   */
  public native Vector
         add(Vector x);

  /**
   * Adds a vector to this vector
   * 
   * @param x
   *          vector to add to this
   * @param alpha
   *          scaling parameter applied to this vector before addition
   * 
   * @return this
   */
  public Vector
         add(Vector x,
             double alpha)
  {
    assert size == x.size() : "Dimensions must be equal";

    for (int i = 0; i < size; i++)
    {
      set(i, get(i) + x.get(i) * alpha);
    }
    // BLAS1.daxpy(alpha, x, this);

    return this;
  }

  /**
   * Return a view of this Vector as a rowsxN matrix
   */
  public DoubleMatrix
         asDenseDoubleMatrix(int rows,
                             int cols)
  {
    DoubleMatrix.Sub matrix = new DoubleMatrix.Sub(buffer, rows, cols, getOffset(0), MiDouble.BYTES, rows, false);
    matrix.setName(getName());
    return matrix;
  }

  /**
   * Return a view of this Vector as a 1xN matrix
   */
  public DoubleMatrix
         asMatrix()
  {
    return asDenseDoubleMatrix(1, size);
  }

  /**
   * Return a view of this Vector as a Nx1 matrix
   */
  public DoubleMatrix
         asRowMatrix()
  {
    return new DoubleMatrix.Sub(buffer, 1, size, getOffset(0), size, getIncrement(), false);
  }

  public Vector reassign(Vector x)
  {
    this.buffer = x.buffer;
    this.size = x.size;
    this.name = x.name;
    this.incrementalCapacityExpansionFactor = x.incrementalCapacityExpansionFactor;
    return this;
  }
  
  public Vector
         assign(Vector x)
  {
    assert size() == x.size() : format("dimensions do not match in assignment: this.size=%d != %d", size(), x.size());
    for (int i = 0; i < x.size(); i++)
    {
      set(i, x.get(i));
    }
    return this;
  }

  public Vector
         copy()
  {
    return new Vector(this);
  }

  public Vector
         copy(VectorContainer container)
  {
    return new Vector(this);
  }

  public MiMatrix
         createMiMatrix()
  {
    return asMatrix().createMiMatrix();
  }

  /**
   * first (order) differences
   * 
   * [X(2)-X(1) X(3)-X(2) ... X(n)-X(n-1)]
   * 
   * @return vector length of this minus 1
   */
  public Vector
         diff()
  {
    if (size == 0)
    {
      return new Vector();
    }
    Vector fd = copy().slice(1, size());
    fd.subtract(slice(0, size() - 1));
    return fd.setName("d" + getName());
  }

  /**
   * Element wise division by a constant
   * 
   * @return this
   */
  public Vector
         divide(double x)
  {
    for (int i = 0; i < size(); i++)
    {
      set(i, get(i) / x);
    }
    return this;
  }

  /**
   * Element wise division, this.element(i) /= x.element(i)
   * 
   * @return this
   */
  public Vector
         divide(Vector x)
  {
    assert size() == x.size();
    for (int i = 0; i < size(); i++)
    {
      set(i, get(i) / x.get(i));
    }
    return this;
  }

  /**
   * Dot-product
   * 
   * @param x
   * 
   * @return
   */
  public double
         dotProduct(Vector x)
  {
    assert x.size == size : "Dimensions do not agree";
    if (x.getIncrement() >= 0 && getIncrement() >= 0)
    {
      return ddot(x.size, x.getBuffer(), x.getOffset(0), x.getIncrement(), getBuffer(), getOffset(0), getIncrement());

    }
    else
    {
      return IntStream.range(0, size()).mapToDouble(i -> get(i) * x.get(i)).sum();
    }
  }

  @Override
  public boolean
         equals(Object obj)
  {
    // TODO: optimize
    if (Vector.class.isAssignableFrom(obj.getClass()))
    {
      Vector v = (Vector) obj;

      if (size != v.size())
      {
        return false;
      }

      for (int i = 0; i < size; i++)
      {
        if (get(i) != v.get(i))
        {
          return false;
        }
      }

      return true;
    }
    else
    {
      return false;
    }

  }

  /**
   * Equals within bounds
   */
  public boolean
         equals(Vector v,
                double bounds)
  {
    if (size != v.size())
    {
      return false;
    }

    for (int i = 0; i < size; i++)
    {
      if (Math.abs(get(i) - v.get(i)) > bounds)
      {
        return false;
      }
    }

    return true;
  }

  /*
   * Element wise exp
   * 
   * @return this
   */
  public native Vector
         exp();

  public Vector
         floor()
  {
    for (int i = 0; i < size(); i++)
    {
      set(i, Math.floor(get(i)));
    }
    return this;
  }

  public IntVector
         findAll(final double val,
                 final Condition cond)
  {
    IntVector indices = new IntVector();
    int i = 0;
    while ((i = find(val, cond, i + 1)) > 0 && i < size())
    {
      indices.append(i);
    }
    return indices;
  }

  /**
   * 
   * Return the first index that matches the specified criteria
   * 
   * @param val
   *          value to compare
   * @param cond
   *          <code>Condition</code>
   * @param start
   *          position to start from
   * 
   * @return index, or -1 if condition not met
   */
  public int
         find(final double val,
              final Condition cond,
              int start)
  {
    for (int i = start; i < size(); i++)
    {
      double x = get(i);

      if ((cond == Condition.EQUAL && x == val) || (cond == Condition.GT && x > val)
          || (cond == Condition.LT && x < val)
          || (cond == Condition.GTE && x >= val)
          || (cond == Condition.LTE && x <= val))
      {
        return i;
      }
    }

    return -1;
  }

  /**
   * 
   * Return the last index that matches the specified criteria
   * 
   * @param val
   *          value to compare
   * @param cond
   *          <code>Condition</code>
   * 
   * @return index, or -1 if condition not met
   */
  public int
         findLast(final double val,
                  final Condition cond)
  {
    int reverseResult = reverse().find(val, cond, 0);
    return reverseResult == -1 ? -1 : (size() - reverseResult - 1);
  }

  /**
   * @return true if any element of this vector is equal to +infinity
   */
  public boolean
         hasAnyInfinities()
  {
    return find(Double.POSITIVE_INFINITY, Condition.EQUAL, 0) != -1;
  }

  public double
         get(int i)
  {
    assert i >= 0 : "index=" + i + " is negative";
    assert i < size() : "Index out of bounds, " + i + " >= " + size();
    assert buffer != null : "buffer is null";
    int offset = getOffset(i);
    double doubleVal = buffer.getDouble(offset);
    return doubleVal;
  }

  public int
         getIncrement()
  {
    return 1;
  }

  /**
   * Returns the index associated with this vector, used for row and column
   * iterators
   * 
   * @return -1, there are no idexes for regular vectors, only for columns and
   *         rows
   */
  public int
         getIndex()
  {
    return -1;
  }

  public String
         getName()
  {
    return name;
  }

  public int
         getOffset(int i)
  {
    return i * MiDouble.BYTES;
  }

  /**
   * Loop thru elements of vector replacing those that return true for
   * {@link Double#isInfinite()} with specified value
   * 
   * @param with
   * @return this
   */
  public Vector
         replaceInfinity(double with)
  {
    for (int i = 0; i < size; i++)
    {
      final double x = get(i);
      set(i, Double.isInfinite(x) ? with : x);
    }
    return this;
  }

  public Vector
         replaceNaN(double with)
  {
    for (int i = 0; i < size; i++)
    {
      final double x = get(i);
      set(i, Double.isNaN(x) ? with : x);
    }
    return this;
  }

  /**
   * In-place reversal of the elements in this vector
   * 
   * @return this
   */
  // public Vector inPlaceReverse()
  // {
  // // TODO: inPlaceReverse() can be optimized
  // return assign( reverse().copy() );
  // }

  /**
   * Inverts each element of this vector
   * 
   * @return this
   */
  public Vector
         inv()
  {
    for (int i = 0; i < size; i++)
    {
      set(i, 1.0 / get(i));
    }

    return this;
  }

  /**
   * Returns true if this matrix is dense
   * 
   * @return
   */
  public boolean
         isContiguous()
  {
    return getIncrement() == 1;
  }

  /**
   * Return true if this vector's storage is contiguous
   * 
   * @return
   */
  public boolean
         isDense()
  {
    return getIncrement() == 1;
  }

  /**
   * Creates a vector of lagged values, lags are 'fed' with 0
   * 
   * @param k
   * 
   * @return this
   */
  public Vector
         lag(int k)
  {
    return lag(k, 0.0);
  }

  /**
   * Creates a vector of lagged values
   * 
   * @param k
   * @param x
   *          value to 'feed the lags' with
   * 
   * @return this
   */
  public Vector
         lag(int k,
             double x)
  {
    assert k > 0 : "k must be > 0";
    slice(k, size()).assign(slice(0, size() - k).copy());
    slice(0, k).assign(x);
    return this;
  }

  /*
   * Element wise log
   * 
   * @return this
   */
  public Vector
         log()
  {
    for (int i = 0; i < size; i++)
    {
      set(i, Math.log(get(i)));
    }

    return this;
  }

  /**
   * Finds the maximum value of this vector
   * 
   * @return
   */
  public double
         fmax()
  {
    double max = Double.NEGATIVE_INFINITY;

    double d;
    for (int i = 0; i < size; i++)
    {
      if ((d = get(i)) > max)
      {
        max = d;
      }
    }

    return max;
  }

  /**
   * 
   * @return
   */

  /**
   * Finds the maximum value of this vector
   * 
   * @param idx
   *          the first element is written with the index of the maximal element,
   *          or at least the very last one if there are multiple "exact" (withing
   *          IEEE finite precision rounding) elements with the same value
   * @return
   */
  public double
         fmax(int[] idx)
  {
    double max = Double.NEGATIVE_INFINITY;

    double d;
    for (int i = 0; i < size; i++)
    {
      if ((d = get(i)) > max)
      {
        max = d;
        idx[0] = i;
      }
    }

    return max;
  }

  /**
   * Calculates the mean of the vector
   * 
   * @param secondMoment
   *          if true, divides by n-1, else divides by n
   * 
   * @return mean value
   */
  public double
         mean()
  {
    return sum() / size;
  }

  /**
   * Finds the minimum value of this vector
   * 
   * @return
   */
  public double
         fmin()
  {
    double min = Double.POSITIVE_INFINITY;

    double d;
    for (int i = 0; i < size; i++)
    {
      if ((d = get(i)) < min)
      {
        min = d;
      }
    }

    return min;
  }

  /**
   * Element wise multiplication by a constant
   * 
   * @return this
   */
  public Vector
         multiply(double x)
  {
    for (int i = 0; i < size; i++)
    {
      set(i, get(i) * x);
    }
    return this;
  }

  /**
   * Element wise multiplication with another vector
   * 
   * @param x
   *          vector to multiply to this
   * 
   * @return this
   */
  public native Vector
         multiply(Vector x);

  /*
   * Element-wise pow
   * 
   * @return this
   */
  public Vector
         pow(double x)
  {
    for (int i = 0; i < size; i++)
    {
      set(i, Math.pow(get(i), x));
    }

    return this;
  }

  /**
   * Replaces all instances of x with y
   * 
   * TODO: unit test
   */
  public Vector
         replace(double x,
                 double y)
  {
    for (int i = 0; i < size; i++)
    {
      if (get(i) == x)
      {
        set(i, y);
      }
    }

    return this;
  }

  /**
   * Returns a view of this vector as a column major matrix
   * 
   * m*n must equal the length of this vector
   * 
   * @param m
   *          rows
   * @param n
   *          columns
   * 
   * @return
   */
  public DoubleMatrix
         reshape(int m,
                 int n)
  {
    assert (m * n) == size() : "dimensions do not agree, m=" + m + ", n=" + n + ", m*n != " + size();

    return new DoubleMatrix.Sub(buffer, m, n, getOffset(0), getIncrement(), m, false);
  }

  /**
   * Returns a backwards view of this matrix
   * 
   * @return new Vector sharing the same underlying buffer as this
   */
  public Vector
         reverse()
  {
    return new Sub(buffer, size, getOffset(size() - 1), getIncrement() * -1);
  }

  /**
   * Round each element, e.g. round 2.341 with 0.01 returns 2.34, 2.345 returns
   * 2.35
   * 
   * @param x
   *          minimum step size
   * 
   * @return this
   */
  public Vector
         round(double x)
  {
    double y = 1 / x;

    for (int i = 0; i < size(); i++)
    {
      set(i, Math.round(get(i) * y) / y);
    }

    return this;
  }

  /**
   * Sets an element of this vector
   * 
   * @param i
   * @param x
   * 
   * @return this
   */
  public Vector
         set(int i,
             double x)
  {
    assert i < size() && i >= 0 : format("i=%d size()=%d", i, size());

    int offset = getOffset(i);
    try
    {
      buffer.putDouble(offset, x);
    }
    catch (IndexOutOfBoundsException e)
    {
      IndexOutOfBoundsException moreInformativeException = new IndexOutOfBoundsException(format("offset=%d > %d", offset, buffer.limit()));
      moreInformativeException.addSuppressed(e);
      throw moreInformativeException;
    }

    return this;
  }

  public Vector
         set(double... x)
  {
    return assign(x);
  }

  /**
   * @param name
   * 
   *          TODO: throw exception if name contains UTF characters
   */
  public Vector
         setName(String name)
  {
    this.name = name;
    return this;
  }

  public final int
         dimension()
  {
    return size;
  }

  public final int
         dim()
  {
    return size;
  }

  public final int
         size()
  {
    return size;
  }

  /**
   * Returns a view of this vector
   * 
   * The subvector begins at the specified beginIndex and extends to the element
   * at index endIndex - 1. Thus the length of the vector is endIndex-beginIndex.
   * 
   * @param beginIndex
   *          the beginning index, inclusive
   * @param endIndex
   *          the ending index, exclusive
   * 
   * @return the specified subvector
   */
  public Vector
         slice(int beginIndex,
               int endIndex)
  {
    assert beginIndex >= 0 : String.format("beginIndex %d must be >= 0", beginIndex);
    assert endIndex <= size() : String.format("endIndex %d must be <= %d", endIndex, size());

    Vector.Sub subset = new Vector.Sub(buffer, endIndex - beginIndex, getOffset(beginIndex), getIncrement());
    subset.setName(getName());
    return subset;
  }

  /**
   * Returns a vector of square roots applied to each element
   */
  public Vector
         sqrt()
  {
    Vector sqv = new Vector(size);

    for (int i = 0; i < size; i++)
    {
      sqv.set(i, Math.sqrt(get(i)));
    }

    return sqv;
  }

  /**
   * Subtracts a vector from this vector
   * 
   * @param x
   *          vector to subtract from this
   * 
   * @return this
   */
  public Vector
         subtract(Vector x)
  {
    return subtract((Vector) x, 1.0);
  }

  /**
   * Subtracts a vector from this vector
   * 
   * @param x
   *          vector to subtract from to this
   * @param alpha
   *          scaling parameter applied to this x before subtraction
   * 
   * @return this
   */
  public Vector
         subtract(Vector x,
                  double alpha)
  {
    assert size == x.size() : "Dimensions must agree, this.size=" + size + " != x.size = " + x.size();

    return add(x, -alpha);
  }

  /**
   * Returns the sum of this vector
   * 
   * @return
   */
  public double
         sum()
  {
    return doubleStream().sum();
  }

  /**
   * Swaps the contents of two vectors
   * 
   * @param x
   * 
   * @return this
   */
  public Vector
         swap(Vector x)
  {
    int len = size();
    assert len == x.size() : "Dimensions must agree";

    // the unit of offset is a byte and the unit of increment is a double
    int thisOffset = getOffset(0);
    int thisIncrement = getIncrement();
    int xOffset = x.getOffset(0);
    int xIncrement = x.getIncrement();
    swap(len, getBuffer(), thisOffset, thisIncrement, x.getBuffer(), xOffset, xIncrement);

    return this;
  }

  /**
   * For each element x=tanh(x)
   */
  public void
         tanh()
  {
    for (int i = 0; i < size; i++)
    {
      set(i, Math.tanh(get(i)));
    }
  }

  /**
   * Convert to an array
   */
  public double[]
         toDoubleArray()
  {
    double[] x = new double[size];

    // TODO: optimize
    for (int i = 0; i < size; i++)
    {
      x[i] = get(i);
    }

    return x;
  }

  /**
   * Return a new DenseDoubleMatrix with the diagonals set with the values from
   * this vector
   */
  public AbstractMatrix
         toDiagMatrix()
  {
    DoubleMatrix cm = new DoubleColMatrix(size, size);

    cm.diag().assign(this);

    return cm;
  }

  @Override
  public String
         toString()
  {
    return print(10);
  }

  public String
         print(int digits)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    if (getName() != null)
    {
      pw.append(getName() + " = ");
    }
    asRowMatrix().print(pw, digits);
    pw.flush();
    return sw.toString();
  }

  private double incrementalCapacityExpansionFactor = 0.25;

  public Vector
         assign(double scalar)
  {
    for (int i = 0; i < size(); i++)
    {
      set(i, scalar);
    }
    return this;
  }

  public Vector
         assign(double... data)
  {
    assert size() == data.length : format("dimensions do not match in assignment: this.size=%d != %d", size(), data.length);
    for (int i = 0; i < data.length; i++)
    {
      set(i, data[i]);
    }
    return this;
  }

  public Vector
         unique()
  {
    TreeSet<Double> values = new TreeSet<Double>();
    for (int i = 0; i < size(); i++)
    {
      values.add(get(i));
    }
    return new Vector(values);
  }

  /**
   * Return a Vector with the same contents but longer length, padded with zeros
   * 
   * @param i
   * @return
   */
  public Vector
         extend(int i)
  {
    Vector newVector = new Vector(size() + i);
    newVector.slice(0, size()).assign(this);
    return newVector;
  }

  public OfDouble
         iterator()
  {
    return new OfDouble()
    {
      int i = 0;

      @Override
      public boolean
             hasNext()
      {
        return i < size();
      }

      @Override
      public Double
             next()
      {
        return get(i++);
      }

      @Override
      public void
             remove()
      {
        throw new UnsupportedOperationException();
      }

      @Override
      public double
             nextDouble()
      {
        return get(i++);
      }
    };
  }

  public int
         getDimension()
  {
    return size();
  }

  public double
         getIncrementalCapacityExpansionFactor()
  {
    return incrementalCapacityExpansionFactor;
  }

  public void
         setIncrementalCapacityExpansionFactor(double incrementalCapacityExpansionFactor)
  {
    this.incrementalCapacityExpansionFactor = incrementalCapacityExpansionFactor;
  }

  public double
         getLeftmostValue()
  {
    return get(0);
  }

  /**
   * 
   * @return this{@link #get(int)}(this{@link #resizeBuffer(int, int)}-1)
   */
  public double
         getRightmostValue()
  {
    return get(size() - 1);
  }

  public Vector
         add(double x)
  {
    for (int i = 0; i < size(); i++)
    {
      set(i, get(i) + x);
    }
    return this;
  }

  public Spliterator.OfDouble
         spliterator()
  {
    return new VectorSpliterator();
  }

  public DoubleStream
         doubleStream()
  {
    return StreamSupport.doubleStream(spliterator(), false);
  }

  public Vector
         apply(IntFunction<Double> func)
  {
    for (int i = 0; i < size; i++)
    {
      set(i, func.apply(i));
    }
    return this;
  }

  @Override
  public void
         write(SeekableByteChannel channel) throws IOException
  {
    createMiMatrix().write(channel);
  }

  public double
         variance()
  {
    final int n = size();
    final double mean = mean();
    return Functions.sum(i -> Math.pow(get(i) - mean, 2), 0, n - 1) / n;
  }

  public double
         getStdev()
  {
    return Math.sqrt(variance());
  }

  public Vector
         cumulativeSum()
  {
    Vector x = new Vector(size());
    double d = 0;
    for (int i = 0; i < size(); i++)
    {
      x.set(i, d += get(i));
    }
    return x;
  }

  public Vector
         subtract(double subtrahend)
  {
    for (int i = 0; i < size(); i++)
    {
      set(i, get(i) - subtrahend);
    }
    return this;
  }

  /**
   * calculate autocovariance coefficient
   * 
   * @param lag
   * @return this{@link #autocovAtLag(int)} / this{@link #variance()}
   */
  public double
         autocovAtLag(int lag)
  {
    int N = size();
    final double m = mean();

    double autocov = 0.0;
    for (int i = 0; i < N - lag; i++)
    {
      autocov += (get(i) - m) * (get(i + lag) - m);
    }
    autocov *= (1.0 / (N - lag));

    return autocov;
  }

  /**
   * calculate autocorrelation coefficient
   * 
   * @param lag
   * @return this{@link #autocovAtLag(int)} / this{@link #variance()}
   */
  public double
         autocorAtLag(int lag)
  {
    return autocovAtLag(lag) / variance();
  }

  public Vector
         autocor(int maxLag)
  {
    double var = variance();
    return new Vector(rangeClosed(0, maxLag).mapToDouble(lag -> autocovAtLag(lag) / var));
  }

  public double
         getLjungBoxStatistic(int maxLag)
  {
    Vector ac = autocor(maxLag);
    int n = size();
    return n * (n + 2) * Functions.sum(k -> Math.pow(ac.get(k), 2) / (n - k), 1, maxLag);
  }

  public Vector
         copyAndAppend(double d)
  {
    int len = size();
    Vector newVec = extend(1);
    newVec.set(len, d);    
    return newVec;
  }

  @Override
  public boolean
         add(Double arg0)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public boolean
         addAll(Collection<? extends Double> arg0)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public void
         clear()
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public boolean
         contains(Object arg0)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public boolean
         containsAll(Collection<?> arg0)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public boolean
         isEmpty()
  {
    return size == 0;
  }

  @Override
  public boolean
         remove(Object arg0)
  {
    throw new UnsupportedOperationException("TODO");

  }

  @Override
  public boolean
         removeAll(Collection<?> arg0)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public boolean
         retainAll(Collection<?> arg0)
  {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Object[]
         toArray()
  {
    Double[] x = new Double[size];

    // TODO: optimize
    for (int i = 0; i < size; i++)
    {
      x[i] = get(i);
    }

    return x;
  }

  @Override
  public <T>
         T[]
         toArray(T[] arg0)
  {
    throw new UnsupportedOperationException("TODO");
  }

}
