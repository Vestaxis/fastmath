package fastmath.arb;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * <code>
 * typedef struct
   {
     fmpz exp;
      mp_limb_t man; 
   }
   mag_struct;
 * </code>
 * 
 * @author crow
 *
 */
public class MagStruct extends Structure
{
  private static final List<String> fields = Arrays.asList( new String[]
  {
    "exp",
    "man"
  } );
  public long exp;
  public long man;
  
  public MagStruct()
  {
  }

  @Override
  protected List<String> getFieldOrder()
  {
    return fields;
  }

}
