package fastmath;

import java.util.ArrayList;

public class BufferCache
{
  final ArrayList<MatrixContainer> matrices;

  final ArrayList<VectorContainer> vectors;

  public MatrixContainer getMatrix( int index )
  {
    while (index >= matrices.size())
    {
      matrices.add( new MatrixContainer() );
    }
    return matrices.get( index );
  }

  public VectorContainer getVector( int index )
  {
    while (index >= vectors.size())
    {
      vectors.add( new VectorContainer() );
    }
    return vectors.get( index );
  }

  public BufferCache()
  {
    matrices = new ArrayList<MatrixContainer>();
    vectors = new ArrayList<VectorContainer>();
  }

  public BufferCache(int numMatrices, int numVectors)
  {
    matrices = new ArrayList<MatrixContainer>( numMatrices );
    vectors = new ArrayList<VectorContainer>( numVectors );
    for ( int i = 0; i < numMatrices; i++ )
    {
      matrices.add( new MatrixContainer() );
    }
    for ( int i = 0; i < numVectors; i++ )
    {
      vectors.add( new VectorContainer() );
    }
  }

}
