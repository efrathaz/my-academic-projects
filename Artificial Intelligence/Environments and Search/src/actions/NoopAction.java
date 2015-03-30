package actions;

import graph.Graph;
import agents.Agent;
import agents.YazidiAgent;

public class NoopAction implements Action {

	@Override
	public void execute(Graph world, Agent agent) {
		
		// Yazidi Agent
		if (agent instanceof YazidiAgent) {
			
			if (((YazidiAgent) agent).getFoodUnits() < 1){
				agent.kill();
				world.getNodeByID(agent.getCurrLocation()).removeAgent(agent);
			}
			else {
				((YazidiAgent) agent).removeFoodUnits(1);
				((YazidiAgent) agent).addScore(1);
			}
		}
		agent.addAction(this);
	}

	@Override
	public String getName() {
		return "NO-OP";
	}

}
