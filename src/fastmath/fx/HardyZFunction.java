package fastmath.fx;

import static fastmath.Console.println;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import fastmath.Pair;
import fastmath.Vector;
import fastmath.arb.Complex;
import fastmath.arb.Real;
import fastmath.arb.RealConstants;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HardyZFunction extends Application
{

  private static final int zerosToReduce = 5;

  private static final int zerosToLoad = 19;

  static int leaveOut = zerosToLoad - zerosToReduce;

  private static double minx = 10;
  private static double maxx = 70;
  private static double miny = minx;
  private static double maxy = maxx;
  private static double xrange = maxx - minx;
  private static double yrange = maxy - miny;
  private static double lineWidth = sqrt( pow( yrange, 2 ) + pow( yrange, 2 ) ) / 1000;

  private Iteration iteratedMap = Iteration.ZHTNewton;

  public static void main( String[] args ) throws FileNotFoundException, IOException
  {
    Vector zeros = ZIterator.readVectorFromCSV( "zeros6" );
    zeros = zeros.slice( 0, zerosToLoad );
    // roots = roots.slice( 0, 500 );

    roots.ensureCapacity( roots.size() );
    int cnt = 0;
    for ( Double zero : zeros )
    {
      roots.add( new Real( zero ) );
    }
    dropList = roots.subList( roots.size() - leaveOut - 2, roots.size() - leaveOut );
    out.println( "dropList " + dropList.size() );

    Real damn = roots.get( zerosToReduce );
    maxx = damn.sixtyFourBitValue() + 20;
    minx = damn.sixtyFourBitValue() - 20;
    miny = minx;
    maxy = maxx;
    xrange = maxx - minx;
    yrange = maxy - miny;
    launch( args );
  }

  private double width;
  private double height;
  private Stage stage;

  @Override
  public void start( Stage primaryStage )
  {
    stage = primaryStage;
    xrange = maxx - minx;
    yrange = maxy - miny;

    stage.setOnCloseRequest( event -> exit( 1 ) );

    Group root = new Group();

    Scene scene = new Scene( root );
    scene.setOnMouseClicked( event -> {
      boolean leftClick = event.getButton() == MouseButton.PRIMARY;
      boolean rightClick = event.getButton() == MouseButton.SECONDARY;
      if ( leftClick || rightClick )
      {
        int px = (int) event.getX();
        int py = (int) event.getY();

        Point2D clicked = inverseTransform.transform( event.getX(), event.getY() );
        out.println( "clicked " + clicked );
        double re = clicked.getX();
        // double re = mapPixelToRealPart( px );
        // double im = mapPixelToImagPart( py );
        Complex pt = new Complex( re, 0 );
        for ( int i = 0; i < 5; i++ )
        {
          Real thing = pt.HardyZReducedTanh( dropList ).divide( pt.getReal().Omega() );
          Real newMinus = pt.subtract( thing ).getReal();
          double spt = newMinus.sixtyFourBitValue();
          Real newPlus = pt.add( thing ).getReal();
          double apt = newPlus.sixtyFourBitValue();

          gc.strokeLine( re, re - 1, re, re + 1 );
          if ( leftClick )
          {
            Real pp = pt.getReal();
            pt = new Complex( new Real( newMinus.getMidpoint() ), RealConstants.ZERO );
            gc.strokeLine( spt, spt - 1, spt, spt + 1 );
            out.println( pp.sixtyFourBitValue() + " mapped to " + spt );
          }
          else
          {
            Real pp = pt.getReal();
            pt = new Complex( new Real( newPlus.getMidpoint() ), RealConstants.ZERO );
            gc.strokeLine( apt, apt - 1, apt, apt + 1 );
            out.println( pp.sixtyFourBitValue() + " mapped to " + apt );
          }
        }

      }
      if ( event.getButton() == MouseButton.MIDDLE )
      {
        selectZoomRegion( event );
      }
    } );

    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    out.println( screenBounds );

    double aspectRatio = screenBounds.getHeight() / screenBounds.getWidth();
    screenBounds = Screen.getPrimary().getVisualBounds();

    out.println( "ap=" + aspectRatio );
    canvas = new Canvas( screenBounds.getWidth() / 1.05 * aspectRatio, screenBounds.getHeight() / 1.05 );
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

  private Affine inverseTransform;

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
      if ( rendering )
      {
        cancelRendering = true;
        while (rendering)
          ;
      }
      cancelRendering = false;
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
      clearBackground();
      render( Color.WHITE, gc );

      long duration = currentTimeMillis() - startTime;
      double renderMinutes = ( (double) duration / 1000.0 ) / 60.0;
      out.println( "Rendered in " + renderMinutes );

      saveImage( canvas, width, height );

    } ).start();
  }

  protected void clearBackground()
  {
    gc.setFill( Color.BLACK );
    gc.fillRect( -minx, -miny, xrange * 5, yrange * 5 );

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

    if ( rotatePlane )
    {
      t.appendRotation( 45, width / 2, height / 2 );
    }
    inverseTransform = t.clone();
    t = invertTransform( t );

    gc.setTransform( t );

  }

  private boolean rotatePlane = false;

  public Affine invertTransform( Affine t )
  {
    try
    {
      t.invert();
      return t;
    }
    catch( NonInvertibleTransformException e )
    {
      throw new RuntimeException( e.getMessage(), e );
    }
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
    Platform.runLater( () -> {

      WritableImage wim = new WritableImage( (int) width, (int) height );
      canvas.snapshot( null, wim );
      File file = new File( "Z.png" );
      try
      {
        ImageIO.write( SwingFXUtils.fromFXImage( wim, null ), "png", file );
        println( "Saved " + file.getAbsolutePath() );
      }
      catch( IOException e )
      {
        e.printStackTrace();

      }
    } );
  }

  PixelPainter painter = null;

  private volatile boolean cancelRendering = false;
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
    // gc.getTransform().trans
    double initx = minx + ( (double) d / width ) * xrange;
    return initx;
  }

  protected void render( Color color, GraphicsContext gc )
  {

    sketchZFunction( color, gc );

  }

  static ArrayList<Real> roots = new ArrayList<Real>();

  private static List<Real> dropList;

  protected static void sketchZFunction( Color color, GraphicsContext gc )
  {
    gc.setStroke( color );
    gc.setLineWidth( lineWidth );

    int i = 1;
    for ( Real root : roots )
    {
      gc.setStroke( i++ % 2 == 1 ? Color.GREEN : Color.BLUE );
      gc.beginPath();
      gc.moveTo( -minx, root.sixtyFourBitValue() );
      gc.lineTo( maxx * 5, root.sixtyFourBitValue() );
      gc.lineTo( -minx, root.sixtyFourBitValue() );
      gc.moveTo( root.sixtyFourBitValue(), -minx );
      gc.lineTo( root.sixtyFourBitValue(), maxx * 5 );
      gc.lineTo( root.sixtyFourBitValue(), -minx );
      gc.closePath();
      gc.stroke();
    }
    gc.stroke();
    gc.setFontSmoothingType( FontSmoothingType.LCD );
    gc.setFont( Font.font( "Verdana", FontWeight.NORMAL, FontPosture.REGULAR, 1 ) );
    gc.setFill( Color.WHITE );
    gc.save();
    gc.translate( 0, -0.0625 );
    gc.scale( 1, -( yrange / xrange ) / 4.0 );
    gc.fillText( format( "%4.2f", minx ), minx, minx, yrange / 20.0 );
    String maxString = format( "%4.2f", maxx );
    double textWidth = getTextWidth( gc, maxString );
    gc.fillText( maxString, maxx - textWidth, maxy - 1, yrange / 20.0 );

    gc.restore();
    gc.setStroke( Color.LIGHTBLUE );
    drawIterationFunction( gc, true, false, null );
    gc.setStroke( Color.YELLOW );
    drawIterationFunction( gc, false, false, null );
    gc.setStroke( Color.GRAY );
    drawIterationFunction( gc, false, true, null );
    drawIterationFunction( gc, false, true, true );
    drawIterationFunction( gc, false, true, false );

    // double sp = ( roots.get( roots.size() - 1 ).sixtyFourBitValue() +
    // Fastmath.instance.nthApproximationZeroD( roots.size() + 1) ) / 2;
    // gc.setStroke( Color.GREEN );
    // gc.strokeLine( sp, miny, sp, maxy );
  }

  public static void drawIterationFunction( GraphicsContext gc, boolean subtract, boolean straight, Boolean above )
  {
    gc.beginPath();
    Real h = new Real( "0.05" );
    Real t = new Real( minx );
    boolean first = false;
    for ( ; t.sixtyFourBitValue() < maxx; t = t.add( h ) )
    {
      final Complex pt = new Complex( t, RealConstants.ZERO );
      // Real z = pt.subtract( new Complex( pt.HardyZReducedTanh( roots.stream()
      // ).divide( t.Omega() ), RealConstants.ZERO ) ).getReal();
      // Real z = pt.subtract( pt.HardyZTanh().divide( t.Omega() ) ).getReal();
      Real thing = pt.HardyZReducedTanh( dropList );
      Real z = straight ? ( above == null ? pt.getReal() : above ? pt.getReal().add( RealConstants.ONE ) : ( pt.getReal().subtract( RealConstants.ONE ) ) )
                        : subtract ? ( pt.subtract( thing ).getReal() ) : ( pt.add( thing ).getReal() );

      double yc = z.sixtyFourBitValue();
      // out.println( yc );
      // iteratedMap.value( t, 0, z, 1 );
      // out.println( t + " " + z[0] );
      if ( first )
      {
        first = false;
        gc.moveTo( t.sixtyFourBitValue(), yc );
      }
      else
      {
        gc.lineTo( t.sixtyFourBitValue(), yc );
      }

    }
    gc.moveTo( minx, 0 );
    gc.closePath();
    gc.stroke();

  }

  public static double getTextWidth( GraphicsContext gc, String maxString )
  {
    double textWidth = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth( maxString, gc.getFont() );
    return textWidth;
  }

  protected void drawRectangle( GraphicsContext gc, double x1, double y1, double x2, double y2, Paint paint )
  {
    gc.setFill( paint );
    gc.fillRect( x1, y1, x2 - x1, y2 - y1 );
  }

  protected static void drawCenteredOval( GraphicsContext gc, double x, double y, double w, double h, Color paint )
  {
    gc.setFill( paint );
    gc.fillOval( x - w / 2, y - h / 2, w, h );
  }

  public static void readZerosFromFile( String filename ) throws FileNotFoundException, IOException
  {
    FileInputStream fis = new FileInputStream( new File( filename ) );
    BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
    String line;
    int k = 0;
    while (( line = reader.readLine() ) != null)
    {
      String[] tokens = line.split( "=" );
      Integer idxz = Integer.valueOf( tokens[0] );
      if ( idxz > zerosToLoad )
      {
        break;
      }
      roots.ensureCapacity( idxz );
      roots.add( idxz - 1, new Real( tokens[1] ) );

    }
    reader.close();
  }

}
