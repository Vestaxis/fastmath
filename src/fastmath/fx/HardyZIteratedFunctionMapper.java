package fastmath.fx;

import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.in;
import static java.lang.System.out;
import static java.util.stream.IntStream.range;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

@SuppressWarnings( { "deprecation", "unused", "unchecked" } )
public class HardyZIteratedFunctionMapper extends Application
{
  /**
   * map out the Fatou/Julia sets around the first root
   * 
   * @param args
   */
  public static void main( String args[] )
  {
    launch( args );

    /**
     * TODO: come up with general purpose algo specialized to this function to
     * map out the Fatou and Julia sets and their boundaries etc
     * 
     */

  }
  
  private Iteration iteratedMap = Iteration.ZNewton;

  private double minx = 8;
  private double maxx = 30;
  private double xrange = maxx - minx;
  private double miny = -15;
  private double maxy = 15;
  private double yrange = maxy - miny;

  @Override
  public void start( Stage primaryStage )
  {

    Group root = new Group();
    Scene scene = new Scene( root, 1620, 750, Color.rgb( 0, 0, 0 ) );

    // create a canvas node
    Canvas canvas = new Canvas();

    // bind the dimensions when the user resizes the window.
    canvas.widthProperty().bind( primaryStage.widthProperty() );
    canvas.heightProperty().bind( primaryStage.heightProperty() );
    scene.setOnMouseMoved( new EventHandler<MouseEvent>()
    {
      @Override
      public void handle( MouseEvent event )
      {
        double px = event.getX();
        double py = event.getY();
        double alpha = ( (double) px / (double) primaryStage.getWidth() );
        double beta = ( (double) py / (double) primaryStage.getHeight() );

        double initx = minx + alpha * xrange;
        double inity = miny + beta * yrange;

        String msg = "(px: " + px + ", py: " + py + ") -- (x: " + initx + ", y: " + inity + ")";

        out.println( msg );
      }
    } );
    // obtain the GraphicsContext (drawing surface)
    final GraphicsContext gc = canvas.getGraphicsContext2D();

    // clear screen
    gc.clearRect( 0, 0, primaryStage.getWidth(), primaryStage.getHeight() );

    // add the single node onto the scene graph
    root.getChildren().add( canvas );
    primaryStage.setScene( scene );
    primaryStage.show();
    int pixelWidth = (int) primaryStage.getWidth();
    int pixelHeight = (int) primaryStage.getHeight();

    // linearMap = new Affine();
    // linearMap.append( new Scale( pixelWidth / xrange, pixelHeight / yrange )
    // );
    // linearMap.append( new Translate( minx, miny ) );

    new Thread( () -> {

      out.format( "mapping the rectangle (%f,%f)-(%f,%f)\n", minx, miny, maxx, maxy );

      // RandomComparator<Integer> shuffler = new RandomComparator<>();
      // range( 0, pixelWidth * pixelHeight ).parallel().forEach( pk -> {

      long startTime = currentTimeMillis();

      // gc.setTransform( linearMap );

      range( 0, HardyZMap.Zroots.length ).forEach( i -> gc.fillOval( HardyZMap.Zroots[i],
                                                                     0,
                                                                     pixelWidth / HardyZMap.Zroots[i],
                                                                     pixelHeight / HardyZMap.Zroots[i] ) );

      out.println( "pW=" + pixelWidth );
      out.println( "pH=" + pixelHeight );
      out.println( "xrange=" + xrange );
      out.println( "yrange=" + yrange );
      out.println();

      gc.translate( 0, pixelHeight / 2 );
      gc.scale( xrange / pixelWidth, yrange / pixelWidth );

      range( 0, pixelWidth ).parallel().forEach( px -> {
        range( 0, pixelHeight ).parallel().forEach( py -> {
          // range( 0, pixelWidth ).boxed().sorted( shuffler
          // ).parallel().forEach( px -> {
          // range( 0, pixelHeight ).boxed().sorted( shuffler
          // ).parallel().forEach( py -> {

          final double alpha = ( (double) px / (double) pixelWidth );
          double beta = ( (double) py / (double) pixelHeight );

          double z[] = new double[2];

          double initx = minx + alpha * xrange;
          double inity = miny + beta * yrange;

          int iters = iteratedMap.convergeToLimit( initx, inity, z );

          int index = HardyZMap.mapPointToIndex( z );
          boolean converges = index >= 0;

          int iterationCount = converges ? iters : Integer.MAX_VALUE;
          // int red = converges ? max( 0, min( iterationCount * 10, 255 ) ) :
          // 255;

          double dxdy = sqrt( pow( xrange, 2 ) + pow( yrange, 2 ) );
          Platform.runLater( () -> {
            double realPartOfNearestRoot = converges ? HardyZMap.Zroots[index] : Double.NaN;
            double b = !converges ? 0 : sqrt( pow( realPartOfNearestRoot - initx, 2 ) + pow( inity, 2 ) );
            // int green = converges ? min( 255, ( index + 1 ) * 35 ) : 0;
            double scaledb = ( b * 2 ) / dxdy;
            // int blue = (int) min( 255, 255.0 * scaledb );
            Color color = index == 0 ? Color.RED : index == 1 ? Color.GREEN : index == 2 ? Color.BLUE : index == 3 ? Color.AQUA : Color.WHITE;
            if ( converges )
            {
              // out.println( "b=" + b + " dxdy=" + dxdy + " scaledb=" + scaledb
              // +
              // " blue=" + blue );
              double brightessFactor = max( 0.25, 1 - ( (double) log( 1 + iterationCount / 10.0 ) ) );
              // out.println( "bf " + brightessFactor + " ic " + iterationCount
              // );
              color = color.deriveColor( 0, 1, brightessFactor, 1 - scaledb );
            }
            // gc.getPixelWriter().setColor( px, py, color );
            gc.setFill( color );
            gc.fillRect( initx, inity, 1, 1 );
            // gc.getPixelWriter().setColor( px, py, !converges ? Color.WHITE :
            // Color.GREEN );
            // gc.getPixelWriter().setColor( px, py, !converges ? Color.WHITE :
            // Color.rgb( red, blue, blue ) );
          } );

        } );

      } );
      long duration = currentTimeMillis() - startTime;
      double renderMinutes = ( (double) duration / 1000.0 ) / 60.0;
      out.println( "Rendered in " + renderMinutes );

      Platform.runLater( () -> {

        WritableImage wim = new WritableImage( (int) primaryStage.getWidth(), (int) primaryStage.getHeight() );
        canvas.snapshot( null, wim );
        File file = new File( "ZFractal" + iteratedMap.toString() + ".png" );
        try
        {
          ImageIO.write( SwingFXUtils.fromFXImage( wim, null ), "png", file );
        }
        catch( IOException e )
        {
          e.printStackTrace();
          System.exit( 1 );
        }
      } );

    } ).start();
  }

  private static String toComplexString( double[] z )
  {
    return z[0] + "i" + z[1];
  }

 

  public static String readLine()
  {
    try
    {
      return new BufferedReader( new InputStreamReader( in ) ).readLine();
    }
    catch( IOException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  protected static void setIterationCount( int[] iters, int i )
  {
    if ( iters != null && iters.length > 0 )
    {
      iters[0] = i;
    }
  }

  public HardyZIteratedFunctionMapper()
  {

  }

  public Iteration getIteratedMap()
  {
    return iteratedMap;
  }

  public void setIteratedMap( Iteration iteratedMap )
  {
    this.iteratedMap = iteratedMap;
  }

}
