package fastmath;

import java.nio.DoubleBuffer;

import com.sun.jna.Library;

public interface BLASLibrary extends Library
{
//  BLASLibrary instance = (BLASLibrary) Native.loadLibrary("blas", BLASLibrary.class);

  int daxpy(int n, double a, DoubleBuffer x, int incx, DoubleBuffer y, int incy);
}
