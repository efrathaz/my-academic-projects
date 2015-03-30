package agents;

import graph.Graph;

import java.util.LinkedList;

import actions.Action;
import actions.NoopAction;

import search.State;


public class ISISMinimax extends AlphaBetaMinimax {

	public ISISMinimax(int id, int start) {
		super(id, start, 0, 0);
	}
	
	@Override
	public Action calcAction(Graph world) {
		
		// search for Yazidi
		Agent yazidi = world.getAgents().get((iID+1)%2);
		iGoal = yazidi.getCurrLocation();
		if(yazidi.getScore() == Integer.MAX_VALUE){
			iScore = Integer.MIN_VALUE;
		}
		else {
			iScore = 0 - yazidi.getScore();
		}
		
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
			
			v = v > min ? v : min;
			
			if (v >= b){
				return v;
			}

			a = a > v ? a : v;
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
		
		int ISIS = iID;
		int yazidi = (iID+1)%2;
		boolean yazidiAtGoal = (state.getLocation(yazidi) == world.getAgents().get(yazidi).getGoal());
		
		// if Yazidi is at goal, state is terminal
		if (yazidiAtGoal || (state.getFoodUnits(yazidi) < 1) || state.getLocation(ISIS) == state.getLocation(yazidi)){
			return true;
		}
		return false;
	}
	
	@Override
	public void print() {
		System.out.println("<Agent ID: " + iID + ", Agent Type: ISISMinimax, Score: " + iScore 
				+ ", Current Location: " + iCurrLocation);
	}
}
