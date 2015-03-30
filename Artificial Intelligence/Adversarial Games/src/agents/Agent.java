package agents;

import graph.Graph;
import graph.Node;

import java.util.ArrayList;
import java.util.LinkedList;

import search.State;
import actions.Action;
import actions.NoopAction;
import actions.TraverseAction;

public abstract class Agent {
	
	protected int iID;
	protected int iCurrLocation;
	protected int iGoal;
	protected int iScore;
	protected int iFoodUnits;
	protected LinkedList<Action> lActions;
	protected boolean bWin;
	protected boolean bAlive;
	protected State currState;
	
	
	public Agent(int id, int start, int goal, int food){
		iID = id;
		iCurrLocation = start;
		iGoal = goal;
		iScore = 0;
		iFoodUnits = food;
		lActions = new LinkedList<Action>();
		bWin = false;
		bAlive = true;
		currState = new State(0, id, new ArrayList<Integer>(), null);
		currState.setScore(id, 0);
	}
	
	// getters and setters
	
	public int getID(){
		return iID;
	}
	
	public int getCurrLocation(){
		return iCurrLocation;
	}
	
	public void setCurrLocation(int location){
		iCurrLocation = location;
	}
	
	public int getScore(){
		return iScore;
	}
	
	public void setScore(int newScore){
		iScore = newScore;
	}
	
	public String getActions(){
		String actions = "";
		int i = 0;
		for (i = 0 ; i < lActions.size() ; i++){
			actions = actions + lActions.get(i).getName() + ", ";
		}
		
	//	actions = actions + lActions.get(i).getName();
		return actions;
	}
	
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
	
	public void addScore(int score){
		iScore += score;
	}
	
	public int numOfActions(){
		return lActions.size();
	}
	
	public void takeFood(Graph world, Node node){
		addFoodUnits(node.getFoodUnits());
		node.setFoodUnits(0);
	}
	
	public boolean isAlive(){
		return bAlive;
	}
	
	public boolean didWin(){
		return bWin;
	}
	
	public void win(){
		bWin = true;
	}
	
	public void addAction(Action action){
		lActions.add(action);
	}
	
	public void kill(){
		bAlive = false;
		iScore = Integer.MAX_VALUE;
	}
	
	protected void updateState(Graph world){
		Node srcNode = world.getNodeByID(iCurrLocation);
		takeFood(world, srcNode);
		currState.setLocation(iID, iCurrLocation);
		currState.setFoodUnits(iID, iFoodUnits);
		currState.setScore(iID, iScore);
		currState.setLocation((iID+1)%2, world.getAgents().get((iID+1)%2).getCurrLocation());
		currState.setFoodUnits((iID+1)%2, world.getAgents().get((iID+1)%2).getFoodUnits());
		currState.setScore((iID+1)%2, world.getAgents().get((iID+1)%2).getScore());
		currState.setPlayer(iID);
		if (currState.getFoodInWorld().size() < world.getNodes().size()){
			for (int i = 0 ; i < world.getNodes().size() ; i++){
				currState.addFoodInWorld(world.getNodeByID(i+1).getFoodUnits());
			}
			return;
		}
		for (int i = 0 ; i < world.getNodes().size() ; i++){
			currState.setFoodInNode(i+1, world.getNodeByID(i+1).getFoodUnits());
		}
	}
	
	protected LinkedList<State> createSuccessors(Graph world, State state) {
		
		int player = state.getPlayer();
		int goal = world.getAgents().get(player).getGoal();
		LinkedList<State> successors = new LinkedList<State>();
		Node currNode = world.getNodeByID(state.getLocation(player));
		
		// if the current player is dead, the other player continues but the state doesn't change
		if (state.getLocation(player) == goal || state.getFoodUnits(player) < 1){
			successors.add(createNullSucc(state));
			return successors;
		}
		
		// create StateNode if no-op doesn't end in death
		if (state.getFoodUnits(player) > 0){
			successors.add(createNoopSucc(state));
		}
		
		// add new states for traverse
		LinkedList<Node> neighbours = currNode.getNeighbours();
		
		for(int i = 0 ; i < neighbours.size() ; i++){
			Node neighbour = neighbours.get(i);
			int newLocation = neighbour.getID();
			int weight = world.getEdgeWeight(state.getLocation(player), newLocation);

			// create StateNode if traverse doesn't end in death
			if (state.getFoodUnits(player) >= weight || (newLocation == goal && state.getFoodUnits(player) == weight)){
				successors.add(createTraverseSucc(state, currNode, neighbour, newLocation, weight));
			}
		}
		return successors;
	}

	protected State createNullSucc(State state) {
		int player = state.getPlayer();
		int opponent = (player+1)%2;
		State succ = new State(state.getDepth()+1, opponent, state.getFoodInWorld(), null);
		
		succ.setLocation(player, state.getLocation(player));
		succ.setFoodUnits(player, state.getFoodUnits(player));
		succ.setScore(player, state.getScore(player));
		
		succ.setLocation(opponent, state.getLocation(opponent));
		succ.setFoodUnits(opponent, state.getFoodUnits(opponent));
		succ.setScore(opponent, state.getScore(opponent));
		
		return succ;
	}

	protected State createNoopSucc(State state){
		
		int player = state.getPlayer();
		int opponent = (player+1)%2;
		State succ = new State(state.getDepth()+1, opponent, state.getFoodInWorld(), new NoopAction());
		
		succ.setLocation(player, state.getLocation(player));
		succ.setFoodUnits(player, state.getFoodUnits(player)-1);
		succ.setScore(player, state.getScore(player)+1);
		
		succ.setLocation(opponent, state.getLocation(opponent));
		succ.setFoodUnits(opponent, state.getFoodUnits(opponent));
		succ.setScore(opponent, state.getScore(opponent));
		
		return succ;
	}
	
	protected State createTraverseSucc(State state, Node source, Node dest, int newLocation, int weight){
		
		int player = state.getPlayer();
		int opponent = (player+1)%2;
		int newScore = state.getScore(player) + state.getFoodUnits(player)*weight;
		int newFood = state.getFoodUnits(player) - weight + state.getFoodInNode(dest.getID());
		
		State succ = new State(state.getDepth()+1, opponent, state.getFoodInWorld(), new TraverseAction(source, dest));
		succ.setFoodInNode(dest.getID(), 0);
		
		succ.setLocation(player, newLocation);
		succ.setFoodUnits(player, newFood);
		succ.setScore(player, newScore);
		
		succ.setLocation(opponent, state.getLocation(opponent));
		succ.setFoodUnits(opponent, state.getFoodUnits(opponent));
		succ.setScore(opponent, state.getScore(opponent));
		
		return succ;
	}
	
	protected boolean cutoffTest(Graph world, State state){
		
		if (state.getDepth() > world.getCutoff()){
			return true;
		}
		return false;
	}
	
	public abstract void print();
	
	public abstract Action calcAction(Graph world);
}
