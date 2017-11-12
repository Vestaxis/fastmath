<TeXmacs|1.99.5>

<style|<tuple|amsart|american>>

<\body>
  <doc-data|<doc-title|Prediction and Simulation of Exponential Self-Exciting
  Processes>|<doc-author|<author-data|<author-name|<date|>>>>|<doc-author|<author-data|<author-name|<space|5em>Stephen
  Crowley>|<\author-affiliation>
    \;
  </author-affiliation>>>><\footnote>
    stephencrowley214@gmail.com
  </footnote>

  \;

  \;

  <\table-of-contents|toc>
    <vspace*|1fn><with|font-series|bold|math-font-series|bold|1.<space|2spc>Hawkes
    Processes> <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
    <no-break><pageref|auto-1><vspace|0.5fn>

    <with|par-left|1tab|1.1.<space|2spc>The Sum-of-Exponentials Self-Exciting
    Process of Arbitrary Order <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
    <no-break><pageref|auto-2>>

    <with|par-left|2tab|1.1.1.<space|2spc>Maximum Likelihood Estimation
    <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
    <no-break><pageref|auto-3>>

    <with|par-left|2tab|1.1.2.<space|2spc>The case when P=1
    <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
    <no-break><pageref|auto-4>>

    <with|par-left|1tab|1.2.<space|2spc>An Expression for the Density of the
    Duration Until the Next Event <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
    <no-break><pageref|auto-5>>

    <with|par-left|2tab|1.2.1.<space|2spc>The Case of Any Order
    <with|mode|math|P=n> <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
    <no-break><pageref|auto-6>>

    <with|par-left|1tab|1.3.<space|2spc>Filtering, Prediction, Estimation,
    etc <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
    <no-break><pageref|auto-7>>

    <with|par-left|1tab|1.4.<space|2spc>Calculation of the Expected Number of
    Events Any Given Time From Now <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
    <no-break><pageref|auto-8>>

    <with|par-left|2tab|1.4.1.<space|2spc>Prediction
    <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
    <no-break><pageref|auto-9>>

    <vspace*|1fn><with|font-series|bold|math-font-series|bold|Bibliography>
    <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
    <no-break><pageref|auto-10><vspace|0.5fn>
  </table-of-contents>

  <section|Hawkes Processes>

  <subsection|The Sum-of-Exponentials Self-Exciting Process of Arbitrary
  Order>

  Let

  <\equation>
    \<theta\><around*|(|t|)>=<choice|<tformat|<table|<row|<cell|0>|<cell|t\<leqslant\>0>>|<row|<cell|1>|<cell|t\<gtr\>0>>>>>
  </equation>

  be the Heaviside step function and <math|<around*|{|T<rsub|i>:T<rsub|i>\<less\>T<rsub|i+1>|}>\<in\>\<bbb-R\>>
  be the time of occurance of the <math|i>-th event of a process. A
  self-exciting process <math|N<rsub|t>> is a uni-variate (linear)
  self-exciting counting process

  <\equation>
    N<rsub|t>=<big|sum><rsub|i:T<rsub|i>\<less\>t>\<theta\><around*|(|t-T<rsub|i>|)>
  </equation>

  \;

  <\equation>
    <tabular|<tformat|<table|<row|<cell|\<lambda\><around*|(|t|)>>|<cell|=\<lambda\><rsub|0><around*|(|t|)>+<big|int><rsub|-\<infty\>><rsup|t>\<nu\><around*|(|t-s|)>\<mathd\>N<rsub|s>>>|<row|<cell|>|<cell|=\<lambda\><rsub|0><around*|(|t|)>+<big|sum><rsub|T<rsub|k>\<less\>t>\<nu\><around*|(|t-T<rsub|k>|)>>>>>><label|HawkesIntensity>
  </equation>

  where <math|\<lambda\><rsub|0><around*|(|t|)>> is a deterministic function
  which will be regarded as a constant <math|\<lambda\><rsub|0><around*|(|t|)>=\<lambda\><rsub|0>=E<around*|[|\<lambda\><rsub|0><around*|(|t|)>|]>>,
  <cite|hawkes-finance><cite|hawkes1971spectra><cite|shek2010modeling><cite|chavez2012high>
  Here, \ <math|\<nu\>:\<bbb-R\><rsub|+>\<rightarrow\>\<bbb-R\><rsub|+>> is a
  kernel function which expresses the positive influence of past events
  <math|T<rsub|i>> on the current value of the intensity process, The
  self-exciting process of order <math|P> is a defined by the
  sum-of-exponentials kernel

  <\equation>
    \<nu\><around*|(|t|)>=<big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j>
    e<rsup|-\<beta\><rsub|j> t><label|kernel>
  </equation>

  The intensity is then written as

  <\equation>
    <tabular|<tformat|<table|<row|<cell|\<lambda\><around*|(|t|)>>|<cell|=\<lambda\><rsub|0><around*|(|t|)>+<big|int><rsub|0><rsup|t><big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j>
    e<rsup|-\<beta\><rsub|j><around*|(|t-s|)>>\<mathd\>N<rsub|s>>>|<row|<cell|>|<cell|=\<lambda\><rsub|0><around*|(|t|)>+<big|sum><rsub|j=1><rsup|P><big|sum><rsub|k=0><rsup|<wide|N|\<breve\>><rsub|t>>\<alpha\><rsub|j>
    e<rsup|-\<beta\><rsub|j><around*|(|t-t<rsub|k>|)>>>>|<row|<cell|>|<cell|=\<lambda\><rsub|0><around*|(|t|)>+<big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j><big|sum><rsub|k=0><rsup|<wide|N|\<breve\>><rsub|t>>
    e<rsup|-\<beta\><rsub|j><around*|(|t-t<rsub|k>|)>>>>|<row|<cell|>|<cell|=\<lambda\><rsub|0><around*|(|t|)>+<big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j
    >B<rsub|j><around*|(|N<rsub|t>|)>>>>>>
  </equation>

  where <math|B<rsub|j><around*|(|i|)>> is given recursively by

  <\equation>
    <tabular|<tformat|<table|<row|<cell|B<rsub|j><around*|(|i|)>>|<cell|=<big|sum><rsub|k=1><rsup|i-1>
    e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i>-t<rsub|k>|)>>>>|<row|<cell|>|<cell|=e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i>-t<rsub|i-1>|)>><rsub|><big|sum><rsub|k=1><rsup|i-1>
    e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i-1>-t<rsub|k>|)>>>>|<row|<cell|>|<cell|=e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i>-t<rsub|i-1>|)>><rsub|>*<around*|(|1+<big|sum><rsub|k=1><rsup|i-2>
    e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i-1>-t<rsub|k>|)>>|)>>>|<row|<cell|>|<cell|=e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i>-t<rsub|i-1><rsub|>|)>><around*|(|1+B<rsub|j><around*|(|i-1|)>|)>>>>>><label|Bj>
  </equation>

  since <math| e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i-1>-t<rsub|i-1>|)>>=e<rsup|-\<beta\><rsub|j>0>=e<rsup|-0>=1>.
  A uni-variate Hawkes process is stationary if the branching ratio
  <math|\<rho\>> is less than one.

  <\equation>
    \<rho\>=<big|sum><rsub|j=1><rsup|P><frac|\<alpha\><rsub|j>|\<beta\><rsub|j>>\<less\>1
  </equation>

  If a Hawkes process is stationary then the stationary unconditional
  intensity is

  <\equation>
    <tabular|<tformat|<table|<row|<cell|\<lambda\>=E<around*|[|\<lambda\><around*|(|t|)>|]>>|<cell|=<frac|\<lambda\><rsub|0>|1-E<around*|[|\<nu\><around*|(|t|)>|]>>>>|<row|<cell|>|<cell|=<frac|\<lambda\><rsub|0>|1-<big|int><rsub|0><rsup|\<infty\>><big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j>
    e<rsup|-\<beta\><rsub|j> t>\<mathd\>t>>>|<row|<cell|>|<cell|=<frac|\<lambda\><rsub|0>|1-<big|sum><rsub|j=1><rsup|P><frac|\<alpha\><rsub|j>|\<beta\><rsub|j>>>>>|<row|<cell|>|<cell|=<frac|\<lambda\><rsub|0>|1-\<rho\>>>>>>>
  </equation>

  where <math|E<around*|(|*\<cdummy\>|)>> is the Lebesgue integral over the
  positive real axis. For consecutive events, the dual-predictable
  projection, aka compensator, is expressed\ 

  <\equation>
    <tabular|<tformat|<table|<row|<cell|\<Lambda\><around*|(|t<rsub|i-1>,t<rsub|i>|)>>|<cell|=<big|int><rsub|t<rsub|i-1>><rsup|t<rsub|i>>\<lambda\><around*|(|t|)>\<mathd\>t>>|<row|<cell|>|<cell|=<big|int><rsub|t<rsub|i-1>><rsup|t<rsub|i>><around*|(|\<lambda\><rsub|0><around*|(|t|)>+<big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j
    >B<rsub|j><around*|(|N<rsub|t>|)>|)>\<mathd\>t>>|<row|<cell|>|<cell|=<big|int><rsub|t<rsub|i-1><rsup|>><rsup|t<rsub|i>>\<lambda\><rsub|0><around*|(|s|)>\<mathd\>s+<big|int><rsub|t<rsub|i-1><rsup|>><rsup|t<rsub|i>><big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j><big|sum><rsub|k=0><rsup|i-1>
    e<rsup|-\<beta\><rsub|j><around*|(|t-t<rsub|k>|)>>\<mathd\>t>>|<row|<cell|>|<cell|=<big|int><rsub|t<rsub|i-1><rsup|>><rsup|t<rsub|i>>\<lambda\><rsub|0><around*|(|s|)>\<mathd\>s+<big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j><big|sum><rsub|k=0><rsup|i-1>
    <big|int><rsub|t<rsub|i-1><rsup|>><rsup|t<rsub|i>>e<rsup|-\<beta\><rsub|j><around*|(|t-t<rsub|k>|)>>\<mathd\>t>>|<row|<cell|>|<cell|=<big|int><rsub|t<rsub|i-1><rsup|>><rsup|t<rsub|i>>\<lambda\><rsub|0><around*|(|s|)>\<mathd\>s+<big|sum><rsub|k=0><rsup|i-1><big|int><rsub|t<rsub|i-1>><rsup|t<rsub|i>>\<nu\><around*|(|t-t<rsub|k>|)>\<mathd\>t>>|<row|<cell|>|<cell|=<big|int><rsub|t<rsub|i-1><rsup|>><rsup|t<rsub|i>>\<lambda\><rsub|0><around*|(|s|)>\<mathd\>s+<big|sum><rsub|k=0><rsup|i-1><big|sum><rsub|j=1><rsup|P><frac|\<alpha\><rsub|j>|\<beta\><rsub|j>><around*|(|e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i-1>-t<rsub|k>|)>>-e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i>-t<rsub|k>|)>>|)>>>|<row|<cell|>|<cell|=<big|int><rsub|t<rsub|i-1><rsup|>><rsup|t<rsub|i>>\<lambda\><rsub|0><around*|(|s|)>\<mathd\>s+<big|sum><rsub|j=1><rsup|P><frac|\<alpha\><rsub|j>|\<beta\><rsub|j>><around*|(|1-e<rsup|-\<beta\><rsub|j>\<Delta\>t<rsub|i>>|)>A<rsub|j><around*|(|i-1|)>>>>>><label|comp>
  </equation>

  where <math|<big|int><rsub|t<rsub|i-1><rsup|>><rsup|t<rsub|i>>\<lambda\><rsub|0><around*|(|s|)>\<mathd\>s=\<lambda\><rsub|0>\<Delta\>t<rsub|i>>
  if <math|\<lambda\><rsub|0><around*|(|t|)>=\<lambda\><rsub|0>> and

  <\equation>
    <tabular|<tformat|<table|<row|<cell|A<rsub|j><around*|(|i|)>>|<cell|=<big|sum><rsub|t<rsub|k>\<leqslant\>t<rsub|i>>e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i>-t<rsub|k>|)>>>>|<row|<cell|>|<cell|=<big|sum><rsub|k=0><rsup|i-1>e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i>-t<rsub|k>|)>>>>|<row|<cell|>|<cell|=1+e<rsup|-\<beta\><rsub|j>\<Delta\>t<rsub|i>>A<rsub|j><around*|(|i-1|)>>>>>><label|A>
  </equation>

  with <math|A<rsub|j><around*|(|0|)>=0> since the integral of the
  exponential kernel (<reference|kernel>) is

  <\equation>
    <tabular|<tformat|<table|<row|<cell|<big|int><rsub|t<rsub|i-1>><rsup|t<rsub|i>>\<nu\><around*|(|t|)>\<mathd\>t>|<cell|=<big|int><rsub|t<rsub|i-1><rsup|>><rsup|t<rsub|i>><big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j>
    e<rsup|-\<beta\><rsub|j> <around*|(|t-t<rsub|k>|)>>\<mathd\>t>>|<row|<cell|>|<cell|=<big|sum><rsub|j=1><rsup|P><frac|\<alpha\><rsub|j>|\<beta\><rsub|j>>
    <around*|(|e<rsup|-\<beta\><rsub|j> t<rsub|i>>-e<rsup|-\<beta\><rsub|j>t<rsub|i-1>>|)>>>>>>
  </equation>

  \ If <math|\<lambda\><rsub|0><around*|(|t|)>> does not vary with time, that
  is, <math|\<lambda\><rsub|0><around*|(|t|)>=\<lambda\><rsub|0>> then
  (<reference|comp>) simplifies to

  <\equation>
    <tabular|<tformat|<table|<row|<cell|\<Lambda\><around*|(|t<rsub|i-1>,t<rsub|i>|)>>|<cell|=\<Delta\>t<rsub|i><rsup|>\<lambda\><rsub|0>+<big|sum><rsub|k=0><rsup|i-1><big|sum><rsub|j=1><rsup|P><frac|\<alpha\><rsub|j>|\<beta\><rsub|j>><around*|(|e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i-1>-t<rsub|k>|)>>-e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i>-t<rsub|k>|)>>|)>>>|<row|<cell|>|<cell|=\<Delta\>t<rsub|i><rsup|>\<lambda\><rsub|0>+<big|sum><rsub|k=0><rsup|i-1><big|int><rsub|t<rsub|i-1>-t<rsub|k>><rsup|t<rsub|i>-t<rsub|k>>\<nu\><around*|(|t|)>\<mathd\>t>>|<row|<cell|>|<cell|=\<Delta\>t<rsub|i><rsup|>\<lambda\><rsub|0>+<big|sum><rsub|j=1><rsup|P><frac|\<alpha\><rsub|j>|\<beta\><rsub|j>><around*|(|1-e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i>-t<rsub|i-1>|)>>|)>A<rsub|j><around*|(|i-1|)>>>>>>
  </equation>

  <subsubsection|Maximum Likelihood Estimation>

  The log-likelihood of a simple point process is written as

  <\equation>
    <tabular|<tformat|<table|<row|<cell|<with|mode|text|>ln\<cal-L\><around*|(|N<around*|(|t|)><rsub|t\<in\><around*|[|0,T|]>>|)>>|<cell|=<big|int><rsub|0><rsup|T><around*|(|1-\<lambda\><around*|(|s|)>|)>\<mathd\>s+<big|int><rsub|0><rsup|T>ln\<lambda\><around*|(|s|)>\<mathd\>N<rsub|s>>>|<row|<cell|>|<cell|=T-<big|int><rsub|0><rsup|T>\<lambda\><around*|(|s|)>\<mathd\>s+<big|int><rsub|0><rsup|T>ln\<lambda\><around*|(|s|)>\<mathd\>N<rsub|s>>>>>>
  </equation>

  which in the case of the sums-of-exponentials self-exciting process of
  order <math|P> can be explicitly written <cite|ozaki1979maximum> as

  <\equation>
    <tabular|<tformat|<table|<row|<cell|ln
    \<cal-L\><around*|(|<around*|{|t<rsub|i>|}><rsub|i=1\<ldots\>n>|)>>|<cell|=<rsub|>T-\<Lambda\><around*|(|0,T|)>+<big|sum><rsub|i=1><rsup|n>ln\<lambda\><around*|(|t<rsub|i>|)>>>|<row|<cell|>|<cell|=T+<big|sum><rsub|i=1><rsup|n><around*|(|ln\<lambda\><around*|(|t<rsub|i>|)>-\<Lambda\><around*|(|t<rsub|i-1>,t<rsub|i>|)>|)>>>|<row|<cell|>|<cell|=T-\<Lambda\><around*|(|0,T|)>+<big|sum><rsub|i=1><rsup|n>ln\<lambda\><around*|(|t<rsub|i>|)>>>|<row|<cell|>|<cell|=<rsub|>T-\<Lambda\><around*|(|0,T|)>+<big|sum><rsub|i=1><rsup|n>ln<around*|(|
    \<lambda\><rsub|0><around*|(|t<rsub|i>|)>+<big|sum><rsub|j=1><rsup|P><big|sum><rsub|k=1><rsup|i-1>\<alpha\><rsub|j>
    e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|i>-t<rsub|k>|)>>|)>>>|<row|<cell|>|<cell|=<rsub|>T-\<Lambda\><around*|(|0,T|)>+<big|sum><rsub|i=1><rsup|n>ln<around*|(|
    \<lambda\><rsub|0><around*|(|t<rsub|i>|)>+<big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j>
    B<rsub|j><around*|(|i|)>|)>>>|<row|<cell|>|<cell|=T-<big|int><rsub|0><rsup|<rsub|T>>\<lambda\><rsub|0><around*|(|s|)>\<mathd\>s-<big|sum><rsub|i=1><rsup|n><big|sum><rsub|j=1><rsup|P><frac|\<alpha\><rsub|j>|\<beta\><rsub|j>><around*|(|1-e<rsup|-\<beta\><rsub|j><around*|(|t<rsub|n>-t<rsub|i>|)>>|)>>>|<row|<cell|>|<cell|+<big|sum><rsub|i=1><rsup|n>ln<around*|(|\<lambda\><rsub|0><around*|(|t<rsub|i>|)>+<big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j>
    B<rsub|j><around*|(|i|)>|)>>>>>>
  </equation>

  where <math|T=t<rsub|n>> and <math|B<rsub|j><around*|(|i|)>>
  <cite|ogata1981lewis> is defined by (<reference|Bj>) If the baseline
  intensity \ is constant <math|\<lambda\><rsub|0><around*|(|t|)>=\<lambda\><rsub|0>>
  then the log-likelihood can be written

  <\equation>
    <tabular|<tformat|<table|<row|<cell|ln
    \<cal-L\><around*|(|<around*|{|t<rsub|1>,\<ldots\>,t<rsub|n>|}><rsub|>|)>>|<cell|=T-\<lambda\><rsub|0>T-<big|sum><rsub|i=1><rsup|n><big|sum><rsub|j=1><rsup|P><frac|\<alpha\><rsub|j>|\<beta\><rsub|j>><around*|(|1-e<rsup|-\<beta\><rsub|j><around*|(|T-t<rsub|i>|)>>|)>>>|<row|<cell|>|<cell|+<big|sum><rsub|i=1><rsup|n>ln<around*|(|\<lambda\><rsub|0>+<big|sum><rsub|j=1><rsup|P>\<alpha\><rsub|j>
    B<rsub|j><around*|(|i|)>|)>>>>>><with|mode|text|><label|hawkesll>
  </equation>

  Note that it was necessary to shift each <math|t<rsub|i>> by
  <math|t<rsub|1>> so that <math|t<rsub|1>=0> and <math|T=t<rsub|n>>. Also
  note that <math|T> is just an additive constant which does not vary with
  the parameters so for the purposes of estimation can be removed from the
  equation. \ 

  <subsubsection|The case when P=1>

  <subsection|An Expression for the Density of the Duration Until the Next
  Event>

  The simplest case occurs when the baseline intensity
  <math|\<lambda\><rsub|0><around*|(|t|)>=\<lambda\><rsub|0>> is constant and
  <math|P=1> where we have

  <\equation>
    \<lambda\><around*|(|<around*|{|t<rsub|i>|}>|)>=\<lambda\><rsub|0>+<big|sum><rsub|t<rsub|i>\<less\>t><big|sum><rsub|j=1><rsup|1>\<alpha\><rsub|j>
    e<rsup|-\<beta\><rsub|j> <around*|(|t-t<rsub|i>|)>>=\<lambda\><rsub|0>+<big|sum><rsub|t<rsub|i>\<less\>t>\<alpha\><rsub|>
    e<rsup|-\<beta\><rsub|> <around*|(|t-t<rsub|i>|)>><label|Hawkes1>
  </equation>

  where

  <\equation>
    \<lambda\>=E<around*|[|\<lambda\><around*|(|t|)>|]>=<frac|\<kappa\>|1-<frac|\<alpha\>|\<beta\>>>
  </equation>

  is the expected value of the unconditional mean intensity.

  \;

  <\equation>
    a<rsub|n>=<big|sum><rsub|k=0><rsup|n>e<rsup|\<beta\><rsub|>t<rsub|k>>
  </equation>

  <\equation>
    b<rsub|n>=<big|sum><rsub|k=0><rsup|n>e<rsup|\<beta\><rsub|><around*|(|t<rsub|k>-t<rsub|n>|)>>
  </equation>

  <\equation>
    c<rsub|n>=<big|sum><rsub|k=0><rsup|n><big|sum><rsub|l=0><rsup|n>e<rsup|\<beta\><rsub|><around*|(|t<rsub|k>+t<rsub|l>-
    t<rsub|n>|)>>
  </equation>

  \;

  The expected time of the next point can be obtained by integrating over the
  unit exponentially distributed parameter \ <math|\<varepsilon\>> appearing
  in the inverse of the compensator

  <with|font-base-size|12|<\equation>
    <with|font-base-size|14|<with|font-base-size|12|\<Lambda\><rsub|><rsup|-1><around*|(|\<varepsilon\>,\<alpha\><rsub|>,\<beta\><rsub|><rsub|>|)>>=<tabular|<tformat|<table|<row|<cell|<with|font-base-size|12|e<rsup|-\<beta\><rsub|>
    T><around*|(|<frac|T a<rsub|n> \ +<frac|a<rsub|n>|\<beta\><rsub|> >
    W<around*|(|<frac|\<alpha\><rsub|> |\<lambda\><rsub|0>>A<rsub|1><around*|(|n|)>\<cdot\>e<rsup|<frac|\<alpha\><rsub|>b<rsub|n>-\<beta\><rsub|>\<varepsilon\>|\<lambda\><rsub|0>>>|)>+<frac|e<rsup|-\<beta\><rsub|>
    T>|\<lambda\><rsub|0> ><around*|(|a<rsub|n>
    \<varepsilon\>-<frac|\<alpha\><rsub|>|\<beta\><rsub|>>
    c<rsub|n>|)>|A<rsub|1><around*|(|n|)>>|)>>>>>>><label|P1pred>>
  </equation>>

  so that\ 

  <\equation>
    \<Epsilon\><rsub|\<varepsilon\>><around*|[|\<Lambda\><rsub|><rsup|-1><rsub|><around*|\||\<cal-F\><rsub|t>|\<nobracket\>>,\<alpha\><rsub|>,\<beta\>|]>=<big|int><rsub|0><rsup|\<infty\>>e<rsup|-\<varepsilon\>>\<Lambda\><rsub|><rsup|-1><around*|(|\<varepsilon\>,\<alpha\><rsub|>,\<beta\><rsub|><rsub|>|)>\<mathd\>\<varepsilon\>
  </equation>

  where <math|A<rsub|1><around*|(|n|)>> is some expression that I need to dig
  out of the Maple worksheet, or re-derive somehow. The recursive equations
  with initial conditions <math|b<rsub|0>=1> and
  <math|d<rsub|0>=e<rsup|\<beta\><rsub|>t<rsub|0>>> are

  <\equation>
    <tabular|<tformat|<table|<row|<cell|a<rsub|n>>|<cell|=a<rsub|n-1>e<rsup|-\<beta\><rsub|>\<Delta\>t<rsub|n>>+1>>|<row|<cell|b<rsub|n><rsup|>>|<cell|=b<rsub|n-1>
    e<rsup|-\<beta\><rsub|>\<Delta\>t<rsub|n>>+1>>|<row|<cell|c<rsub|n>>|<cell|=c<rsub|n-1>e<rsup|-\<beta\><rsub|>\<Delta\>t<rsub|n>>+e<rsup|\<beta\><rsub|>t<rsub|n>>+2
    a<rsub|n-1>>>>>>
  </equation>

  It would be nice to have expressions like this involving the Lambert W
  function for <math|P\<gtr\>1> but neither Maple nor Mathematica were able
  to find any solutions in terms of \Pknown\Q functions for <math|P\<gtr\>1>.
  It is noted that Equation (<reference|P1pred>) has the form

  <\equation>
    <tabular|<tformat|<table|<row|<cell|<big|int><rsub|0><rsup|\<infty\>><around*|(|p+q
    W<around*|(|r e<rsup|-s x+t>|)>+u x|)>e<rsup|-x>\<mathd\>x<label|lambertW6>>>>>>
  </equation>

  which is a function of 6 variables, <math|<around*|{|p,q,r,s,t,u|}>>, for
  which it would be a very nice thing to have a closed form expression, in
  order to avoid a recourse to numerical or Monte Carlo integration. It seems
  that such an expression is very likely to exist because if we drop the
  variable <math|s> from Equation (<reference|lambertW6>) we get a
  closed-form expression of the form

  <with|font-base-size|10|<\equation>
    <with|font-base-size|10|<big|int><rsub|0><rsup|\<infty\>><around*|(|p+q
    W<around*|(|r e<rsup|- x+t>|)>+u x|)>e<rsup|-x>\<mathd\>x=q W<around*|(|r
    e<rsup|t>|)>+<frac|q |W<around*|(|r e<rsup|t>|)>>-q+u+p-<frac|q|r
    e<rsup|t>>><with|font-base-size|10|>
  </equation>>

  We can break this problem down into a more manageable one by calculating
  some more integrals to see if we can find a pattern. Let us begin with the
  integral

  <\equation>
    <big|int><rsub|0><rsup|\<infty\>>W<around*|(|e<rsup|-s
    x>|)>e<rsup|-x>\<mathd\>x=W<around*|(|1|)>\<noplus\>+<around*|(|-<frac|1|s>|)><rsup|-<frac|1|s>>
    <around*|(|\<Gamma\><around*|(|<frac|1|s>|)>-s
    \<Gamma\><around*|(|1+<frac|1|s>,-<frac|W<around*|(|1|)>|s>|)>|)>
  </equation>

  whose closed-form expression was found by Vladimir Reshetnikov.\ 

  <subsubsection|The Case of Any Order <math|P=n>>

  Let <math|S=t<rsub|n>> where <math|n> is the number of points that have
  occured so far and

  <\equation>
    <with|font-base-size|10|\<varphi\><around*|(|P,\<#3B5\>,x,\<alpha\>,\<beta\>,S|)><rsub|>=<with|font-base-size|9|<tabular|<tformat|<table|<row|<cell|<with|font-base-size|10|<with|font-base-size|9|<around*|(|<big|prod><rsub|k=1><rsup|P>\<beta\><rsub|k>|)>><around*|(|\<lambda\><rsub|0>
    x-<around*|(|\<varepsilon\>+\<lambda\><rsub|0>S|)>|)>e<rsup|<big|sum><rsub|k=1><rsup|P>\<beta\><rsub|k><around*|(|x+
    S|)>>>+>>|<row|<cell|<with|font-base-size|8|<big|sum><rsub|m=1><rsup|P><around*|(|<big|prod><rsub|k=1><rsup|<with|font-base-size|7|>P><choice|<tformat|<table|<row|<cell|\<alpha\><rsub|k>>|<cell|m=k>>|<row|<cell|\<beta\><rsub|k>>|<cell|m\<neq\>k>>>>>|)>><with|font-base-size|10|<big|sum><rsub|k=0><rsup|n>e<rsup|<big|sum><rsub|j=1><rsup|P>\<beta\><rsub|j><around*|(|x+<choice|<tformat|<table|<row|<cell|S>|<cell|j\<neq\>m>>|<row|<cell|t<rsub|k>>|<cell|j=m>>>>>|)>>-e<rsup|<big|sum><rsub|j=1><rsup|P>\<beta\><rsub|j><around*|(|S+<with|font-base-size|9|<choice|<tformat|<table|<row|<cell|x>|<cell|j\<neq\>m>>|<row|<cell|
    t<rsub|k>>|<cell|j=m>>>>>>|)>>>>>|<row|<cell|=\<tau\><around*|(|x,\<varepsilon\>|)>+<big|sum><rsub|j=1><rsup|P>\<phi\><rsub|j>
    <big|sum><rsub|k=0><rsup|N<rsub|T>><around*|(|\<sigma\><rsub|j,k><around*|(|x,x|)>-\<sigma\><rsub|j,k><around*|(|x,S|)>|)>>>>>><label|prediction>>>
  </equation>

  then the time of occurance of the next point of the process is equal to the
  value of <math|x> which solves <math|\<varphi\><around*|(|P,\<#3B5\>,x,\<alpha\>,\<beta\>,S|)><rsub|>=0>
  for a given value of <math|\<varepsilon\>> which is not known
  ahead-of-time. In simulation, <math|\<varepsilon\>> is a randomly chosen
  value drawn from a unit exponential distribution. In operation,
  <math|\<varepsilon\>> is the integral of the conditional intensity
  accumulated over the interval spanning the last point and the arrival of
  the next point. Let <math|x<around*|(|\<varepsilon\>|)>> be the implicity
  defined function\ 

  <\equation>
    x<around*|(|\<varepsilon\>|)>=<around*|{|x:\<varphi\><around*|(|P,\<#3B5\>,x,\<alpha\>,\<beta\>,S|)><rsub|>=0|}>
  </equation>

  The expected value <math|E<rsub|\<varepsilon\>><around*|[|x<around*|(|\<varepsilon\>|)>|]>>
  of the solution over all possible values of <math|\<varepsilon\>> is equal
  to\ 

  <\equation>
    x<around*|(|*\<varepsilon\>|)>=<big|int><rsub|0><rsup|\<infty\>>e<rsup|-\<varepsilon\>>x<around*|(|\<varepsilon\>|)>\<mathd\>\<varepsilon\>
  </equation>

  where <math|x<around*|(|\<varepsilon\>|)>=x<around*|(|\<varepsilon\>;P,x,\<#3B1\>,\<#3B2\>,S|)>>
  is shortened notation to indicate that <math|x> is a univariate function of
  <math|\<varepsilon\>> only and the variables <math|\<lambda\><rsub|0>,>
  <math|\<alpha\>>, <math|\<beta\>> and P are constants. Note that the
  product of piece-wise functions can be written as\ 

  <\equation>
    <tabular|<tformat|<table|<row|<cell|<big|prod><rsub|k=1><rsup|<with|font-base-size|7|>P><choice|<tformat|<table|<row|<cell|\<alpha\><rsub|k>>|<cell|m=k>>|<row|<cell|\<beta\><rsub|k>>|<cell|m\<neq\>k>>>>>>|<cell|=\<alpha\><rsub|m><around*|(|<big|prod><rsub|k=1><rsup|m-1>\<beta\><rsub|k>|)><around*|(|<big|prod><rsub|k=m+1><rsup|P>\<beta\><rsub|k>|)>>>|<row|<cell|>|<cell|=\<alpha\><rsub|m><below|<big|prod><rsub|k=1><rsup|P>|k\<neq\>m>\<beta\><rsub|k>>>>>>
  </equation>

  where

  <\equation>
    <tabular|<tformat|<table|<row|<cell|\<sigma\><rsub|m,k><around*|(|x,x|)>>|<cell|=<big|sum><rsub|j=1><rsup|P>\<beta\><rsub|j><around*|(|x+<choice|<tformat|<table|<row|<cell|T>|<cell|j\<neq\>m>>|<row|<cell|t<rsub|k>>|<cell|j=m>>>>>|)>>>|<row|<cell|>|<cell|=\<beta\><rsub|m><around*|(|x+t<rsub|k>|)><rsub|>+<big|sum><rsub|j=1><rsup|m-1>\<beta\><rsub|j><around*|(|x+T|)>+<big|sum><rsub|j=m+1><rsup|P>\<beta\><rsub|j><around*|(|x+T|)>>>|<row|<cell|>|<cell|=\<beta\><rsub|m><around*|(|x+t<rsub|k>|)><rsub|>+<below|<big|sum><rsub|j=1><rsup|P>|j\<neq\>m>\<beta\><rsub|j><around*|(|x+T|)>>>>>>
  </equation>

  <\equation>
    <tabular|<tformat|<table|<row|<cell|\<sigma\><rsub|m,k><around*|(|x,T|)>=>|<cell|<big|sum><rsub|j=1><rsup|P>\<beta\><rsub|j><around*|(|T+<with|font-base-size|9|<choice|<tformat|<table|<row|<cell|x>|<cell|j\<neq\>m>>|<row|<cell|
    t<rsub|k>>|<cell|j=m>>>>>>|)>>>|<row|<cell|>|<cell|=\<beta\><rsub|m><around*|(|T+t<rsub|k>|)>+<big|sum><rsub|j=1><rsup|m-1>\<beta\><rsub|j><around*|(|x+T|)>+<big|sum><rsub|j=m+1><rsup|P>\<beta\><rsub|j><around*|(|x+T|)>>>|<row|<cell|>|<cell|=\<beta\><rsub|m><around*|(|T+t<rsub|k>|)>+<below|<big|sum><rsub|j=1><rsup|P>|j\<neq\>m>\<beta\><rsub|j><around*|(|x<rsub|>+T|)>>>>>>
  </equation>

  <\equation>
    \<tau\><around*|(|x,\<varepsilon\>|)>=<around*|(|<around*|(|x-T|)>\<kappa\>-\<varepsilon\>|)>\<upsilon\>\<eta\><around*|(|x|)>
  </equation>

  <\equation>
    \<eta\><with|font-base-size|10|<around*|(|x|)>=e<rsup|<around*|(|x+
    T|)><big|sum><rsub|k=1><rsup|P>\<beta\><rsub|k>>>
  </equation>

  <\equation>
    \<upsilon\>=<big|prod><rsub|k=1><rsup|P>\<beta\><rsub|k>
  </equation>

  <\equation>
    <wide|v|\<bar\>><rsub|m>=<below|<big|sum><rsub|k=1><rsup|P>|k\<neq\>m>\<beta\><rsub|k>
  </equation>

  \;

  so that (<reference|prediction>) can be rewritten as

  <\equation>
    <with|font-base-size|10|<with|font-base-size|10|\<varphi\><rsub|P><around*|(|x<around*|(|\<varepsilon\>|)>|)>=><tabular|<tformat|<table|<row|<cell|<with|font-base-size|10|\<tau\><around*|(|x,\<varepsilon\>|)>+<big|sum><rsub|j=1><rsup|P>\<phi\><rsub|j>
    <big|sum><rsub|k=0><rsup|N<rsub|T>><around*|(|\<sigma\><rsub|j,k><around*|(|x,x|)>-\<sigma\><rsub|j,k><around*|(|x,T|)>|)>><with|font-base-size|10|<rsup|>>>>>>>><label|uc>
  </equation>

  The derivative\ 

  <\equation>
    <with|font-base-size|10|<tabular|<tformat|<table|<row|<cell|\<varphi\><rprime|'><rsub|P><around*|(|x<around*|(|\<varepsilon\>|)>|)>=<with|font-base-size|9|\<upsilon\><with|font-base-size|8|<around*|(|\<lambda\><rsub|0>\<eta\><around*|(|x|)><with|font-base-size|9|>+\<tau\><around*|(|x,\<varepsilon\>|)>|)>+<big|sum><rsub|m=1><rsup|P>\<phi\><rsub|m>
    <big|sum><rsub|k=0><rsup|n><around*|(|v
    \<sigma\><rsub|m,k><around*|(|x|)>-<wide|v|\<bar\>><rsub|m>
    \<sigma\><rsub|m,k><around*|(|T|)>|)>>>>>>>>><tabular|<tformat|<table|<row|<cell|>>|<row|<cell|>>>>>
  </equation>

  is needed so that the Newton sequence can be expressed as

  <\equation>
    <tabular|<tformat|<table|<row|<cell|x<rsub|i+1>>|<cell|=x<rsub|i>-<frac|\<varphi\><rsub|P><around*|(|x<rsub|i>|)>|\<varphi\><rsub|P><rprime|'><around*|(|x<rsub|i>|)>>>>|<row|<cell|>|<cell|<with|font-base-size|5|=x<rsub|i>-<frac|\<tau\><around*|(|x<rsub|i>,\<varepsilon\>|)>+<big|sum><rsub|m=1><rsup|P>\<phi\><rsub|m>
    <big|sum><rsub|k=0><rsup|n><around*|(|\<sigma\><rsub|m,k><around*|(|x<rsub|i>,x<rsub|i>|)>-\<sigma\><rsub|m,k><around*|(|x<rsub|i>,T|)>|)>|\<upsilon\><around*|(|\<kappa\>\<eta\><around*|(|x<rsub|i>|)>+\<tau\><around*|(|x<rsub|i>,\<varepsilon\>|)>|)>+<big|sum><rsub|m=1><rsup|P>\<phi\><rsub|m>
    <big|sum><rsub|k=0><rsup|n><around*|(|\<mu\>
    \<sigma\><rsub|m,k><around*|(|x<rsub|i>|)>-\<mu\><rsub|m>
    \<sigma\><rsub|m,k><around*|(|T|)>|)>>>>>>>>
  </equation>

  and simplified a bit(at least notationally) if we let

  <\equation>
    \<rho\><around*|(|x,d|)>=<with|font-base-size|9|<tabular|<tformat|<table|<row|<cell|<big|sum><rsub|m=1><rsup|P>\<phi\><rsub|m><big|sum><rsub|k=0><rsup|n><around*|(|\<sigma\><rsub|m,k><around*|(|x|)><choice|<tformat|<table|<row|<cell|1>|<cell|d=0>>|<row|<cell|v>|<cell|d=1>>>>>
    - \ \<sigma\><rsub|m,k><around*|(|T|)><choice|<tformat|<table|<row|<cell|1>|<cell|d=0>>|<row|<cell|<wide|v|\<bar\>><rsub|m>>|<cell|d=1>>>>>|)>>>>>>>
  </equation>

  then

  <\equation>
    <with|font-base-size|10|<tabular|<tformat|<table|<row|<cell|x<rsub|i+1><around*|(|\<varepsilon\>|)>>|<cell|=x<rsub|i><around*|(|\<varepsilon\>|)>-<frac|\<varphi\><rsub|P><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>|)>|\<varphi\><rsub|P><rprime|'><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>|)>>>>|<row|<cell|>|<cell|<with|font-base-size|10|=x<rsub|i>-<frac|\<tau\><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>,\<varepsilon\>|)>+\<rho\><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>,0|)>|\<upsilon\><around*|(|\<kappa\>\<eta\><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>|)>+\<tau\><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>,\<varepsilon\>|)>|)>+\<rho\><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>,1|)>>>>>>>>>
  </equation>

  so that

  <\equation>
    \<Lambda\><rsub|P><rsup|-1><around*|(|\<varepsilon\>;t<rsub|0>\<ldots\>T|)>=lim<rsub|m\<rightarrow\>\<infty\>>x<rsub|m><around*|(|\<varepsilon\>|)>
  </equation>

  which leads to the expression for the expected arrival time of the next
  point

  <\equation>
    <big|int><rsub|0><rsup|\<infty\> >\<Lambda\><rsub|P><rsup|-1><around*|(|\<varepsilon\>;t<rsub|0>\<ldots\>T|)>e<rsup|-\<varepsilon\>>\<mathd\>\<varepsilon\>=<big|int><rsub|0><rsup|\<infty\>
    >lim<rsub|m\<rightarrow\>\<infty\>>x<rsub|m><around*|(|\<varepsilon\>|)>e<rsup|-\<varepsilon\>>\<mathd\>\<varepsilon\><label|Etn1>
  </equation>

  \;

  Fatou's lemma<cite|Mti> can probably be invoked so that the order of the
  limit and the integral in Equation (<reference|Etn1>) can be exchanged,
  with perhaps the introduction of another function, which of course would
  greatly reduce the computational complexity of the equation. The sequence
  of functions is known as a Newton sequence
  <cite-detail|RandomIntegralEquations|3.3p118> There is also the limit

  <\equation>
    <tabular|<tformat|<table|<row|<cell|lim<rsub|x\<rightarrow\>\<infty\>><frac|\<varphi\><rsub|P><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>|)>|\<varphi\><rsub|P><rprime|'><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>|)>>>|<cell|=lim<rsub|x\<rightarrow\>\<infty\>><frac|\<tau\><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>,\<varepsilon\>|)>+\<rho\><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>,0|)>|\<upsilon\><around*|(|\<kappa\>\<eta\><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>|)>+\<tau\><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>,\<varepsilon\>|)>|)>+\<rho\><around*|(|x<rsub|i><around*|(|\<varepsilon\>|)>,1|)>>>>|<row|<cell|>|<cell|=<frac|1|\<lambda\>>>>>>>
  </equation>

  which converges to the inverse of the stationary rate.

  <subsection|Filtering, Prediction, Estimation, etc><label|univarPred>

  The next occurrence time of a point process, given the most recent time of
  occurrence of a point of a process, can be predicted by solving for the
  unknown time <math|t<rsub|n+1>> when <math|<around*|{|t<rsub|n>|}>> is a
  sequence of event times. Let \ 

  <\equation>
    <tabular*|<tformat|<table|<row|<cell|\<Lambda\><rsub|next><around*|(|t<rsub|n>,\<delta\>|)>=<around*|{|t<rsub|n+1>:\<Lambda\><around*|(|t<rsub|n>,t<rsub|n+1>|)>=\<delta\>|}>>>>>><label|up>
  </equation>

  where

  <\equation>
    \<Lambda\><around*|(|t<rsub|n>,t<rsub|n+1>|)>=<big|int><rsub|t<rsub|n>><rsup|t<rsub|n+1>>\<lambda\><around*|(|s;\<frak-F\><rsub|s>|)>\<mathd\>s
  </equation>

  and <math|><math|\<frak-F\><rsub|s>> is the <math|\<sigma\>>-algebra
  filtration up to and including time <math|s> and the parameters of
  <math|\<lambda\>> are fixed. The multivariate case is covered in Section
  (<reference|multivarPred>). The idea is to integrate over the solution of
  Equation (<reference|up>) with all possible values of
  <math|\<varepsilon\>>, distributed according to the unit exponential
  distribution. The reason for the plural form, time(s), rather than the
  singular form, time, is that Equation (<reference|up>) actually has a
  single real solution and <math|N> number of complex solutions, where
  <math|N> is the number of points that have occurred in the process up until
  the time of prediction. This set of complex expected future event arrival
  times is deemed the <em|constellation> of the process, which becomes more
  detailed with the occurance \ of each event(the increasing
  <math|\<sigma\>>-algebra filtration). We shall ignore the constellation for
  now, and single out the sole real valued element as the expected real time
  until the next event. After all, does it even make \ sense to say
  \Psomething will probably happen around <math|9.8+i7.2> seconds from now?\Q
  where <math|i> is the imaginary unit, <math|i=<sqrt|-1>>. The recursive
  equations for the resemble the heta functions of number theory if you one
  extends from real valued <math|\<beta\>\<in\>\<bbb-R\>> to a complex
  <math|\<beta\>=i>.

  <subsection|Calculation of the Expected Number of Events Any Given Time
  From Now>

  The expected number of events given any time from now whatsoever can be
  calculated by integrating out <math|\<varepsilon\>> since the process which
  is adapted to the compensator will be closer to being a unit rate Poisson
  process the closer the parameters are to being correct and the model
  actually being a good model of the phemenona it is being applied to. Let
  <math|F<rsub|t>> be all points up until now, let\ 

  <\equation*>
    E<around*|(|t<rsub|n+1>|)>=<big|int><rsub|0><rsup|\<infty\>>\<Lambda\><rsup|-1><around*|(|\<varepsilon\>;\<alpha\>,\<beta\>,F<rsub|t<rsub|n>>|)>e<rsup|-\<varepsilon\>>\<mathd\>\<varepsilon\>
  </equation*>

  then iterate the process, by proceeding as if the next point of the process
  will occur at the predicted time, simply append the expectation to the
  current state vector, and project the next point, repeating the process as
  fast ast the computer will go until some sufficient stopping criteria is
  met. This equation seems very similiar to the infinite horizon discounted
  regulator of optimal control; see <cite-detail|ocvshjb|1.1>.

  \;

  <subsubsection|Prediction><label|multivarPred>

  The next event arrival time of the <math|m>-th dimension of a multivariate
  Hawkes process having the usual exponential kernel can be predicted in the
  same way as the uni-variate process in Section (<reference|univarPred>), by
  solving for the unknown <math|t<rsub|n+1>> in the equation

  <\equation>
    <around*|{|t<rsub|n+1><rsup|m>:\<varepsilon\>=\<Lambda\><rsup|m><around*|(|t<rsub|n><rsup|m>,t<rsub|n+1><rsup|m>|)>=<big|int><rsub|t<rsup|m><rsub|n>><rsup|t<rsup|m><rsub|n+1>>\<lambda\><rsup|m><around*|(|s;\<frak-F\><rsub|s>|)>\<mathd\>s<label|mp>|}>
  </equation>

  where <math|\<Lambda\><rsup|m><around*|(|t<rsub|n><rsup|m>,t<rsub|n+1><rsup|m>|)>>
  is the compensator from Equation (<reference|lhm>)<math|> and
  <math|\<frak-F\><rsub|s>> is the filtration up to time <math|s> and the
  parameters of <math|\<lambda\><rsup|m>> are fixed. As is the case for the
  uni-variate Hawkes process, the idea is to average over all possible
  realizations of <math|\<varepsilon\>> (of which there are an uncountable
  infinity) weighted according to an exponential unit distribution. Another
  idea for more accurate prediction is to model the deviation of the
  generalized residuals from a true exponential distribution and then include
  the predicted error when calculating this expectation.\ 

  Let the most recent arrival time of the pooled and <math|m>-th processes
  respectively be given by

  <\equation>
    T=max<around*|(|T<rsub|m>:m=1\<ldots\>M|)>
  </equation>

  <\equation>
    T<rsup|><rsub|m >=max<around*|(|t<rsup|m><rsub|n>:n=0\<ldots\>N<rsup|m>-1|)>=t<rsup|m><rsub|N<rsup|m>-1>
  </equation>

  and\ 

  <\equation>
    <wide|N|\<breve\>><rsup|n><rsub|T<rsub|m>><rsup|>=<big|sum><rsub|k=0><rsup|<wide|N|\<breve\>><rsup|n>><choice|<tformat|<table|<row|<cell|1>|<cell|t<rsup|n><rsub|k>\<less\>T<rsub|m>>>|<row|<cell|0>|<cell|>>>>>
  </equation>

  count the number of points occurring in the <math|n>-th dimension before
  the most recent point of the <math|m>-th dimension and

  <\equation>
    <wide|N|\<breve\>><around*|(|t<rsub|j><rsup|m>\<less\>t<rsub|k><rsup|n>|)>
  </equation>

  then the next arrival time for a given value of the exponential random
  variable <math|\<varepsilon\>> of the <math|m>-th dimension of a
  multivariate Hawkes process having the standard exponential kernel is found
  by solving for the real root of\ 

  <\equation>
    <with|font-base-size|9|\<varphi\><rsub|m><around*|(|x<around*|(|\<varepsilon\>|)>;\<cal-F\><rsub|T>|)>=>\<tau\><rsub|m><around*|(|x,\<varepsilon\>|)>+<big|sum><rsub|l=1><rsup|P><big|sum><rsub|i=1><rsup|M>\<phi\><rsub|m,i,l>
    <big|sum><rsub|k=0><rsup|<wide|N|\<breve\>><rsub|T<rsub|m>><rsup|i>><around*|(|\<sigma\><rsub|m,i,l,k><around*|(|x,x|)>-\<sigma\><rsub|m,i,l,k><around*|(|x,T<rsub|m>|)>|)><label|mc>
  </equation>

  which is similar to the uni-variate case

  <\equation>
    <with|font-base-size|10|<with|font-base-size|10|\<varphi\><rsub|P><around*|(|x<around*|(|\<varepsilon\>|)>|)>=><tabular|<tformat|<table|<row|<cell|<with|font-base-size|10|\<tau\><around*|(|x,\<varepsilon\>|)>+<big|sum><rsub|j=1><rsup|P>\<phi\><rsub|j>
    <big|sum><rsub|k=0><rsup|<wide|N|\<breve\>><rsub|T>><around*|(|\<sigma\><rsub|j,k><around*|(|x,x|)>-\<sigma\><rsub|j,k><around*|(|x,T|)>|)>><with|font-base-size|10|<rsup|>>>>>>>>
  </equation>

  where

  <\equation>
    \<cal-F\><rsub|T>=<around*|{|\<kappa\><rsub|\<ldots\>>,\<alpha\><rsub|\<ldots\>>,\<beta\><rsub|\<ldots\>>,t<rsup|1><rsub|0>\<ldots\>t<rsup|1><rsub|N<rsup|1>>\<leqslant\>T,\<ldots\>,t<rsup|m><rsub|0>\<ldots\>t<rsup|m><rsub|N<rsup|m>>\<leqslant\>T,\<ldots\>,t<rsup|M><rsub|0>\<ldots\>t<rsup|M><rsub|N<rsup|M>>\<leqslant\>T|}>
  </equation>

  is the filtration up to time <math|T>, to be interpreted as the set of
  available information, here denoting fitted parameters and observed arrival
  times of all dimensions, and where\ 

  <\equation>
    \<tau\><rsub|m><around*|(|x,\<varepsilon\>|)>=<around*|(|<around*|(|x-T<rsub|m>|)>\<kappa\><rsub|m>-\<varepsilon\>|)>\<upsilon\><rsub|m>\<eta\><rsub|m><around*|(|x|)>
  </equation>

  <\equation>
    \<eta\><rsub|m><with|font-base-size|10|<around*|(|x|)>=e<rsup|<around*|(|x+
    T<rsub|m>|)><big|sum><rsub|j=1><rsup|P><big|sum><rsub|n=1><rsup|M>\<beta\><rsub|m,n,j>>>
  </equation>

  can be seen to be similar to the uni-variate equations
  <math|\<tau\><around*|(|x,\<varepsilon\>|)>=<around*|(|<around*|(|x-T|)>\<kappa\>-\<varepsilon\>|)>\<upsilon\>\<eta\><around*|(|x|)>>
  and <math|\<eta\><with|font-base-size|10|<around*|(|x|)>=e<rsup|<around*|(|x+T|)><big|sum><rsub|k=1><rsup|P>\<beta\><rsub|k>>>>
  and

  <\equation>
    \<upsilon\><rsub|m>=<big|prod><rsub|j=1><rsup|P><big|prod><rsub|n=1><rsup|M>\<beta\><rsub|m,n,j>
  </equation>

  <\equation>
    \<phi\><rsub|m,p,k>=<big|prod><rsub|j=1><rsup|P><big|prod><rsub|n=1><rsup|M><choice|<tformat|<table|<row|<cell|\<alpha\><rsub|m,n,j>>|<cell|n=p
    and j=k>>|<row|<cell|\<beta\><rsub|m,n,j>>|<cell|n\<neq\>p or
    j\<neq\>k>>>>>
  </equation>

  <\equation>
    \<sigma\><rsub|m,i,l,k><around*|(|x,a|)>=e<rsup|<big|sum><rsub|j=1><rsup|P><big|sum><rsub|n=1><rsup|M>\<beta\><rsub|m,n,j><choice|<tformat|<table|<row|<cell|a+t<rsup|n><rsub|k>>|<cell|n=i
    and j=l>>|<row|<cell|x+T<rsub|n>>|<cell|n\<neq\>i orj\<neq\>l>>>>>>
  </equation>

  For comparison, the uni-variate case is Equation (<reference|uc>) where

  <\equation>
    \<sigma\><rsub|m,k><around*|(|x,a|)>=e<rsup|<around*|(|a+t<rsub|k>|)>\<beta\><rsub|m><rsub|>+<around*|(|x+T|)><below|<big|sum><rsub|j=1><rsup|P>|j\<neq\>m>\<beta\><rsub|j>>=e<rsup|<big|sum><rsub|j=1><rsup|P>\<beta\><rsub|j><choice|<tformat|<table|<row|<cell|a+t<rsub|k><rsub|>>|<cell|j=m>>|<row|<cell|x+T>|<cell|j\<neq\>m>>>>>>
  </equation>

  \;

  <\bibliography|bib|tm-plain|references.bib>
    <\bib-list|9>
      <bibitem*|1><label|bib-ocvshjb>Martino Bardi<localize| and >Italo
      Capuzzo-Dolcetta. <newblock><with|font-shape|italic|Optimal Control and
      Viscosity Solutions of Hamilton-Jacobi-Bellman Equations (Systems &
      Control: Foundations & Applications)>. <newblock>Birkauser Boston,
      1<localize| edition>, 1997.<newblock>

      <bibitem*|2><label|bib-RandomIntegralEquations>A.T.<nbsp>Bharucha-Reid.
      <newblock><with|font-shape|italic|Random Integral Equations>,
      <localize|volume><nbsp>96<localize| of
      ><with|font-shape|italic|Mathematics in Science and Engineering>.
      <newblock>Academic Press, 1972.<newblock>

      <bibitem*|3><label|bib-chavez2012high>V.<nbsp>Chavez-Demoulin<localize|
      and >JA McGill. <newblock>High-frequency financial data modeling using
      hawkes processes. <newblock><with|font-shape|italic|Journal of Banking
      & Finance>, 2012.<newblock>

      <bibitem*|4><label|bib-hawkes1971spectra>A.G.<nbsp>Hawkes.
      <newblock>Spectra of some self-exciting and mutually exciting point
      processes. <newblock><with|font-shape|italic|Biometrika>, 58(1):83\U90,
      1971.<newblock>

      <bibitem*|5><label|bib-ogata1981lewis>Y.<nbsp>Ogata. <newblock>On
      lewis' simulation method for point processes.
      <newblock><with|font-shape|italic|Information Theory, IEEE Transactions
      on>, 27(1):23\U31, 1981.<newblock>

      <bibitem*|6><label|bib-ozaki1979maximum>T.<nbsp>Ozaki.
      <newblock>Maximum likelihood estimation of hawkes' self-exciting point
      processes. <newblock><with|font-shape|italic|Annals of the Institute of
      Statistical Mathematics>, 31(1):145\U155, 1979.<newblock>

      <bibitem*|7><label|bib-Mti>M.M.<nbsp>Rao.
      <newblock><with|font-shape|italic|Measure Theory and Integration>,
      <localize|volume> 265<localize| of ><with|font-shape|italic|Pure and
      Applied Mathematics>. <newblock>Marcel Dekker, 2nd, Revised and
      Expanded<localize| edition>, 2004.<newblock>

      <bibitem*|8><label|bib-shek2010modeling>H.<nbsp>Shek.
      <newblock>Modeling high frequency market order dynamics using
      self-excited point process. <newblock><with|font-shape|italic|Available
      at SSRN 1668160>, 2010.<newblock>

      <bibitem*|9><label|bib-hawkes-finance>Ioane<nbsp>Muni Toke.
      <newblock>An introduction to hawkes processes with applications to
      finance. <newblock><with|font-shape|italic|???>,
      <slink|http://fiquant.mas.ecp.fr/ioane_files/HawkesCourseSlides.pdf>,
      2012.<newblock>
    </bib-list>
  </bibliography>

  \;
</body>

<\initial>
  <\collection>
    <associate|page-medium|paper>
    <associate|sfactor|4>
  </collection>
</initial>

<\references>
  <\collection>
    <associate|A|<tuple|10|3|TheHawkesProcess.tm>>
    <associate|Bj|<tuple|6|2|TheHawkesProcess.tm>>
    <associate|Etn1|<tuple|43|7|TheHawkesProcess.tm>>
    <associate|Hawkes1|<tuple|16|4|TheHawkesProcess.tm>>
    <associate|HawkesIntensity|<tuple|3|1|TheHawkesProcess.tm>>
    <associate|P1pred|<tuple|21|5|TheHawkesProcess.tm>>
    <associate|auto-1|<tuple|1|1|TheHawkesProcess.tm>>
    <associate|auto-10|<tuple|60|9|TheHawkesProcess.tm>>
    <associate|auto-2|<tuple|1.1|1|TheHawkesProcess.tm>>
    <associate|auto-3|<tuple|1.1.1|3|TheHawkesProcess.tm>>
    <associate|auto-4|<tuple|1.1.2|4|TheHawkesProcess.tm>>
    <associate|auto-5|<tuple|1.2|4|TheHawkesProcess.tm>>
    <associate|auto-6|<tuple|1.2.1|5|TheHawkesProcess.tm>>
    <associate|auto-7|<tuple|1.3|7|TheHawkesProcess.tm>>
    <associate|auto-8|<tuple|1.4|7|TheHawkesProcess.tm>>
    <associate|auto-9|<tuple|1.4.1|8|TheHawkesProcess.tm>>
    <associate|bib-Mti|<tuple|7|9|TheHawkesProcess.tm>>
    <associate|bib-RandomIntegralEquations|<tuple|2|9|TheHawkesProcess.tm>>
    <associate|bib-chavez2012high|<tuple|3|9|TheHawkesProcess.tm>>
    <associate|bib-hawkes-finance|<tuple|9|9|TheHawkesProcess.tm>>
    <associate|bib-hawkes1971spectra|<tuple|4|9|TheHawkesProcess.tm>>
    <associate|bib-ocvshjb|<tuple|1|9|TheHawkesProcess.tm>>
    <associate|bib-ogata1981lewis|<tuple|5|9|TheHawkesProcess.tm>>
    <associate|bib-ozaki1979maximum|<tuple|6|9|TheHawkesProcess.tm>>
    <associate|bib-shek2010modeling|<tuple|8|9|TheHawkesProcess.tm>>
    <associate|comp|<tuple|9|3|TheHawkesProcess.tm>>
    <associate|footnote-1|<tuple|1|1|TheHawkesProcess.tm>>
    <associate|footnr-1|<tuple|1|1|TheHawkesProcess.tm>>
    <associate|hawkesll|<tuple|15|4|TheHawkesProcess.tm>>
    <associate|kernel|<tuple|4|1|TheHawkesProcess.tm>>
    <associate|lambertW6|<tuple|24|5|TheHawkesProcess.tm>>
    <associate|mc|<tuple|52|8|TheHawkesProcess.tm>>
    <associate|mp|<tuple|47|8|TheHawkesProcess.tm>>
    <associate|multivarPred|<tuple|1.4.1|8|TheHawkesProcess.tm>>
    <associate|prediction|<tuple|27|5|TheHawkesProcess.tm>>
    <associate|uc|<tuple|37|6|TheHawkesProcess.tm>>
    <associate|univarPred|<tuple|1.3|7|TheHawkesProcess.tm>>
    <associate|up|<tuple|45|7|TheHawkesProcess.tm>>
  </collection>
</references>

<\auxiliary>
  <\collection>
    <\associate|bib>
      hawkes-finance

      hawkes1971spectra

      shek2010modeling

      chavez2012high

      ozaki1979maximum

      ogata1981lewis

      Mti

      RandomIntegralEquations

      ocvshjb
    </associate>
    <\associate|toc>
      <vspace*|1fn><with|font-series|<quote|bold>|math-font-series|<quote|bold>|1.<space|2spc>Hawkes
      Processes> <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-1><vspace|0.5fn>

      <with|par-left|<quote|1tab>|1.1.<space|2spc>The Sum-of-Exponentials
      Self-Exciting Process of Arbitrary Order
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-2>>

      <with|par-left|<quote|2tab>|1.1.1.<space|2spc>Maximum Likelihood
      Estimation <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-3>>

      <with|par-left|<quote|2tab>|1.1.2.<space|2spc>The case when P=1
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-4>>

      <with|par-left|<quote|1tab>|1.2.<space|2spc>An Expression for the
      Density of the Duration Until the Next Event
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-5>>

      <with|par-left|<quote|2tab>|1.2.1.<space|2spc>The Case of Any Order
      <with|mode|<quote|math>|P=n> <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-6>>

      <with|par-left|<quote|1tab>|1.3.<space|2spc>Filtering, Prediction,
      Estimation, etc <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-7>>

      <with|par-left|<quote|1tab>|1.4.<space|2spc>Calculation of the Expected
      Number of Events Any Given Time From Now
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-8>>

      <with|par-left|<quote|2tab>|1.4.1.<space|2spc>Prediction
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-9>>

      <vspace*|1fn><with|font-series|<quote|bold>|math-font-series|<quote|bold>|Bibliography>
      <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-10><vspace|0.5fn>
    </associate>
  </collection>
</auxiliary>