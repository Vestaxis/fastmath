package fastmath;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ColIterator<V extends Vector> implements Iterator<V>, Iterable<V>
{
  final AbstractMatrix x;

  int i;

  public ColIterator(AbstractMatrix x)
  {
    this.x = x;
    i = 0;
  }

  @Override
  public boolean hasNext()
  {
    return i < x.getColCount();
  }

  @SuppressWarnings("unchecked")
  @Override
  public V next()
  {
    i++;
    return (V) x.col( i - 1 );
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<V> iterator()
  {
    return this;
  }

  public Stream<V> stream( boolean parallel )
  {
    return StreamSupport.stream( spliterator(), parallel );
  }
}
