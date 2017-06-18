package math.geometry.differential;

import math.Field;
import math.space.VectorSpace;
import stochastic.SigmaField;

public interface TopologicalVectorSpace<F extends Field<?>> extends TopologicalSpace<F, SigmaField<F>>, VectorSpace<F>
{

}
