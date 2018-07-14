package fastmath.matfile;

import java.util.Iterator;

public class MxInt32 extends MxClass
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public final static int mxINT32_CLASS = 12; // 32-bit, signed integer

  @Override
  public Type
         getArrayType()
  {
    return Type.INT32;
  }

  private MiElement realPart;

  private MiElement imagPart;

  /**
   * Construct an Int32 array, realParts MiInt32, no Imag parts
   */
  public MxInt32(MiInt32 rp)
  {
    super(null);
    realPart = rp;
    imagPart = null;
  }

  /**
   * Construct an Int32 array, realParts MiInt32, no Imag parts
   */
  public MxInt32(int[] values)
  {
    super(null);
    realPart = new MiInt32(values);
    imagPart = null;
  }

  public MxInt32(Iterator<MiElement> iter, boolean isComplex)
  {
    super(null);
    realPart = iter.next();
    imagPart = isComplex ? iter.next() : null;
  }

  @Override
  public long
         numBytes(long pos)
  {
    long startPos = pos;

    pos += realPart.totalSize(pos);

    if (imagPart != null)
    {
      pos += imagPart.totalSize(pos);
    }

    return pos - startPos;
  }

  public Object
         toObject()
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
