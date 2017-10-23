package util;

import java.util.ArrayDeque;
import java.util.Arrays;

import stochastic.processes.pointprocesses.finance.Side;

public class TradeClassifier
{
  public TradeClassifier( int maxHistLength)
  {
    super();
  }

  @Override
  public String toString()
  {
    return Arrays.toString(hist.toArray());
  }

  private final ArrayDeque<Double> hist = new ArrayDeque<>();

  public void record(double price)
  {
    hist.push(price);
  }

  public Side classify(double price)
  {
    for (Double histPrice : hist)
    {
      if (price > histPrice)
      {
        return Side.Buy;
      }
      else if (price < histPrice) { return Side.Sell; }
    }
    return Side.Unknown;
  }
}