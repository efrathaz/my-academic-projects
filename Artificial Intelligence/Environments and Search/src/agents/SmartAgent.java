package agents;

import java.util.LinkedList;
import java.util.PriorityQueue;

import actions.Action;
import actions.NoopAction;
import actions.TraverseAction;
import graph.Func;
import graph.Graph;
import graph.Node;
import search.ComparatorByF;
import search.ComparatorByH;
import search.StateNode;

public class SmartAgent extends YazidiAgent {

	protected int iT;  // sum of all the expansions for this agent
	protected int iCurrExpansionsNum;
	protected int iMaxExpansionsNum;
	protected String sMethod;  // Greedy / A* / RTA*
	protected StateNode currState;
	protected LinkedList<StateNode> statePath;
	
	public SmartAgent(int id, int start, int goal, String method, int maxExpansions) {
		super(id, start, goal);
		iT = 0;
		iCurrExpansionsNum = 0;
		sMethod = method;
		currState = new StateNode(iCurrLocation, iScore, iFoodUnits, null, null);
		statePath = new LinkedList<StateNode>();
		iMaxExpansionsNum = maxExpansions;
	}
	
	public int getExpansionNum(){
		return iT;
	}
	
	public String type(){
		if (sMethod == "Greedy"){
			return "Greedy Heuristic Yazidi";
		}
		else if (sMethod == "A*"){
			return "A* Yazidi";
		}
		return "RTA* Yazidi";
	}
	
	public Action calcAction(Graph world){
		
		// update current state according to world
		Node srcNode = world.getNodeByID(iCurrLocation);
		takeFood(world, srcNode);
		currState.setLocation(iCurrLocation);
		currState.setFoodUnits(iFoodUnits);
		
		// update statePath
		searchStatePath(world);
		
		if (statePath.isEmpty()){
			return new NoopAction();
		}
		
		// get next action and state from the actionPath
		StateNode newState = statePath.removeFirst();
		return newState.getPrevAction();
	}

	// update statePath to be the best path of actions according to method (Greedy / A*)
	private void searchStatePath(Graph world){
		
		LinkedList<StateNode> lVisitedStates = new LinkedList<StateNode>();
		PriorityQueue<StateNode> qExpansion;
		StateNode nodeToExpand;
		
		// init qExpansion with the appropriate comparator (h(n) for greedy / f(n) for A*)
		if (sMethod == "Greedy"){
			qExpansion = new PriorityQueue<StateNode>(1, new ComparatorByH());
		}
		else {
			qExpansion = new PriorityQueue<StateNode>(1, new ComparatorByF());
		}
		// init qExpansion with current state (root)
		qExpansion.add(currState);
		
		currState.setH(heuristic(world, currState));
		currState.setF(currState.getH());
		iCurrExpansionsNum = 0; // start counting number of expansions
		
		// add best state to lVisitedStates, and expand it if it's != goal
		while (!qExpansion.isEmpty()){
			nodeToExpand = qExpansion.poll();
			
			if (nodeToExpand.getLocation() == iGoal || iCurrExpansionsNum == iMaxExpansionsNum){
				// build path from currState to nodeToExpand
				buildStatePath(nodeToExpand);
				return;
			}
			lVisitedStates.add(nodeToExpand);
			
			// expand
			LinkedList<StateNode> succStates = createSuccessors(world, nodeToExpand);

			// add successor state that hasn't been visited to qExpansion
			for (StateNode successor : succStates){
				if (lVisitedStates.contains(successor)){
					continue;
				}
				// calculate tentative g(successor)
				int tempG = nodeToExpand.getG() + getActionScore(world, nodeToExpand, successor);
				
				// add successor to qExpansion and update its g and f
				if (!qExpansion.contains(successor) || tempG <= successor.getG()){
					int tempH = heuristic(world, successor);
					int tempF = tempH + tempG;
					successor.setParentState(nodeToExpand);
					successor.setG(tempG);
					successor.setH(tempH);
					successor.setF(tempF);
					if (!qExpansion.contains(successor)){
						qExpansion.add(successor);
					}
				}
			}
			iCurrExpansionsNum++;
			iT++;
		} // end while
		
		// if qExpansion is empty and iGoal is not found, then there is no action path
		statePath.clear();
	}

	private int getActionScore(Graph world, StateNode currState, StateNode nextState) {
		// NOOP
		if (nextState.getPrevAction() instanceof NoopAction){
			return 1;
		}
		// traverse
		int weight = world.getEdgeWeight(currState.getLocation(), nextState.getLocation());
		return weight * currState.getFoodUnits();
	}

	private LinkedList<StateNode> createSuccessors(Graph world, StateNode currState) {
		
		LinkedList<StateNode> successors = new LinkedList<StateNode>();
		Node currNode = world.getNodeByID(currState.getLocation());
		
		// create StateNode if no-op doesn't end in death
		if (currState.getFoodUnits() > 0){
			successors.add(new StateNode(currState.getLocation(), currState.getG()+1, currState.getFoodUnits()-1, new NoopAction(), currState));
		}
		
		// add new states for traverse
		for(Node neighbour : currNode.getNeighbours()){
			int newLocation = neighbour.getID();
			int weight = world.getEdgeWeight(currState.getLocation(), newLocation);

			// create StateNode if traverse doesn't end in death
			if (currState.getFoodUnits() >= weight || (newLocation == iGoal && currState.getFoodUnits() == weight)){
				int newScore = currState.getG() + currState.getFoodUnits()*weight;
				int newFood = currState.getFoodUnits() - weight + neighbour.getFoodUnits();
				successors.add(new StateNode(newLocation, newScore, newFood, new TraverseAction(currNode, neighbour), currState));
			}
		}
		return successors;
	}

	private void buildStatePath(StateNode goal) {
		
		StateNode temp = goal;
		statePath.clear();
		
		while (!temp.equals(currState)){
			statePath.addFirst(temp);
			temp = temp.getParentState();
		}
	}

	private int heuristic(Graph world, StateNode srcState) {
		int srcNodeID = srcState.getLocation();
		int pathScore = 0;
		LinkedList<Integer> minPath = world.findPath(srcNodeID, iGoal, new Func() {
			public int calcWeight(int weight){
				return weight*weight;
			}
		});
		if (minPath.size() >= 1){
			int weight = world.getEdgeWeight(srcNodeID, minPath.get(0));
			pathScore = pathScore + weight*weight;
			for (int i = 0 ; i < minPath.size()-1 ; i++){
				weight = world.getEdgeWeight(minPath.get(i), minPath.get(i+1));
				pathScore = pathScore + weight*weight;
			}
		}
		return pathScore;
	}

	public void print(){
		if (sMethod == "Greedy"){
			System.out.println("<Agent ID: " + iID + ", Agent Type: Greedy Heuristic Yazidi, Score: " + iScore 
					+ ", Current Location: " + iCurrLocation + ", Goal Location: " 
					+ iGoal + ", Food Units: " + iFoodUnits);
		}
		else {
			System.out.println("<Agent ID: " + iID + ", Agent Type: A* Yazidi, Score: " + iScore 
					+ ", Current Location: " + iCurrLocation + ", Goal Location: " 
					+ iGoal + ", Food Units: " + iFoodUnits);
		}
	}
	
}
