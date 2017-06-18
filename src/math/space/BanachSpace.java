package math.space;

import math.Field;
import math.geometry.differential.FrechetSpace;
import math.properties.Complete;

/**
 * a Banach space (pronounced [ˈbanax]) is a complete normed vector space. Thus,
 * a Banach space is a vector space with a metric that allows the computation of
 * vector length and distance between vectors and is complete in the sense that
 * a Cauchy sequence of vectors always converges to a well defined limit in the
 * space.
 * 
 * Banach spaces are named after the Polish mathematician Stefan Banach, who
 * introduced and made a systematic study of them in 1920–1922 along with Hans
 * Hahn and Eduard Helly.[1] Banach spaces originally grew out of the study of
 * function spaces by Hilbert, Fréchet, and Riesz earlier in the century. Banach
 * spaces play a central role in functional analysis. In other areas of
 * analysis, the spaces under study are often Banach spaces.
 */
@Complete
public interface BanachSpace<F extends Field<?>> extends NormedVectorSpace<F>, FrechetSpace<F>
{

}
