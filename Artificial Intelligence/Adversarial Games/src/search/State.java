package search;

import java.util.ArrayList;

import actions.Action;

public class State {
	
	private int iDepth;
	private int iPlayer;
	private int[] aLocations;
	private int[] aFoodUnits;
	private int[] aScores;
	private ArrayList<Integer> aFoodInWorld;
	private Action prevAction;
	
	public State(int depth, int player, ArrayList<Integer> food, Action prev){
		iDepth = depth;
		iPlayer = player;
		aLocations = new int[2];
		aFoodUnits = new int[2];
		aScores = new int[2];
		prevAction = prev;
		aFoodInWorld = new ArrayList<Integer>();
		for (int i = 0 ; i < food.size() ; i++){
			aFoodInWorld.add(food.get(i));
		}
	}
	
	// getters and setters
	
	public int getDepth() {
		return iDepth;
	}
	
	public void setDepth(int iDepth) {
		this.iDepth = iDepth;
	}

	public int getPlayer() {
		return iPlayer;
	}

	public void setPlayer(int iPlayer) {
		this.iPlayer = iPlayer;
	}

	public int getLocation(int player) {
		return aLocations[player];
	}

	public void setLocation(int player, int newLoc) {
		aLocations[player] = newLoc;
	}

	public int getFoodUnits(int player) {
		return aFoodUnits[player];
	}

	public void setFoodUnits(int player, int newFoodUnits) {
		aFoodUnits[player] = newFoodUnits;
	}

	public int getScore(int player) {
		return aScores[player];
	}

	public void setScore(int player, int newScore) {
		aScores[player] = newScore;
	}

	public Action getPrevAction() {
		return prevAction;
	}

	public void setPrevAction(Action prevAction) {
		this.prevAction = prevAction;
	}

	public ArrayList<Integer> getFoodInWorld() {
		return aFoodInWorld;
	}
	
	public int getFoodInNode(int i) {
		return aFoodInWorld.get((i-1));
	}

	public void setFoodInNode(int i, int n) {
		aFoodInWorld.set((i-1), n);
	}
	
	public void addFoodInWorld(int n) {
		aFoodInWorld.add(n);
	}
	
	// methods
	
	public void print(){
		System.out.println("Depth = " + iDepth + ", loc = (" + aLocations[0] +  ", " + aLocations[1] + "), " +
							"score = (" + aScores[0] + ", " + aScores[1] + "), "+
							"food = (" + aFoodUnits[0] + ", " + aFoodUnits[1] + ")");
	}

}
