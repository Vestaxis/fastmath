package stochastic.processes.point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import fastmath.DoubleRowMatrix;
import fastmath.Pair;
import fastmath.Vector;
import fastmath.matfile.MatFile;

/**
 * TODO: implement Lee-Ready tick test
 * 
 * @see https://www.acsu.buffalo.edu/~keechung/MGF743/Readings/Inferring%20trade%20direction%20from%20intraday%20data.pdf
 * 
 */
public class MPP2Mat
{

  private static double openTime = 9.5;

  final static TimeUnit timeUnits = TimeUnit.MILLISECONDS;

  public static void main(String[] args) throws FileNotFoundException, IOException
  {
    MarkedPointProcess mpp = loadMppFile(args[0]);

    Vector midPrice = new Vector(1);
    Pair<DoubleRowMatrix, DoubleRowMatrix> buySellMatrix = mpp.getBuySellMatrix(timeUnits);

    //DoubleRowMatrix tradeMatrix = mpp.getTradeMatrix(timeUnits);
    MatFile.write(args[1], buySellMatrix.left.createMiMatrix(), buySellMatrix.right.createMiMatrix() );
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
