package stochastic.processes.pointprocesses.finance;

import fastmath.DoubleMatrix;
import fastmath.IntVector;
import fastmath.Vector;
import util.TradeClassifier;

/**
 * Also called a "list-history"
 * 
 *
 */
public class TradingFiltration
{
  public TradingFiltration(DoubleMatrix markedPoints)
  {
    super();
    this.markedPoints = markedPoints;
    times = markedPoints.col(0).setName("times");
    prices = markedPoints.col(1).setName("prices");
    types = new IntVector(times.size());
    buyTimes = new Vector(times.size());
    sellTimes = new Vector(times.size());

    classifyTradeSequences();
  }

  public void classifyTradeSequences()
  {
    TradeClassifier classifier = new TradeClassifier();

    int buyCount = 0;
    int sellCount = 0;
    for (int i = 0; i < times.size(); i++)
    {
      double price = prices.get(i);
      Side side = classifier.classify(price);
      types.set(i, side.ordinal());
      classifier.record(price);
      double t = times.get(i);
      if (side == Side.Buy)
      {
        buyTimes.set(buyCount++, t);
      }
      else if (side == Side.Sell)
      {
        sellTimes.set(sellCount++, t);
      }
    }
    buyTimes = buyTimes.slice(0, buyCount);
    sellTimes = sellTimes.slice(0, sellCount);

    tradeIndexes = NasdaqTradingStrategy.getIndices(times);
    buyIndexes = NasdaqTradingStrategy.getIndices(buyTimes);
    sellIndexes = NasdaqTradingStrategy.getIndices(sellTimes);

  }

  public Vector times;
  public Vector prices;
  public DoubleMatrix markedPoints;
  public IntVector types;
  public Vector buyTimes;
  public Vector sellTimes;
  public int[] tradeIndexes;
  public int[] buyIndexes;
  public int[] sellIndexes;
}