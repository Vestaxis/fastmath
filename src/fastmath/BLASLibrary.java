package fastmath;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * TODO: see https://alm.engr.colostate.edu/cb/wiki/16999 and get rid of native
 * methods... call fortran functions with JNA instead
 */
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
