package fastmath.matfile;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.SeekableByteChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import fastmath.AbstractBufferedObject;
import fastmath.BufferUtils;
import fastmath.DoubleColMatrix;
import fastmath.DoubleMatrix;

/**
 * The MATLAB Version 5 MAT-file format supports all the MATLAB V5 array types,
 * including multidimensional numeric arrays, character arrays, sparse arrays,
 * cell arrays, structures, and objects.
 */
@SuppressWarnings("resource")
public class MatFile implements Iterable<MiElement>
{
  public static void
         main(String args[]) throws IOException
  {
    MatFile matFile = new MatFile(new File(args[0]));
    out.println("Parsing " + matFile);
    matFile.forEach(x -> out.println(x));
  }

  @Override
  public String
         toString()
  {
    return String.format("MatFile[file=%s]", file);
  }

  protected static final int HEADER_OFFSET = 128;

  public static class Header extends AbstractBufferedObject
  {
    private static final long serialVersionUID = 1L;

    public static final int HEADER_TEXT_LEN = 116;

    public static final int SUBSYS_SPECIFIC_HEADER_LEN = 8;

    // text + 2 shorts = 128 bytes
    public static final int HEADER_LEN = HEADER_TEXT_LEN + SUBSYS_SPECIFIC_HEADER_LEN + 4;

    /**
     * Allocate HEADER_LEN=128 bytes
     */
    public Header()
    {
      super(HEADER_LEN);
    }

    /**
     * Assign shortData and charBUffer
     * 
     */
    public Header(ByteBuffer byteBuffer)
    {
      super(byteBuffer);
    }

    /**
     * Allocate 128 bytes and assigned header string
     * 
     * @param buffer
     *          existing buffer to use
     * @param header
     *          text to assign to preceeding buffer param
     */
    public Header(ByteBuffer buffer, String header)
    {
      this(buffer);
      BufferUtils.copy(header, buffer);
    }

    /**
     * Construct new matfile, writing file header
     * 
     * @param filechannel
     *          fileChannel positioned at byte 0
     * 
     * @param header
     *          first 124 bytes of string are written to header, padded with
     *          whitespace
     * @throws IOException
     */
    public Header(FileChannel filechannel, String header) throws IOException
    {
      this(header);
      filechannel.write(buffer);
    }

    /**
     * Construct new matfile, writing file header
     * 
     * @param filechannel
     *          fileChannel positioned at byte 0
     * 
     * @param header
     *          first 124 bytes of string are written to header, padded with
     *          whitespace
     * @throws IOException
     */
    public Header(FileChannel filechannel) throws IOException
    {
      this();
      filechannel.read(buffer);
    }

    /**
     * Allocate 128 bytes and assigned header string, little-endian storage
     * 
     * @param header
     */
    public Header(String header)
    {
      this();
      int i = 0;
      for (; i < header.length() && i < HEADER_TEXT_LEN; i++)
      {
        buffer.put((byte) header.charAt(i));
      }
      for (; i < HEADER_TEXT_LEN; i++)
      {
        buffer.put((byte) ' ');
      }
      for (i = 0; i < SUBSYS_SPECIFIC_HEADER_LEN; i++)
      {
        buffer.put((byte) 0);
      }

      buffer.putShort(VERSION);
      buffer.putShort(LITTLE_ENDIAN);
      buffer.flip();
    }

    @Override
    public ByteBuffer
           getBuffer()
    {
      return buffer;
    }

  }

  public static final short BIG_ENDIAN = 0x494D; // IM

  public static final short LITTLE_ENDIAN = 0x4D49; // MI

  public static final short READ_MODE = 1;

  private static final int SIXTY_FOUR_BITS = 8;

  public static final short VERSION = 0x0100;

  public static final short WRITE_MODE = 2;

  /**
   * Pads dataOutput to the nearest 8-byte boundary
   * 
   * @param channel
   *          TODO!
   * @param pos
   *          TODO!
   * 
   * @return new buffer position
   * 
   * @throws IOException
   *           TODO!
   */
  public static long
         pad(SeekableByteChannel channel) throws IOException
  {
    long skip = SIXTY_FOUR_BITS - (channel.position() % SIXTY_FOUR_BITS);

    if (skip < SIXTY_FOUR_BITS)
    {
      channel.write(ByteBuffer.allocateDirect((int) skip));

    }

    return channel.position();
  }

  /**
   * Pad up to the next 8 byte boundary
   * 
   * @param pos
   *          file offset
   * 
   * @return pos + ( ( (int) ( pos ) % 8 > 0 ) ? ( 8 - (int) ( pos ) % 8 ) : 0 )
   */
  public static long
         pad(long pos)
  {
    int skip = (int) (pos) % 8;

    return pos + ((skip > 0) ? (8 - skip) : 0);
  }

