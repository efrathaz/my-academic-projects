ASSIGNMENT DESCRIPTION:

Goals:

Sequential decision making under uncertainty using belief-state MDP for decision-making: the Yazidi refugee problem. (This is an obscure variant of the Canadian treveler problem.)

Yazidi Refugee Problem - Domain Description

The domain description is silmilar to that described in assignment 1, except that now we do not know the locations of the ISIS terrorists, similar to assignment 4. For simplicity, however, we will assume that the terrorists appear independently, with a known given probability, at each vertex. They are revealed with certainty when a Yazidi agent reaches a neigbouring vertex.

Thus, in the current problem to be solved, we are given a weighted undirected graph, where each vertex has a known probability of having terrorists. The probabilities of terrorists are mutually independent, and they do not move. The Yazidi agent's only action are traveling between vertices. Traversal costs are just the weight of the edge in this variant. Also for simplicity, we will assume only one agent, starting at s, and only one goal at vertex t, The problem is to find a policy that brings the agent from s to t, that has minimal expected cost, without encountering terrorists.

The graph can be provided in a manner similar to previous assignments, for example:

#V 4    ; number of vertices n in graph (from 1 to n)

#V 1 0    ; Vertex 1, 0 probability of terrorists.
#V 3 0.3  ; Vertex 3, 0.3  probability of terrorists.

etc.

#E 1 2 W3   ; Edge from vertex 1 to vertex 2, weight 3
#E 2 3 W2   ; Edge from vertex 2 to vertex 3, weight 2
#E 3 4 W3   ; Edge from vertex 3 to vertex 4, weight 3
#E 2 4 W4   ; Edge from vertex 2 to vertex 4, weight 4
#E 1 4 W100 ; Edge from vertex 1 to vertex 4, weight 100

#Start 1
#Goal 4

The start and goal vertices, should be determined via some form of user input (as in the file, or querying the user). For example, in the above graph the start vertex is 1, and the goal vertex is 4.

Solution method:

The Canadian traveller problem is known to be PSPACE-complete (and it is likely that the Yazidi treveler problem is also PSPACE complete) so you will be required to solve only very small instances. We will require that the entire belief space be stored in memory explicitly, and thus impose a limit of at most 10 vertices with possible terrorists in the graph. Your program should initialize belief space value functions and use a form of value iteration (discussed in class) to compute the value function for the belief states. Maintain the optimal action during the value iteration for each belief state, so that you have the optimal policy at convergence.

Requirements:

Your program should read the data, including the parameters (start goal vertices). You should construct the policy, and present it in some way. Provide at least the following types of output:

A full printout of the value of each belief-state, and the optimal action in that belief state, if it exists. (Print something that indicates so if this state is irregular, e.g. if it is unreachable).
Run a sequence of simulations. That is, generate a graph instance (terrorist locations) according to the terrorists distributions, and run the agent through this graph based on the (optimal) policy computed by your algorithm. Display the graph instance and sequence of actions. Allow the user to run additional simulations, for a newly generated instance in each case.