package fastmath.matfile;

import java.nio.ByteBuffer;

import fastmath.Vector;

public class MiDouble extends MiElement
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public ByteBuffer getBuffer()
  {
    return vector.getBuffer();
  }

  public final static int miDOUBLE = 9;

  public static final int BYTES = Double.SIZE / Byte.SIZE;

  final Vector vector;

  /**
   * Creates a new MiDouble object.
   * 
   * @param slice
   *          TODO!
   */
  public MiDouble(ByteBuffer slice)
  {
    super( slice );
    vector = new Vector( slice );
  }

  public MiDouble(Vector vector)
  {
    super( vector.getBuffer() );
    this.vector = vector;
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public int getDataType()
  {
    return miDOUBLE;
  }

  /**
   * Returns the underlying Vector
   * 
   * @return
   */
  public Vector getVector()
  {
    return vector;
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  public double[] toArray()
  {
    return vector.toDoubleArray();
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer( getClass().getSimpleName() + "[" + vector.size() + "]={" );

    for ( int i = 0; i < vector.size(); i++ )
    {
      if ( i > 0 )
      {
        sb.append( "," );
      }

      sb.append( vector.get( i ) );
    }

    sb.append( "}" );

    return sb.toString();
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  public Object toObject()
  {
    throw new UnsupportedOperationException();
  }

}
