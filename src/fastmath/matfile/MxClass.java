package fastmath.matfile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

import fastmath.AbstractBufferedObject;
import fastmath.AbstractMatrix;

public abstract class MxClass extends AbstractBufferedObject
{

  protected MxClass()
  {
    super( null );
  }

  protected MxClass(ByteBuffer buffer)
  {
    super( buffer );
  }

  public static enum Type
  {
   CELL(1),
   STRUCT(2), // Structure
   CHAR(4), // Character array
   DOUBLE(6), // Double precision array
   INT32(12); // 32-bit, signed integer

    private final int type;

    private Type(int type)
    {
      this.type = type;
    }

    public int getType()
    {
      return type;
    }

    public static Type valueOf( int l )
    {
      switch( l )
      {
      case 1:
        return CELL;
      case 2:
        return STRUCT;
      case 4:
        return CHAR;
      case 6:
        return DOUBLE;
      case 12:
        return INT32;
      default:
        throw new IllegalArgumentException( "Invalid value(" + l + ")" );
      }
    }

  };

  public abstract Type getArrayType();

  /**
   * The sum of the totalSize of each element in this Array
   * 
   * @param pos
   *          TODO!
   * 
   * @return TODO!
   */
  @Override
  public abstract long numBytes( long pos );

  /**
   * 
   * @param dimensionsArray
   * @return
   */
  public <M extends AbstractMatrix> M toDenseDoubleMatrix( MiInt32 dimensionsArray )
  {
    throw new UnsupportedOperationException();
  }

  public void write( SeekableByteChannel channel ) throws IOException
  {
    writeBuffer( channel );
  }
}
