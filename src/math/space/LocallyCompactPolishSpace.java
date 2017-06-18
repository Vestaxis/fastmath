package math.space;

import math.Set;
import math.Topology;

public interface LocallyCompactPolishSpace<X extends Set, B extends Topology<? extends X>> extends LocallyCompactSpace<X, B>, PolishSpace<X, B>
{

}
