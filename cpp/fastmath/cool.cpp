#include <fastmath.h>
#include <iostream>
#include <fstream>
#include <iomanip>

using namespace std;
using namespace arbpp;

int main (int argc, char *args[])
{
  arb lastx ;
//  cout << sizeof(arb_t) << endl;
//  cout << sizeof(arb) << endl;
//  cout << sizeof(complexarb) << endl;
//  cout << sizeof(acb_t) << endl;
//  cout << sizeof(carb) << endl;
//
//  complexarb hmm(arb("7.5"),arb("0.4"));
//  cout << "hmm " << hmm << endl;
//  cout.flush();
//  complexarb res = cgamma(hmm,true);
//  cout << hmm << " -> " << res  << endl;
//  exit(1);

  ofstream zerofile;
  zerofile.open ("Gram.txt");
  cout << fixed << setprecision(9) ;
  zerofile << fixed << setprecision(9);

  for ( int i = 1; i < 100000000; i++ )
  {
    arb x = abs( nthGramPoint(  i ) );


    cout << "g[" << i << "]=" << x << endl;

    zerofile << x << endl;

    lastx = x;
  }

  zerofile.close();
}
