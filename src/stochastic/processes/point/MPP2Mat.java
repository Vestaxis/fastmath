package stochastic.processes.point;

import static java.lang.System.out;
import static util.Plotter.plot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fastmath.DoubleRowMatrix;
import fastmath.Pair;
import fastmath.matfile.MatFile;

public class MPP2Mat
{

  private static double openTime = 9.5;

  final static TimeUnit timeUnits = TimeUnit.MILLISECONDS;

  public static void main(String[] args) throws FileNotFoundException, IOException
  {
    String mppFile = args[0];
    String matFile = args[1];
    mpp2mat(mppFile, matFile);
  }

  public static DoubleRowMatrix mpp2mat(String mppFile, String matFile) throws FileNotFoundException, IOException
  {
    MarkedPointProcess mpp = MarkedPointProcess.loadMppFile(mppFile);

    Pair<DoubleRowMatrix, DoubleRowMatrix> buySellMatrix = mpp.getBuySellMatrix(timeUnits);

    DoubleRowMatrix tradeMatrix = mpp.getTradeMatrix(timeUnits);
    out.println( "bucketCounts=" + mpp.getBucketCounts() );
    out.println( "writing to " + matFile );
    plot( "counts", mpp.getBucketCounts() );
    MatFile.write(matFile, buySellMatrix.left.createMiMatrix(), buySellMatrix.right.createMiMatrix(), tradeMatrix.createMiMatrix() );
    return tradeMatrix;
  }

  private static double closeTime = 16;
  

}
