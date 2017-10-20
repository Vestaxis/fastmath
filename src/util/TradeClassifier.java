package util;

import java.util.ArrayDeque;
import java.util.Arrays;

public class TradeClassifier
{
  @Override
  public String toString()
  {
    return Arrays.toString(hist.toArray());
  }

  private ArrayDeque<Double> hist = new ArrayDeque<>();
 
  private int maxHistLength = 5;
  
  public void record( double price )
  {
    hist.push(price);
    while ( hist.size() > maxHistLength )
    {
      hist.removeLast();
    }
  }
  
}
