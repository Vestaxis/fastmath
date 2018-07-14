package util;

import java.util.ArrayList;
import java.util.function.Function;

/*
 * Extended ConcurrentHashMap which has methods such as getOrCreate
 * @author crow
 * @param <K>
 * @param <V>
 */
public class AutoArrayList<V> extends ArrayList<V>
{
  public AutoArrayList(int initialCapacity, Class<? super V> valueClass)
  {
    super(initialCapacity);
    this.valueClass = valueClass;
  }

  public AutoArrayList(int initialCapacity, Function<Integer, V> constructor)
  {
    super(initialCapacity);
    this.constructor = constructor;
    this.valueClass = null;
  }

  private static final long serialVersionUID = 1L;

  final Class<? super V> valueClass;

  private Function<Integer, V> constructor;

  /**
   * Specifies the class of V to be instantiated when getOrCreate creates a new
   * value, if null then this{@link #newValueInstance(Object)} must be over-ridden
   * 
   * @param valueClass
   */
  public AutoArrayList(Class<? super V> valueClass)
  {
    this.valueClass = valueClass;
  }

  public AutoArrayList(Function<Integer, V> constructor)
  {
    this.constructor = constructor;
    this.valueClass = null;
  }

  /**
   * Constructs with null value class so this{@link #newValueInstance(Object)}
   * must be over-ridden
   */
  public AutoArrayList()
  {
    this.valueClass = null;
  }

  /**
   * Calls this{@link #newValueInserted(Object, Object)} when newValueInstance()
   * returned an instance that was not beaten by another thread
   */
  @SuppressWarnings("unchecked")
  public V
         getOrCreate(Integer key)
  {
    V value = key < size() ? get(key) : null;
    if (value == null)
    {
      synchronized (this)
      {
        try
        {
          value = (valueClass == null ? newValueInstance(key) : (V) valueClass.getConstructor().newInstance());
        }
        catch (Exception e)
        {
          throw new RuntimeException(e);
        }
      }
    }

    return value;
  }

  public V
         newValueInstance(Integer key)
  {
    if (constructor != null)
    {
      V constructed = constructor.apply(key);
      while ( size() < key )
      {
        add(null);
      }
      add(key, constructed);
      return constructed;
    }
    throw new UnsupportedOperationException("newInstance() must be overriden if not instantiated with a value class or constructor function");
  }

}
