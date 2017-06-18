package fastmath.arb;

import com.sun.jna.Structure.ByReference;

public class ComplexReference extends Complex implements ByReference
{

  public ComplexReference()
  {
    
  }
  
  public ComplexReference(Complex complex)
  {
    super(complex);
  }
}
