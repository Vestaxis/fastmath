package fastmath.fx;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.err;
import static java.lang.System.exit;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import com.sleepycat.je.tree.Tree;

import fastmath.ExponentialMovingAverage;
import fastmath.Fastmath;
import fastmath.Pair;
import fastmath.Vector;
import fastmath.arb.Complex;
import fastmath.arb.Real;

public class ZIterator
{

  public static final int digits = 8;
  private static final int maxIters = 5000;

  static class Chunker
  {
    int max;

    int current = 0;

    static final int chunkSize = 1000;

    Chunker(int max)
    {
      this.max = max;
    }

    synchronized Pair<Integer, Integer> getChunk()
    {
      Pair<Integer, Integer> range = new Pair<>();
      range.left = current;
      range.right = min( current + chunkSize, max - 1 );
      current = range.right + 1;
      if ( range.left > range.right )
      {
        return null;
      }

      return range;
    }
  }

  static TreeMap<Integer, Complex> batchEndRoots = new TreeMap<>();

  public static void main( String[] args ) throws FileNotFoundException, IOException, InterruptedException
  {

    Vector ozeros = readVectorFromCSV( "zeros6" );
    Chunker chunker = new Chunker( ozeros.size() );
    chunker.current = 1;
    // chunker.current = 1;
    totalVerified.set( chunker.current );

    // boolean thing = true;
    // while (thing)
    // {
    // Pair<Integer, Integer> chunk = chunker.getChunk();
    // if ( chunk == null )
    // {
    // exit( 1 );
    // }
    // else
    // {
    // out.println( chunk );
    // }
    // }

    final Vector zeros = ozeros;
    out.println( "loaded " + zeros.size() + " zeros from file" );

    threadCount = Runtime.getRuntime().availableProcessors();

    openOutputFile();
    // verifyRange( zeros, 1, 15000, null );

    for ( int t = 0; t < threadCount; t++ )
    {
      final int tn = t;
      // int batchStart = max( 1, t * batchSize );
      // int batchStop = t == threadCount - 1 ? zeros.size() - 1 : ( t + 1 ) *
      // batchSize - 1;
      Thread thread = new Thread()
      {

        @Override
        public void run()
        {
          while (true)
          {

            Pair<Integer, Integer> chunk = chunker.getChunk();
            if ( chunk == null )
            {
              out.println( "Thread " + tn + " finished!! " );
              break;
            }
            int batchStart = chunk.left;
            int batchStop = chunk.right;
            out.println( "thread " + tn + " range " + batchStart + " " + batchStop );
            verifyRange( zeros, batchStart, batchStop, foundRoots );
          }
        }
      };
      thread.setName( "batch" + t );
      thread.start();

    }
    while (true)
    {
      Thread.sleep( 1000000 );
    }

    // int start = 41400;
    // int stop = zeros.size();
    // // int start = 39324;
    // verifyRange( zeros, start, stop );

  }

  public static void openOutputFile() throws FileNotFoundException
  {
    File outputFile = new File( "zeros" );
    if ( outputFile.exists() )
    {
      outputFile.delete();
    }
    outputStream = new FileOutputStream( outputFile, true );
    output = new PrintWriter( outputStream );
  }

  static AtomicInteger totalVerified = new AtomicInteger();

  static ExponentialMovingAverage ewma = new ExponentialMovingAverage( 0.01, 1.0 / 15 );
  private static int removeLastCount = 20;

  static public void verifyRange( final Vector zeros, int start, int stop, ArrayList<Real> removeList )
  {
    AtomicInteger progress = new AtomicInteger( 0 );

    Complex lastZero = new Complex();
    // IntStream.rangeClosed( start, zeros.size() ).forEach( n -> {
    IntConsumer ahh = n -> {
      try
      {
        // Arrays.stream( notyetConvergingPoints ).forEach( n -> {
        long startTime = currentTimeMillis();
        if ( !testConvergence( zeros, n, lastZero, false, start, null ) )
        {
          // synchronized( divergentPoints )
          {
            // divergentPoints.add( n );
            err.println( "BAH!!! n=" + n );
            exit( 1 );
          }

        }
        double dt = ( currentTimeMillis() - startTime ) / 1000.0;

        double avgdt;
        // synchronized( ewma )
        {
          avgdt = ewma.average( dt ) / threadCount;
        }
        int cnt = progress.incrementAndGet();
        int totalCnt = totalVerified.incrementAndGet();
        // out.println( "cnt=" + cnt );
        double minutesLeft = ( ( avgdt * ( zeros.size() - totalCnt ) ) / 60.0 );
        double hoursLeft = minutesLeft / 60.0;
        if ( ( cnt % 50 ) == 49 )
        {
          out.format( "%s cnt=%d totalCnt=%d rate=%f/sec eta=%f hours\n", Thread.currentThread().getName(), cnt + start - 1, totalCnt, 1.0 / avgdt, hoursLeft );
          // out.println( "cnt=" + cnt + " rate=" + ( 1.0 / avgdt ) + "/sec
          // eta=" + hoursLeft + " hours, convergentPoints=" + convergentPercent
          // + "%" );
        }
      }
      catch( Throwable t )
      {
        t.printStackTrace( System.err );
        System.err.flush();
        System.exit( 1 );
      }

      synchronized( batchEndRoots )
      {
        batchEndRoots.put( stop, lastZero );
      }

    };
    // ahh.accept( 1313198 );
    IntStream.rangeClosed( start, stop ).forEach( ahh );
  }

