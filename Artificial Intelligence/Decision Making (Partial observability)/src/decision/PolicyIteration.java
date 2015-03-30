package decision;

import graph.Graph;

import java.util.HashMap;
import java.util.LinkedList;

public class PolicyIteration {
	
	private Policy pi;
	private HashMap<State, Double> tempUtilities;
	
	public PolicyIteration(Graph graph, LinkedList<Integer> nodesWithProb){
		pi = new Policy(graph, nodesWithProb);
		tempUtilities = new HashMap<State, Double>();
		
	}
	
	public Policy findMaxPolicy(){
		
		double maxU, tempU;
		boolean hasConverged = false;
		State currentState;
		Action currentAction, bestAction;
		
		// set arbitrary actions and utility values in pi
		setArbitraryValues();
		
		// while the values in pi are still changing
		while(!hasConverged){
			
			// save the current utility values of the states in tempUtilities (for convergence-check later on)
			updateTempUtilities(); 
			
			// for each state
			for (int i = 0 ; i < pi.getStates().size() ; i++){
				
				currentState = pi.getStates().get(i);
				
				// if not dead state
				if (isDeadState(currentState) == 0){
				
					maxU = currentState.getUtility();
					bestAction = currentState.getBestAction();
					
					// find maximal utility
					for(int j = 0 ; j < pi.getActions().size() ; j++){
						
						currentAction = pi.getActions().get(j);
						tempU = calcUtility(currentState, currentAction);  // R(s,a) + sum of all possible successor states s': P(s'|s,a)*U(s')
						
						if (tempU > maxU){
							maxU = tempU;
							bestAction = currentAction;
							
							LinkedList<State> newBestStates = new LinkedList<State>();
							currentState.bestStates = newBestStates;
							
							currentState.print();
							currentAction.print();
							for (int k = 0; k < currentState.getOptionalStates().size() ; k++){
								State tmpS = currentState.getOptionalStates().get(k);
								
								if (currentAction.getSrc() != currentState.getLocation() || currentAction.getDest() != tmpS.getLocation()){
									currentState.getOptionalStates().get(k).deleteOptionalStates(tmpS);
								}
								else if(!contains(currentState.bestStates, tmpS)){
									tmpS.print();
									currentState.addBestStates(tmpS);
								}
							}
						}
					}
					
					// update best action and utility value
					currentState.setBestAction(bestAction);
					currentState.setUtility(maxU);	
				}
			}
			
			// check if there was a change in the utility values between this iteration and the last iteration
			hasConverged = convergenceCheck();
		}
		
		return pi;
	}
	
	/*
	 * set arbitrary best action and utility for each state
	 * for goal states: best action = null, utility = 0
	 * for other states: best action = first possible action, utility = already set to -100
	 */
	private void setArbitraryValues(){
		State s;
		Action a;
		
		for(int i = 0 ; i < pi.getStates().size() ; i++){
			s = pi.getStates().get(i);
			if(s.getLocation() == pi.getGoal()){
				s.setBestAction(null);
				s.setUtility(0.0);
			}
			else{
				for (int j = 0 ; j < pi.getActions().size() ; j++){
					a = pi.getActions().get(j);
					// if "a" is a possible action in state "s", set it as best action and break
					if(a.getSrc() == s.getLocation()){
						s.setBestAction(a);
						break;
					}
				}
			}
		}
	}
	
	/*
	 * update tempUtilities with the current utility values of the states in the policy
	 */
	private void updateTempUtilities(){
		State s;
		double u;
		
		for(int i = 0 ; i < pi.getStates().size() ; i++){
			s = pi.getStates().get(i);
			u = s.getUtility();
			tempUtilities.put(s, u);
		}
	}
	
