package agents;
import java.util.ArrayList;

import actions.Action;
import actions.AidAction;
import actions.BombAction;
import actions.NoopAction;
import actions.SpeechAction;
import graph.Graph;
import graph.Node;


public class USAAgent extends Agent {

	private int iBombCost;
	
	public USAAgent(int id, int start, int cost){
		super(id, start);
		iBombCost = cost;
	}

	public String type(){
		return "USA";
	}
	
	public void print(){
		System.out.println("<Agent ID: " + iID + ", Agent Type: USA Agent, Score: " + iScore);
	}	

	public void takeFood(Graph world, Node node){}
	
	public Action calcAction(Graph world){
		
		// send aidPacks every 5 iterations from 0,5..
		if (world.getIterNum()%5 == 0){
			return new AidAction();
		}
		
		// bomb every 5 iteration from 1,6..
		else if ((world.getIterNum()+1)%5 == 0){
			ArrayList<Node> nodes = world.getNodes();
			for (int i = 0 ; i < nodes.size() ; i++){
				if (nodes.get(i).hasISIS()){
					return  new BombAction(nodes.get(i), iBombCost);
				}
			}
			return new NoopAction();
		}
		
		// speech in all other iterations
		return new SpeechAction();
	}
}
