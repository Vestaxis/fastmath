package util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/*
 * Extended ConcurrentHashMap which has methods such as getOrCreate
 * @author crow
 * @param <K>
 * @param <V>
 */
public class AutoHashMap<K, V> extends ConcurrentHashMap<K, V> implements AutoMap<K, V>
{
  private static final long serialVersionUID = 1L;

  final Class<? super V> valueClass;

  private Function<K, V> constructor;

  /**
   * Specifies the class of V to be instantiated when getOrCreate creates a new
   * value, if null then this{@link #newValueInstance(Object)} must be
   * over-ridden
   * 
   * @param valueClass
   */
  public AutoHashMap(Class<? super V> valueClass)
  {
    this.valueClass = valueClass;
  }

  public AutoHashMap(Function<K, V> constructor)
  {
    this.constructor = constructor;
    this.valueClass = null;
  }

  /**
   * Constructs with null value class so this{@link #newValueInstance(Object)}
   * must be over-ridden
   */
  public AutoHashMap()
  {
    this.valueClass = null;
  }

  /**
   * Calls this{@link #newValueInserted(Object, Object)} when newValueInstance()
   * returned an instance that was not beaten by another thread
   */
  @Override
  @SuppressWarnings("unchecked")
  public V getOrCreate( K key )
  {
    V value = get( key );
    if ( value == null )
    {
      synchronized(this)
      {
      try
      {
        V previousValue = putIfAbsent( key, value = ( valueClass == null ? newValueInstance( key ) : (V) valueClass.newInstance() ) );
        if ( previousValue != null )
        {
          value = previousValue;
        }
        else
        {
          newValueInserted( key, value );
        }
      }
      catch( Exception e )
      {
        throw new RuntimeException( e );
      }
      }
    }

    return value;
  }

  protected void newValueInserted( K key, V value )
  {
  }

  @Override
  public V newValueInstance( K key )
  {
    if ( constructor != null )
    {
      return constructor.apply( key );
    }
    throw new UnsupportedOperationException( "newInstance() must be overriden if not instantiated with a value class or constructor function" );
  }

}
