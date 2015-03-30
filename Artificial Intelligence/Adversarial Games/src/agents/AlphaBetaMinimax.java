package agents;

import java.util.LinkedList;

import search.State;
import graph.Graph;
import graph.Node;
import heuristic.SquareEdgesHeuristic;
import actions.Action;
import actions.NoopAction;
import actions.TraverseAction;

public class AlphaBetaMinimax extends Agent {

	public AlphaBetaMinimax(int id, int start, int goal, int food) {
		super(id, start, goal, food);
	}
	
	@Override
	public Action calcAction(Graph world) {
		
		// update current state according to world
		updateState(world);
		
		Action action = alphaBetaDecision(world);
		
		return action;
	}
	
	public Action alphaBetaDecision(Graph world){
		
		int a = Integer.MIN_VALUE;
		int b = Integer.MAX_VALUE;
		Action chosenAction = new NoopAction();
		int min;
		
		LinkedList<State> successors = createSuccessors(world, currState);
		
		// find the successor state with the maximal MinValue
		for (int i = 0 ; i < successors.size() ; i++){
			
			min = minValue(world, successors.get(i), a, b);
			
			if (min > a){
				a = min;
				chosenAction = successors.get(i).getPrevAction();
			}
		}
		return chosenAction;
	}
	
	private int maxValue(Graph world, State state, int a, int b){
		
		int i, min, v = Integer.MIN_VALUE;

		// check terminal
		if(terminalTest(world, state)){
			return utility(world, state);
		}
		
		// check cutoff - if true use heuristic
		if (cutoffTest(world, state)){
			return evaluateValue(world, currState);
		}
		
		LinkedList<State> successors = createSuccessors(world, state);
		
		// calc minValue for each successor
		for (i = 0 ; i < successors.size() ; i++){
			
			min = minValue(world, successors.get(i), a, b);
			
			v = v < min ? min : v;
			
			if (v >= b){
				return v;
			}

			a = a < v ? v : a;
		}
		return v;
	}
	
	private int minValue(Graph world, State state, int a, int b){
		
		int i, max, v = Integer.MAX_VALUE;

		// if terminal, return utility value
		if(terminalTest(world, state)){
			return utility(world, state);
		}
		
		// if reached cutoff, evaluate utility value
		if (cutoffTest(world, state)){
			return evaluateValue(world, currState);
		}

		LinkedList<State> successors = createSuccessors(world, state);
		
		// calc minValue for each successor
		for (i = 0 ; i < successors.size() ; i++){
			
			max = maxValue(world, successors.get(i), a, b);
			
			v = v < max ? v : max;
			
			if (v <= a){
				return v;
			}
			
			b = b < v ? b : v;
		}
		return v;
	}

	private boolean terminalTest(Graph world, State state){
		
		boolean atGoal = (state.getLocation(iID) == iGoal);
		int ISISLocation = state.getLocation((iID+1)%2);
		
		if (atGoal || (state.getFoodUnits(iID) < 1) || (state.getLocation(iID) == ISISLocation)){
			return true;
		}
		return false;
	}
	
	protected int utility(Graph world, State state){
		
		int score;
		int player = state.getPlayer();
		int yazidi, ISIS;
		
		if(world.getAgents().get(player) instanceof ISISMinimax){
			ISIS = player;
			yazidi = (player+1)%2;
			score = (state.getScore((player+1)%2));
		}
		else {
			yazidi = player;
			ISIS = (player+1)%2;
			score = state.getScore(player);
		}
		if (state.getLocation(yazidi) == state.getLocation(ISIS)){
			return Integer.MIN_VALUE;
		}
		if (state.getLocation(yazidi) == world.getAgents().get(yazidi).getGoal()){
			return (0 - score);
		}
		return Integer.MIN_VALUE;
	}
	
	protected int evaluateValue(Graph world, State state){
		
		SquareEdgesHeuristic heuristic = new SquareEdgesHeuristic();
		int score, yazidi, yazidiGoal;
		int player = state.getPlayer();
		
		if(world.getAgents().get(player) instanceof ISISMinimax){
			yazidi = (player+1)%2;
		}
		else {
			yazidi = player;
		}
		
		yazidiGoal = world.getAgents().get(yazidi).getGoal();
		score = heuristic.run(world, state, yazidi, yazidiGoal) + state.getScore(yazidi);
		
		if (state.getLocation(yazidi) == world.getAgents().get(yazidi).getGoal()){
			return (0 - score);
		}
		return Integer.MIN_VALUE;
	}
	
