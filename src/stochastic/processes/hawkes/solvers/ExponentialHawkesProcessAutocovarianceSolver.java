package stochastic.processes.hawkes.solvers;

import com.maplesoft.externalcall.MapleException;
import com.maplesoft.openmaple.Engine;
import com.maplesoft.openmaple.EngineCallBacksDefault;

public class ExponentialHawkesProcessAutocovarianceSolver
{
    public static void main( String args[] )
    {
        String a[];
        Engine t;
        int i;
        a = new String[1];
        a[0] = "java";
        try
        {
            t = new Engine( a, new EngineCallBacksDefault(), null, null );
            t.evaluate("with(cl):");
        }
        catch ( MapleException e )
        {
            System.out.println( "An exception occurred\n" );
            return;
        }
        System.out.println( "Done\n" );
    }
}