package stochastic.processes.pointprocesses;

import fastmath.Vector;

public class MarkedPoint extends Vector
{

  private int type;


  public MarkedPoint(int m)
  {
    super( m );
  }

  
  public int getType()
  {
    return type;
  }


  public void setType( int type )
  {
    this.type = type;
  }


  public Double getTime()
  {
    // TODO Auto-generated method stub
    return null;
  }


  public Vector getMarks()
  {
    // TODO Auto-generated method stub
    return null;
  }
}
