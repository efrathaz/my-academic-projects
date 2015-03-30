package decision;

import graph.Graph;
import graph.Node;

import java.util.LinkedList;

public class Policy {

	public static final int T = 1;
	public static final int F = 2;
	public static final int U = 3;
	
	private LinkedList<State> states;
	private LinkedList<Action> actions;
	private Graph graph;
	
	public Policy(Graph g, LinkedList<Integer> unknownNodes){
		graph = g;
		states = new LinkedList<State>();
		actions = new LinkedList<Action>();
		createInitialState(unknownNodes);
		createStateSpace();
	}
	
	public int getGoal(){
		return graph.getGoal();
	}
	
	public LinkedList<State> getStates(){
		return states;
	}
	
	public LinkedList<Action> getActions(){
		return actions;
	}
	
	public int getActionCost(Action a){
		return graph.getEdgeWeight(a.getSrc(), a.getDest());
	}
	
	public double getISISprob(int nodeIndex){
		return graph.getISISprobByID(nodeIndex);
	}
	
	private void createInitialState(LinkedList<Integer> unknownNodes){
		
		LinkedList<ISISAtNode> ISISinfo = new LinkedList<ISISAtNode>();
		
		for (int i = 0 ; i < unknownNodes.size() ; i++){
			ISISinfo.add(new ISISAtNode(unknownNodes.get(i), U));
		}
		states.add(new State(graph.getStart(), ISISinfo));
	}
	
	private void createStateSpace(){
		
		int i, j, k, currLocation, currNeighbour;
		State currState;
		LinkedList<Node> neighbours;
		LinkedList<Integer> nodesToUpdate = new LinkedList<Integer>();
		
		
		// stop when no new state has been added
		for(i = 0 ; i < states.size() ; i++){
			
			currState = states.get(i);
			currLocation = currState.getLocation();
			
			if(currLocation == graph.getGoal()){
				continue;
			}
			
			neighbours = graph.getNeighbours(currLocation);
			
			// add state per action
			for (j = 0 ; j < neighbours.size() ; j++){
				currNeighbour = neighbours.get(j).getID();
				
				// create action for traversing from currLocation to currNeighbour
				if (!actionExists(currLocation, currNeighbour)){
					actions.add(new Action(currLocation, currNeighbour));
				}
				
				LinkedList<ISISAtNode> ISISlocations = currState.getISISinfo();
				
				// for each node where we don't know if there is ISIS in it
				for (k = 0 ; k < ISISlocations.size() ; k++){
					ISISAtNode Ti = ISISlocations.get(k);
					
					// if that node is our neighbor, then we know if it has ISIS or not
					if(Ti.getValue() == U && graph.getEdgeWeight(Ti.getNodeID(), currNeighbour) > -1){
						nodesToUpdate.add(Ti.getNodeID());
					}
				}
				
				// if we are not in a node whose neighbors might have ISIS
				if (nodesToUpdate.isEmpty()){
					State s = new State(currNeighbour, ISISlocations);
					if (!stateExists(s)){
						states.add(s);
					}
				}
				
				// if we are in a node whose neighbors might have ISIS, create state for each possibility
				createStates(currNeighbour, ISISlocations, nodesToUpdate);
			}
		}
	}


	// create states with permutations of the nodes that might have ISIS
	private void createStates(int nodeID, LinkedList<ISISAtNode> ISISinfo, LinkedList<Integer> nodesToUpdate){
		
		if(nodesToUpdate.isEmpty()){
			State s = new State(nodeID, ISISinfo);
			if (!stateExists(s)){
				states.add(s);
			}
		}
		else{
			int nodeToUpdate = nodesToUpdate.removeFirst();
			int index = 0;
			
			// find index of nodeToUpdate in ISISinfo
			for (int i = 0 ; i < ISISinfo.size() ; i++){
				if (ISISinfo.get(i).getNodeID() == nodeToUpdate){
					index = i;
					break;
				}
			}
			
			// create states where TnodeToUpdate = T
			LinkedList<ISISAtNode> ISISinfo_T = deepCopy1(ISISinfo);
			ISISinfo_T.get(index).setValue(T);
			LinkedList<Integer> nodesToUpdate_T = deepCopy2(nodesToUpdate);
			createStates(nodeID, ISISinfo_T, nodesToUpdate_T);
			
			// create states where TnodeToUpdate = F
			LinkedList<ISISAtNode> ISISinfo_F = deepCopy1(ISISinfo);
			ISISinfo_F.get(index).setValue(F);
			LinkedList<Integer> nodesToUpdate_F = deepCopy2(nodesToUpdate);
			createStates(nodeID, ISISinfo_F, nodesToUpdate_F);
		}
	}
	
	private LinkedList<ISISAtNode> deepCopy1(LinkedList<ISISAtNode> e1) {
		LinkedList<ISISAtNode> e2 = new LinkedList<ISISAtNode>();
		for (int i = 0 ; i < e1.size() ; i++){
			e2.add(new ISISAtNode(e1.get(i).getNodeID(), e1.get(i).getValue()));
		}
		return e2;
	}
	
	private LinkedList<Integer> deepCopy2(LinkedList<Integer> e1) {
		LinkedList<Integer> e2 = new LinkedList<Integer>();
		for (int i = 0 ; i < e1.size() ; i++){
			e2.add(e1.get(i));
		}
		return e2;
	}
	
	private boolean actionExists(int v, int u) {
		for (int i = 0 ; i < actions.size() ; i++){
			if(actions.get(i).getSrc() == v && actions.get(i).getDest() == u){
				return true;
			}
		}
		return false;
	}
	
	private boolean stateExists(State s){
		for (int i = 0 ; i < states.size() ; i++){
			if(states.get(i).equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public void printStates(){
		System.out.println("STATES:");
		for (int i = 0 ; i < states.size() ; i++){
			states.get(i).print();
		}
	}
	
	public void printActions(){
		System.out.println("ACTIONS:");
		for (int i = 0 ; i < actions.size() ; i++){
			actions.get(i).print();
		}
	}
	
	private int isDeadS(State s){
		LinkedList<ISISAtNode> sISISinfo = s.getISISinfo();
		
		// check if dead because of ISIS
		for (int i=0 ; i<sISISinfo.size() ; i++){
			if (sISISinfo.get(i).getValue() == 1 && sISISinfo.get(i).getNodeID() == s.getLocation()){
				s.setUtility(-1000.0);
				return 1;
			}
		}
		return 0;
	}
	
	public void print(){
		for (int i = 0 ; i < states.size() ; i++){
			System.out.print("*State: ");
			states.get(i).print();
			System.out.print("Utility: ");
			System.out.println(states.get(i).getUtility());
			if (states.get(i).getBestStates().size() > 0){
				System.out.print("Next States: ");
				for (int j = 0 ; j < states.get(i).getBestStates().size() ; j++){
					states.get(i).getBestStates().get(j).print();
				}
			}
			else{
				System.out.println("No next State ");
			}
			if (isDeadS(states.get(i)) == 0){
				if (states.get(i).getBestAction() != null){
					System.out.print("Best action: ");
					states.get(i).getBestAction().print();
				}
				else {
					System.out.println("Finished!");
				}
			}
			else{
				System.out.println("No action");
			}
		}
	}
}
