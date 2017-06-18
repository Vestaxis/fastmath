package util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class StringHashSet extends HashSet<String> implements StringSet
{
  private static final long serialVersionUID = 1L;

  public StringHashSet()
  {

  }

  public StringHashSet(String strings[])
  {
    super( Arrays.asList( strings ) );
  }

  public StringHashSet(StringSet set)
  {
    super( set );
  }

  public StringHashSet(List<String> stringList)
  {
    super( stringList );
  }

}
