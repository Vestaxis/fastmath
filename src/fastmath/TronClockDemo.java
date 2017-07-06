package fastmath;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Driver class to run the demo which extends from JavaFX's Application class.
 * The demo will make use of JavaFX's Canvas API. This class creates three
 * clocks which are animated using an AnimationTimer class.
 *
 * Inspired by http://burnwell88.deviantart.com/art/Clock-136761577 and
 * http://rainmeter.net/cms/
 *
 * Created with IntelliJ IDEA. User: cdea Date: 10/15/12 Time: 12:20 AM
 *
 */
public class TronClockDemo extends Application
{

  public static void main( String[] args )
  {
    Application.launch( args );
  }

  @Override
  public void start( final Stage primaryStage )
  {

    Group root = new Group();
    Scene scene = new Scene( root, 1000, 500, Color.rgb( 0, 0, 0 ) );

    // create a canvas node
    Canvas canvas = new Canvas();

    // bind the dimensions when the user resizes the window.
    canvas.widthProperty().bind( primaryStage.widthProperty() );
    canvas.heightProperty().bind( primaryStage.heightProperty() );

    // obtain the GraphicsContext (drawing surface)
    final GraphicsContext gc = canvas.getGraphicsContext2D();

    // create three clocks
    final ArcClock blueClock = new ArcClock( 20, Color.BLUE, Color.BLUEVIOLET, 320 );
    final ArcClock greenClock = new ArcClock( 20, Color.BLUE, Color.GREEN, 320 );
    final ArcClock redClock = new ArcClock( 20, Color.BLUE, Color.RED, 320 );

    // create an animation (update & render loop)
    new AnimationTimer()
    {
      @Override
      public void handle( long now )
      {
        // update clocks
        blueClock.update( now );
        greenClock.update( now );
        redClock.update( now );

        // clear screen
        gc.clearRect( 0, 0, primaryStage.getWidth(), primaryStage.getHeight() );

        // draw blue clock
        blueClock.draw( gc );
        // save the origin or the current state
        // of the Graphics Context.
        gc.save();

        // shift x coord position the width of a clock plus 20 pixels
        gc.translate( blueClock.maxDiameter + 20, 0 );
        greenClock.draw( gc );

        // shift x coord position past the first clock
        gc.translate( blueClock.maxDiameter + 20, 0 );
        redClock.draw( gc );

        // reset Graphics Context to last saved point.
        // Translate x, y to (0,0)
        gc.restore();

      }
    }.start();

    // add the single node onto the scene graph
    root.getChildren().add( canvas );
    primaryStage.setScene( scene );
    primaryStage.show();
  }

}