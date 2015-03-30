package main;
import java.util.LinkedList;

import graph.Graph;
import actions.Action;
import agents.Agent;
import agents.YazidiAgent;

public class Simulator {
	
	private Graph world;
	
	public Simulator(Graph world){
		this.world = world;
	}
	
	// changes the state of the world according to a specific agent's action
	public void simulate(){
		
		boolean worldEnd = false;
		LinkedList<Agent> agents = world.getAgents();
		
		while(!worldEnd){
			for (int i = 0 ; i < agents.size() ; i++){
				Agent agent = agents.get(i);
				if (agent.isAlive() && !(agent.didWin())){
					Action action = agent.calcAction(world);
					if (action != null){
						action.execute(world, agent);
						System.out.println("\n" + agent.type() + ": " + action.getName() + "\n");
						world.printState();
					}
				}
			}
			worldEnd = checkEnd(agents);
			world.incIterNum();
		}
	}
	
	// return true if all the refugees are either dead or reached their goal location
	private static boolean checkEnd(LinkedList<Agent> agents){
		for (int i = 0 ; i < agents.size() ; i++){
			Agent agent = agents.get(i);
			if (agent instanceof YazidiAgent && agent.isAlive() && !agent.didWin()){
				return false;
			}
		}
		return true;
	}

}