	/* 
	 * for state s and action a, calculate R(s,a) + sum of s': P(s'|s,a)*U(s')
	 */
	private double calcUtility(State s, Action a){
		double sum = 0;
		boolean isSumRelevant = false;
		
		
		if(s.getLocation() == pi.getGoal()){
			return 0;
		}
		
		
		
		for (int i = 0 ; i < pi.getStates().size() ; i++){
			State newS = pi.getStates().get(i);
			if (!s.equals(newS)){
				if (newS.getLocation() == pi.getGoal()){
					updateTreminalUtility(s,a,newS);
				}			
				double prob = prob(s, a, newS);
				double utility = newS.getUtility(); 
				
				
				if (prob > 0){
					isSumRelevant = true;
					for (int j = 0 ; j < pi.getStates().size() ; j++){
						State tmpS = pi.getStates().get(j);
						if (s.equals(tmpS)){
							pi.getStates().get(j).addOptionalStates(newS);
						}
					}
				}
				sum += prob*utility;
				//System.out.println(sum);
			}
		}
		if (!isSumRelevant){
//			System.out.println("ans: 0");
			return -1000;
		}
		double ansPerAction = reward(a)+sum;
//		System.out.println("*reward(a)" + reward(a) + ", sum: " + sum + ",ans: "+ ansPerAction);
		return ansPerAction;
	}
	
	/*
	 * P(newS|oldS, a)
	 */
	private double prob(State oldS, Action a, State newS){
		
		// no chance of "a" resulting in state "newS"
		if (a.getSrc() != oldS.getLocation() || a.getDest() != newS.getLocation()){
			return 0;
		}
		
		// check if newS is a death state
		if (isDeadState(newS) == 1){
			return 0;
		}
		
		LinkedList<ISISAtNode> oldISISinfo = oldS.getISISinfo();
		LinkedList<ISISAtNode> newISISinfo = newS.getISISinfo();

		double ISISprob = 1; // return 1 when legal and no changes in ISISinfo
		int currOldNodeVal;
		int currNewNodeVal;
		
		// check all changes ISISinfo
		for (int i=0; i<oldISISinfo.size(); i++){
			currOldNodeVal = oldISISinfo.get(i).getValue();
			currNewNodeVal = newISISinfo.get(i).getValue();
			
			if (currOldNodeVal != currNewNodeVal){
				// no chance of information regression from T\F to T\F\U
				if (currOldNodeVal != 3){
					return 0;
				}
				// remember new information in ISISprob
				int currNewNodeId = newISISinfo.get(i).getNodeID();
				if (currNewNodeVal == 1){
					// T ISIS in node
					ISISprob *= pi.getISISprob(currNewNodeId); 
				}
				else {
					// F ISIS is not in node
					ISISprob *= (1-pi.getISISprob(currNewNodeId)); 
				} 
			}
		}
		return ISISprob;
	}
	
	/*
	 * R(s, a)
	 */
	private double reward(Action a){
		return 0 - pi.getActionCost(a);
	}
	
	/*
	 * Check if agent is killed by ISIS in state s
	 * 1- killed
	 * 0- not killed
	 */
	private int isDeadState(State s){
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
	
	private void updateTreminalUtility(State oldS,Action a,State newS){
		// oldS and a lead to newS
		if (a.getSrc() == oldS.getLocation() && a.getDest() == newS.getLocation()){
			newS.setUtility(0.0);
			newS.setBestAction(null);
		}
	}
	
	/*
	 * compare the utility values from this iteration and the last iteration
	 * if the values almost didn't change, than the policy has converged
	 */
	private boolean convergenceCheck(){
		for (int i = 0 ; i < pi.getStates().size() ; i++){
			State s = pi.getStates().get(i);
			double newU = s.getUtility();
			double oldU = tempUtilities.get(s);
			double absDiff = Math.abs(newU-oldU);
			if (absDiff > 0.1){
				return false;
			}
		}
		return true;
	}
	
	private boolean contains(LinkedList<State> states, State s){
		for (int i = 0 ; i < states.size() ; i++){
			if(states.get(i).equals(s)){
				return true;
			}
		}
		return false;
	}
}
