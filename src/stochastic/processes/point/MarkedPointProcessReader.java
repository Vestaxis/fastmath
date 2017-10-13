package stochastic.processes.point;

import static java.lang.Math.floor;
import static java.lang.System.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import fastmath.DoubleRowMatrix;
import fastmath.Pair;
import fastmath.Vector;
import fastmath.exceptions.FastMathException;
import fastmath.matfile.MatFile;
import stochastic.processes.pointprocesses.finance.ArchivableEvent;
import stochastic.processes.pointprocesses.finance.TradeTick;
import stochastic.processes.pointprocesses.finance.TwoSidedQuote;
import stochastics.annotations.Units;
import util.DateUtils;

public class MarkedPointProcessReader
{

  @Units(time = TimeUnit.HOURS)
  public final static double openTime = 9.5;

  @Units(time = TimeUnit.HOURS)
  public final static double closeTime = 16;

  @Units(time = TimeUnit.HOURS)
  public static final double tradingDuration = closeTime - openTime;
  
  public static void main(String args[]) throws IOException, FastMathException
	{

		String filenameOfMarkedPointProcess = args[0];
		String outputMatfileName = args[1];

		Vector trades = loadPointProcessFromFile(filenameOfMarkedPointProcess, args[1] );


	}

	public static class TradingProcess
	{
		private int n;

		private String symbol;

		public TradingProcess(String symbol, int n)
		{
			this.n = n;
			this.symbol = symbol;
			sells = new DoubleRowMatrix(n, 1);
			sells.setName(symbol + "_sells");
			trades = new DoubleRowMatrix(n, 1);
			trades.setName(symbol);
			buys = new DoubleRowMatrix(n, 1);
			buys.setName(symbol + "_buys");
			prices = new DoubleRowMatrix(n, 1);
			prices.setName(symbol + "_price");

		}

		private DoubleRowMatrix trades;

		public DoubleRowMatrix buys;

		public DoubleRowMatrix sells;

		public DoubleRowMatrix prices;
		
	
		public DoubleRowMatrix getTrades()
		{
			return trades;
		}

		public void setTrades(DoubleRowMatrix trades)
		{
			this.trades = trades;
		}

	}

	protected static Vector loadPointProcessFromFile( String filenameOfMarkedPointProcess, String symbol ) throws IOException
	{
			RandomAccessFile raf = new RandomAccessFile(filenameOfMarkedPointProcess, "r");

			RandomAccessFile indexFile = new RandomAccessFile(filenameOfMarkedPointProcess + ".idx", "r");

			File mppFile = new File(filenameOfMarkedPointProcess);
			MarkedPointProcess mpp = new MarkedPointProcess(new Pair<>(raf, indexFile), mppFile, symbol);

			out.println("reading " + mpp);

			LinkedList<Double> times = new LinkedList<>();

			double dt = DateUtils.convertTimeUnits(1, TimeUnit.SECONDS, TimeUnit.HOURS);
			int n = (int) (tradingDuration / dt);
			// IntegerValuedAutoregressiveProcess inarProcess = new
			// IntegerValuedAutoregressiveProcess(1, dt, n );


			double bidPrice = 0, askPrice = Double.POSITIVE_INFINITY;
			double midPrice = 0;
			int tradeCount = 0;
			boolean verbose = false;
			Double openPrice = null;
			Double closePrice = null;

			
			String outputMatfileName = new String( "/data/" + symbol + ".mat");
			
			ArrayList<Double> trades = new ArrayList<>();
			
			for (ArchivableEvent event : mpp)
			{
				double spread = askPrice - bidPrice;
				// out.println( "event=" + event );
				int tk = (int) floor((event.getTimeOfDay() - openTime) / dt);

				if (event instanceof TradeTick)
				{
					TradeTick trade = (TradeTick) event;
					
					trades.add((double) event.getTimestampInMilliseconds());
				} else if (event instanceof TwoSidedQuote)
				{
					TwoSidedQuote quote = (TwoSidedQuote) event;
					bidPrice = quote.getBid();
					askPrice = quote.getAsk();
					midPrice = (bidPrice + askPrice) / 2;
				}
				

			}

			
			out.println("Writing to " + outputMatfileName);
			
//			Vector h = tradeCounts.asVector().getRescaledRange().setName("h");

			Vector tradeVector = new Vector( trades ).setName(symbol);
			MatFile.write(outputMatfileName, tradeVector.createMiMatrix() );

			return tradeVector;				
	}
	
