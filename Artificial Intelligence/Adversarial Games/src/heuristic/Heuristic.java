package heuristic;

import graph.Graph;
import search.State;

public interface Heuristic {

	public int run(Graph world, State srcState, int player, int goal);
	
}
