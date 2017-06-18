package fastmath.matfile;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.util.Iterator;

import fastmath.AbstractBufferedObject;
import fastmath.AbstractMatrix;
import fastmath.DoubleColMatrix;

public class MxDouble extends MxClass
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @return realPart.limit()+imagPart.limit()
   */
  @Override
  public int limit()
  {
    int lim = 0;

    if ( realPart != null )
    {
      lim += realPart.limit();
    }

    if ( imagPart != null )
    {
      lim += imagPart.limit();
    }

    return lim;
  }

  public final static int mxDOUBLE_CLASS = 6; // Double precision array

  @Override
  public Type getArrayType()
  {
    return Type.DOUBLE;
  }

  private final MiElement realPart;

  private final MiElement imagPart;

  public AbstractBufferedObject getRealPart()
  {
    return realPart;
  }

  public AbstractBufferedObject getImagPart()
  {
    return imagPart;
  }

  public MxDouble(MiDouble realPart, MiInt32 dimensions)
  {
    super( null );
    this.realPart = realPart;
    this.imagPart = null;
  }

  public MxDouble(Iterator<MiElement> iter, boolean isComplex, MiInt32 dimensions)
  {
    super( null );
    realPart = iter.next();
    imagPart = isComplex ? iter.next() : null;
  }

  /**
   * Return the size including header bytes
   */
  @Override
  public long numBytes( long pos )
  {
    long startPos = pos;

    pos += realPart.totalSize( pos );

    if ( imagPart != null )
    {
      pos += imagPart.totalSize( pos );
    }

    return pos - startPos;
  }

  @Override
  public String toString()
  {
    return "mxDouble{ realPart = " + realPart + " imagPart = " + imagPart + " }";
  }

  @Override
  public void write( SeekableByteChannel channel ) throws IOException
  {
    if ( realPart != null )
    {
      realPart.write( channel );
    }
    if ( imagPart != null )
    {
      imagPart.write( channel );
    }
  }

  @Override
  public AbstractMatrix toDenseDoubleMatrix( MiInt32 dimensions )
  {
    if ( !( realPart instanceof MiDouble ) )
    {
      throw new UnsupportedOperationException( "realPart.class = " + realPart.getClass().getName() );
    }

    if ( imagPart != null )
    {
      if ( !( imagPart instanceof MiDouble ) )
      {
        throw new UnsupportedOperationException( "imagPart.class = " + imagPart.getClass().getName() );
      }

      throw new UnsupportedOperationException( "Complex types not supported yet" );
    }

    if ( dimensions.getSize() != 2 )
    {
      throw new UnsupportedOperationException( "Number of dimensions, " + dimensions.getSize() + " != 2" );
    }

    return new DoubleColMatrix( ( (MiDouble) realPart ).getVector().getBuffer().order( ByteOrder.nativeOrder() ),
                                dimensions.elementAt( 0 ),
                                dimensions.elementAt( 1 ) );
  }
}
