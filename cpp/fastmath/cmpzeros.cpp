#include <fastmath.h>
#include <iostream>
#include <fstream>
#include <iomanip>

using namespace std;

int main (int argc, char *args[])
{

  cout << fixed << setprecision(9) ;

  FILE * instFile;
    double d=0;


    instFile = fopen(args[0], "r");

    while ( !feof(instFile))
    {
      if ( fscanf(instFile, "%lf\n", &d) != 1 )
      {
        perror(args[0]);
        exit(1);
      }
      cout << "read " << d << endl;
    }

    fclose(instFile);

//  int i;
//  while ( afile.good() && bfile.good() )
//  {
//    double diff = a-b;
//    if ( abs(a-b)<pow(10,-14))
//    {
//
//      cout << i << " " << a << " " << b << endl;
//    }
//  }
}
