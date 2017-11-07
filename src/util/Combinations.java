package util;

import static java.lang.System.out;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.math3.util.CombinatoricsUtils;

import com.github.dakusui.combinatoradix.HomogeniousCombinator;

public class Combinations
{

  public static void main(String[] args) 
  {
    int P = 3;

    List<String> vars = Stream.concat(rangeClosed(1, P).mapToObj(i -> "α" + i), rangeClosed(1, P).mapToObj(i -> "β" + i)).collect(Collectors.toList());

    /**
     * vars=[α1,..,αP,β1,..,βP]
     */
    int n = (int) CombinatoricsUtils.binomialCoefficient(P + 1, 2);

    /**
     * This predicate filters the output of HomogeniousCombinator so that a subset is retained according 
     * to various rules regarding the multiplicities,  indices, powers, degees, etc. 
     * 
     * TODO: generalize the last rule "excluded" so it works for P != 3
     */
    Predicate<? super List<String>> predicate = l -> {
      boolean twoVarsPresent = new HashSet<String>(l).size() > 1;
      boolean moreThanOneIndexPresent = getIndexSet(l).size() > 1;
      TreeMap<Integer, AtomicInteger> indexRepetitions = getIndexRepetitions(l);
      HashSet<Integer> indexCounts = getIndexCounts(indexRepetitions);

      TreeMap<String, AtomicInteger> multiplicities = getTermMultiplicities(l);
      int maxMultiplicity = multiplicities.values().stream().mapToInt(atom -> atom.get()).max().getAsInt();

      boolean containsAtLeastOneβ = getCharSet(l).contains("β");
      boolean maxMultiplicityNoGreaterThanP = maxMultiplicity <= P;
      boolean atLeastPTermsPresent = multiplicities.keySet().size() >= P;
      boolean everyIndexPresent = indexRepetitions.size() == P;
      boolean noIndiciesAppearMoreThanPTimes = indexCounts.stream().allMatch(i -> i <= P);
      TreeMap<String, AtomicInteger> varMult = getVariableMultiplicities(l);

      int αcount = getAtomicInt(varMult, "α");
      int βcount = getAtomicInt(varMult, "β");

      boolean αcountLessThanOrEqualtToβCount = αcount <= βcount;
      AutoHashMap<Integer, AtomicInteger> termMultiplicityCounts = getTermMultiplicityCounts(vars, l, P);

      int[] termCounts = getTermCounts(P, termMultiplicityCounts);

      TreeMap<Integer, AtomicInteger> indexReps = getIndexRepetitions(l);

      int[] termMultiplicities = getTermMultiplicitiesArray(vars, l, P);

      boolean excluded = Arrays.equals(termCounts, new int[]
      { P, 1, 1, 1, 0, 0 }) && range(0, P).allMatch(i -> termMultiplicities[i] < P) && αcount == βcount;

      return twoVarsPresent && moreThanOneIndexPresent
             && containsAtLeastOneβ
             && maxMultiplicityNoGreaterThanP
             && atLeastPTermsPresent
             && everyIndexPresent
             && noIndiciesAppearMoreThanPTimes
             && αcountLessThanOrEqualtToβCount
             && !excluded;
    };

    StreamSupport.stream(new HomogeniousCombinator<>(vars, n).spliterator(), false)
                 .filter(predicate)
                 .sorted(getTermMultipleComparator(vars, P))
                 .forEach(row -> {
                   printRow(vars, row, P);
                 });

  }

  public static Comparator<? super List<String>> getTermMultipleComparator(List<String> vars, int P)
  {
    return (a, b) -> {
      int[] aMultiples = getTermMultiplicitiesArray(vars, a, P);
      int[] bMultiples = getTermMultiplicitiesArray(vars, b, P);

      for (int i = 0; i < P * 2; i++)
      {
        if (aMultiples[i] < bMultiples[i])
        {
          return -(i + 1);
        }
        else if (aMultiples[i] > bMultiples[i]) { return i + 1; }
      }
      return 0;
    };
  };

