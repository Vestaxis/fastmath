package util;

import static java.lang.String.format;
import static java.lang.System.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fastmath.Vector;
import fastmath.matfile.MatFile;
import stochastic.processes.point.MPP2Mat;

/**
 * TODO: see {@link MPP2Mat} for a prototype
 * 
 * "date": "1508791213", "tid": "24236014", "price": "5934.80", "type": "0",
 * "amount":
 */
public class JSONBitcoinParser
{

  public static class PriceRecord
  {
    long date;

    long tid;

    double price;

    int type;

    double amount;

    @Override
    public String toString()
    {
      return format("PriceRecord[date=%d, tid=%d, price=%f, type=%d, amount=%f]", date, tid, price, type, amount);
    }
  }

  public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException
  {
    String filename = args.length == 0 ? "/home/stephen/git/bitcoin-data/bitcoin-trade-data-buy-vs-sell.json" : args[0];
    Gson gson = new Gson();
    PriceRecord[] records = gson.fromJson(new FileReader( filename ), PriceRecord[].class);
    Arrays.sort(records, new Comparator<PriceRecord>()
    {
      @Override
      public int compare(PriceRecord arg0, PriceRecord arg1)
      {
        return Long.compare(arg0.date, arg1.date);
      }
    });
    Vector times = new Vector(records.length);
    int i = 0;
    for (PriceRecord record : records)
    {
      out.println(record);
      times.set(i++, record.date);
    }

    Vector dt = times.diff();
    out.println("E[dt]=" + dt.mean());
    out.println("var[dt]=" + dt.variance());
    
    MatFile.write("bitcoin.mat", times.setName("times").createMiMatrix() );
   
  }

}
