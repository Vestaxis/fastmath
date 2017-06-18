package fastmath;

import javafx.scene.paint.Color;

/**
 * ArcPieceBuilder is conveniecy class for the user of the API to build ArcPiece
 * instances using the builder pattern.
 *
 * Disclaimer: to make things simple I don't use JavaFX Properties and do not
 * use getters/setters on the domain model.
 *
 */
public class ArcPieceBuilder
{
  ArcPiece arcPiece = new ArcPiece();

  public static ArcPieceBuilder create()
  {
    return new ArcPieceBuilder();
  }

  public ArcPieceBuilder x( double x )
  {
    arcPiece.x = x;
    return this;
  }

  public ArcPieceBuilder y( double y )
  {
    arcPiece.y = y;
    return this;
  }

  public ArcPieceBuilder w( double w )
  {
    arcPiece.w = w;
    return this;
  }

  public ArcPieceBuilder h( double h )
  {
    arcPiece.h = h;
    return this;
  }

  public ArcPieceBuilder startAngle( double startAngle )
  {
    arcPiece.startAngle = startAngle;
    return this;
  }

  public ArcPieceBuilder arcExtent( double arcExtent )
  {
    arcPiece.arcExtent = arcExtent;
    return this;
  }

  public ArcPieceBuilder strokeWidth( double width )
  {
    arcPiece.strokeWidth = width;
    return this;
  }

  public ArcPieceBuilder strokeColor( Color c )
  {
    arcPiece.strokeColor = c;
    return this;
  }

  public ArcPieceBuilder clockwise()
  {
    arcPiece.clockwise = true;
    return this;
  }

  public ArcPieceBuilder counterClockwise()
  {
    arcPiece.clockwise = false;
    return this;
  }

  public ArcPieceBuilder displayTimePerFrameMillis( long millis )
  {
    arcPiece.displayTimePerFrameMillis = millis;
    return this;
  }

  public ArcPieceBuilder pixelsToMove( double numPixels )
  {
    arcPiece.pixelsToMove = numPixels;
    return this;
  }

  public ArcPiece build()
  {
    return arcPiece;
  }
}