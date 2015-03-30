package agents;

import graph.Graph;
import graph.Node;

import java.util.LinkedList;
import java.util.Scanner;

import actions.Action;
import actions.NoopAction;
import actions.TraverseAction;

public class HumanISISAgent extends ISISMinimax {

	private Scanner scanner;
	
	public HumanISISAgent(int id, int start, Scanner s) {
		super(id, start);
		scanner = s;
	}

	public String type(){
		return "Human ISIS";
	}
	
	protected void updateState(Graph world){
		currState.setLocation(iID, iCurrLocation);
		currState.setFoodUnits(iID, iFoodUnits);
		currState.setLocation((iID+1)%2, world.getAgents().get((iID+1)%2).getCurrLocation());
	}
	
	public Action calcAction(Graph world){
		
		int i, actionNum;
		int index = 1;
		Node srcNode = world.getNodeByID(iCurrLocation);

		// update state
		takeFood(world, srcNode);
		updateState(world);
		
		// print action options to the user
		LinkedList<Node> lNeigbours  = world.getNeighbours(iCurrLocation);
		
		System.out.println("Human ISIS, ID " + iID + ", Please choose your action:");
		for (i=0; i<lNeigbours.size(); i++){
			System.out.println("[" + index + "] Traverse to node number " + lNeigbours.get(i).getID());
			index++;
		}
		System.out.println("[" + index + "] No-op");
		System.out.print("> ");
		
		// receive action from user
		actionNum = scanner.nextInt();

		// handle action
		if (actionNum <= lNeigbours.size()){
			int dest = lNeigbours.get(actionNum-1).getID();
			Node destNode = world.getNodeByID(dest);
			return new TraverseAction(srcNode, destNode);
		}		
		return new NoopAction();
	}

	@Override
	public void print() {
		System.out.println("<Agent ID: " + iID + ", Agent Type: ISISMinimax, Score: " + iScore 
				+ ", Current Location: " + iCurrLocation);
	}	
}
