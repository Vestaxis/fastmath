package fastmath;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface BLASLibrary extends Library
{
  BLASLibrary instance = (BLASLibrary) Native.loadLibrary("blas", BLASLibrary.class);

  void daxpy_(int n,
              double a,
              Pointer x,
              int incx,
              Pointer y,
              int incy);
}
