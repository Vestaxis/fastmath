package fastmath.arb;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * <code>
typedef struct
{
    ulong p;    /* underlying prime 
int e; /* exponent 
nmod_t pe; /* modulus 
nmod_t phi; /* phi(p^e) 
ulong g; /* conrey generator 
dlog_precomp_struct*dlog; /* precomputed data for discrete log mod p^e 
}dirichlet_prime_group_struct;*
</code>*
 * 
 *
 */
public class DirichletPrimeGroup extends Structure
{
  private static final List<String> fieldOrder = Arrays.asList( new String[]
  {
    "p",
    "e",
    "pe",
    "phi",
    "g",
    "dlog"
  } );

  public long p;
  public int e;
  public NMod pe;
  public NMod phi;
  public long g;
  public Pointer dlog;

  public DirichletPrimeGroup()
  {
  }

  @Override
  protected List<String> getFieldOrder()
  {
    return fieldOrder;
  }

}
