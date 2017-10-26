package util;

import static java.lang.System.out;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.math3.util.CombinatoricsUtils;

import com.github.dakusui.combinatoradix.HomogeniousCombinator;
import com.maplesoft.externalcall.MapleException;

import stochastic.processes.hawkes.solvers.ExponentialHawkesProcessAutocovarianceSolver;

public class Combinations
{

  public static void main(String[] args) throws MapleException
  {
    ExponentialHawkesProcessAutocovarianceSolver.main(args);
    List<String> vars = Arrays.asList("α1", "α2", "β1", "β2");
    int n = (int) CombinatoricsUtils.binomialCoefficient(3, 2);

    StreamSupport.stream(new HomogeniousCombinator<>(vars, n).spliterator(),
                         false)
                 .filter(l -> {
                   Stream<String> chars =
                                        l.stream()
                                         .map(x -> String.valueOf(x.charAt(0)));
                   Set<String> charSet = chars.collect(Collectors.toSet());
                   return new HashSet<String>(l).size() > 1
                          && charSet.contains("β");
                 })
                 .forEach(out::println);
  }

}