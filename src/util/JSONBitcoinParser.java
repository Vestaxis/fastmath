package util;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import stochastic.processes.point.MPP2Mat;

/**
 * TODO: see {@link MPP2Mat} for a prototype
 * 
 *
 */
public class JSONBitcoinParser
{

  public class PriceRecord
  {
    double t;
    
    double price;
  }
  
  public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException
  {
    Gson gson = new Gson();
    PriceRecord record = gson.fromJson(new FileReader( args[0] ) , PriceRecord.class );
    

  }

}
