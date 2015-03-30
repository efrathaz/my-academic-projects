ASSIGNMENT DESCRIPTION:

In this first exercise you are asked to implement an environment simulator that runs a variant of the Canadian traveller problem (CTP). Then, you will implement some agents that live in the environment and evaluate their performance.

In the Canadian traveller problem, we are given a weighted graph, and the goal is to travel as cheaply as possible from a given start vertex to a given goal vertex. However, unlike standard shortest path problems in graphs, which have easy known solutions (e.g. the Dijkstra algorithm), here the problem is that some of the edges (an edge being an abstraction of a road in the real world) may be blocked by snow or ice, this being the Canadian traveller problem.

In the open research problem version of CTP, the agent can only tell which edges are blocked when visiting an incedent vertex. This leads to a problem of shortest path under uncertainty, with which we are not ready to deal yet. Instead, we will introduce a variant, called the Yazidi refugee problem, with certain knowledge but also add ways to unblock paths, making this an interesting search problem. In this problem there are three types of agents: Yazaidi refugees, ISIS terrorists, and US coalition bombers. Yazidi refugees begin as some graph node, and need to reach a goal - a node representing a city in the Kurdish controlled region in Iraq. ISIS terrorists attempt to kidnap Yazaidi refugees. US coalition bombers sometimes drop bombs, obliterating whetever is at a graph node, or aid packages, which stay at a node until either terrorists or refugees get there. A refugee can travel an edge if carrying a sufficient amount of food, and the food is consumed along the way. An aid package contains food that can be picked up. Your program should first act as an environment simulator. Then, then take up the task of a Yazidi refugee's planning a path (and later on a policy) to safety.

Yazidi refugee problem environment

The environment consists of a weighted unidrected graph. Each node can contain aid packages, and be visited by either ISIS terrorists or Yazidi refugees. The environment can contain one or more agents of each type, each with known starting location, and a required goal location (Yzaidis only). For each agent, there are state variables designating its current location, and for Yazidis also the amount of food being carried.

An agent (ISIS or Yazidi) can apply 2 types of action: traverse (like move in the standard CTP), and no-op. The no-op action does nothing, except consuming 1 unit of food if the agent is a Yazidi refugee (refugee dies if its turn arrives and if carrying no food and no aid package at node). The results of a traverse (from a current vertex V to a specific adjacent vertex U) action is as follows. A traverse operation by ISIS always succeeds. In addition, if U contains aid packages they are looted (and thus disappear), and if U has a Yazidi refugee, the latter agent dies. A traverse operation by a Yazidi is successful if there are no ISIS terrorists at U and a sufficient amount of food is carried: at least equal to the weight of the edge. The amount of food being carried is reduced by the weight of the edge. Any aid packages at U are automatically picked up. However, if the traversal is unsuccessful the refugee dies.

A US coalition agent can be: give a speech (by Obama, costs nothing and achieves nothing), drop an aid package containing some units of food at any node (cost equal to the amount of food units), or bomb any node (cost b, a user-specified parameter). The cost of actions (for Yazidis, since ISIS don't care about cost) is as follows: no-op has a cost 1 traverse has a cost equal to the edge weight, multiplied by the amount of food carried when starting the action. Actions resulting in death have infinite cost.

The simulator should keep track of score, i.e. the number of actions done by each agent, and the total cost encurred by the agent for traversing edges, etc.

Implementation part I: simulator + simple agents

Initially you will implement the environment simulator, and several simple (non-AI) agents. The environment simulator should start up by reading the graph from a file, in a format of your choice. We suggest using a simple adjancency list in an ASCII file, that initially specifies the number of vertices. For example (comments beginning with a semicolon):

#V 4    ; number of vertices n in graph (from 1 to n)

#E 1 2 W1  ; Edge from vertex 1 to vertex 2, weight 1
#E 3 4 W1  ; Edge from vertex 3 to vertex 4, weight 1
#E 2 3 W1  ; Edge from vertex 2 to vertex 3, weight 1
#E 1 3 W4  ; Edge from vertex 1 to vertex 3, weight 4
#E 2 4 W5  ; Edge from vertex 2 to vertex 4, weight 5
The simulator should query the user about the number of agents and what agent program to use for each of them, from a list defined below. Initialization parameters for each agent (initial and goal position) are also to be queried from the user.

After the above initialization, the simulator should run each agent in turn, performing the actions retured by the agents, and update the world accordingly. Additionally, the simulator should be capable of displaying the state of the world after each step, with the appropriate state of the agents and their score. A simple screen display in ASCII is sufficient (no bonuses for fancy graphics - this is not the point in this course!).

Each agent program (a function) works as follows. The agent is called by the simulator, together with a set of observations. The agent returns a move to be carried out in the current world state. The agent is allowed to keep an internal state (for example, a computed optimal path, or anything else desired) if needed. In this assignment, the agents can observe the entire state of the world.

You should implement the following agents:

A human agent, i.e. read the next move from the user, and return it to the simulator.
An Obama simulation automaton. This agent will work as follows: every 5 turns, (starting at turn 0) it drops an aid package with 10 units of food at the lowest-numbered node that currently contains no aid package. At the turn after dropping an aid package, it bombs a node containing an ISIS terrorist, if any, preferring a lower-numbered node when there is more than one possibility. Otherwise, and on the rest of the turns, Obama gives speeches.
A greedy Yazidi agent, that works as follows: the agent should compute the shortest path to its goal, and try to follow it. If there is no such path, do no-op.
A greedy ISIS agent, that works as follows: the agent should compute the shortest path (counting each edge as 1, ignoring the weights) to the closest Yazidi refugee, and traverse the first edge on that path. Prefer the lowest-numbered node in case of ties. If there is no such path, do no-op.
At this stage, you should run the environment with three agents participating in each run: an Obama simulator, and two other agents that can be chosen by the user. Your program should display and record the scores. In particular, you should run the greedy agent with various initial configurations. Also, test your environment with several agents in the same run, to see that it works correctly. You would be advised to do a good job here w.r.t. program extensibility, modularity, etc. much of this code may be used for some of the following assignments as well.

Implementation part II: search agents

Now, after chapter 4, you will be implementing intelligent Yazidi agents (this is part 2 of the assignment) that need to act in this environment. Each agent should assume that it is acting alone, regardless of whether it is true. You will be implementing a "search" agent as defined below. All the algorithms will use a heuristic evaluation function of your choice.

A greedy search agent, that picks the move with best immediate heuristic value.
An agent using A* search, with the same heuristic.
An agent using a simplified version of real-time A*.
The performance measure will be composed of two parts: S, the agent's score, and T, the number of search expansion steps performed by the search algorithm. The performance of an agent will be:

   P = f * S + T
Clearly, a better agent will have P as small as possible. The parameter f is a weight constant. You should compare the performance of the three agents (each acting alone) for the following values of f: 1, 100, 10000. Note that the higher the f parameter, the more important it is to expend computational resources in order to get a better score!

The program gets 3 arguments:
	1. Graph (as a text file)
	2. Bomb cost (number)
	3. Part of assignment (1 or 2)
	
* f is set in the main class.
* MaxExpansions is set by the user when creating a new RTA* agent.
* In the graph, number of food units may be specified for certain nodes.