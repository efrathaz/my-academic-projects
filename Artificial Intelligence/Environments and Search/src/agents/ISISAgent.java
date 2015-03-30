package agents;

import graph.Func;
import graph.Graph;
import graph.Node;

import java.util.ArrayList;
import java.util.LinkedList;

import actions.Action;
import actions.NoopAction;
import actions.TraverseAction;

public class ISISAgent extends Agent {
	
	private LinkedList<Integer> lPath;
	
	public ISISAgent(int id, int start){
		super(id, start);
		lPath = new LinkedList<Integer>();
	}

	private boolean findPath(Graph world){
		
		ArrayList<Node> nodes = world.getNodes();
		LinkedList<Integer> path = new LinkedList<Integer>();
		int minPathLength = world.getN();
		Node closestNode = null;

		lPath.clear();
//		world.bfs(iCurrLocation);
		world.dijkstra(iCurrLocation, new Func() {
			public int calcWeight(int weight){
				return 1;
			}
		});
		
		// check which Yazidi is the closest
		for (int i = 0 ; i < nodes.size() ; i++){
			
			Node tmpNode = nodes.get(i);
			int tmpDist = tmpNode.getDist();
			
			// don't check current node
			if (tmpNode.getID() == iCurrLocation){
				continue;
			}
			
			if (tmpDist < minPathLength && tmpNode.hasYazidis()){
				closestNode = tmpNode;
				minPathLength = tmpDist;
			}
		}
		
		// build path
		if (closestNode != null){
			
			if (closestNode.getDist() != 0){
				Node temp = closestNode;
				while(temp.getNodeParent() != null){
					path.addFirst(temp.getID());
					temp = temp.getNodeParent();
				}
			}
			// check if there is a path from current location to goal location
			if (path.size() > 0){
				lPath = path;
				return true;
			}
		}
		return false;
	}
	
	public void takeFood(Graph world, Node node){
		Node currentNode = world.getNodeByID(iCurrLocation);
		currentNode.setFoodUnits(0);
	}
	
	public String type(){
		return "Greedy ISIS";
	}
	
	public void print(){
		System.out.println("<Agent ID: " + iID + ", Agent Type: Greedy ISIS, Score: " + iScore + ", Current Location: " + iCurrLocation);
	}
	
	public Action calcAction(Graph world){
		
		Node srcNode = world.getNodeByID(iCurrLocation);
		takeFood(world, srcNode);
		
		boolean foundPath = findPath(world);
		
		// check if traverse is possible
		if (foundPath){			
			int dest = lPath.removeFirst();
			Node destNode = world.getNodeByID(dest);
			return new TraverseAction(srcNode, destNode);
		}
		return new NoopAction();
	}	
}
