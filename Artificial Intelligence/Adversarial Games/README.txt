ASSIGNMENT DESCRIPTION:

Game Environment:

As before, the environment consists of an undirected weighted graph. This time, the Obama agent is no longer part of this environment, so the aid packages will already be at the nodes from the beginning, so make the modification needed to support this (preferebly in the initialization file). A Yazidi agent has a start location and a goal location. For each agent, there are is a state variable designating it current location and amount of food it is carrying.

An agent can apply 2 types of action: traverse (like move in the standard CTP), and no-op. Semantics of the action are as in assignment 1, repeated below. The no-op action does nothing, except consuming 1 unit of food if the agent is a Yazidi refugee (refugee dies if its turn arrives and if carrying no food and no aid package at node). The results of a traverse (from a current vertex V to a specific adjacent vertex U) action is as follows. A traverse operation by ISIS always succeeds. In addition, if U contains aid packages they are looted (and thus disappear), and if U has a Yazidi refugee, the latter agent dies. A traverse operation by a Yazidi is successful if there are no ISIS terrorists at U and a sufficient amount of food is carried: at least equal to the weight of the edge. The amount of food being carried is reduced by the weight of the edge. Any aid packages at U are automatically picked up. However, if the traversal is unsuccessful the refugee dies.

Note that in this assignment the agents can be completely adversarial, or semi-cooperating, as discussed below. We will also assume that a user defined horizon T exists, the game always stops after T moves by each agent, or when all Yazidi refugees reach their goal or die, whichever comes first.

Implementation Steps:

The simulator should query the user about the parameters, the type of game (see below) as well as other initialization parameters for each agent (T, position, goals).

After the above initialization, the simulator should run each agent in turn, performing the actions retured by the agents, and update the world accordingly. Additionally, the simulator should be capable of displaying the world status after each step, with the appropriate state of the agents and their score. Here there are two types of agent programs: human (i.e. read input from keyboard) and game tree search agent.

Each agent program (a function) works as follows. The agent is called by the simulator, together with a set of observations. The agent returns a move to be carried out in the current world state. The agent is allowed to keep an internal state if needed. In this assignment, the agents can observe the entire state of the world. You should support the following types of games:

A zero sum game: Yazidi vs. ISIS. The self-score of an agent is the sum of earned rewards minus the cost of its moves (including penalty if applicable). The total score of an agent is its self-score minus the self-score of the opposing agent. Here you should implement an "optimal" agent using mini-max, with alpha-beta pruning.
A non zero-sum game: Yazaidi vs. Yazidi. Same as above but the agent score consisting only of its self-score. Ties are broken cooperatively.
A fully cooperative game Yazidi vs. Yazidi: the agents aim to maximize the sum of the self-scores of both agents.
Since the game tree will usually be too big to reach terminal positions in the search, you should also implement a cutoff, and a heuristic static evaluation function for each game. You may use the same heuristic for all games, if you think this is justified.