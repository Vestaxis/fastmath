package fastmath.fx;

import static fastmath.Console.println;
import static fastmath.Functions.W;
import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;
import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import fastmath.DoubleRowMatrix;
import fastmath.Functions;
import fastmath.Pair;
import fastmath.arb.Complex;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HardyZFixedPointOperator extends Application
{

  private double minx = 6720;
  private double maxx = 6740;
  private double miny = minx - 2;
  private double maxy = maxx + 2;
  private double xrange = maxx - minx;
  private double yrange = maxy - miny;
  private double lineWidth = sqrt( pow( yrange, 2 ) + pow( yrange, 2 ) ) / 2000;

  private Iteration iteratedMap = Iteration.ZHTNewton;

  public static void main( String[] args )
  {    launch( args );
  }

  private int convergents[][];
  private double width;
  private double height;
  private Stage stage;

  public double GramPoint( int i )
  {
    return Gram[i];
  }

  @Override
  public void start( Stage primaryStage )
  {
    stage = primaryStage;
    xrange = maxx - minx;
    yrange = maxy - miny;
    primaryStage.setTitle( iteratedMap.toString() );
    stage.setOnCloseRequest( event -> exit( 1 ) );
    Group root = new Group();

    Scene scene = new Scene( root );
    scene.setOnMouseClicked( event -> {
      if ( event.getButton() == MouseButton.MIDDLE )
      {
        selectZoomRegion( event );
      }
      else
      {
        tracePath( event );
      }
    } );

    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    out.println( screenBounds );
    canvas = new Canvas( screenBounds.getWidth(), screenBounds.getHeight() );
    stage.sizeToScene();
    gc = canvas.getGraphicsContext2D();
    gc.save();

    root.getChildren().add( canvas );
    primaryStage.setScene( scene );
    primaryStage.setResizable( false );
    // primaryStage.setMaximized( true );
    primaryStage.initStyle( StageStyle.UTILITY );
    primaryStage.show();
    while (!primaryStage.isShowing())
    {
      Thread.yield();
    }

    width = canvas.getWidth();
    height = canvas.getHeight();
    setupCoordinateSystem();
    out.println( "drawing on canvas which is " + width + " by " + height );
    Platform.runLater( () -> {
      drawShapes( stage );
    } );
  }

  private Pair<Double, Double> upperLeft;

  private Pair<Double, Double> bottomRight;

  private void selectZoomRegion( MouseEvent event )
  {
    int px = (int) event.getX();
    int py = (int) event.getY();
    double re = mapPixelToRealPart( px );
    double im = mapPixelToImagPart( py );
    if ( upperLeft == null )
    {
      upperLeft = new Pair<>( re, im );
    }
    else if ( bottomRight == null )
    {
      bottomRight = new Pair<>( re, im );
    }
    if ( upperLeft != null && bottomRight != null )
    {
      zoom( upperLeft, bottomRight );
      upperLeft = null;
      bottomRight = null;
    }
  }

  private void zoom( Pair<Double, Double> ul, Pair<Double, Double> lr )
  {
    Platform.runLater( () -> {

      println( "zoom to " + ul + " , " + lr );

      minx = ul.left;
      maxx = lr.left;
      miny = ul.right;
      maxy = ul.left;
      start( new Stage() );
      // Application.launch( HardyZMap.class, new String[]
      // {
      // Double.toString( minx ),
      // Double.toString( maxx ),
      // Double.toString( miny ),
      // Double.toString( maxy )
      // } );
    } );
  }

  protected void tracePath( MouseEvent event )
  {
    int px = (int) event.getX();
    int py = (int) event.getY();
    double re = mapPixelToRealPart( px );
    double im = mapPixelToImagPart( py );
    DoubleRowMatrix path = Functions.iterateAlongPath( iteratedMap, re, im, 1 );
    gc.setLineWidth( lineWidth );
    gc.setStroke( event.getButton() == MouseButton.PRIMARY ? Color.WHITE : Color.BLACK );
    for ( int i = 0; i < path.getRowCount() - 1; i++ )
    {
      drawCenteredOval( gc, path.get( i, 0 ), path.get( i, 1 ), 0.1, 0.1, Color.SKYBLUE );
      gc.strokeLine( path.get( i, 0 ), path.get( i, 1 ), path.get( i + 1, 0 ), path.get( i + 1, 1 ) );
    }

    println( "Path=\n" + path );
    /**
     * TODO: implement zooming in on a region by dragging to draw a rectangle.
     * use page flipping to animate the zoom rectangle etc and maybe initialize
     * the new screen with a scaled up/interpolated copy while the rendering
     * thread fills in the details
     */
    int idx = HardyZFixedPointOperator.mapPointToNearestIndex( re );
    double nr = HardyZFixedPointOperator.Zroots[idx];
    double dist = sqrt( pow( re - nr, 2 ) + pow( im, 2 ) );
    int converges = convergents == null ? -1 : convergents[px][py];
    out.println( "clicked (" + re + "," + im + "), the nearest root is y[" + idx + "]=" + nr + " at a distance of " + dist
                 + ( converges == -1 ? " where it diverges" : " where it converges to root#" + converges ) );
  }

  private void drawShapes( Stage stage )
  {

    gc.setLineWidth( 0.01 );

    // loadImage( canvas, width, height );

    render();

  }

  protected void render()
  {

    new Thread( () -> {

      out.format( "mapping the rectangle (%f,%f)-(%f,%f)\n", minx, miny, maxx, maxy );

      // range( 0, pixelWidth * pixelHeight ).parallel().forEach( pk -> {

      long startTime = currentTimeMillis();

      // gc.setTransform( linearMap );
      out.println( "pW=" + width );
      out.println( "pH=" + height );
      out.println( "xrange=" + xrange );
      out.println( "yrange=" + yrange );
      out.println();

      setupCoordinateSystem();
      clearBackgroundAndDrawHorizontalAxis();
      drawPointsAndLines( Color.WHITE, gc );

      long duration = currentTimeMillis() - startTime;
      double renderMinutes = ( (double) duration / 1000.0 ) / 60.0;
      out.println( "Rendered in " + renderMinutes );

      saveImage( canvas, width, height );

    } ).start();
  }

  protected void clearBackgroundAndDrawHorizontalAxis()
  {
    gc.setFill( Color.BLACK );
    gc.fillRect( minx, miny, xrange, yrange );

    // TODO: draw grid and label lines
    // gc.setLineWidth( lineWidth );
    // gc.setStroke( Color.WHITE );
    // gc.strokeLine( minx, miny, maxx, maxy );
  }

  protected void setupCoordinateSystem()
  {
    gc.restore();
    // double aspectRatio = width / height;
    // yrange = xrange / aspectRatio;
    // miny = -yrange / 2;
    // maxy = yrange / 2;
    // out.println( "yrange set to " + yrange + " to conserve aspect ratio " +
    // aspectRatio );

    Affine t = new Affine();
    double xratio = xrange / width;
    double yratio = yrange / height;
    t.appendTranslation( minx, miny );
    t.appendScale( xratio, yratio );
    try
    {
      t.invert();
    }
    catch( NonInvertibleTransformException e )
    {
      throw new RuntimeException( e.getMessage(), e );
    }
    gc.setTransform( t );

  }

  protected boolean loadImage( Canvas canvas, double width, double height )
  {
    File file = new File( iteratedMap.toString() + ".png" );
    if ( file.canRead() )
    {
      Platform.runLater( () -> {

        WritableImage wim = new WritableImage( (int) width, (int) height );
        try
        {
          gc.drawImage( SwingFXUtils.toFXImage( ImageIO.read( file ), wim ), 0, 0 );
          println( "Loaded " + file.getAbsolutePath() );
        }
        catch( IOException e )
        {
          e.printStackTrace();
        }
      } );
      return true;
    }
    else
    {
      return false;
    }
  }

  protected void saveImage( Canvas canvas, double width, double height )
  {
    // Platform.runLater( () -> {
    //
    // WritableImage wim = new WritableImage( (int) width, (int) height );
    // canvas.snapshot( null, wim );
    // File file = new File( iteratedMap.toString() + ".png" );
    // try
    // {
    // ImageIO.write( SwingFXUtils.fromFXImage( wim, null ), "png", file );
    // println( "Saved " + file.getAbsolutePath() );
    // }
    // catch( IOException e )
    // {
    // e.printStackTrace();
    // System.exit( 1 );
    // }
    // } );
  }

  PixelPainter painter = null;

  private volatile boolean rendering;

  public class PixelPainter extends AnimationTimer
  {

    public PixelPainter()
    {
      start();
    }

    @Override
    public void handle( long now )
    {
      Point point;
      while (( point = points.poll() ) != null)
      {
        gc.getPixelWriter().setColor( point.re, point.im, point.color );

      }
    }

  }

  public static class Point
  {
    public Point(int re, int im, Color color)
    {
      this.re = re;
      this.im = im;
      this.color = color;
    }

    public int re;
    public int im;
    public Color color;
  }

  ConcurrentLinkedQueue<Point> points = new ConcurrentLinkedQueue<>();
  private GraphicsContext gc;
  private Canvas canvas;

  public Color[] colorCycle;
  private static double ballRadius = 0.15;
  // private double aspectRatio;

  protected Color getIndexColor( int index )
  {
    return index == -1 ? Color.WHITE : colorCycle[index % colorCycle.length];
  }

  protected double mapPixelToImagPart( double d )
  {
    double inity = miny + ( (double) d / height ) * yrange;
    return inity;
  }

  protected double mapPixelToRealPart( double d )
  {
    double initx = minx + ( (double) d / width ) * xrange;
    return initx;
  }

  protected void drawPointsAndLines( Color color, GraphicsContext gc )
  {
    int idx = 1;
    int colorCount = 0;
    double z[] = new double[2];

    double az = Functions.approximationZero( 6400 ).sixtyFourBitValue();
    println( "approximation zero 6400 = " + az );
    drawCenteredOval( gc, az, az, 0.01, 0.05, Color.ORANGE );
    sketchZTanhNewtonMap( color, gc );

    // sketchRealPartOfZetaFunction( color, gc, z );

    // sketchImaginaryPartOfZetaFunction( color, gc, z );

    ballRadius = min( abs( xrange ) / 200, abs( yrange ) / 200 );
    // lineWidth = ballRadius / 100;
    for ( double zroot : HardyZFixedPointOperator.Zroots )
    {
      double approximateZero = z0( idx++ );
      if ( zroot > minx && zroot < maxx )
      {
        colorCount++;
        drawCenteredOval( gc, zroot, 0, ballRadius, ballRadius, Color.YELLOW );
        drawCenteredOval( gc, approximateZero, 0, ballRadius, ballRadius, Color.LIGHTGREEN );
        // TODO: draw text indicating indices and coordinates
      }
    }
    if ( colorCycle == null )
    {
      colorCycle = ColorUtils.generateVisuallyDistinctColors( max( 5, colorCount ), .8f, .3f );
    }

    gc.setStroke( Color.HOTPINK );
    double y;
    for ( int i = 0; ( y = Functions.almostGramPoint( i ) ) < maxx; i++ )
    {
      // println( "drawing approximate Gram point at " + y );
      // gc.strokeLine( y, -0.5, y, 0.5 );
      drawCenteredOval( gc, y, 0, ballRadius, ballRadius, Color.BLUEVIOLET );
      // FIXME: put some text here
    }

  }

  public static void drawPath( Path path, GraphicsContext context )
  {
    context.beginPath();
    for ( PathElement e : path.getElements() )
    {
      if ( e instanceof ClosePath )
      {
        context.closePath();
      }
      else if ( e instanceof CubicCurveTo )
      {
        CubicCurveTo c = (CubicCurveTo) e;
        context.bezierCurveTo( c.getControlX1(), c.getControlY1(), c.getControlX2(), c.getControlY2(), c.getX(), c.getY() );
      }
      else if ( e instanceof LineTo )
      {
        LineTo l = (LineTo) e;
        context.lineTo( l.getX(), l.getY() );
      }
      else if ( e instanceof MoveTo )
      {

        MoveTo m = (MoveTo) e;
        context.moveTo( m.getX(), m.getY() );
      }
      else if ( e instanceof QuadCurveTo )
      {
        QuadCurveTo q = (QuadCurveTo) e;
        context.quadraticCurveTo( q.getControlX(), q.getControlY(), q.getX(), q.getY() );
        break;
      }
    }
  }

  protected void sketchZTanhNewtonMap( Color color, GraphicsContext gc )
  {
    double pz[] = new double[2];
    Functions.ZNewton( minx, 0, pz );
    gc.setStroke( color );
    gc.setLineWidth( lineWidth );
    gc.beginPath();
    gc.moveTo( minx, 0 );
    double h = 0.1;
    for ( double t = minx; t < maxx; t += h )
    {
      final Complex pt = new Complex( t, 0 );
      Complex z = pt.HardyZ().tanh();
      out.println( z.getReal().sixtyFourBitValue() );
      // iteratedMap.value( t, 0, z, 1 );
      // out.println( t + " " + z[0] );
      gc.lineTo( t, z.getReal().sixtyFourBitValue() );

    }
    Functions.ZNewton( minx, 0, pz );
    gc.moveTo( maxx, pz[0] );
    gc.closePath();
    gc.stroke();

  }

  protected void drawRectangle( GraphicsContext gc, double x1, double y1, double x2, double y2, Paint paint )
  {
    gc.setFill( paint );
    gc.fillRect( x1, y1, x2 - x1, y2 - y1 );
  }

  public static double z0( int n )
  {
    return 2.0 * PI * ( n - 1.375 ) / W( ( n - 1.375 ) / E );
  }

  /**
   * 
   * @param z
   * @return -1 if z is not (within epislon) a root of Z
   */
  public static int mapPointToIndex( double z[] )
  {
    double re = z[0];
    double im = z[1];
    return mapPointToIndex( re, im );
  }

  /**
   * 
   * @param re
   * @param im
   * @return the index of the root if it matches within epsilon (+/-0.001)
   */
  public static int mapPointToIndex( double re, double im )
  {
    for ( int i = 0; i < Zroots.length; i++ )
    {
      double fixedPoint = Zroots[i];
      double realDist = abs( re - fixedPoint );
      double imagDist = abs( im );
      if ( sqrt( realDist * realDist + imagDist * imagDist ) < pow( 10, -3 ) )
      {
        return i;
      }
    }
    return -1;
  }

  public static int mapPointToNearestIndex( double re )
  {
    Entry<Double, Integer> left = Zidx.floorEntry( re );
    Entry<Double, Integer> right = Zidx.ceilingEntry( re );
    if ( left == null && right != null )
    {
      return right.getValue();
    }
    if ( right == null && left != null )
    {
      return left.getValue();
    }
    if ( left == null && right == null )
    {
      return -1;
    }
    return abs( re - left.getKey() ) < abs( re - right.getKey() ) ? left.getValue() : right.getValue();
  }

  protected static void drawCenteredOval( GraphicsContext gc, double x, double y, double w, double h, Color paint )
  {
    gc.setFill( paint );
    gc.fillOval( x - w / 2, y - h / 2, w, h );
  }

  /**
   * FIXME: replace with generated sequence
   */
  public static double Gram[] =
  {
    17.8455995404108608,
    23.1702827012463093,
    27.6701822178163380,
    31.7179799547640532,
    35.4671842971002161,
    38.9992099640260748,
    42.3635503920573380,
    45.5930289815035223,
    48.7107766217933329,
    51.7338428133461044,
    54.6752374468532563,
    57.5451651795472544,
  };

  /**
   * FIXME: replace with reading from zeros1 or generate sequence after proof is
   * finished :)
   */
  public static double Zroots[] =
  {
    14.1347251417346937904,
    21.0220396387715549926,
    25.0108575801456887632,
    30.4248761258595132103,
    32.9350615877391896906,
    37.5861781588256712572,
    40.9187190121474951873,
    43.327073281,
    48.005150881,
    49.773832478,
    52.970321478,
    56.446247697,
    59.347044003,
    60.831778525,
    65.112544048,
    67.079810529,
    69.546401711,
    72.067157674,
    75.704690699,
    77.144840069,
    79.337375020,
    82.910380854,
    84.735492981,
    87.425274613,
    88.809111208,
    92.491899271,
    94.651344041,
    95.870634228,
    98.831194218,
    101.317851006,
    103.725538040,
    105.446623052,
    107.168611184,
    111.029535543,
    111.874659177,
    114.320220915,
    116.226680321,
    118.790782866,
    121.370125002,
    122.946829294,
    124.256818554,
    127.516683880,
    129.578704200,
    131.087688531,
    133.497737203,
    134.756509753,
    138.116042055,
    139.736208952,
    141.123707404,
    143.111845808,
    146.000982487,
    147.422765343,
    150.053520421,
    150.925257612,
    153.024693811,
    156.112909294,
    157.597591818,
    158.849988171,
    161.188964138,
    163.030709687,
    165.537069188,
    167.184439978,
    169.094515416,
    169.911976479,
    173.411536520,
    174.754191523,
    176.441434298,
    178.377407776,
    179.916484020,
    182.207078484,
    184.874467848,
    185.598783678,
    187.228922584,
    189.416158656,
    192.026656361,
    193.079726604,
    195.265396680,
    196.876481841,
    198.015309676,
    201.264751944,
    202.493594514,
    204.189671803,
    205.394697202,
    207.906258888,
    209.576509717,
    211.690862595,
    213.347919360,
    214.547044783,
    216.169538508,
    219.067596349,
    220.714918839,
    221.430705555,
    224.007000255,
    224.983324670,
    227.421444280,
    229.337413306,
    231.250188700,
    231.987235253,
    233.693404179,
    236.524229666,
    237.769820481,
    239.555477573,
    241.049157796,
    242.823271934,
    244.070898497,
    247.136990075,
    248.101990060,
    249.573689645,
    251.014947795,
    253.069986748,
    255.306256455,
    256.380713694,
    258.610439492,
    259.874406990,
    260.805084505,
    263.573893905,
    265.557851839,
    266.614973782,
    267.921915083,
    269.970449024,
    271.494055642,
    273.459609188,
    275.587492649,
    276.452049503,
    278.250743530,
    279.229250928,
    282.465114765,
    283.211185733,
    284.835963981,
    286.667445363,
    287.911920501,
    289.579854929,
    291.846291329,
    293.558434139,
    294.965369619,
    295.573254879,
    297.979277062,
    299.840326054,
    301.649325462,
    302.696749590,
    6727.203484213,
    6727.861353027

  };

  static TreeMap<Double, Integer> Zidx = new TreeMap<Double, Integer>();

  static
  {
    for ( int i = 0; i < HardyZFixedPointOperator.Zroots.length; i++ )
    {
      Zidx.put( HardyZFixedPointOperator.Zroots[i], i );
    }
  }

}
