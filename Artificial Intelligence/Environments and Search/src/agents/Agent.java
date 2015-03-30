package agents;

import graph.Graph;
import graph.Node;

import java.util.LinkedList;

import actions.Action;

public abstract class Agent {
	
	public static final String ACTION_NOOP = "No op";
	public static final String ACTION_TRAVERSE = "Traverse";
	
	protected int iCurrLocation;
	protected int iScore;
	protected int iID;
	protected LinkedList<Action> lActions;
	protected boolean bWin;
	
	
	public Agent(int id, int start){
		iCurrLocation = start;
		iScore = 0;
		iID = id;
		lActions = new LinkedList<Action>();
		bWin = false;
	}
	
	// getters and setters
	
	public int getCurrLocation(){
		return iCurrLocation;
	}
	
	public void setCurrLocation(int location){
		iCurrLocation = location;
	}
	
	public int getScore(){
		return iScore;
	}
	
	public String getActions(){
		String actions = "";
		int i = 0;
		for (i = 0 ; i < lActions.size()-1 ; i++){
			actions = actions + lActions.get(i).getName() + ", ";
		}
		actions = actions + lActions.get(i).getName();
		return actions;
	}
	
	// methods	
	
	public void addScore(int score){
		iScore += score;
	}
	
	public int numOfActions(){
		return lActions.size();
	}
		
	public boolean isAlive(){
		if (iScore < 0){
			return false;
		}
		return true;
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
		iScore = -1;
	}
	
	public abstract String type();
	
	public abstract void takeFood(Graph world, Node node);
	
	public abstract void print();
	
	public abstract Action calcAction(Graph world);	
}
