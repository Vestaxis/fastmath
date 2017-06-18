package fastmath;

import static fastmath.Functions.Z;
import static fastmath.Functions.ZNewton;
import static fastmath.Functions.Zd;
import static fastmath.Functions.lnΓ;
import static fastmath.Functions.Γ;
import static fastmath.Functions.ζ;
import static fastmath.Functions.ϑ;
import static java.lang.Math.pow;

import fastmath.arb.Complex;
import junit.framework.TestCase;

public class FunctionsTest extends TestCase
{

  private static double ε18 = pow( 10, -18 );

  private static double ε15 = pow( 10, -15 );

  private static double ε10 = pow( 10, -10 );

  private static double ε7 = pow( 10, -7 );

  public void testRealGamma()
  {
    assertEquals( 0.88817765859688764895, Γ( 1.54 ), pow( 10, -15 ) );
  }


  public static void testComplexLnΓ()
  {
    double z[] = new double[2];
    lnΓ( 1.54, 0, z );
    assertEquals( -0.11858349001301166, z[0], ε15 );
    assertEquals( 0, z[1], ε7 );

    lnΓ( 1.54, -3.2, z );
    assertEquals( -2.884058186, z[0], ε7 );
    assertEquals( -2.001381154, z[1], ε7 );

    lnΓ( 1.54, 3.2, z );
    assertEquals( -2.884058186, z[0], ε7 );
    assertEquals( 2.001381154, z[1], ε7 );

    lnΓ( -1.54, 3.2, z );
    assertEquals( -6.598469754, z[0], ε7 );
    assertEquals( -3.285206941, z[1], ε7 );

    lnΓ( -1.54, -3.2, z );
    assertEquals( -6.598469754, z[0], ε7 );
    assertEquals( 3.285206941, z[1], ε7 );

    lnΓ( -4, 5, z );
    assertEquals( -14.67242454, z[0], ε7 );
    assertEquals( -5.831145439, z[1], ε7 );

    lnΓ( -4, -5, z );
    assertEquals( -14.67242454, z[0], ε7 );
    assertEquals( 5.831145439, z[1], ε7 );

    lnΓ( -0.75, -6.55, z );
    assertEquals( -11.7254389077716079819991018353, z[0], ε15 );
    assertEquals( -3.68458424667379915362378888025, z[1], ε15 );

    lnΓ( -0.75, 6.55, z );
    assertEquals( -11.7254389077716079819991018353, z[0], ε15 );
    assertEquals( 3.68458424667379915362378888025, z[1], ε15 );

  }

  public void testComplexΓ()
  {
    double z[] = new double[2];
    Γ( 7.0, 5.0, z );
    assertEquals( -115.23991975877083853082790278224693, z[0], ε15 );
    assertEquals( -43.558288885674999945574559017170623, z[1], ε15 );

    Γ( -3.4, 1.0, z );
    assertEquals( -0.352826720648729292508071306732e-2, z[0], ε15 );
    assertEquals( 0.301387037447151721513917428321e-1, z[1], ε15 );

    Γ( -3.4, -1.0, z );
    assertEquals( -0.352826720648729292508071306732e-2, z[0], ε15 );
    assertEquals( -0.301387037447151721513917428321e-1, z[1], ε15 );

    Γ( 3.4, 1.0, z );
    assertEquals( 1.16990400763256447025917304607, z[0], ε15 );
    assertEquals( 2.23310426702965685834250007262, z[1], ε15 );

  }

  public void testΨ()
  {
    double r[] = new double[2];
    Functions.Ψ( 0.25, 7, r );
    assertEquals( 1.94569737369985, r[0], ε7 );
    assertEquals( 1.60655646162596, r[1], ε7 );

    Functions.Ψ( 0.25, -7, r );
    assertEquals( 1.94569737369985, r[0], ε10 );
    assertEquals( -1.60655646162596, r[1], ε10 );
  }

  public void testZSteffensen()
  {
    Complex t = new Complex( 13, 0 );
    final Complex Zs = t.ZSteffensen( 1 );
    assertEquals( 14.604410082377371140, Zs.getReal().sixtyFourBitValue() );
    assertEquals( 0.0, Zs.getImaginary().sixtyFourBitValue() );
  }

