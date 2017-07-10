package fastmath;

import junit.framework.TestCase;

public class DoubleRowMatrixTest extends TestCase {
	public static void testGetSparse()
	{
		DoubleRowMatrix row = new DoubleRowMatrix( new double[][] { { 1400, 2.3 }, { 1900.4, 2.5 }, {2000,5.5}, {4000,7.6}, {4001,7.8} } );
		double getFirst = row.getSparse(1405, 0);
		System.out.println( "getFirst=" + getFirst );
		assertEquals( 2.3, getFirst );
		double getSecond = row.getSparse(1901, 0);
		System.out.println( "getSecond=" + getSecond );
		assertEquals( 2.5, getSecond );
		double getThird = row.getSparse(2001, 0);
		System.out.println( "getThird=" + getThird );
		assertEquals( 5.5, getThird );
		double getZeroth = row.getSparse(0, 0);
		System.out.println( "getZeroth=" + getZeroth );
		assertEquals( 0.0, getZeroth );
	}
}
