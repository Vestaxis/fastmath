package fastmath.matfile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Iterator;

import fastmath.AbstractBufferedObject;
import fastmath.AbstractMatrix;

public class MiMatrix extends MiElement
{
  public final static int miMATRIX = 14;

  public final static int mxCELL_CLASS = 1; // Cell array

  public final static int mxOBJECT_CLASS = 3; // Object

  public final static int mxSPARSE_CLASS = 5; // Sparse array

  public final static int mxSINGLE_CLASS = 7; // Single precision array

  public final static int mxINT8_CLASS = 8; // 8-bit, signed integer

  public final static int mxUINT8_CLASS = 9; // 8-bit, unsigned integer

  public final static int mxINT16_CLASS = 10; // 16-bit, signed integer

  public final static int mxUINT16_CLASS = 11; // 16-bit, unsigned integer

  public final static int mxUINT32_CLASS = 13; // 32-bit unsigned, integer

  public static final int FLAG_COMPLEX = 1 << 11;

  public static final int FLAG_GLOBAL = 1 << 10;

  public static final int FLAG_LOGICAL = 1 << 9;

  private final MiUInt32 arrayFlags;

  private MiInt32 dimensionsArray;

  private final MiInt8 arrayName;

  private MxClass value;

  private long streamStart;

  private long streamStop;

  private String name;

  /**
   * Creates a new MiMatrix object.
   * 
   * @param array
   *          TODO!
   * @param dimensions
   *          TODO!
   * @param name
   *          TODO!
   */
  public MiMatrix(MxClass array, MiInt32 dimensions, String name)
  {
    super( null );
    arrayFlags = new MiUInt32( 2 );
    int arrayType = array.getArrayType().getType();
    arrayFlags.setElementAt( 0, arrayType );
    arrayFlags.setElementAt( 1, 0 );
    dimensionsArray = dimensions;
    arrayName = new MiInt8( name );
    this.name = name;
    value = array;
  }

  /**
   * Creates a new MiMatrix object.
   * 
   * @param values
   *          TODO!
   * @param name
   *          TODO!
   */
  public MiMatrix(String values, String name)
  {
    super( null );
    value = new MxChar( values );

    arrayFlags = new MiUInt32( 2 );
    arrayFlags.setElementAt( 0, value.getArrayType().getType() );
    arrayFlags.setElementAt( 1, 0 );

    dimensionsArray = new MiInt32( new int[]
    {
      1,
      values.length()
    } );

    arrayName = new MiInt8( name );
  }

  /**
   * Create a Matrix of MiInt32 values
   * 
   * @param values
   *          TODO!
   * @param name
   *          TODO!
   */
  public MiMatrix(int values[], String name)
  {
    super( null );
    value = new MxInt32( values );

    arrayFlags = new MiUInt32( 2 );
    arrayFlags.setElementAt( 0, value.getArrayType().getType() );
    arrayFlags.setElementAt( 1, 0 );

    dimensionsArray = new MiInt32( new int[]
    {
      values.length,
      1
    } );

    arrayName = new MiInt8( name );
  }

  public MiMatrix(MatFile matFile, ByteBuffer buffer, boolean swapped)
  {
    super( buffer );
    MiIterator iter = new MiIterator( matFile, buffer );

    arrayFlags = iter.nextElement();

    dimensionsArray = iter.nextElement();

    arrayName = iter.nextElement();
    name = arrayName.asString();

    readValue( iter );
  }

  protected void readValue( Iterator<MiElement> iter )
  {
    int arrayClass = getArrayClass();
    switch( arrayClass )
    {
    case MxInt32.mxINT32_CLASS:
      value = new MxInt32( iter, isComplex() );

      break;

    case MxChar.mxCHAR_CLASS:
      value = new MxChar( iter, isComplex() );

      break;

    case MxDouble.mxDOUBLE_CLASS:
      value = new MxDouble( iter, isComplex(), dimensionsArray );

      break;

    case MxStruct.mxSTRUCT_CLASS:
      value = new MxStruct( iter );

      break;

    default:
      throw new UnsupportedOperationException( "Unsupported type of arrayClass " + MxClass.Type.valueOf( arrayClass ) );
    }
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public int getDataType()
  {
    return miMATRIX;
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  public final boolean isComplex()
  {
    return ( getFlags() & FLAG_COMPLEX ) == FLAG_COMPLEX;
  }

  /**
   * @return arrayFlags[1]
   */
  public long getFlags()
  {
    return (int) ( ( arrayFlags.elementAt( 0 ) >> 16 ) & 0xFF );
  }

  /**
   * arrayFlags[1] = flags
   * 
   * @param flags
   */
  public void setFlags( long flags )
  {
    arrayFlags.setElementAt( 1, flags );
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  public boolean isGlobal()
  {
    return ( getFlags() & FLAG_GLOBAL ) == FLAG_GLOBAL;
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  public boolean isLogical()
  {
    return ( getFlags() & FLAG_LOGICAL ) == FLAG_LOGICAL;
  }

  public final int getArrayClass()
  {
    return (int) ( ( arrayFlags.elementAt( 0 ) >> 24 ) & 0xFF );
  }

  public String getName()
  {
    return name;
    // return arrayName.asString();
  }

  public AbstractBufferedObject getMxClass()
  {
    return value;
  }

  /**
   * TODO!
   * 
   * @return TODO!
   */
  @Override
  public String toString()
  {
    return "MiMatrix\"" + getName() + "\"" + "[" + dimensionsArray + "]\"" + arrayName + "\"{" + value + "}";
  }

  public <M extends AbstractMatrix> M toDenseDoubleMatrix()
  {
    M denseDoubleMatrix = value.toDenseDoubleMatrix( dimensionsArray );
    denseDoubleMatrix.setName( getName() );
    return denseDoubleMatrix;
  }

  /**
   * TODO!4
   * 
   * @param pos
   *          TODO!
   * 
   * @return TODO!
   */
  @Override
  public final long numBytes( long pos )
  {
    long startPos = pos;

    long arrayFlagSize = arrayFlags.totalSize( pos );

    // For data elements representing MATLAB arrays, (type
    // miMATRIX), the value of the Number of Bytes field includes padding bytes
    // in the total. For all other MAT-file data types, the value of the Number
    // of Bytes field does not include padding bytes.
    pos += arrayFlagSize;
    pos += dimensionsArray.totalSize( pos );
    pos += arrayName.totalSize( pos );
    if ( value != null )
    {
      pos += value.numBytes( pos );
    }
    else
    {
      // just use full header
      pos += 8;
      pos += ( streamStop - streamStart );
    }

    long totalLen = pos - startPos;

    return totalLen;
  }

  @Override
  public void write( SeekableByteChannel channel ) throws IOException
  {
    synchronized( channel )
    {
      writeHeader( channel );
      arrayFlags.write( channel );
      dimensionsArray.write( channel );
      arrayName.write( channel );
      MatFile.pad( channel );
      value.write( channel );
      MatFile.pad( channel ); // MiMatrix elements are padded
    }
  }

  /**
   * @return Returns the dimensionsArray.
   */
  public MiInt32 getDimensionsArray()
  {
    return dimensionsArray;
  }

}