  public void testZd()
  {
    double result[] = new double[2];
    Zd( 13, 4.7, result );
    assertEquals( .42440015396466786284846186198610154, result[0], ε15 );
    assertEquals( 2.4656806242238148178483887339482189, result[1], ε15 );

    Zd( 36, 9, result );
    assertEquals( 554.25577602419369897724446549877278, result[0], ε15 );
    assertEquals( -2357.3960705885495555293019546355277, result[1], ε15 );

    Zd( 36, -9, result );
    assertEquals( 554.25577602419369897724446549877278, result[0], ε15 );
    assertEquals( 2357.3960705885495555293019546355277, result[1], ε15 );
  }

  public void testZ()
  {
    double result[] = new double[2];

    Z( 13, -4.7, result );
    assertEquals( -4.8308073500893160937460491914366815, result[0], ε15 );
    assertEquals( -2.9360886182990631621239559283323098, result[1], ε15 );

    Z( 13, 4.7, result );
    assertEquals( -4.8308073500893160937460491914366815, result[0], ε15 );
    assertEquals( 2.9360886182990631621239559283323098, result[1], ε15 );

    Z( 25, 0, result );
    assertEquals( -0.01487248389797099820581667275892163845447631218041568767049752398049546145582674887807292105580778031, result[0], ε15 );
    assertEquals( 0, result[1], ε15 );

    Z( 35, 0.69, result );
    assertEquals( 3.167103877623915973999943931493880055116897503904479051631044410052569613682771792968293828629237091, result[0], ε15 );
    assertEquals( 0.4326260352061854727616073061641705173178977752415261690873230631471074540830405912044787468961247163, result[1], ε15 );

    Z( -35, 0.69, result );
    assertEquals( 3.167103877623915973999943931493880055116897503904479051631044410052569613682771792968293828629237091, result[0], ε15 );
    assertEquals( -0.4326260352061854727616073061641705173178977752415261690873230631471074540830405912044787468961247163, result[1], ε15 );

    Z( -35, -0.69, result );
    assertEquals( 3.167103877623915973999943931493880055116897503904479051631044410052569613682771792968293828629237091, result[0], ε15 );
    assertEquals( 0.4326260352061854727616073061641705173178977752415261690873230631471074540830405912044787468961247163, result[1], ε15 );

    Z( 35, -0.69, result );
    assertEquals( 3.167103877623915973999943931493880055116897503904479051631044410052569613682771792968293828629237091, result[0], ε15 );
    assertEquals( -0.4326260352061854727616073061641705173178977752415261690873230631471074540830405912044787468961247163, result[1], ε15 );

    Z( 36, 9, result );
    assertEquals( 2692.5513515566075528097323826662393, result[0], ε15 );
    assertEquals( 252.58563520904384702521574648882671, result[1], ε15 );

    Z( 36, -9, result );
    assertEquals( 2692.5513515566075528097323826662393, result[0], ε15 );
    assertEquals( -252.58563520904384702521574648882671, result[1], ε15 );

  }

  public void testϑ()
  {
    double r[] = new double[2];

    ϑ( 17.8455995404108608168263384125, 0, r );

    assertEquals( 0, r[0], ε15 );
    assertEquals( 0, r[1], ε15 );

    ϑ( 17.8455995404108608168263384125, 1, r );

    assertEquals( -0.0140053917688534784264264245, r[0], ε15 );
    assertEquals( .522136120192835059458240169375, r[1], ε15 );

    ϑ( -17.8455995404108608168263384125, 1, r );

    assertEquals( 0.0140053917688534784264264245, r[0], ε15 );
    assertEquals( 0.522136120192835059458240169375, r[1], ε15 );

    ϑ( -13.1, 0, r );

    assertEquals( 2.12859288645153312422668381539, r[0], ε15 );
    assertEquals( 0, r[1], ε15 );

    ϑ( -13.1, -2, r );

    assertEquals( 2.20467124308369553753797387654, r[0], ε15 );
    assertEquals( -0.73835557597465332587678607964, r[1], ε15 );

  }


  // public static void testζd()
  // {
  // double r[] = new double[2];
  //
  // ζd( 2, 0, r );
  // assertEquals( -.937548254315843753702574094568, r[0], ε15 );
  // assertEquals( 0, r[1], ε15 );
  //
  // ζd( 0.5, 19, r );
  // assertEquals( -.539402869632323702481561533546, r[0], ε10 );
  // assertEquals( 1.14276796797750122499725678544, r[1], ε10 );
  //
  // }

