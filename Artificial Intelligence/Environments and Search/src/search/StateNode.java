package search;

import actions.Action;

public class StateNode {

	private int iLocation;
	private int iFoodUnits;
	private int iG; // g(n) = the score. path cost from initial state to this state
	private int iH; // h(n) = heuristic for this state
	private int iF; // f(n) = g(n) + h(n)
	private Action prevAction;
	private StateNode parentState;
	
	public StateNode(){
		iLocation = 0;
		iFoodUnits = 0;
		iG = Integer.MAX_VALUE;
		iH = 0;
		iF = 0;
		prevAction = null;
		parentState = null;
	}
	
	public StateNode(int location, int score, int food, Action action, StateNode parent){
		iLocation = location;
		iFoodUnits = food;
		iG = score;
		iH = 0;
		iF = 0;
		prevAction = action;
		parentState = parent;
	}
	
	public int getLocation() {
		return iLocation;
	}

	public void setLocation(int iLocation) {
		this.iLocation = iLocation;
	}

	public int getFoodUnits() {
		return iFoodUnits;
	}

	public void setFoodUnits(int iFoodUnits) {
		this.iFoodUnits = iFoodUnits;
	}

	public int getG() {
		return iG;
	}

	public void setG(int g) {
		this.iG = g;
	}

	public int getH() {
		return iH;
	}

	public void setH(int h) {
		this.iH = h;
	}
	public int getF() {
		return iF;
	}

	public void setF(int f) {
		this.iF = f;
	}
	
	public Action getPrevAction() {
		return prevAction;
	}

	public void setPrevAction(Action prevAction) {
		this.prevAction = prevAction;
	}

	public StateNode getParentState() {
		return parentState;
	}

	public void setParentState(StateNode parentState) {
		this.parentState = parentState;
	}
	
	public void print(){
		System.out.println("Location: " + iLocation + ", Food: " + iFoodUnits + ", G: " + iG + ", H: " + iH + ", F: " + iF);
	}
}
