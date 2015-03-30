package actions;

import graph.Graph;
import agents.Agent;

public interface Action {
	
	public void execute(Graph world, Agent agent);
	
	public String getName();
	
}