  public static int[] getTermCounts(int P, AutoHashMap<Integer, AtomicInteger> termMultiplicityCounts)
  {
    int[] termCounts = rangeClosed(0, P * 2 - 1).map(i -> {
      AtomicInteger atom = termMultiplicityCounts.get(i);
      return atom == null ? 0 : atom.get();
    }).toArray();
    return termCounts;
  }

  public static int getAtomicInt(TreeMap<String, AtomicInteger> varMult, String whichVar)
  {
    AtomicInteger αcounter = varMult.get(whichVar);
    int αcount = αcounter == null ? 0 : αcounter.get();
    return αcount;
  }

  public static HashSet<Integer> getIndexCounts(TreeMap<Integer, AtomicInteger> indexRepetitions)
  {
    HashSet<Integer> indexCounts = new HashSet<>();
    indexRepetitions.values().forEach(atom -> indexCounts.add(atom.get()));
    return indexCounts;
  }

  public static AutoHashMap<Integer, AtomicInteger> getTermMultiplicityCounts(List<String> vars, List<String> row, int P)
  {
    AutoHashMap<Integer, AtomicInteger> termMultiplicityCounts = new AutoHashMap<>(AtomicInteger.class);
    int[] multiplicities = getTermMultiplicitiesArray(vars, row, P);

    rangeClosed(0, 2 * P - 1).forEach(pos -> termMultiplicityCounts.getOrCreate(multiplicities[pos]).incrementAndGet());
    return termMultiplicityCounts;
  }

  public static void printRow(List<String> vars, List<String> row, int P)
  {
    AutoHashMap<Integer, AtomicInteger> termMultiplicityCounts = new AutoHashMap<>(AtomicInteger.class);
    int[] multiplicities = getTermMultiplicitiesArray(vars, row, P);

    rangeClosed(0, 2 * P - 1).forEach(pos -> termMultiplicityCounts.getOrCreate(multiplicities[pos]).incrementAndGet());
    int[] termCounts = rangeClosed(0, P * 2 - 1).map(i -> {
      AtomicInteger atom = termMultiplicityCounts.get(i);
      return atom == null ? 0 : atom.get();
    }).toArray();

    TreeMap<Integer, AtomicInteger> indexReps = getIndexRepetitions(row);

    TreeMap<String, AtomicInteger> varMults = getVariableMultiplicities(row);
    int varMultsArray[] = new int[]
    { varMults.get("α") == null ? 0 : varMults.get("α").get(), varMults.get("β").get() };

    out.format("multiplicities=%s var#s%s idx#s%s term#%s\n",
               Arrays.toString(multiplicities),
               Arrays.toString(varMultsArray),
               indexReps,
               Arrays.toString(termCounts));
  }

  public static TreeMap<Integer, AtomicInteger> getIndexRepetitions(List<String> l)
  {
    AutoHashMap<Integer, AtomicInteger> repetitions = new AutoHashMap<>(AtomicInteger.class);

    l.forEach(var -> repetitions.getOrCreate(Integer.valueOf(var.substring(1, var.length()))).getAndIncrement());
    return new TreeMap<Integer, AtomicInteger>(repetitions);
  }

  public static int[] getTermMultiplicitiesArray(List<String> vars, List<String> l, int P)
  {

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

  public static TreeMap<String, AtomicInteger> getTermMultiplicities(List<String> l)
  {
    AutoHashMap<String, AtomicInteger> multiplicities = new AutoHashMap<>(AtomicInteger.class);

    l.forEach(var -> multiplicities.getOrCreate(var).getAndIncrement());
    return new TreeMap<String, AtomicInteger>(multiplicities);
  }

  public static TreeMap<String, AtomicInteger> getVariableMultiplicities(List<String> l)
  {
    AutoHashMap<String, AtomicInteger> multiplicities = new AutoHashMap<>(AtomicInteger.class);

    l.forEach(var -> multiplicities.getOrCreate(String.valueOf(var.charAt(0))).getAndIncrement());
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