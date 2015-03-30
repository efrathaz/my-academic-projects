package actions;

import graph.Graph;
import graph.Node;
import agents.Agent;
import agents.YazidiAgent;

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
		if (agent instanceof YazidiAgent) {
			int edgeWeight = world.getEdgeWeight(agent.getCurrLocation(), destNode.getID());
			
			if (destNode.getID() == ((YazidiAgent) agent).getGoal() && ((YazidiAgent) agent).getFoodUnits() >= edgeWeight){
				agent.win();
				
				// takes FoodUnits from new node
				((YazidiAgent) agent).updateFields(world, destNode, this);
			}
			else if (((YazidiAgent) agent).getFoodUnits() < edgeWeight || destNode.hasISIS()){
				agent.kill();
			}
			else {
				// traverse to new node
				destNode.addAgent(agent);
				
				// takes FoodUnits from new node
				((YazidiAgent) agent).updateFields(world, destNode, this);
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
