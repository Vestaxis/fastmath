package stochastic.processes.hawkes.solvers;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import com.maplesoft.externalcall.MapleException;
import com.maplesoft.openmaple.Algebraic;
import com.maplesoft.openmaple.Engine;
import com.maplesoft.openmaple.EngineCallBacksDefault;
import com.maplesoft.openmaple.List;

public class ExponentialHawkesProcessAutocovarianceSolver
{
  public static void main(String args[]) throws MapleException
  {
    String a[];
    Engine t;
    a = new String[1];
    a[0] = "java";
    String libDir = "c:\\\\research";
    t = new Engine(a, new EngineCallBacksDefault(), null, null);
    t.evaluate(format("libname:=libname,\"%s\":", libDir));
    t.evaluate("with(cl):");
    t.evaluate("lcovar:=proc (P) options operator, arrow; (sum(alpha[j]*(lambda+mustar(beta[j]))/(beta[j]+s), j = 1 .. P))/(1-sum(alpha[j]/(beta[j]+s), j = 1 .. P)) end proc:");
    t.evaluate("lcovareq:=proc (k, P) options operator, arrow; eval(mustar(s) = lcovar(P), s = beta[k]) end proc:");
    t.evaluate("lcovarsol:=proc (P) options operator, arrow; solve(cl:-toset(cl:-flist(proc (k) options operator, arrow; lcovareq(k, P) end proc, 1 .. P)), cl:-toset(cl:-flist(proc (k) options operator, arrow; mustar(beta[k]) end proc, 1 .. P))) end proc:");

    // t.evaluate("lcovarsol(1);");
    // t.evaluate("lcovarsol(2);");
    List solutions = (List) t.evaluate("map(tolist,map(x->tolist(denom(op(2,x))),tolist(lcovarsol(3)))):");
    List first = (List) solutions.select(1);
    List second = (List) solutions.select(2);
    int i = 0;
    java.util.List<Algebraic> rows = listIterator(first);
    for (Algebraic term : rows)
    {
      List expanded = (List) t.evaluate("expandPow( tolist(" + term.toString() + ")):");
      java.util.List<Algebraic> terms = listIterator(expanded).stream().collect(Collectors.toList());

      
    }
    
//    String expandedListString = replaceChars(terms.stream().map(expression -> expression.toString()).collect(Collectors.joining(",")));
//    out.println(++i + ": " + expandedListString);
    
    //rows.sort(new TermComparator());
    String firstSol = replaceChars(first.toString());
    // String ass = firstSol.replace("beta", "β");
    out.println("P=" + solutions.length());
    out.println("first=" + firstSol);
    out.println("second=" + replaceChars(second.toString()));

    System.out.println("Goodbye");
  }

  public class TermComparator implements Comparator<java.util.List<Algebraic>>
  {

    @Override
    public int compare(java.util.List<Algebraic> a, java.util.List<Algebraic> b)
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

  public static java.util.List<Algebraic> listIterator(List first) throws MapleException
  {
    java.util.List<Algebraic> list = rangeClosed(1, first.length()).mapToObj(i -> {
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
    return first.replace("beta", "β").replace("alpha", "α");
  }
}