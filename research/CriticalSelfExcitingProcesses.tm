<TeXmacs|1.99.5>

<style|generic>

<\body>
  <doc-data|<doc-title|Critical Self-Exciting Processes and Their Relation To
  Parabolic Fixed-Points of Holomorphic Dynamical
  Systems>|<doc-author|<author-data|<author-name|Stephen
  Crowley>|<\author-affiliation>
    stephencrowley214@gmail.com
  </author-affiliation>>>>

  A self-exciting process is a (simple) point process whose conditional
  intensity function is a function of the times between previous occurances
  of the process

  <\equation>
    <tabular*|<tformat|<table|<row|<cell|\<lambda\><around*|(|t|)>>|<cell|=\<lambda\><rsub|0><around*|(|t|)>+<big|int><rsub|0><rsup|t>\<nu\><around*|(|t-s|)>\<mathd\>N<rsub|s>>>|<row|<cell|>|<cell|=\<lambda\><rsub|0><around*|(|t|)>+<big|sum><rsub|j=1><rsup|N<rsub|t>>\<nu\><around*|(|t-T<rsub|j>|)>>>>>>
  </equation>

  where <math|N<rsub|t>> is the counting function which counts the number of
  occurances of the process up to time <math|T>

  <\equation>
    N<rsub|t>=<big|sum><rsub|T<rsub|i>\<less\>t>\<theta\><around*|(|t-T<rsub|i>|)>
  </equation>

  where

  <\equation>
    \<theta\><around*|(|t|)>=<choice|<tformat|<table|<row|<cell|0>|<cell|t\<leqslant\>0>>|<row|<cell|1>|<cell|t\<gtr\>0>>>>>
  </equation>

  is the Heaviside step function and <math|<around*|{|T<rsub|i>:T<rsub|i>\<less\>T<rsub|i+1>|}>\<in\>\<bbb-R\>>
  is the time of occurance of the <math|i>-th event of a process. The
  variable <math|\<lambda\><rsub|0><around*|(|t|)>> is the deterministic
  baseline(aka background) intensity of the process which has the mean value

  <\equation>
    <tabular|<tformat|<table|<row|<cell|<wide|\<lambda\>|\<bar\>><rsub|0>>|<cell|=lim<rsub|t\<rightarrow\>\<infty\>><frac|1|t><big|int><rsub|0><rsup|t>\<lambda\><rsub|0><around*|(|t|)>\<mathd\>t>>|<row|<cell|>|<cell|=lim<rsub|t\<rightarrow\>\<infty\>><big|int><rsub|0><rsup|t><frac|1|t>\<lambda\><rsub|0><around*|(|t|)>\<mathd\>t>>|<row|<cell|>|<cell|=<big|int><rsub|0><rsup|\<infty\>><frac|1|t>\<lambda\><rsub|0><around*|(|t|)>\<mathd\>t>>|<row|<cell|>|<cell|=<big|int><rsub|0><rsup|\<infty\>><frac|\<lambda\><rsub|0><around*|(|t|)>|t>\<mathd\>t>>>>>
  </equation>

  where the bar indicates that it is the mean of the function
  <math|\<lambda\><rsub|0><around*|(|t|)>> over its domain
  <math|\<bbb-R\><rsup|+>>. When <math|\<lambda\><rsub|0><around*|(|t|)>=\<lambda\><rsub|0>>
  is a constant function we have <math|<wide|\<lambda\>|\<bar\>><rsub|0>=\<lambda\><rsub|0>>.
  The function <math|\<nu\><around*|(|t|)>\<gtr\>0> is a real-valued function
  called the kernel function, as it determines how the conditional intensity
  of the process evolves as a function of the occurance times of the process.
  The branching ratio of the process <math|\<rho\>> is what determines its
  long-time behaviour and it is equal to the Lebesgue measure of
  <math|\<nu\><around*|(|t|)>>

  <\equation>
    \<rho\>=<big|int><rsub|0><rsup|\<infty\>>\<nu\><around*|(|t|)>\<mathd\>t
  </equation>

  The unconditional intensity <math|\<lambda\>> of the process <math|N> is
  then given by

  <\equation>
    <tabular|<tformat|<table|<row|<cell|\<lambda\>=E<around*|[|\<lambda\><around*|(|t|)>|]>>|<cell|=<frac||1-<big|int><rsub|0><rsup|\<infty\>>\<nu\><around*|(|t|)>\<mathd\>t>>>|<row|<cell|>|<cell|=<frac|<wide|\<lambda\>|\<bar\>><rsub|0>|1-\<rho\>>>>|<row|<cell|>|<cell|>>>>>
  </equation>

  when <math|\<rho\>\<less\>1>. When <math|\<rho\>=1> the unconditional
  intensity is taken as a limit

  <\equation>
    \<lambda\>=lim<rsub|s\<rightarrow\>\<infty\>><frac|\<lambda\><rsub|0>|1-<big|int><rsub|0><rsup|s>\<nu\><around*|(|t|)>\<mathd\>t>
  </equation>

  <\bibliography|bib|tm-plain|references>
    \;
  </bibliography>

  \ 

  \;

  \;
</body>

<\initial>
  <\collection>
    <associate|page-type|letter>
  </collection>
</initial>

<\references>
  <\collection>
    <associate|auto-1|<tuple|7|?|../../../.TeXmacs/texts/scratch/no_name_19.tm>>
  </collection>
</references>