package stochastic.processes.point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import fastmath.DoubleRowMatrix;
import fastmath.Pair;
import fastmath.matfile.MatFile;

/**
 * TODO: implement Lee-Ready tick test
 * 
 *
 */
public class MPP2Mat
{

	private static double openTime = 9.5;

	final static TimeUnit timeUnits = TimeUnit.SECONDS;

	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		MarkedPointProcess mpp = loadMppFile(args[0]);
		DoubleRowMatrix tradeMatrix = mpp.getTradeMatrix();
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
