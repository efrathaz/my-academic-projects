package actions;

import java.util.ArrayList;

import graph.Graph;
import graph.Node;
import agents.Agent;

public class AidAction implements Action {

	private static final int AID_PACKAGE_SIZE = 10;
	
	@Override
	public void execute(Graph world, Agent agent) {
		if (dropAidPackage(world.getNodes())){
			agent.addScore(AID_PACKAGE_SIZE);
			agent.addAction(this);
		}

	}

	// drop aid package at the lowest-numbered node that currently contains no aid package
	private boolean dropAidPackage(ArrayList<Node> nodes){		
		for (int i = 0 ; i < nodes.size() ; i++){
			if (nodes.get(i).getFoodUnits() < AID_PACKAGE_SIZE){
				nodes.get(i).setFoodUnits(AID_PACKAGE_SIZE);
				return true;
			}
		}
		return false;	
	}

	@Override
	public String getName() {
		return "Aid";
	}
}
