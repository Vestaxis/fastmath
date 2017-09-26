package fastmath.fx;

import static java.lang.System.out;

import java.util.Arrays;

import fastmath.Pair;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Screen;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class CanvasTest extends Application
{

  public CanvasTest()
  {
  }

  public static void main( String[] args )
  {
    launch( args );
  }

  double maxx = 36;

  double minx = 8;

  double maxy = 5;

  double miny = -5;

  private double xrange;

  private Stage primaryStage = new Stage();

  private double yrange;

  private Canvas canvas;

private double aspectRatio;

  private GraphicsContext gc;

  private double pixelWidth;

  private double pixelHeight;

  @Override
  public void start( Stage arg0 ) throws Exception
  {
    xrange = maxx - minx;
    yrange = maxy - miny;
    Group root = new Group();

    pixelWidth = Screen.getPrimary().getBounds().getWidth();
    pixelHeight = Screen.getPrimary().getBounds().getHeight();
    canvas = new Canvas( pixelWidth, pixelHeight );
    // yrange = xrange / aspectRatio;
    // out.print( "yrange set to " + yrange + " to conserve aspect ratio" );
    gc = canvas.getGraphicsContext2D();
    gc.save();

    root.getChildren().add( canvas );
    Scene scene = new Scene( root );
    scene.setOnMouseClicked( event -> {
      System.out.println( event );
      if ( event.getButton() == MouseButton.MIDDLE )
      {
        selectZoomRegion( event );
      }
      else
      {
        tracePath( event );
      }
    } );

    scene.widthProperty().addListener( ( observableValue, oldSceneWidth, newSceneWidth ) -> {
      pixelWidth = newSceneWidth.doubleValue();
      aspectRatio = canvas.getWidth() / canvas.getHeight();
      out.println( "pixelWidth=" + pixelWidth );
    } );
    scene.heightProperty().addListener( ( observableValue, oldSceneHeight, newSceneHeight ) -> {
      pixelHeight = newSceneHeight.doubleValue();
      aspectRatio = canvas.getWidth() / canvas.getHeight();
      out.println( "pixelHeight=" + pixelHeight );
    } );

    primaryStage.setScene( scene );
    primaryStage.show();
    Platform.runLater( () -> {
      try
      {
        Thread.sleep( 500 );
      }
      catch( InterruptedException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      drawOn( primaryStage );
    } );
  }

  private void tracePath( MouseEvent event )
  {
    // TODO Auto-generated method stub

  }

  private void selectZoomRegion( MouseEvent event )
  {
    // TODO Auto-generated method stub

  }

  public Pair<Double, Double> mapPixel( int px, int py )
  {
    double x = minx + ( maxx - minx ) * ( px + 0.5 ) / pixelWidth;
    double y = miny + ( maxy - miny ) * ( py + 0.5 ) / pixelHeight;
    return new Pair<Double, Double>( x, y );
  }

  private void drawOn( Stage stage )
  {
    Affine t = new Affine();
    double xratio = xrange / pixelWidth;
    double yratio = yrange / pixelHeight;
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
    // gc.translate( minx, miny );
    // gc.scale( xratio, yratio );

    out.println( Arrays.asList( mapPixel( 0, 0 ) ) );

    out.println( Arrays.asList( mapPixel( 300, 200 ) ) );
    out.println( t.transform( new Point2D( 300, 200 ) ) );

    gc.setLineWidth( 0.1 );
    gc.strokeLine( minx, miny, maxx, maxy );
  }

}
