package math;

import java.util.function.Function;

public interface SetFunction<D extends Set, R extends Set> extends Set, Function<D, R>
{

}