	@Override
	protected LinkedList<State> createSuccessors(Graph world, State state) {
		
		int player = state.getPlayer();
		int goal = world.getAgents().get(player).getGoal();
		LinkedList<State> successors = new LinkedList<State>();
		Node currNode = world.getNodeByID(state.getLocation(player));
		boolean isISIS = (world.getAgents().get(player) instanceof ISISMinimax);
		
		// create StateNode if no-op doesn't end in death
		if (isISIS){
			successors.add(createISISNoopSucc(state));
		}
		else if (state.getFoodUnits(player) > 0){
			successors.add(createNoopSucc(state));
		}
		
		// add new states for traverse
		LinkedList<Node> neighbours = currNode.getNeighbours();
		
		for(int i = 0 ; i < neighbours.size() ; i++){
			Node neighbour = neighbours.get(i);
			int newLocation = neighbour.getID();
			int weight = world.getEdgeWeight(state.getLocation(player), newLocation);

			// create StateNode if traverse doesn't end in death
			if (isISIS){
				successors.add(createISISTraverseSucc(state, currNode, neighbour, newLocation, weight));
			}
			else if (state.getFoodUnits(player) >= weight || (newLocation == goal && state.getFoodUnits(player) == weight)){
				successors.add(createTraverseSucc(state, currNode, neighbour, newLocation, weight));
			}
		}
		return successors;
	}
	
	@Override
	protected State createNoopSucc(State state){
		
		int player = state.getPlayer();
		int opponent = (player+1)%2;
		int newScore = state.getScore(player)+1;
		
		State succ = new State(state.getDepth()+1, opponent, state.getFoodInWorld(), new NoopAction());
		
		succ.setLocation(player, state.getLocation(player));
		succ.setFoodUnits(player, state.getFoodUnits(player)-1);
		succ.setScore(player, newScore);
		
		succ.setLocation(opponent, state.getLocation(opponent));
		succ.setFoodUnits(opponent, state.getFoodUnits(opponent));
		succ.setScore(opponent, (0-newScore));
		
		return succ;
	}
	
	protected State createISISNoopSucc(State state){
		
		int ISIS = state.getPlayer();
		int yazidi = (ISIS+1)%2;
		
		State succ = new State(state.getDepth()+1, yazidi, state.getFoodInWorld(), new NoopAction());
		
		succ.setLocation(ISIS, state.getLocation(ISIS));
		succ.setFoodUnits(ISIS, state.getFoodUnits(ISIS));
		succ.setScore(ISIS, state.getScore(ISIS));
		
		succ.setLocation(yazidi, state.getLocation(yazidi));
		succ.setFoodUnits(yazidi, state.getFoodUnits(yazidi));
		succ.setScore(yazidi,  state.getScore(yazidi));
		
		return succ;
	}
	
	@Override
	protected State createTraverseSucc(State state, Node source, Node dest, int newLocation, int weight){
		
		int yazidi = state.getPlayer();
		int ISIS = (yazidi+1)%2;
		int newScore = state.getScore(yazidi) + state.getFoodUnits(yazidi)*weight;
		int newFood = state.getFoodUnits(yazidi) - weight + state.getFoodInNode(dest.getID());
	
		State succ = new State(state.getDepth()+1, ISIS, state.getFoodInWorld(), new TraverseAction(source, dest));
		state.setFoodInNode(dest.getID(), 0);
		
		succ.setLocation(yazidi, newLocation);
		succ.setFoodUnits(yazidi, newFood);
		succ.setScore(yazidi, newScore);
		
		succ.setLocation(ISIS, state.getLocation(ISIS));
		succ.setFoodUnits(ISIS, state.getFoodUnits(ISIS));
		succ.setScore(ISIS, (0-newScore));
		
		return succ;
	}
	
	protected State createISISTraverseSucc(State state, Node source, Node dest, int newLocation, int weight){
		
		int ISIS = state.getPlayer();
		int yazidi = (ISIS+1)%2;
	
		State succ = new State(state.getDepth()+1, yazidi, state.getFoodInWorld(), new TraverseAction(source, dest));
		state.setFoodInNode(dest.getID(), 0);

		succ.setLocation(ISIS, newLocation);
		succ.setFoodUnits(ISIS, state.getFoodUnits(ISIS));
		succ.setScore(ISIS, state.getScore(ISIS));
		
		succ.setLocation(yazidi, state.getLocation(yazidi));
		succ.setFoodUnits(yazidi, state.getFoodUnits(yazidi));
		succ.setScore(yazidi, state.getScore(yazidi));
		
		return succ;
	}

	@Override
	public void print() {
		System.out.println("<Agent ID: " + iID + ", Agent Type: YazidiMinimax, Score: " + iScore 
				+ ", Current Location: " + iCurrLocation + ", Goal Location: " 
				+ iGoal + ", Food Units: " + iFoodUnits);
	}
}
