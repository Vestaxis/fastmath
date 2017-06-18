package fastmath.arb;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class Mantissa extends Structure
{

  public long d0;

  public long d1;

  @Override
  protected List<String> getFieldOrder()
  {
    return Arrays.asList( "d0", "d1" );
  }

}
