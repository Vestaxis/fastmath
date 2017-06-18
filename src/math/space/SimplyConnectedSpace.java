package math.space;

/**
 * a topological space is called simply-connected (or 1-connected) if it is
 * path-connected and every path between two points can be continuously
 * transformed, staying within the space, into any other such path while
 * preserving the two endpoints in question (see below for an informal
 * discussion).
 * 
 * If a space is not simply-connected, it is convenient to measure the extent to
 * which it fails to be simply-connected; this is done by the fundamental group.
 * Intuitively, the fundamental group measures how the holes behave on a space;
 * if there are no holes, the fundamental group is trivial — equivalently, the
 * space is simply connected.
 */
public interface SimplyConnectedSpace extends ConnectedSpace
{

}
