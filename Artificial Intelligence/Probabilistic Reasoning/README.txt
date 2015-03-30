ASSIGNMENT DESCRIPTION:

Goals:

Probabilistic reasoning using Bayes networks, with scenarios similar to the Yazidi refugee problem environment of assignment 1.

Uncertain Yazidi Refugee Problem - Domain Description

Unfortunately, in the real world, the location of the ISIS terrorists are not known with certainty. Pinpointing their locations is important both for the refugees (in order to avoid them) and to Obama (so they can be bombed). However, it is known that the terrorists tend to be in locations close to their targets (Yzaidis, oil fields, and other resources), not all of which are necessarily known, but some of which can be observed. ISIS terrorists also cause destruction, and this can also be observed.

Our probabilistic model is: ISIS targets and resources are independently generated at vertices with a certain probability depending on the known vertex identity v, a probability Pr(v). Thus we have a binary random variable standing in for the existence of a resource at node v, with the obvious prior distribution. In each vertex v, ISIS terrorists occur randomly based on the resources in v and neighboring nodes. Thus we have one binary random variable for existence of ISIS at each vertex, depending on the resource variables in the relevant nodes (as stated above), with a noisy or conditional distribution. Finally, we have one binary variable stating wherther there is significant destruction at each node, and these depend on the existence of ISIS at this and neighboring vertices, again with a noisy or distribution.

The causal strength of the noisy-or distributions are determined by the length of the edges: the longer the edge, the weaker the causal strength, since e.g. it is more likely that destruction will occur if the ISIS are closer. The conditional probability will be: P(terrorists at v| resources at node u and at no other node near v) is Pt * max( (4-weight)/5, 0), where weight is the weight of the edge from u to v. Note that u can also equal v, in which case the weight of the edge is taken as 0. Probability of leakage (spontaneous terrorists apperance) is 0.1, where Pt is a user specified number between 0 and 1. Likewise, the conditional probability of destructin will be noisy-or, with P(destruction at v| terrorists at node u and at no other node near v) equal to Pd * max( (4-weight)/5, 0), with Pd a user specified number between 0 and 1.

All in all, you have 3 types of variables (BN nodes): resources (one for each vertex), ISIS terrorists (one for each vertex), destruction (one for each vertex).

In your program, a file specifies the geometry (graph), and the probability parameters (e.g. Pt). Then, you enter some locations where resources and destruction are reported either present or absent (and the rest remain unknown). This is the evidence in the problem. Once evidence is instantiated, you need to perform reasoning about the likely locations of terrorists:

What is the probability that each of the vertices contains terrorists?
What is the probability that a certain path (set of edges) is free from terrorists? (Note that the distributions of terrorists in vertices are NOT necessarily independent.)
What is the path from a given location to a goal that has the highest probability of being free from terrorists? (bonus)
Input can be as an ASCII file, similar to graph descriptions in previous assignments, for example:

#V 4    ; number of vertices n in graph (from 1 to n)

#V 1 0.4  ; Vertex 1, probability of resource 0.4
#V 2 0.2  ; Vertex 2, probability of resource 0.2

#E 1 2 W1 ; Edge between vertices 1 and 2, weight 1
#E 2 3 W3 ; Edge between vertices 2 and 3, weight 3
#E 3 4 W3 ; Edge between vertices 3 and 4, weight 3
#E 2 4 W4 ; Edge between vertices 2 and 4, weight 4

#Goal V3  ; To be used in optional bonus, if applicable


Requirements:

Your program should read the data, including the distribution parameters, which are defined as above. The program should construct a Bayes network according to the scenario. The program should also allow for an output of the Bayes network constructed for the scenario.

For example, part of the output for the above graph, for the parameter values Pt = 0.9, Pd = 0.7, would be:

VERTEX 1: resource
  P(resource) = 0.4
  P(no resource) = 0.6

VERTEX 1: terrorists | resources at 1, resources at 2
  P(terr | not res 1, not res 2) = 0.1
  P(no terr | not res 1, not res 2) = 0.9

  P(terr | not res 1, res 2) = 0.54
  P(no terr | not res 1, res 2) = 0.46

  etc.

VERTEX 1: destruction | terrorists at 1, terrorists at 2
   ...

VERTEX 2:    (etc.)
 
You should then query the user for a set of evidence. We do this by reading one piece of evidence at a time (e.g. "destruction reported at vertex 1", and then "destruction reported not to be at vertex 3" etc.). The online interactive operations your program should support are:

Reset evidence list to empty.
Add piece of evidence to evidence list.
Do probabilistic reasoning (1, 2, or 3, whichever your program supports) and report the results.
Quit.
Probabilistic reasoning should be done in order to answer the questions on distribution of terrorists and report on the answers, including all the posterior probabilities. You may use any algorithm in the literature that supports solution of BNs, including simple enumerarion, variable elimination, polytree propagation, or sampling.


========= Constructing the Bayesian Network =========

Every variable has: 
	# Type (1 = resource, 2 = terrorists, 3 = destruction)
	# Node id
	# Parents
	# Joint distribution table with (2 ^ numOfParents) entries. 
			For example, if a variable v has 3 parents:
			entry 0 will hold the probability of v given that all of his parents are false, 
			entry 1 will hold the probability of v given: parent1 = true, parent2 = false, parent3 = false, 
			entry 7 will hold the probability of v given that all of his parents are true.

1. For every node i in the graph, we created a variable ri representing existence of resources in that node.
   Variable ri has no parents and its table contains only one entry: P(ri) and P(not ri).

2. For every node i in the graph, we created a variable ti representing existence of terrorists in that node.

3. For every node i in the graph, we created a variable di representing existence of destruction in that node.

So if the graph has 3 nodes, the variables will be added to the network in the following order:
r1, r2, r3, t1, t2, t3, d1, d2, d3.

That way, when creating the table for a certain variable, the tables of its parents will already be updated.

================ Reasoning Algorithm ================

We used the Enumeration algorithm.








