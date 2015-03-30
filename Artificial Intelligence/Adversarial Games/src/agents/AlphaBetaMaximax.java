package agents;

import java.util.LinkedList;

import search.State;
import graph.Graph;
import heuristic.SquareEdgesHeuristic;
import actions.Action;
import actions.NoopAction;

public class AlphaBetaMaximax extends Agent {

	public AlphaBetaMaximax(int id, int start, int goal, int food) {
		super(id, start, goal, food);
	}

	@Override
	public Action calcAction(Graph world) {
		
		// update current state according to world
		updateState(world);
		
		Action action = alphaBetaDecision(world);
		
		return action;
	}
	
	// this function returns an action
	public Action alphaBetaDecision(Graph world){
		
		int a = Integer.MIN_VALUE;
		int b = Integer.MAX_VALUE;
		Action chosenAction = new NoopAction();
		int max = 0;
		
		LinkedList<State> successors = createSuccessors(world, currState);
		
		// find the successor state with the maximal MinValue
		for (int i = 0 ; i < successors.size() ; i++){
			
			max = maxValue(world, successors.get(i), a, b);
			
			if (max > a){
				a = max;
				chosenAction = successors.get(i).getPrevAction();
			}
		}
		return chosenAction;
	}
	
	private int maxValue(Graph world, State state, int a, int b){
		
		int i, max, v = Integer.MIN_VALUE;
		
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
			
			max = maxValue(world, successors.get(i), a, b);
			
			v = v < max ? max : v;
			
			if (v >= b){
				return v;
			}

			a = a < v ? v : a;
		}
		return v;
	}
	
	protected boolean terminalTest(Graph world, State state){

		int player = state.getPlayer();
		int opponent = (player+1)%2;
		boolean playerAtGoal = (state.getLocation(player) == world.getAgents().get(player).getGoal());
		boolean opponentAtGoal = (state.getLocation(opponent) == world.getAgents().get(opponent).getGoal());
		boolean playerDead = (!playerAtGoal && (state.getFoodUnits(player) < 1));
		boolean opponentDead = (!opponentAtGoal && (state.getFoodUnits(opponent) < 1));
		
		// if both players are at goal, state is terminal
		if (playerAtGoal && opponentAtGoal){
			return true;
		}
		// if one of the players is dead, state is terminal
		if (playerDead || opponentDead){
			return true;
		}
		return false;
	}
	
	private int utility(Graph world, State state){
		
		int player = state.getPlayer();
		int opponent = (player+1)%2;
		boolean playerAtGoal = (state.getLocation(player) == world.getAgents().get(player).getGoal());
		boolean opponentAtGoal = (state.getLocation(opponent) == world.getAgents().get(opponent).getGoal());
		
		if (playerAtGoal && opponentAtGoal){
			int sum = state.getScore(player) + state.getScore(opponent);
			return 0 - sum;
		}
		return Integer.MIN_VALUE;
	}


	protected int evaluateValue(Graph world, State state) {
		
		SquareEdgesHeuristic heuristic = new SquareEdgesHeuristic();
		
		int player = state.getPlayer();
		int opponent = (player+1)%2;
		int playerGoal = world.getAgents().get(player).getGoal();
		int opponentGoal = world.getAgents().get(opponent).getGoal();
		int playerScore, opponentScore, sum;
		boolean playerAtGoal = (state.getLocation(player) == playerGoal);
		boolean opponentAtGoal = (state.getLocation(opponent) == opponentGoal);
		
		if (playerAtGoal && opponentAtGoal){
			playerScore = heuristic.run(world, state, player, playerGoal) + state.getScore(player);
			opponentScore = heuristic.run(world, state, opponent, opponentGoal) + state.getScore(opponent);
			sum = playerScore + opponentScore;
			return 0 - sum;
		}
		return Integer.MIN_VALUE;
	}

	@Override
	public void print() {
		System.out.println("<Agent ID: " + iID + ", Agent Type: AlphaBetaMaximax, Score: " + iScore 
				+ ", Current Location: " + iCurrLocation + ", Goal Location: " 
				+ iGoal + ", Food Units: " + iFoodUnits);
	}
}
