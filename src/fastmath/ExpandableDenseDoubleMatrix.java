package fastmath;

public class ExpandableDenseDoubleMatrix extends DoubleColMatrix
{
  private static final long serialVersionUID = 1L;

  private int currentRow = -1;

  private double rowStorageExpansionFactor = 1.5;

  public ExpandableDenseDoubleMatrix(String name, int m, int n)
  {
    super( m, n );
    this.name = name;
  }

  public ExpandableDenseDoubleMatrix(int m, int n)
  {
    super( m, n );
  }

  /**
   * Resizes the buffer so that it only contains the rows up to and including
   * currentRow. If currentRow+1==numRows() then nothing happens.
   * 
   * @returns # of rows trimmed
   */
  public int compactify()
  {
    final int shrunkNumRows = ( currentRow + 1 );
    if ( shrunkNumRows != getRowCount() )
    {
      final int numTrimmed = getRowCount() - shrunkNumRows;
      safelyChangeNumRows( currentRow + 1 );
      return numTrimmed;
    }
    else
    {
      return 0;
    }
  }

  /**
   * Starts at -1 (empty)
   * 
   * @param currentRow
   */
  public void setCurrentRow( int currentRow )
  {
    this.currentRow = currentRow;
  }

  /**
   * Starts at -1 (empty)
   * 
   * @return
   */
  public int getCurrentRow()
  {
    return currentRow;
  }

  /**
   * Adds a row, expanding storage space if necessary
   * 
   * @return currentRow which was just added
   */
  public int addRow()
  {
    currentRow++;
    if ( currentRow >= getRowCount() )
    {
      growRows();
    }
    return currentRow;
  }

  /**
   * Grow number of rows by rowStorageExpansionFactor.
   * 
   * numRows(after)=(numRows(before)+1)*rowStorageExpansionFactor
   */
  public void growRows()
  {
    safelyChangeNumRows( (int) ( ( getRowCount() + 1 ) * rowStorageExpansionFactor ) );
  }

  /**
   * Creates a new matrix of dimension newNumRows by this.numCols() and copies
   * existing contents and swaps the underlying buffer of this matrix, so
   * temporarily about twice the ram is required for the swap
   * 
   * @param newNumRows
   */
  private void safelyChangeNumRows( int newNumRows )
  {
    DoubleMatrix newMatrix = new ExpandableDenseDoubleMatrix( name, newNumRows, getColCount() );
    final int minRows = Math.min( getRowCount(), newNumRows );
    newMatrix.sliceRows( 0, minRows ).assign( this.sliceRows( 0, minRows ) );
    this.numCols = newMatrix.numCols;
    this.numRows = newMatrix.numRows;
    this.buffer = newMatrix.buffer;
  }

  /**
   * Defaults to 1.5
   * 
   * @param rowStorageExpansionFactor
   * 
   * @see this{@link #growRows()}
   */
  public void setRowStorageExpansionFactor( double rowStorageExpansionFactor )
  {
    this.rowStorageExpansionFactor = rowStorageExpansionFactor;
  }

  /**
   * Defaults to 1.5
   * 
   * @return rowStorageExpansionFactor
   * 
   * @see this{@link #growRows()}
   * 
   */
  public double getRowStorageExpansionFactor()
  {
    return rowStorageExpansionFactor;
  }

}
