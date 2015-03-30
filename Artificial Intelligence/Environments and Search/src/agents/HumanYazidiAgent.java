package agents;

import java.util.LinkedList;
import java.util.Scanner;

import actions.Action;
import actions.NoopAction;
import actions.TraverseAction;
import graph.Graph;
import graph.Node;

public class HumanYazidiAgent extends YazidiAgent {
	
	private Scanner scanner;
	
	public HumanYazidiAgent(int id, int start, int goal, Scanner s) {
		super(id, start, goal);
		scanner = s;
	}
	
	public String type(){
		return "Human Yazidi";
	}
	
	public Action calcAction(Graph world){
		
		int i, actionNum;
		int index = 1;
		Node srcNode = world.getNodeByID(iCurrLocation);
		
		takeFood(world, srcNode);
		
		// print action options to the user
		LinkedList<Node> lNeigbours  = world.getNeighbours(iCurrLocation);
		
		System.out.println("Human Yazidi, ID " + iID + ", Please choose your action:");
		for (i=0; i<lNeigbours.size(); i++){
			System.out.println("[" + index + "] Traverse to node number " + lNeigbours.get(i).getID());
			index++;
		}
		System.out.println("[" + index + "] No-op");
		System.out.print("> ");
		
		// receive action from user
		actionNum = Integer.parseInt(scanner.next());
		System.out.println("You chose option number " + actionNum);

		// handle action
		if (actionNum <= lNeigbours.size()){			
			int dest = lNeigbours.get(actionNum-1).getID();
			Node destNode = world.getNodeByID(dest);
			
			int edgeWeight = world.getEdgeWeight(iCurrLocation, dest);
			
			if (edgeWeight > iFoodUnits){
				return new NoopAction();
			}
			else{
				return new TraverseAction(srcNode, destNode);
			}
		}
		return new NoopAction();
	}
}
