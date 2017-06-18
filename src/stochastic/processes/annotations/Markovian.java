package stochastic.processes.annotations;

/**
 * Let (Ω,\mathcal{F},\mathbb{P}) be a probability space with a filtration
 * (\mathcal{F}_t,\ t \in T), for some (totally ordered) index set T; and let
 * (S,\mathcal{S}) be a measure space. An S-valued stochastic process X=(X_t,\
 * t\in T) adapted to the filtration is said to possess the <i>Markov
 * property</i> with respect to the \{\mathcal{F}_t\} if, for each A\in
 * \mathcal{S} and each s,t\in T with s < t,
 * 
 * \mathbb{P}(X_t \in A |\mathcal{F}_s) = \mathbb{P}(X_t \in A| X_s).[4]
 * 
 * A Markov process is a stochastic process which satisfies the Markov property
 * with respect to its natural filtration.
 * 
 * @see {@link MarkovProcess}
 */
public @interface Markovian
{

}
