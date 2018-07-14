package fastmath;

import static java.lang.Math.pow;

import fastmath.exceptions.IllegalValueError;
import fastmath.exceptions.SingularFactorException;
import fastmath.matfile.MiDouble;
import junit.framework.TestCase;

public class DoubleMatrixTest extends TestCase
{

  public void testAppendColumn()
  {
    DoubleColMatrix a = new DoubleColMatrix( new double[][]
    {
      {
        2,
        -1,
        0
      },
      {
        -1,
        2,
        -1,
      },
      {
        0,
        -1,
        2
      }
    } );

    Vector newCol = a.appendColumn();

    newCol.assign( new double[]
    {
      9,
      4,
      0
    } );

    DoubleColMatrix b = new DoubleColMatrix( new double[][]
    {
      {
        2,
        -1,
        0,
        9
      },
      {
        -1,
        2,
        -1,
        4
      },
      {
        0,
        -1,
        2,
        0
      }
    } );
    assertEquals( b, a );
  }

  public void testSum()
  {
    DoubleMatrix a = new DoubleColMatrix( new double[][]
    {
      {
        1,
        2,
        3,
        4,
        5
      },
      {
        6,
        7,
        8,
        9,
        10
      }
    } );
    Vector asum = a.sum();
    assertEquals( asum, new Vector( new double[]
    {
      7,
      9,
      11,
      13,
      15
    } ) );
  }

  public void testColMatrix()
  {
    DoubleColMatrix matrix = new DoubleColMatrix( 2, 2 );
    matrix.set( 0, 0, 1.0 );
    matrix.set( 0, 1, 2.0 );
    matrix.set( 1, 0, 3.0 );
    matrix.set( 1, 1, 4.0 );
    Vector diag = matrix.diag();
    assertEquals( 5.0, diag.sum() );
  }

  public void testAsVector()
  {
    DoubleMatrix a = new DoubleColMatrix( new double[][]
    {
      {
        1,
        2,
        3,
        4,
        5
      },
      {
        6,
        7,
        8,
        9,
        10
      }
    } );
    Vector avec = a.asVector();
    assertEquals( avec, new Vector( new double[]
    {
      1,
      6,
      2,
      7,
      3,
      8,
      4,
      9,
      5,
      10
    } ) );
  }

  public void testLdivideSquare() throws SingularFactorException, IllegalValueError
  {
    DoubleMatrix a = new DoubleColMatrix( new double[][]
    {
      {
        2,
        -1,
        0
      },
      {
        -1,
        2,
        -1,
      },
      {
        0,
        -1,
        2
      }
    } );

    DoubleMatrix b = new DoubleColMatrix( new double[][]
    {
      {
        1,
        2,
      },
      {
        3,
        4,
      },
      {
        5,
        6
      }
    } );
    DoubleMatrix eq = new DoubleColMatrix( new double[][]
    {
      {
        3.5,
        5,
      },
      {
        6,
        8,
      },
      {
        5.5,
        7
      }
    } );
    DoubleMatrix res = a.ldivide( b );
    assertTrue( res.equals( eq, pow( 0.1, 10 ) ) );
  }

  public void testLdivide() throws SingularFactorException, IllegalValueError
  {
    DoubleMatrix a = new DoubleColMatrix( new double[][]
    {
      {
        1,
        4,
      },
      {
        2,
        5,
      },
      {
        3,
        6
      }
    } );
    DoubleMatrix b = new DoubleColMatrix( new double[][]
    {
      {
        7,
        10,
      },
      {
        8,
        11,
      },
      {
        9,
        12
      }
    } );
    DoubleMatrix eq = new DoubleColMatrix( new double[][]
    {
      {
        -1,
        -2
      },
      {
        2,
        3
      }
    } );
    DoubleMatrix res = a.ldivide( b );
    assertTrue( res.equals( eq, pow( 0.1, 14 ) ) );
  }

