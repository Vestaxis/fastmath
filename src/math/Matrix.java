package math;

public interface Matrix<K extends Field<?>> extends Set
{
  public int getRowCount();

  public int getColCount();

}
