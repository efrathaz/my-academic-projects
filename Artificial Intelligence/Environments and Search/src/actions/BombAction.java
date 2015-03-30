package actions;


import graph.Graph;
import graph.Node;
import agents.Agent;

public class BombAction implements Action {

	private Node nodeToBomb;
	private int iBombCost;
	
	public BombAction(Node n, int cost){
		nodeToBomb = n;
		iBombCost = cost;
	}
	
	@Override
	public void execute(Graph world, Agent agent) {
		nodeToBomb.bomb();
		agent.addScore(iBombCost);
		agent.addAction(this);
	}

	@Override
	public String getName() {
		return "Bomb";
	}

}
