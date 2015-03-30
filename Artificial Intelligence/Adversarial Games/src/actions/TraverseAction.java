package actions;

import graph.Graph;
import graph.Node;
import agents.Agent;
import agents.ISISMinimax;

public class TraverseAction implements Action {

	private Node srcNode;
	private Node destNode;
	
	public TraverseAction(Node source, Node destination){
		srcNode = source;
		destNode = destination;
	}
	
	@Override
	public void execute(Graph world, Agent agent) {
		
		// Yazidi Agent
		if (!(agent instanceof ISISMinimax)) {
			int edgeWeight = world.getEdgeWeight(agent.getCurrLocation(), destNode.getID());
			
			if (destNode.getID() == agent.getGoal() && agent.getFoodUnits() >= edgeWeight){
				agent.win();
				
				// update agent's fields
				agent.setCurrLocation(destNode.getID());
				agent.addScore(edgeWeight * agent.getFoodUnits());
				agent.removeFoodUnits(edgeWeight);
				agent.takeFood(world, destNode);
			}
			else if (agent.getFoodUnits() < edgeWeight || destNode.hasISIS()){
				agent.kill();
			}
			else {
				// traverse to new node
				destNode.addAgent(agent);
				
				// update agent's fields
				agent.setCurrLocation(destNode.getID());
				agent.addScore(edgeWeight * agent.getFoodUnits());
				agent.removeFoodUnits(edgeWeight);
				agent.takeFood(world, destNode);
			}
			agent.addAction(this);
		}
		
		// ISIS Agent
		else {
			destNode.setFoodUnits(0);
			destNode.killYazidis();
			destNode.addAgent(agent);
			agent.setCurrLocation(destNode.getID());
			agent.addAction(this);
		}
		
		srcNode.removeAgent(agent);
	}
	
	@Override
	public String getName() {
		return "(" + srcNode.getID() + "-->" + destNode.getID() + ")";
	}

}
