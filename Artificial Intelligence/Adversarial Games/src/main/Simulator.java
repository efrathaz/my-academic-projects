package main;
import java.util.LinkedList;

import graph.Graph;
import actions.Action;
import agents.Agent;
import agents.ISISMinimax;

public class Simulator {
	
	private Graph world;
	
	public Simulator(Graph world){
		this.world = world;
	}
	
	// changes the state of the world according to a specific agent's action
	public void simulate(){
		
		int iterNum = 0;
		boolean worldEnd = false;
		LinkedList<Agent> agents = world.getAgents();
		
		while(!worldEnd && iterNum <= world.getMaxIterNum()){
			for (int i = 0 ; i < agents.size() ; i++){
				Agent agent = agents.get(i);
				if (agent.isAlive() && !(agent.didWin())){
					Action action = agent.calcAction(world);
					if (action != null){
						action.execute(world, agent);
						System.out.println("\n" + agent.getID() + ": " + action.getName() + "\n");
						world.printState();
					}
				}
			}
			worldEnd = checkEnd(agents);
			iterNum++;
		}
	}
	
	// return true if all the refugees are either dead or reached their goal location
	private static boolean checkEnd(LinkedList<Agent> agents){
		for (int i = 0 ; i < agents.size() ; i++){
			Agent agent = agents.get(i);
			if (!(agent instanceof ISISMinimax) && agent.isAlive() && !agent.didWin()){
				return false;
			}
		}
		return true;
	}

}
