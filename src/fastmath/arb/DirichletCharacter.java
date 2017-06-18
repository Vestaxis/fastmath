package fastmath.arb;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.ByValue;

/**
 * <code> 
  typedef struct
  {
    ulong n;           /* number 
    ulong * log;       /* s.t. prod generators[k]^log[k] = number 
  }
  dirichlet_char_struct; 
 * </code>
 * 
 * @author crow
 *
 */
public class DirichletCharacter extends Structure implements ByValue
{
  public long n;

  public Pointer log; // pointer to array of ulongs

  private static final List<String> fieldOrder = Arrays.asList( new String[]
  {
    "n",
    "log"
  } );

  @Override
  protected List<String> getFieldOrder()
  {
    return fieldOrder;
  }

  public Complex χ( DirichletGroup group, int n )
  {
    Complex chi = new Complex();
    ArbLibrary.instance.acb_dirichlet_chi( chi, group, this, n, Real.bits );
    return chi;
  }

}
