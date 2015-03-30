package agents;

import java.util.LinkedList;

import search.State;
import graph.Graph;
import heuristic.SquareEdgesHeuristic;
import actions.Action;
import actions.NoopAction;

public class SemiCooperative extends Agent {

	public SemiCooperative(int id, int start, int goal, int food) {
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
		int[] max = {0, 0};
		
		LinkedList<State> successors = createSuccessors(world, currState);
		
		// find the successor state with the maximal MinValue
		for (int i = 0 ; i < successors.size() ; i++){
			
			max = maxValue(world, successors.get(i), a, b);
			
			if (a < max[iID]){
				a = max[iID];
				chosenAction = successors.get(i).getPrevAction();
			}
		}
		return chosenAction;
	}
	
	private int[] maxValue(Graph world, State state, int a, int b){
		
		int i;
		int player = state.getPlayer();
		int opponent = (player+1)%2;
		int[] v = {Integer.MIN_VALUE, Integer.MIN_VALUE};
		int[] max = {0, 0};
		
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
			
			if (v[player] < max[player]){
				v = max;
			}
			
			// if equal, check what is best for the other player
			if (v[player] == max[player]){
				if (v[opponent] < max[opponent]){
					v = max;
				}
			}
			
			if (v[player] >= b){
				return v;
			}

			if (v[player] > a){
				a = v[player];
			}
		}
		return v;
	}
	
	private boolean terminalTest(Graph world, State state){

		boolean atGoal = (state.getLocation(iID) == iGoal);
		
		// if player is at goal, state is terminal
		if (atGoal || (state.getFoodUnits(iID) < 1)){
			return true;
		}
		
		return false;
	}

	private int[] utility(Graph world, State state){
		int[] v = {0, 0};
		int player = iID;
		int opponent = (player+1)%2;
		boolean playerAtGoal = (state.getLocation(player) == iGoal);
		
		if (playerAtGoal){
			v[player] = (0 - state.getScore(player));
		}
		else{
			v[player] = Integer.MIN_VALUE;
		}
		if (state.getScore(opponent) == Integer.MAX_VALUE){
			v[opponent] = Integer.MIN_VALUE;
		}
		else{
			v[opponent] = (0 - state.getScore(opponent));
		}
		return v;
	}
	
	
	private int[] evaluateValue(Graph world, State state){
		
		SquareEdgesHeuristic heuristic = new SquareEdgesHeuristic();
		int[] v = {0, 0};
		int player = iID;
		int opponent = (player+1)%2;
		int opponentGoal = world.getAgents().get(opponent).getGoal();
		int playerScore, opponentScore;
		
		if (state.getLocation(player) == iGoal){
			playerScore = (heuristic.run(world, state, player, iGoal) + state.getScore(player));
			v[player] = (0 - playerScore);
		}
		else{
			v[player] = Integer.MIN_VALUE;
		}
		if (state.getScore(opponent) != Integer.MAX_VALUE){
			opponentScore = (heuristic.run(world, state, opponent, opponentGoal) + state.getScore(opponent));
			v[opponent] = (0 - opponentScore);
		}
		else{
			v[opponent] = Integer.MIN_VALUE;
		}
		return v;
	}

	@Override
	public void print() {
		System.out.println("<Agent ID: " + iID + ", Agent Type: SemiCooperative, Score: " + iScore 
				+ ", Current Location: " + iCurrLocation + ", Goal Location: " 
				+ iGoal + ", Food Units: " + iFoodUnits);
	}
}
