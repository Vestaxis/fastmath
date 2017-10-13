package stochastic.processes.pointprocesses;

import static java.lang.System.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import fastmath.DoubleRowMatrix;
import fastmath.Pair;
import fastmath.Vector;
import fastmath.matfile.MatFile;
import stochastic.processes.pointprocesses.finance.TradeTick;

/**
 * TODO: implement Lee-Ready tick test
 * 
 *
 */
public class MPP2Mat
{

	private static double openTime = 9.5;

	final static TimeUnit timeUnits = TimeUnit.MILLISECONDS;

	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		MarkedPointProcess mpp = loadMppFile(args[0]);
		DoubleRowMatrix tradeMatrix = new DoubleRowMatrix(0, TradeTick.FIELDCNT).setName(mpp.symbol);

		final Vector lastt = new Vector(1);
		mpp.tradeStream().forEach(event -> {
			Vector point = new Vector(event.getMarks());
			double fractionalHourOfDay = event.getTimeOfDay();
			double t = event.getTimestampInMilliseconds();
			//double t = fractionalHourOfDay;
			//double t = DateUtils.convertTimeUnits(fractionalHourOfDay, TimeUnit.MILLISECONDS, timeUnits);
      point.set(0, t );
			double prevt = lastt.get(0);
			if (prevt == t)
			{
				return;
			}

			if (fractionalHourOfDay >= openTime && fractionalHourOfDay <= closeTime)
			{
				lastt.set(0, t);
				out.println("point " + point);
				tradeMatrix.appendRow(point);
			}
		});
		MatFile.write(args[1], tradeMatrix.createMiMatrix());
	}

	private static double closeTime = 16;

	private static MarkedPointProcess loadMppFile(String mppFilename) throws FileNotFoundException, IOException
	{
		Pair<RandomAccessFile, RandomAccessFile> mppDataIndexPair = new Pair<>(new RandomAccessFile(mppFilename, "r"),
				new RandomAccessFile(mppFilename + ".idx", "r"));

		File mppFile = new File(mppFilename);
		String symbol = mppFile.getName().split("-")[0];
		return new MarkedPointProcess(mppDataIndexPair, mppFile, symbol);
	}

}
