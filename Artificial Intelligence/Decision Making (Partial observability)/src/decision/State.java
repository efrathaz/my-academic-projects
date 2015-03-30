package decision;

import java.util.LinkedList;

public class State{
	
	private int currLocation;
	private LinkedList<ISISAtNode> ISISinfo;
	private double utility;
	private Action bestAction;
	private LinkedList<State> optionalStates;
	public LinkedList<State> bestStates;
	
	public State(int location, LinkedList<ISISAtNode> ISIS){
		ISISinfo = ISIS;
		currLocation = location;
		utility = -100;
		bestAction = null;
		optionalStates = new LinkedList<State>();
		bestStates = new LinkedList<State>();
	}
	
	// getters and setters
	
	public int getLocation() {
		return currLocation;
	}
	
	public double getUtility() {
		return utility;
	}
	
	public void setUtility(double utility) {
		this.utility = utility;
	}
	
	public Action getBestAction() {
		return bestAction;
	}
	
	public void setBestAction(Action bestAction) {
		this.bestAction = bestAction;
	}
	
	public LinkedList<ISISAtNode> getISISinfo() {
		return ISISinfo;
	}

	public void setISISinfo(int node, int value) {
		for (int i = 0 ; i < ISISinfo.size() ; i++){
			if (ISISinfo.get(i).getNodeID() == node){
				ISISinfo.get(i).setValue(value);
				break;
			}
		}
	}
	
	
	public LinkedList<State> getOptionalStates() {
		return optionalStates;
	}

	public void addOptionalStates(State s) {
		optionalStates.add(s);
	}
	
	public LinkedList<State> getBestStates() {
		return bestStates;
	}

	public void addBestStates(State s) {
		bestStates.add(s);
	}
	
	public void setBestStates(LinkedList<State> bestList) {
		bestStates = bestList;
	}
	
	public State clearBestStates() {
		State s = null;
		for (int k=0; k < bestStates.size(); k++){
			s = bestStates.removeFirst();
		}
		return s;
	}
	
	public void deleteOptionalStates(State s) {
		for (int k=0; k < optionalStates.size(); k++){
			State tmpS = optionalStates.get(k);
			if (s.equals(tmpS)){
				optionalStates.remove(tmpS);
			}
		
		}
	}
	
	public void print(){
		
		String s = "[V" + currLocation;
		
		for (int i = 0 ; i < ISISinfo.size() ; i++){
			switch(ISISinfo.get(i).getValue()){
				case 1:
					s = s + ", T" + ISISinfo.get(i).getNodeID() + "=T";
					break;
				case 2:
					s = s + ", T" + ISISinfo.get(i).getNodeID() + "=F";
					break;
				case 3:
					s = s + ", T" + ISISinfo.get(i).getNodeID() + "=U";
			}
		}
		s = s + "]";
		System.out.println(s);
	}

	
	public boolean equals(State other) {
		
		if (currLocation != other.getLocation()){
			return false;
		}
		
		LinkedList<ISISAtNode> otherISISinfo = other.getISISinfo();
		
		for(int i = 0 ; i < ISISinfo.size() ; i++){
			
			ISISAtNode a = ISISinfo.get(i);
			ISISAtNode b = otherISISinfo.get(i);
			
			if (a.getValue() != b.getValue()){
				return false;
			}
		}
		
		return true;
	}
}