  /**
   * Contains the two characters, M and I, written to the MAT-file in this order,
   * as a 16-bit value. If, when read from the MAT-file as a 16-bit value, the
   * characters appear in reversed order (IM rather than MI), it indicates that
   * the program reading the MAT-file must perform byte-swapping to interpret the
   * data in the MAT-file correctly.
   */
  // private short endianIndicator;
  private FileChannel fileChannel;

  /**
   * The first 124 bytes of the header can contain text data in human-readable
   * form. This text typically provides information that describes how the
   * MAT-file was created. For example, MAT-files created by MATLAB include the
   * following information in their headers: MATLAB version Platform on which the
   * file was created Date and time the file was created
   */
  private Header header;

  private final File file;

  private final boolean readOnly;

  /**
   * Open a file for READING
   * 
   * @param fileChannel
   * @throws possibly
   *           a RuntimeException wrapping an IOException
   */
  public MatFile(File file)
  {
    try
    {
      RandomAccessFile raf = new RandomAccessFile(file, "r");
      this.file = file;
      this.setFileChannel(raf.getChannel());
      this.header = new Header(this.getFileChannel());
      readOnly = true;
    }
    catch (IOException e)
    {
      throw new RuntimeException(file.getAbsolutePath() + ": " + e.getMessage(), e);
    }

  }

