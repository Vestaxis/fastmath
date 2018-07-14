package fastmath.matfile;

import java.util.Iterator;

public class MxChar extends MxClass
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public final static int mxCHAR_CLASS = 4; // Character array

  @Override
  public Type getArrayType()
  {
    return Type.CHAR;
  }

  private MiElement realPart;

  private MiElement imagPart;

  public MxChar(Iterator<MiElement> iter, boolean isComplex)
  {
    super( null );
    realPart = iter.next();
    imagPart = isComplex ? (MiElement) iter.next() : null;
  }

  public MxChar(String values)
  {
    super( null );
    realPart = new MiUInt16( values );
    imagPart = null;
  }

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

  public Object toObject()
  {
    throw new UnsupportedOperationException();
  }

  // public long write( ByteBuffer buffer, long pos )
  // throws IOException
  // {
  // pos = realPart.write( buffer, pos );
  // if ( imagPart != null )
  // {
  // pos = imagPart.write( buffer, pos );
  // }
  // return pos;
  // }

}