  public static double getZero( final Vector zeros, int i )
  {
    return zeros.get( i - 1 );
  }

  static PrintWriter output;
  public static FileOutputStream outputStream;

  public static boolean testConvergence( Vector zeros, int n, Complex lastZero, boolean print, int start, List<Real> removeList )
  {
    // double x0d = Fastmath.instance.nthApproximationZeroD( n );
    double x0d = new Real().pi().multiply( 2 ).multiply( new Real( n - 11.0 / 8.0 ) ).divide( new Real( ( n - 11.0 / 8.0 ) / exp( 1.0 ) ).W() ).sixtyFourBitValue();
    // double x0d = n == 1 ? Fastmath.instance.nthApproximationZeroD( n )
    // :Fastmath.instance.nthApproximationZeroD( n - 1 );
    // double x0d = ( getZero( zeros, n - 1 ) + getZero( zeros, n - 2 ) ) / 2;
    // double x0d = Fastmath.instance.nthApproximationZeroD( n );
    // double x0d = Fastmath.instance.nthApproximationZeroD( n );
    // double x0d = Fastmath.instance.nthApproximationZeroD( n );

    // out.println( "Starting from " + x0d + " except " + lastZero );

    Complex yn = iterateFrom( zeros, n, x0d, lastZero, print, removeList );
    recordPoint( n, yn );
    boolean converged = yn != null;
    // double roundedZero = lastZero.getReal().sixtyFourBitValue();
    // // out.println( "Rounding " + lastZero + " to " + roundedZero );
    // lastZero.assign( new Complex( roundedZero, 0 ) );
    //
    // ARF mp = lastZero.getReal().getMidpoint();
    //
    // //out.println( "Starting from " + x0d + " and converged to " +
    // lastZero.getReal().sixtyFourBitValue() );
    // out.println( "MB=" + mp.getValue() );
    // Real hmm = new Real( mp );
    // out.println( "Original " + lastZero );
    // out.println( "Reconstructed " + hmm );

    return converged;
  }

  public static ArrayList<Real> foundRoots = new ArrayList<>();
  private static int threadCount;

  public static void recordPoint( int n, Complex yn )
  {
    Real yr = yn.getReal();
    // foundRoots.ensureCapacity( n );
    // ;
    // foundRoots.add( n - 1, new Real( yr.getMidpoint() ) );

    output.printf( "%d=%s\n", n, yr.sixtyFourBitValue() );
    output.flush();
  }

  /**
   * TODO: do a binary search for convergence point
   */
  public static Complex iterateFrom( Vector knownZeros, int n, double x0d, Complex to, boolean print, List<Real> removedPoints )
  {
    Complex x0 = new Complex( x0d, 0 );
    double actualZero = getZero( knownZeros, n );

    // out.println( "Starting from the " + n + "-th approximate zero at " +
    // x0.getReal().sixtyFourBitValue() );

    // Complex xn = x0.iterate( n % 2 == 1 ? Iteration.ZHTOdd :
    // Iteration.ZHTEven, maxIters, 1, print, to, actualZero, x0d, n,
    // removedPoints );
    Complex xn = x0.iterate( n % 2 == 1 ? Iteration.ZHTOdd : Iteration.ZHTEven, maxIters, 1, print, to, actualZero, x0d, n, removedPoints );
    if ( xn == null )
    {
      return null;
    }
    if ( to != null )
    {
      to.assign( xn );
    }
    double accumulationPoint = xn.getReal().sixtyFourBitValue();
    double diff = accumulationPoint - actualZero;
    boolean converged = abs( diff ) <= pow( 10, -digits );
    if ( !converged )
    {
      System.err.println( "failed to converge: n=" + n + " diff=" + diff + " accumulationPoint=" + accumulationPoint + " actualZero=" + actualZero );
      return null;
    }
    // else
    // {
    // out.println( "\n\n CONVERGED " + n + "\n\n" );
    // }
    return to;

  }

  public static Vector readVectorFromCSV( String filename ) throws FileNotFoundException, IOException
  {
    FileInputStream fis = new FileInputStream( new File( filename ) );
    BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
    LinkedList<Double> t = new LinkedList<>();
    String line;
    int k = 0;
    while (( line = reader.readLine() ) != null)
    {
      String[] tokens = line.split( "," );
      if ( tokens.length <= 1 )
      {
        tokens = line.split( " " );
      }
      for ( int i = 0; i < tokens.length; i++ )
      {

        if ( tokens[i].trim().length() > 0 )
        {
          t.add( Double.valueOf( tokens[i].trim() ) );
        }
      }

    }
    reader.close();
    Vector tvec = new Vector( t.size() );
    AtomicInteger i = new AtomicInteger();
    t.forEach( ts -> {
      tvec.set( i.getAndIncrement(), ts );
    } );
    return tvec;
  }

}
