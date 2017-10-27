package util;

import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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

    HashSet<java.util.List<String>> present = new HashSet<>();

    int P = 3;
    ExponentialHawkesProcessAutocovarianceSolver.enumerate(P).forEach(row -> {
      out.println(row);
      present.add(row);
    });

    out.println("....");

    List<String> vars = Stream
                              .concat(rangeClosed(1, P).mapToObj(i -> "α" + i),
                                      rangeClosed(1, P).mapToObj(i -> "β" + i))
                              .collect(Collectors.toList());

    int n = (int) CombinatoricsUtils.binomialCoefficient(P + 1, 2);

    AtomicInteger thereCount = new AtomicInteger();
    AtomicInteger extraCount = new AtomicInteger();

    StreamSupport.stream(new HomogeniousCombinator<>(vars, n).spliterator(),
                         false)
                 .filter(l -> {
                   boolean twoVarsPresent = new HashSet<String>(l).size() > 1;
                   boolean moreThanOneIndexPresent = getIndexSet(l).size() > 1;

                   int maxMultiplicity =
                                       getMultiplicities(l).values()
                                                           .stream()
                                                           .mapToInt(atom -> atom.get())
                                                           .max()
                                                           .getAsInt();

                   boolean containsAtLeastOneβ = getCharSet(l).contains("β");
                   boolean maxMultiplicityNoGreaterThanP = maxMultiplicity <= P;

                   return twoVarsPresent && moreThanOneIndexPresent
                          && containsAtLeastOneβ
                          && maxMultiplicityNoGreaterThanP;
                 })
                 .forEach(row -> {
                   boolean there = present.contains(row);
                   if (there)
                   {
                     thereCount.incrementAndGet();
                   }
                   else
                   {
                     extraCount.incrementAndGet();
                   }

                   out.println(row + " "
                               + (there ? " " : "*")
                               + " multiplicities="
                               + getMultiplicities(row));

                 });
    out.println("matching count " + thereCount);
    out.println("extra count " + extraCount + " (starred)");
  }

  public static AutoHashMap<String, AtomicInteger> getMultiplicities(
      List<String> l)
  {
    AutoHashMap<String, AtomicInteger> multiplicities =
                                                      new AutoHashMap<>(AtomicInteger.class);

    l.forEach(var -> multiplicities.getOrCreate(var).getAndIncrement());
    return multiplicities;
  }

  public static Set<String> getIndexSet(List<String> l)
  {
    Stream<String> chars = l.stream().map(x -> x.substring(1, x.length()));
    Set<String> charSet = chars.collect(Collectors.toSet());
    return charSet;
  }

  public static Set<String> getCharSet(List<String> l)
  {
    Stream<String> chars = l.stream().map(x -> String.valueOf(x.charAt(0)));
    Set<String> charSet = chars.collect(Collectors.toSet());
    return charSet;
  }

}