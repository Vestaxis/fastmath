package stochastic.measures;

import math.Set;
import math.space.CompleteSeparableMetricSpace;
import math.space.Space;
import stochastic.SigmaField;

public interface MeasurableSpace<X extends Set, Σ extends SigmaField<X>> extends Space, CompleteSeparableMetricSpace<X, Σ>
{
  public Σ getSigmaField();
}