	protected static TradingProcess loadTradingProcessFromFile(String filenameOfMarkedPointProcess,
			String outputMatfileName, boolean squash, String symbol ) throws FileNotFoundException, IOException
	{
		RandomAccessFile raf = new RandomAccessFile(filenameOfMarkedPointProcess, "r");

		RandomAccessFile indexFile = new RandomAccessFile(filenameOfMarkedPointProcess + ".idx", "r");

		File mppFile = new File(filenameOfMarkedPointProcess);
		MarkedPointProcess mpp = new MarkedPointProcess(new Pair<>(raf, indexFile), mppFile, symbol);

		out.println("reading " + mpp);

		LinkedList<Double> times = new LinkedList<>();

		double dt = DateUtils.convertTimeUnits(1, TimeUnit.SECONDS, TimeUnit.HOURS);
		int n = (int) (tradingDuration / dt);
		// IntegerValuedAutoregressiveProcess inarProcess = new
		// IntegerValuedAutoregressiveProcess(1, dt, n );

		TradingProcess filtration = new TradingProcess(symbol, n);

		DoubleRowMatrix buys = filtration.buys;

		DoubleRowMatrix sells = filtration.sells;

		double bidPrice = 0, askPrice = Double.POSITIVE_INFINITY;
		double midPrice = 0;
		int tradeCount = 0;
		boolean verbose = false;
		Double openPrice = null;
		Double closePrice = null;

		DoubleRowMatrix tradeCounts = filtration.getTrades();
		for (ArchivableEvent event : mpp)
		{
			double spread = askPrice - bidPrice;
			// out.println( "event=" + event );
			int tk = (int) floor((event.getTimeOfDay() - openTime) / dt);

			if (event instanceof TradeTick)
			{
				TradeTick trade = (TradeTick) event;
				double tradePrice = trade.getPrice();
				int tradeSize = trade.getVolume();

				if (tradePrice > 0)
				{
					if (tk > 0 && tk < n)
					{
						if (openPrice == null)
						{
							openPrice = tradePrice;
						}
						closePrice = tradePrice;

						double increment =  1;

						tradeCounts.set(tk, 0, tradeCounts.get(tk, 0) + increment);

						boolean isBuyerInitiated = tradePrice > midPrice;
						boolean isSellerInitiated = tradePrice < midPrice;
						boolean isIndeterminate = tradePrice == midPrice;

						
						if (isBuyerInitiated)
						{
							buys.set(tk, 0, buys.get(tk, 0) + increment);

						}
						if (isSellerInitiated)
						{
							sells.set(tk, 0, sells.get(tk, 0) + increment);
						}

					} else
					{

						// disregard pre-market trade activity for now
					}
				}
			} else if (event instanceof TwoSidedQuote)
			{
				TwoSidedQuote quote = (TwoSidedQuote) event;
				bidPrice = quote.getBid();
				askPrice = quote.getAsk();
				midPrice = (bidPrice + askPrice) / 2;
			}
			
			if (tk > 0 && tk < n)
			{
				filtration.prices.set(tk, 0, midPrice );
			}

		}

		for ( int i = 1; i < n; i++ )
		{
			if ( filtration.prices.get(i, 0) == 0 )
			{
				filtration.prices.set(i, 0, filtration.prices.get(i - 1, 0) );
			}
		}
		for ( int i = 0; i < n; i++ )
		{
			if ( filtration.prices.get(i, 0) == 0 )
			{
				filtration.prices.set(i, 0, openPrice );
			}
		}
		
		out.println("Writing to " + outputMatfileName);

		if (squash)
		{
			tradeCounts.asVector().add(1).log();
			filtration.buys.asVector().add(1).log();
			filtration.sells.asVector().add(1).log();
		}
		
//		Vector h = tradeCounts.asVector().getRescaledRange().setName("h");

		MatFile.write(outputMatfileName, tradeCounts.createMiMatrix(), buys.createMiMatrix(),
				sells.createMiMatrix(), filtration.prices.createMiMatrix() );

		
		double range = Math.abs( closePrice - openPrice );
		midPrice = ( closePrice + openPrice ) / 2;
		double pcntRange = ( range / midPrice ) * 100;
		
		out.println("range " + openPrice + " -> " + closePrice + "    " + pcntRange + "%" );
		
		return filtration;
	}
}
