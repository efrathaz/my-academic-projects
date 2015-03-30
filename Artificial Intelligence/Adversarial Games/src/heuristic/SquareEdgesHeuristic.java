package heuristic;

import java.util.LinkedList;

import graph.Func;
import graph.Graph;
import search.State;

public class SquareEdgesHeuristic implements Heuristic {

	@Override
	public int run(Graph world, State srcState, int player, int goal) {
		int srcNodeID = srcState.getLocation(player);
		int pathScore = 0;
		LinkedList<Integer> minPath = world.findPath(srcNodeID, goal, new Func() {
			public int calcWeight(int weight){
				return weight*weight;
			}
		});
		if (minPath.size() >= 1){
			int weight = world.getEdgeWeight(srcNodeID, minPath.get(0));
			pathScore = pathScore + weight*weight;
			for (int i = 0 ; i < minPath.size()-1 ; i++){
				weight = world.getEdgeWeight(minPath.get(i), minPath.get(i+1));
				pathScore = pathScore + weight*weight;
			}
		}
		return pathScore;
	}

}
