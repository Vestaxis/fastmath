package fastmath.arb;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * <code>
 * typedef struct
 * {
 *       ulong q;                /* modulus                                 8
 *       ulong q_even;           /* even part of modulus                    16
 *       nmod_t mod;             /* modulus with precomputed inverse     +24=40
 *       ulong rad_q;            /* radical = product of odd primes         48
 *       ulong phi_q;            /* phi(q) = group size                     56
 *       slong neven;            /* number of even components (in 0,1,2)    64
 *       slong num;              /* number of prime components (even + odd) 72
 *       ulong expo;             /* exponent = largest order in G           80
 *       dirichlet_prime_group_struct *P;                                   88       
 *       ulong * generators;     /* generators lifted mod q                 96  
 *       ulong * PHI;            /* PHI(k) = expo / phi(k)               // 104    
 * }
 * dirichlet_group_struct;
 * </code>
 * 
 * sizeof should be 104
 */
public class DirichletGroup extends Structure
{
  public long q;

  public long q_even;

  public NMod mod = new NMod();

  public long rad_q;
  public long phi_q;
  public long neven;
  public long num;
  public long exp;
  public DirichletPrimeGroupReference P;
  public long generators; // pointer to ulongs
  public long phi; // pointer to ulongs

  private static final List<String> fieldOrder = Arrays.asList( new String[]
  {
    "q",
    "q_even",
    "mod",
    "rad_q",
    "phi_q",
    "neven",
    "num",
    "exp",
    "P",
    "generators",
    "phi"
  } );

  public DirichletGroup()
  {
  }

  public DirichletGroup(int q)
  {
    init( q );
  }

  public DirichletCharacter newCharacter()
  {
    DirichletCharacter q = new DirichletCharacter();
    ArbLibrary.instance.dirichlet_char_init( q, this );
    return q;
  }

  protected void init( int q )
  {
    assert mod != null;
    ArbLibrary.instance.dirichlet_group_init( this, q );
  }

  @Override
  protected List<String> getFieldOrder()
  {
    return fieldOrder;
  }

}
