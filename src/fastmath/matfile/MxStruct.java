package fastmath.matfile;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class MxStruct extends MxClass implements NamedWritable
{

  public final static int mxSTRUCT_CLASS = 2; // Structure

  private MiInt32 fieldNameLengthArray;

  private final List<String> fieldNames = new ArrayList<String>();

  private MiInt8 fieldNamesArray;

  private final TreeMap<String, MiElement> fields = new TreeMap<>();

  private String name;

  private boolean needsRefresh;

  public MxStruct()
  {
    super( null );
  }

  @SafeVarargs
  public <W extends MiElement> MxStruct(String name, W... elements)
  {
    super( null );
    setName( name );
    Arrays.asList( elements ).forEach( element -> addField( element.getName(), element ) );
  }

  protected MxStruct(Iterator<MiElement> iter)
  {
    super( null );
    MiElement element = iter.next();
    fieldNameLengthArray = (MiInt32) element;
    assert fieldNameLengthArray.getSize() != 1 : "Field Name Length should be a 1 Int32 value. See Page 25 of MAT-File Format Documentation";

    int fieldNameLength = fieldNameLengthArray.elementAt( 0 );

    fieldNamesArray = (MiInt8) element;
    int numFields = (int) fieldNamesArray.numBytes( 0 ) / fieldNameLength; // use
    if ( ( fieldNamesArray.numBytes( 0 ) % fieldNameLength ) != 0 )
    {
      throw new UnsupportedOperationException( "fieldNames length invalid" );
    }

    for ( int i = 0; i < numFields; i++ )
    {
      String fieldName = fieldNamesArray.asString().substring( i * fieldNameLength, ( ( i + 1 ) * fieldNameLength ) ).trim();
      fieldNames.add( fieldName );
      fields.put( fieldName, iter.next() );
    }

    needsRefresh = false;
  }

  @Override
  public Type getArrayType()
  {
    return Type.STRUCT;
  }

  public String getName()
  {
    return name;
  }

  /**
   * 
   */
  @Override
  public long numBytes( long pos )
  {
    long startPos = pos;

    if ( needsRefresh )
    {
      refresh();
    }

    long a = fieldNameLengthArray.totalSize( pos );
    pos += a;
    a = fieldNamesArray.totalSize( pos );
    pos += a;
    for ( MiElement element : fields.values() )
    {
      a = element.totalSize( pos );
      pos += a;
    }

    return ( pos - startPos );
  }

  public void put( String key, MiElement value )
  {
    if ( !fields.containsKey( key ) )
    {
      fieldNames.add( key );
    }

    fields.put( key, value );

    needsRefresh = true;
  }

  private void refresh()
  {
    int maxFieldNameSize = fields.keySet().stream().mapToInt( name -> name.length() ).max().orElse( 0 );

    fieldNameLengthArray = new MiInt32( 1 );
    fieldNameLengthArray.setElementAt( 0, maxFieldNameSize );

    StringBuffer fieldNameBuffer = new StringBuffer();

    for ( String fieldName : fieldNames )
    {
      fieldNameBuffer.append( fieldName );
      int fieldNameLength = fieldName.length();
      if ( fieldNameLength < maxFieldNameSize )
      {
        for ( int i = 0; i < maxFieldNameSize - fieldNameLength; i++ )
        {
          fieldNameBuffer.append( "\000" );
        }
      }
    }

    fieldNamesArray = new MiInt8( fieldNameBuffer.toString() );

    needsRefresh = false;
  }

  /**
   * adds an entry to this{@link #fields} and sets this{@link #needsRefresh} to
   * true
   * 
   * @param name
   * @param value
   */
  public void addField( String name, MiElement value )
  {
    fields.put( name, value );
    needsRefresh = true;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  @Override
  public String toString()
  {
    return "mxStruct " + fields;
  }

  @Override
  public void write( SeekableByteChannel channel ) throws IOException
  {
    if ( needsRefresh )
    {
      refresh();
    }

    fieldNameLengthArray.write( channel );

    fieldNamesArray.write( channel );

    for ( String fieldName : fieldNames )
    {
      MiElement element = fields.get( fieldName );
      element.write( channel );
    }
  }

}