  /**
   * Open a file for WRITING, writes the 128 byte header, if exists, it is renamed
   * to the same filename with ".bak" appended and if the JVM exits before
   * this{@link #close()} is called, the original file will be restored by
   * {@link Runtime#addShutdownHook(Thread)}
   * 
   * @param fileChannel
   * @throws IOException
   */
  public MatFile(File file, String headerText) throws IOException
  {
    RandomAccessFile raf;
    {
      if (file.exists())
      {
        backupFile = new File(file.getAbsolutePath() + ".bak");
        file.renameTo(backupFile);
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
          @Override
          public void
                 run()
          {
            if (backupFile != null)
            {
              err.println("Restoring " + backupFile + " to " + file);
              file.delete();
              backupFile.renameTo(file);
            }
          }
        });

      }
      raf = new RandomAccessFile(file, "rw");
      this.file = file;
      raf.setLength(0);
      this.setFileChannel(raf.getChannel());
      this.header = new Header(this.getFileChannel(), headerText);
      readOnly = false;
    }

  }

  public MatFile(String filename)
  {
    this(new File(filename));
  }

  /**
   * Closes fileChannel, deletes the empty file if nothing was written while it
   * was open for writing
   * 
   * @throws IOException
   * 
   * @return false file was deleted because it was empty, true if it was
   *         successfully closed
   */
  public boolean
         close()
  {
    try
    {
      getFileChannel().close();
      backupFile = null;
    }
    catch (IOException e)
    {
      throw new RuntimeException(file.getName() + ": " + e.getMessage(), e);
    }
    return true;

  }

  public FileChannel
         getFileChannel()
  {
    return fileChannel;
  }

  public Header
         getHeader()
  {
    return header;
  }

  /**
   * @return an {@link MiElement} {@link Iterator}
   */
  @Override
  public MiIterator
         iterator()
  {
    try
    {
      long fileSize = getFileChannel().size();
      if (fileSize < Header.HEADER_LEN)
      {
        throw new IOException(file.getName() + " is not a .mat file");
      }
      MappedByteBuffer buffer = getFileChannel().map(MapMode.READ_ONLY, HEADER_OFFSET, fileSize - HEADER_OFFSET);
      return new MiIterator(this, buffer);
    }
    catch (Exception e)
    {
      throw new IllegalArgumentException("unable to read file header: " + e.getMessage(), e);
    }
  }

  /**
   * Reads all the variables out of the file
   * 
   * @return a map of variable names to MiMatrix objects
   */
  public Map<String, MiMatrix>
         readVariables()
  {
    HashMap<String, MiMatrix> vars = new HashMap<String, MiMatrix>();

    for (AbstractBufferedObject obj : this)
    {
      if (obj instanceof MiMatrix)
      {
        MiMatrix matrix = (MiMatrix) obj;

        vars.put(matrix.getName(), matrix);
      }
      else
      {
        throw new UnsupportedOperationException("Invalid element " + obj.getClass().getName());
      }
    }

    return vars;
  }

  /**
   * Like this{@link #readVariables(String)} but filters variable names against a
   * regular expression
   * 
   * @param regularExpression
   * @return
   */
  public Map<String, MiMatrix>
         readVariables(String regularExpression)
  {
    Pattern pattern = Pattern.compile(regularExpression);

    HashMap<String, MiMatrix> vars = new HashMap<String, MiMatrix>();

    for (AbstractBufferedObject obj : this)
    {
      if (obj instanceof MiMatrix)
      {
        MiMatrix matrix = (MiMatrix) obj;

        String variableName = matrix.getName();
        if (pattern.matcher(variableName).matches())
        {
          vars.put(variableName, matrix);
        }
      }
      else
      {
        // this never occurs if the file follows the spec
        throw new UnsupportedOperationException("Invalid element " + obj.getClass().getName());
      }
    }

    return vars;
  }

  public void
         setFileChannel(FileChannel fileChannel)
  {
    this.fileChannel = fileChannel;
  }

  public void
         setHeader(Header header)
  {
    this.header = header;
  }

  private final TreeSet<String> writtenNames = new TreeSet<String>();

  /**
   * Output an MiWritableElement (Matrix or Vector, etc) to this
   * {@link #fileChannel} and adds the elements {@link MiMatrix#getName()}
   * 
   * @param element
   * @param name
   * @throws RuntimeException
   *           wrapping an IOException if thrown
   * @return the name of the matrix written
   * @throws IOException
   */
  public synchronized void
         write(MiElement element) throws IOException
  {
    if (element == null)
    {
      return;
    }
    element.write(fileChannel);
    elementsWritten++;
  }

  private int elementsWritten = 0;

  private File backupFile;

  public static void
         write(String filename,
               MiElement... writables) throws IOException
  {
    File file = new File(filename);
    write(file, writables);
  }

  public static void
         write(File file,
               MiElement... writables) throws IOException
  {
    MatFile matFile = new MatFile(file, MatFile.class.getName());
    for (MiElement writable : writables)
    {
      matFile.write(writable);
    }
    matFile.close();
  }

  public TreeSet<String>
         getWrittenNames()
  {
    return writtenNames;
  }

  public File
         getFile()
  {
    return file;
  }

  /**
   * loads a single DoubleColMatrix from a {@link MatFile}
   * 
   * @param filename
   * @param variableName
   * @return null if not found
   * @throws IOException
   */
  public static DoubleColMatrix
         loadMatrix(String filename,
                    String variableName) throws IOException
  {
    MatFile matFile = new MatFile(new File(filename));
    try
    {
      MiMatrix ρ = matFile.readVariables(variableName).get(variableName);
      return ρ != null ? ρ.toDenseDoubleMatrix() : null;
    }
    finally
    {
      matFile.close();
    }
  }

  @SuppressWarnings("unchecked")
  public <S extends MiElement>
         Stream<S>
         stream()
  {
    return (Stream<S>) StreamSupport.stream(spliterator(), false);
  }

  /**
   * Like this{@link #stream()} but prefilters non-matrix elements
   * 
   * @return a {@link Stream} of {@link DoubleMatrix}es
   */
  public Stream<DoubleMatrix>
         matrixStream()
  {
    Stream<MiMatrix> variables = stream();
    variables = variables.filter(x -> x instanceof MiMatrix);
    Stream<DoubleMatrix> matrices = variables.map(x -> x.toDenseDoubleMatrix());
    return matrices;
  }

  /**
   * 
   * @return the number of times this {@link #write(String, MiWritableElement...)}
   *         was called
   * 
   */
  public int
         getElementsWritten()
  {
    return elementsWritten;
  }

  /**
   * a map version of this{@link #loadMatrices()}
   * 
   * @return
   * @throws IOException
   */
  public Map<String, DoubleColMatrix>
         matrixMap() throws IOException
  {
    Map<String, DoubleColMatrix> map = new HashMap<>();
    loadMatrices().forEach(matrix -> map.put(matrix.getName(), matrix));
    return map;
  }

  /**
   * a map version of this{@link #loadMatrices()}
   * 
   * @return
   * @throws IOException
   */
  public Map<String, DoubleColMatrix>
         matrixMap(Predicate<String> predicate)
  {
    TreeMap<String, DoubleColMatrix> map = new TreeMap<>();
    loadMatrices().forEach(matrix -> {
      if (predicate.test(matrix.getName()))
      {
        map.put(matrix.getName(), matrix);
      }
    });
    return map;
  }

  public static List<DoubleColMatrix>
         loadMatrices(String filename) throws IOException
  {
    return new MatFile(new File(filename)).loadMatrices();
  }

  public List<DoubleColMatrix>
         loadMatrices()
  {
    try
    {
      Stream<DoubleColMatrix> matrixStream = readVariables().values().stream().map(miMatrix -> miMatrix.toDenseDoubleMatrix());
      return matrixStream.collect(Collectors.toList());
    }
    finally
    {
      close();
    }
  }
}
