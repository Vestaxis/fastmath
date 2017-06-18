package util;

import java.util.Map;

public interface AutoMap<K, V> extends Map<K, V>
{
  /**
   * Returns get(key) if the entry exists, if not then a newInstance is created
   * via valueClass if not null or newInstance() otherwise. The new elemnet is
   * then inserted and returned via putIfAbsent.
   * 
   * @param key
   * @return
   * 
   * @throws RuntimeException
   *           if valueClass.newInstance throws an exception
   */
  public abstract V getOrCreate( K key );

  /**
   * Override to return new instance when getOrCreate needs it
   * 
   * @param key
   * @return
   * 
   * @throws UnsupportedOperationException
   *           if not-overridden by subclass
   */
  public abstract V newValueInstance( K key );

}