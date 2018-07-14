package fastmath.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class BufferTools
{

  public static BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );

  public static void reverseBytes( byte[] b, int i, int j )
  {
    throw new UnsupportedOperationException( "TODO" );

  }

  public static void fastChannelCopy( final ReadableByteChannel src, final WritableByteChannel dest ) throws IOException
  {
    final ByteBuffer buffer = ByteBuffer.allocateDirect( 16 * 1024 );
    while (src.read( buffer ) != -1)
    {
      // prepare the buffer to be drained
      buffer.flip();
      // write to the channel, may block
      dest.write( buffer );
      // If partial transfer, shift remainder down
      // If buffer is empty, same as doing clear()
      buffer.compact();
    }
    // EOF will leave buffer in fill state
    buffer.flip();
    // make sure the buffer is fully drained.
    while (buffer.hasRemaining())
    {
      dest.write( buffer );
    }
  }

  public static void fastChannelCopy( InputStream inputStream, OutputStream outputStream ) throws IOException
  {
    final ReadableByteChannel inputChannel = Channels.newChannel( inputStream );
    final WritableByteChannel outputChannel = Channels.newChannel( outputStream );
    try
    {
      BufferTools.fastChannelCopy( inputChannel, outputChannel );
    }
    finally
    {
      inputChannel.close();
      outputChannel.close();
    }
  }

}
