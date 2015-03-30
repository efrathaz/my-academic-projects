package actions;

import graph.Graph;
import agents.Agent;

public class SpeechAction implements Action {

	@Override
	public void execute(Graph world, Agent agent) {
		agent.addAction(this);
	}

	@Override
	public String getName() {
		return "Speech";
	}

}
