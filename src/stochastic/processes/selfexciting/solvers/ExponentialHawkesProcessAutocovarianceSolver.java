package stochastic.processes.selfexciting.solvers;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;

import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.maplesoft.externalcall.MapleException;
import com.maplesoft.openmaple.Algebraic;
import com.maplesoft.openmaple.Engine;
import com.maplesoft.openmaple.EngineCallBacksDefault;
import com.maplesoft.openmaple.List;

import fastmath.NativeUtils;
import util.AutoHashMap;

public class ExponentialHawkesProcessAutocovarianceSolver
{
  public static void main(String args[]) throws MapleException
  {
    HashSet<java.util.List<String>> present = new HashSet<>();

    enumerate(2).forEach(row -> {
      out.println(row);
      present.add(row);
    });

    System.out.println("Goodbye");
  }

  public static Stream<java.util.List<String>> enumerate(int P)
      throws MapleException
  {
    return enumerateAlgebra(P).stream()
                              .map(row -> row.subList(1, row.size()))
                              .map(algRow -> algRow.stream()
                                                   .map(alg -> replaceChars(alg.toString()))
                                                   .collect(Collectors.toList()));
  }

  public static java.util.List<java.util.List<Algebraic>> enumerateAlgebra(
      int P) throws MapleException
  {
    String a[];
    Engine t;
    a = new String[1];
    a[0] = "java";
    String libDir =
                  NativeUtils.isTerribleExcuseForAnOperatingSystem() ? "c:\\\\research"
                                                                     : "/home/stephen/research";
    t = new Engine(a, new EngineCallBacksDefault(), null, null);
    t.evaluate(format("libname:=libname,\"%s\":", libDir));
    t.evaluate("with(cl):");
    t.evaluate("lcovar:=proc (P) options operator, arrow; (sum(alpha[j]*(lambda+mustar(beta[j]))/(beta[j]+s), j = 1 .. P))/(1-sum(alpha[j]/(beta[j]+s), j = 1 .. P)) end proc:");
    t.evaluate("lcovareq:=proc (k, P) options operator, arrow; eval(mustar(s) = lcovar(P), s = beta[k]) end proc:");
    t.evaluate("lcovarsol:=proc (P) options operator, arrow; solve(cl:-toset(cl:-flist(proc (k) options operator, arrow; lcovareq(k, P) end proc, 1 .. P)), cl:-toset(cl:-flist(proc (k) options operator, arrow; mustar(beta[k]) end proc, 1 .. P))) end proc:");

    // t.evaluate("lcovarsol(1);");
    // t.evaluate("lcovarsol(2);");
    List solutions =
                   (List) t.evaluate(format("map(tolist,map(x->tolist(denom(op(2,x))),tolist(lcovarsol(%d)))):",
                                            P));
    // List solutions = (List)
    // t.evaluate("map(tolist,map(x->tolist(denom(op(2,x))),tolist(lcovarsol(2)))):");
    List first = (List) solutions.select(1);
    List second = (List) solutions.select(2);
    int i = 0;

    java.util.List<java.util.List<Algebraic>> expansion =
                                                        expand(t,
                                                               list2list(t,
                                                                         first));
    return expansion;
  }

  /**
   * TODO: multipliers
   * 
   * @param t
   * @param summands
   * @return
   */
  private static java.util.List<java.util.List<Algebraic>> expand(Engine t,
      java.util.List<Algebraic> summands)
  {
    java.util.List<java.util.List<Algebraic>> expandedSummands =
                                                               summands.stream()
                                                                       .map(row -> evaluateList(t,
                                                                                                "expandPow( tolist("
                                                                                                   + row.toString()
                                                                                                   + ")):"))
                                                                       .map(list -> list2list(t,
                                                                                              list))
                                                                       .collect(Collectors.toList());

    // out.println("SORTED");

    // AtomicInteger counter = new AtomicInteger();
    // expandedSummands.stream()
    // .sorted(new TermComparator())
    // .map(list -> replaceChars(list.toString()))
    // .forEach( line -> out.println( counter.incrementAndGet() + " " + line ));

    AutoHashMap<String, AtomicInteger> referenceCounts =
                                                       new AutoHashMap<String, AtomicInteger>(AtomicInteger.class);

    expandedSummands.stream()
                    .forEach(list -> list.stream()
                                         .filter(expr -> expr.toString()
                                                             .contains("["))
                                         .forEach(expr -> referenceCounts.getOrCreate(expr.toString())
                                                                         .getAndIncrement()));

    out.println("referenceCounts=" + replaceChars(referenceCounts.toString()));

    return expandedSummands;
  }

  static List evaluateList(Engine t, String expr)
  {
    try
    {
      return (List) t.evaluate(expr);
    }
    catch (MapleException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public static class TermComparator
      implements Comparator<java.util.List<? extends Algebraic>>
  {

    @Override
    public int compare(java.util.List<? extends Algebraic> a,
        java.util.List<? extends Algebraic> b)
    {
      int n = a.size();
      assert n == b.size();

      for (int i = 0; i < n; i++)
      {
        Algebraic aterm = a.get(i);
        Algebraic bterm = b.get(i);
        String aStr = aterm.toString();
        String bStr = bterm.toString();
        int cmp = aStr.compareTo(bStr);
        if (cmp != 0) { return cmp; }

      }
      return 0;
    }

  }

  public static java.util.List<Algebraic> list2list(Engine t, List first)
  {
    int n = 0;
    try
    {
      n = first.length();
    }
    catch (MapleException e1)
    {
      throw new RuntimeException(e1.getMessage(), e1);
    }
    java.util.List<Algebraic> list = rangeClosed(1, n).mapToObj(i -> {
      try
      {
        return first.select(i);
      }
      catch (MapleException e)
      {
        throw new RuntimeException(e.getMessage(), e);
      }
    }).collect(Collectors.toList());
    return list;
  }

  public static String replaceChars(String first)
  {
    return first.replace("beta", "β")
                .replace("alpha", "α")
                .replaceAll("\\[", "")
                .replaceAll("\\]", "");
  }
}