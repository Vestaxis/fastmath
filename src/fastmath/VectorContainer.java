package fastmath;

import java.io.Serializable;

public class VectorContainer implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Vector vector;

  public Vector getVector( int size )
  {
    if ( vector == null )
    {
      vector = new Vector( size );
    }
    assert size == vector.size();
    return vector;
  }

  public Vector getVector()
  {
    return vector;
  }
}
