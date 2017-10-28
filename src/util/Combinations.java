package util;

import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.math3.util.CombinatoricsUtils;

import com.github.dakusui.combinatoradix.HomogeniousCombinator;
import com.maplesoft.externalcall.MapleException;

import fastmath.Vector;
import stochastic.processes.hawkes.solvers.ExponentialHawkesProcessAutocovarianceSolver;

public class Combinations
{

  public static Comparator<? super List<String>> getTermMultipleComparator(
      int P)
  {
    return (a, b) -> {
      int[] aMultiples = getTermMultiplicitiesArray(a, P);
      int[] bMultiples = getTermMultiplicitiesArray(b, P);

      for (int i = 0; i < P * 2; i++)
      {
        if (aMultiples[i] < bMultiples[i])
        {
          return -(i+1);
        }
        else if (aMultiples[i] > bMultiples[i]) { return i+1; }
      }
      return 0;
    };
  };

  public static void main(String[] args) throws MapleException
  {

    HashSet<java.util.List<String>> present = new HashSet<>();

    int P = 3;
    out.print("Actual solution computed via Maple:");
    ExponentialHawkesProcessAutocovarianceSolver.enumerate(P)
                                                .sorted(getTermMultipleComparator(P))
                                                .forEach(row -> {
                                                  printRow(row, true, P);
                                                  present.add(row);
                                                });

    out.println("In-progress solution computed by filtering of a HomogeniousCombinator: ");

    List<String> vars = Stream
                              .concat(rangeClosed(1, P).mapToObj(i -> "α" + i),
                                      rangeClosed(1, P).mapToObj(i -> "β" + i))
                              .collect(Collectors.toList());

    int n = (int) CombinatoricsUtils.binomialCoefficient(P + 1, 2);

    AtomicInteger thereCount = new AtomicInteger();
    AtomicInteger extraCount = new AtomicInteger();
    ArrayList<ArrayList<String>> extraTerms = new ArrayList<>();

    StreamSupport.stream(new HomogeniousCombinator<>(vars, n).spliterator(),
                         false)
                 .filter(l -> {
                   boolean twoVarsPresent = new HashSet<String>(l).size() > 1;
                   boolean moreThanOneIndexPresent = getIndexSet(l).size() > 1;
                   TreeMap<Integer, AtomicInteger> indexRepetitions =
                                                                    getIndexRepetitions(l);
                   TreeMap<String, AtomicInteger> multiplicities =
                                                                 getTermMultiplicities(l);
                   int maxMultiplicity =
                                       multiplicities.values()
                                                     .stream()
                                                     .mapToInt(atom -> atom.get())
                                                     .max()
                                                     .getAsInt();

                   boolean containsAtLeastOneβ = getCharSet(l).contains("β");
                   boolean maxMultiplicityNoGreaterThanP = maxMultiplicity <= P;
                   boolean atLeastPTermsPresent = multiplicities.keySet()
                                                                .size() >= P;
                   boolean everyIndexPresent = indexRepetitions.size() == P;

                   return twoVarsPresent && moreThanOneIndexPresent
                          && containsAtLeastOneβ
                          && maxMultiplicityNoGreaterThanP
                          && atLeastPTermsPresent
                          && everyIndexPresent;
                 })
                 .sorted(getTermMultipleComparator(P))
                 .forEach(row -> {
                   boolean there = present.contains(row);
                   if (there)
                   {
                     thereCount.incrementAndGet();
                   }
                   else
                   {
                     extraTerms.add(new ArrayList<>(row));
                     extraCount.incrementAndGet();
                   }

                   printRow(row, there, P);

                 });
    out.println("matching count " + thereCount);
    out.println("extra count " + extraCount + " (starred)");
    out.println("TERMS TO BE FILTERED:");

    extraTerms.stream()
              .sorted(getTermMultipleComparator(P))
              .forEach(row -> printRow(row, false, P));
  }

  public static void printRow(List<String> row, boolean there, int P)
  {
    out.format("%s termMultiples=%s varMultiples=%12s indexReps=%s\n",
               (there ? " " : "*"),
               Arrays.toString(getTermMultiplicitiesArray(row, P)),
               getVariableMultiplicities(row),
               getIndexRepetitions(row));
  }

  public static TreeMap<Integer, AtomicInteger> getIndexRepetitions(
      List<String> l)
  {
    AutoHashMap<Integer, AtomicInteger> repetitions =
                                                    new AutoHashMap<>(AtomicInteger.class);

    l.forEach(var -> repetitions.getOrCreate(Integer.valueOf(var.substring(1,
                                                                           var.length())))
                                .getAndIncrement());
    return new TreeMap<Integer, AtomicInteger>(repetitions);
  }

  public static int[] getTermMultiplicitiesArray(List<String> l, int P)
  {
    List<String> vars = Stream
                              .concat(rangeClosed(1, P).mapToObj(i -> "α" + i),
                                      rangeClosed(1, P).mapToObj(i -> "β" + i))
                              .collect(Collectors.toList());

    TreeMap<String, AtomicInteger> m = getTermMultiplicities(l);

    int r[] = new int[P * 2];
    for (int i = 0; i < r.length; i++)
    {
      String ithVarName = vars.get(i);
      AtomicInteger ithVarCounter = m.get(ithVarName);
      if (ithVarCounter != null)
      {
        r[i] = ithVarCounter.get();
      }
    }
    return r;

  }

  public static TreeMap<String, AtomicInteger> getTermMultiplicities(
      List<String> l)
  {
    AutoHashMap<String, AtomicInteger> multiplicities =
                                                      new AutoHashMap<>(AtomicInteger.class);

    l.forEach(var -> multiplicities.getOrCreate(var).getAndIncrement());
    return new TreeMap<String, AtomicInteger>(multiplicities);
  }

  public static TreeMap<String, AtomicInteger> getVariableMultiplicities(
      List<String> l)
  {
    AutoHashMap<String, AtomicInteger> multiplicities =
                                                      new AutoHashMap<>(AtomicInteger.class);

    l.forEach(var -> multiplicities.getOrCreate(String.valueOf(var.charAt(0)))
                                   .getAndIncrement());
    return new TreeMap<String, AtomicInteger>(multiplicities);
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