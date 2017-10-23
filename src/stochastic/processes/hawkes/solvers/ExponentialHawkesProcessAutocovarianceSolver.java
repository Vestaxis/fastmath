package stochastic.processes.hawkes.solvers;

import static java.lang.String.format;

import com.maplesoft.externalcall.MapleException;
import com.maplesoft.openmaple.Engine;
import com.maplesoft.openmaple.EngineCallBacksDefault;

public class ExponentialHawkesProcessAutocovarianceSolver
{
  public static void main(String args[]) throws MapleException
  {
    String a[];
    Engine t;
    int i;
    a = new String[1];
    a[0] = "java";
    String libDir = "c:\\\\research";
    t = new Engine(a, new EngineCallBacksDefault(), null, null);
    t.evaluate(format("libname:=libname,\"%s\";", libDir));
    t.evaluate("with(cl):");
    t.evaluate("lcovar:=proc (P) options operator, arrow; (sum(alpha[j]*(lambda+mustar(beta[j]))/(beta[j]+s), j = 1 .. P))/(1-sum(alpha[j]/(beta[j]+s), j = 1 .. P)) end proc:");
    t.evaluate("lcovareq:=proc (k, P) options operator, arrow; eval(mustar(s) = lcovar(P), s = beta[k]) end proc:"); 
    t.evaluate("lcovarsol:=proc (P) options operator, arrow; solve(cl:-toset(cl:-flist(proc (k) options operator, arrow; lcovareq(k, P) end proc, 1 .. P)), cl:-toset(cl:-flist(proc (k) options operator, arrow; mustar(beta[k]) end proc, 1 .. P))) end proc:");  
        
    t.evaluate("lcovarsol(1);");
    t.evaluate("lcovarsol(2);");
    System.out.println("Done\n");
  }
}