package stochastic.processes.pointprocesses;

import math.space.LocallyCompactPolishSpace;

/**
 * a determinantal point process is a stochastic point process, the probability
 * distribution of which is characterized as a determinant of some function.
 * 
 * 
 * @param <X>
 */
public interface DeterminentalPointProcess<X extends LocallyCompactPolishSpace<?, ?>> extends PointProcess<X>
{

}
