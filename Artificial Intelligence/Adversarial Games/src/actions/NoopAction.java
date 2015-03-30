package actions;

import graph.Graph;
import agents.Agent;
import agents.ISISMinimax;

public class NoopAction implements Action {

	@Override
	public void execute(Graph world, Agent agent) {
		
		// Yazidi Agent
		if (!(agent instanceof ISISMinimax)) {
			
			if (agent.getFoodUnits() < 1){
				agent.kill();
				world.getNodeByID(agent.getCurrLocation()).removeAgent(agent);
			}
			else {
				agent.removeFoodUnits(1);
				agent.addScore(1);
			}
		}
		agent.addAction(this);
	}

	@Override
	public String getName() {
		return "NO-OP";
	}

}
