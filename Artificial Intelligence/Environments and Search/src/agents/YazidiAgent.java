package agents;
import graph.Func;
import graph.Graph;
import graph.Node;

import java.util.LinkedList;

import actions.Action;
import actions.NoopAction;
import actions.TraverseAction;


public class YazidiAgent extends Agent {
	
	protected int iGoal;
	protected int iFoodUnits;
	private LinkedList<Integer> lPath;
	
	public YazidiAgent(int id, int start, int goal){
		super(id, start);
		iGoal = goal;
		iFoodUnits = 0;
		lPath = new LinkedList<Integer>();
	}
	
	// getters and setters
	
	public int getGoal(){
		return iGoal;
	}
	
	public int getFoodUnits(){
		return iFoodUnits;
	}
	
	// methods
	
	public void addFoodUnits(int food){
		iFoodUnits = iFoodUnits + food;
	}
	
	public void removeFoodUnits(int food){
		iFoodUnits = iFoodUnits - food;
	}
	
	public String type(){
		return "Greedy Yazidi";
	}
	
	public void print(){
		System.out.println("<Agent ID: " + iID + ", Agent Type: Greedy Yazidi, Score: " + iScore 
							+ ", Current Location: " + iCurrLocation + ", Goal Location: " 
							+ iGoal + ", Food Units: " + iFoodUnits);
	}

	public Action calcAction(Graph world){
		
		Node srcNode = world.getNodeByID(iCurrLocation);
		takeFood(world, srcNode);
		// on first iteration calculate path
		if (world.getIterNum() == 0){
			lPath = world.findPath(iCurrLocation, iGoal, new Func() {
				public int calcWeight(int weight){
					return weight;
				}
			});
		}
		
		// check if traverse is possible
		if (lPath.size() > 0){
			
			int dest = lPath.removeFirst();
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
	
	public void takeFood(Graph world, Node node){
		iFoodUnits = iFoodUnits + node.getFoodUnits();
		node.setFoodUnits(0);
	}
	
	public void updateFields(Graph world, Node newNode, Action action){
		
		int edgeWeight = world.getEdgeWeight(iCurrLocation, newNode.getID());
		iCurrLocation = newNode.getID();
		iScore = iScore + edgeWeight * iFoodUnits;	
		iFoodUnits = iFoodUnits - edgeWeight;
		takeFood(world, newNode);
	}
	
}