  public void testProd()
  {
    DoubleMatrix b = new DoubleColMatrix( new double[][]
    {
      {
        7,
        10,
      },
      {
        8,
        11,
      },
      {
        9,
        12
      }
    } );
    DoubleMatrix a = new DoubleColMatrix( new double[][]
    {
      {
        2,
        -1,
        0
      },
      {
        -1,
        2,
        -1,
      },
      {
        0,
        -1,
        2
      }
    } );
    DoubleColMatrix c = a.prod( b );
    DoubleMatrix shouldBe = new DoubleColMatrix( new double[][]
    {
      {
        6,
        9
      },
      {
        0,
        0
      },
      {
        10,
        13
      }
    } );
    assertEquals( shouldBe, c );

  }

  public void testProdTransA()
  {
    DoubleMatrix b = new DoubleColMatrix( new double[][]
    {
      {
        7,
        10,
      },
      {
        8,
        11,
      },
      {
        9,
        12
      }
    } );
    DoubleMatrix a = new DoubleColMatrix( new double[][]
    {
      {
        2,
        -1,
        0
      },
      {
        -1,
        2,
        -1,
      },
      {
        0,
        -1,
        2
      }
    } );
    DoubleColMatrix c = a.trans().prod( b );
    DoubleMatrix shouldBe = new DoubleColMatrix( new double[][]
    {
      {
        6,
        9
      },
      {
        0,
        0
      },
      {
        10,
        13
      }
    } );
    assertEquals( shouldBe, c );

  }

  public void testProdTransB()
  {
    DoubleMatrix b = new DoubleColMatrix( new double[][]
    {
      {
        7,
        8,
        9
      },
      {
        10,
        11,
        12
      }
    } );
    DoubleMatrix a = new DoubleColMatrix( new double[][]
    {
      {
        2,
        -1,
        0
      },
      {
        -1,
        2,
        -1,
      },
      {
        0,
        -1,
        2
      }
    } );
    DoubleColMatrix c = a.prod( b.trans() );
    DoubleMatrix shouldBe = new DoubleColMatrix( new double[][]
    {
      {
        6,
        9
      },
      {
        0,
        0
      },
      {
        10,
        13
      }
    } );
    assertEquals( shouldBe, c );

  }

  public void testDoubleRowMatrixAppend()
  {
    DoubleRowMatrix a = new DoubleRowMatrix( 0, 5 );
    Vector newRow = a.appendRow();
    newRow.set( 2, 6.9 );
    assertEquals( 6.9, a.get( 0, 2 ) );

  }

  public void testResizeSmaller()
  {
    DoubleColMatrix x = new DoubleColMatrix( new double[][]
    {
      {
        1,
        2,
        3,
        4
      },
      {
        5,
        6,
        7,
        8
      },
      {
        9,
        10,
        11,
        12
      },
      {
        13,
        14,
        15,
        16
      }
    } );
    x.resize( 2, 2 );
    System.out.println( x );
  }

  public void testResizeLarger()
  {
    DoubleColMatrix x = new DoubleColMatrix( new double[][]
    {
      {
        1,
        2,
        3,
        4
      },
      {
        5,
        6,
        7,
        8
      },
      {
        9,
        10,
        11,
        12
      },
      {
        13,
        14,
        15,
        16
      }
    } );
    x.resize( 5, 5 );
    System.out.println( x );
  }

  public void testRowsAndColumnsFromOffset()
  {
    for ( int numRows = 1; numRows < 10; numRows++ )
    {
      for ( int numCols = 1; numCols < 10; numCols++ )
      {
        DoubleColMatrix dcm = new DoubleColMatrix( numRows, numCols );

        for ( int i = 0; i < dcm.numRows; i++ )
        {
          for ( int j = 0; j < dcm.numCols; j++ )
          {
            int offset = dcm.getOffset( i, j ) / MiDouble.BYTES;
            int col = dcm.getOffsetCol( offset );
            int row = dcm.getOffsetRow( offset );
            assertEquals( Integer.toString( offset ), i, col );
            assertEquals( Integer.toString( offset ), j, row );
          }
        }
      }
    }
  }

}