  public static void testZNewton()
  {
    double r[] = new double[2];

    ZNewton( 12.0, 2.0, r );
    assertEquals( 12.4684044301930357987181844950, r[0], ε15 );
    assertEquals( -1.07949222404664391077082165672, r[1], ε15 );

    ZNewton( 12.0, -2.0, r );
    assertEquals( 12.4684044301930357987181844950, r[0], ε15 );
    assertEquals( 1.07949222404664391077082165672, r[1], ε15 );

    ZNewton( 6.2, 4.0, r );
    assertEquals( 3.21629658115384382175789547394, r[0], ε15 );
    assertEquals( 3.16081453181957753005264814337, r[1], ε15 );

    ZNewton( 36.5, 0.0, r );
    assertEquals( 37.814538981925482677787021447952956, r[0], ε15 );
    assertEquals( 0, r[1], ε15 );
  }

  public static void testζ()
  {
    double r[] = new double[2];
    double x = 2;
    double y = 0;
    ζ( x, y, r );

    assertEquals( 1.644934066848226436, r[0], ε18 );
    assertEquals( 0, r[1], ε18 );

    x = 3.4;
    y = 7.2;
    ζ( x, y, r );

    assertEquals( 1.0206407158967914573, r[0], ε18 );
    assertEquals( 0.073502627338817720814, r[1], ε15 );

    x = -3.5;
    y = 3;

    ζ( x, y, r );

    assertEquals( -0.039984251832396, r[0], ε15 );
    assertEquals( 0.1072726838528944, r[1], ε15 );

    x = 0.5;
    y = 14;
    ζ( x, y, r );
    assertEquals( 0.022241142609993589246, r[0], pow( 10, -14 ) );
    assertEquals( -0.10325812326645005790, r[1], pow( 10, -14 ) );

    x = 0.7;
    y = 0.4;
    ζ( x, y, r );
    assertEquals( -.6443351023079596578585717842547561767947539612753569616107307673394797194461551717458564596420574312, r[0], ε15 );
    assertEquals( -1.569722421372582734126841256878624915414502249028697613843156287890330740850353843583404237846013679, r[1], ε15 );

    x = 0.8;
    y = 0.2;
    ζ( x, y, r );
    assertEquals( -1.937353609658866480901584293536823780166238387775861609763770138421778328249629772672045020010669147, r[0], ε15 );
    assertEquals( -2.485054684985720652772989235484418043925474867227205698649710840379360478474778022372938395571870853, r[1], ε15 );

    x = 2.0;
    y = 2.0;
    ζ( x, y, r );
    assertEquals( .867351829635993064984331343735, r[0], ε15 );
    assertEquals( -0.275127238807857648618660643100, r[1], ε15 );

  }

  public void testMaxExp()
  {

  }

  public void testOuterProduct()
  {
    Vector a = new Vector( new double[]
    {
      1,
      2,
      3,
      4,
      5
    } );
    Vector b = new Vector( new double[]
    {
      6,
      7,
      8,
      9,
      10
    } );
    DoubleMatrix op = Functions.outerProduct( a, b );
    assertTrue( op.row( 0 ).equals( b ) );
    assertTrue( op.row( 1 ).equals( new Vector( new double[]
    {
      12,
      14,
      16,
      18,
      20
    } ) ) );
    assertTrue( op.row( 2 ).equals( new Vector( new double[]
    {
      18,
      21,
      24,
      27,
      30
    } ) ) );
    assertTrue( op.row( 3 ).equals( new Vector( new double[]
    {
      24,
      28,
      32,
      36,
      40
    } ) ) );
    assertTrue( op.row( 4 ).equals( new Vector( new double[]
    {
      30,
      35,
      40,
      45,
      50
    } ) ) );
    // log.info("op " + op);
  }

  public void testUniformRandom()
  {
    Pair<Double, Double> range = new Pair<>( 2.2, 6.7 );
    double minx = Double.MAX_VALUE;
    double maxx = Double.MIN_VALUE;
    for ( int i = 0; i < 1000; i++ )
    {
      double x = Functions.uniformRandom( range );
      maxx = x > maxx ? x : maxx;
      minx = x < minx ? x : minx;
      assertTrue( range.left <= x && x <= range.right );
    }
    // System.out.println(minx + " " + maxx );
  }

  public void testLambertW()
  {
    assertEquals( 0.567143290409783872999968662210355549753815787186512508135, Functions.W( 1.0 ) );
  }

}
