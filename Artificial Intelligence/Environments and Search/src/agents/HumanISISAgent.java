package agents;

import graph.Graph;
import graph.Node;

import java.util.LinkedList;
import java.util.Scanner;

import actions.Action;
import actions.NoopAction;
import actions.TraverseAction;

public class HumanISISAgent extends ISISAgent {

	private Scanner scanner;
	
	public HumanISISAgent(int id, int start, Scanner s) {
		super(id, start);
		scanner = s;
	}

	public String type(){
		return "Human ISIS";
	}
	
	public Action calcAction(Graph world){
		
		int i, actionNum;
		int index = 1;
		Node srcNode = world.getNodeByID(iCurrLocation);
		takeFood(world, srcNode);
		
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
}
