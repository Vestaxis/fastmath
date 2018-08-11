using Sharpen;

namespace fastmath
{
	/// <summary>
	/// TODO: see https://alm.engr.colostate.edu/cb/wiki/16999 and get rid of native
	/// methods...
	/// </summary>
	/// <remarks>
	/// TODO: see https://alm.engr.colostate.edu/cb/wiki/16999 and get rid of native
	/// methods... call fortran functions with JNA instead
	/// </remarks>
	public abstract class BLASLibrary : com.sun.jna.Library
	{
		public const fastmath.BLASLibrary instance = (fastmath.BLASLibrary)com.sun.jna.Native
			.loadLibrary("blas", Sharpen.Runtime.getClassForType(typeof(fastmath.BLASLibrary
			)));

		public abstract void daxpy_(int n, double a, com.sun.jna.Pointer x, int incx, com.sun.jna.Pointer
			 y, int incy);
	}

	public static class BLASLibraryConstants
	{
	}
}
