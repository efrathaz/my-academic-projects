package graph;
import java.util.LinkedList;

import agents.Agent;
import agents.ISISAgent;
import agents.YazidiAgent;

public class Node implements Comparable<Node>{

	private int iID; // ID is a number between 1 to n
	private int iFoodUnits;
	private LinkedList<Agent> lNodeAgents; 
	private LinkedList<Node> lNeighbours;
	private int iDist;
//	private int iDistance;
	private Node nodeParent;
	private boolean bVisited;
	
	public Node(int id){
		iID = id;
		iFoodUnits = 0;
		lNodeAgents = new LinkedList<Agent>();
		lNeighbours = new LinkedList<Node>();
		iDist = 0;
//		iDistance = 0;
		nodeParent = null;
		bVisited = false;
	}
	
	// getters and setters
	
	public int getID(){
		return iID;
	}
	
	public int getFoodUnits(){
		return iFoodUnits;
	}
	
	public void setFoodUnits(int units){
		iFoodUnits = units;
	}
	
	public LinkedList<Node> getNeighbours(){
		return lNeighbours;
	}
	
	public void setNeighbours(LinkedList<Node> neighbours){
		lNeighbours = neighbours;
	}
	
	public int getDist() {
		return iDist;
	}

	public void setDist(int dist) {
		this.iDist = dist;
	}

	public Node getNodeParent() {
		return nodeParent;
	}

	public void setNodeParent(Node nodeParent) {
		this.nodeParent = nodeParent;
	}

	public boolean isVisited() {
		return bVisited;
	}

	public void setVisited(boolean visited) {
		this.bVisited = visited;
	}
	
	// methods
	
	public void addAgent(Agent agent){
		lNodeAgents.add(agent);
	}
	
	public void removeAgent(Agent agent){
		lNodeAgents.remove(agent);
	}
	
	public void killYazidis(){
		for (int i = 0 ; i < lNodeAgents.size() ; i++){
			if (lNodeAgents.get(i) instanceof YazidiAgent){
				lNodeAgents.get(i).kill();
				lNodeAgents.remove(lNodeAgents.get(i));
			}
		}
	}
	
	//TODO: check function
	public boolean hasYazidis(){
		for (int i = 0 ; i < lNodeAgents.size() ; i++){
			if (lNodeAgents.get(i) instanceof YazidiAgent){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasISIS(){
		for (int i = 0 ; i < lNodeAgents.size() ; i++){
			if (lNodeAgents.get(i) instanceof ISISAgent){
				return true;
			}
		}
		return false;
	}
	
	public void bomb(){
		iFoodUnits = 0;
		for (int i = 0 ; i < lNodeAgents.size() ; i++){
			lNodeAgents.get(i).kill();
		}
		lNodeAgents.clear();
	}
	
	public void print(){
		System.out.println("[Node ID: " + iID + ", Number of food units: " + iFoodUnits);
		if (lNodeAgents.size() > 0){
			System.out.println(" Agents:");
			for (int i = 0 ; i < lNodeAgents.size() ; i++){
				System.out.print("\t");
				lNodeAgents.get(i).print();
			}
			System.out.println("]");
		}
		else{
			System.out.println(" No agents]");
		}
	}

	@Override
	public int compareTo(Node other) {
		return Integer.compare(iDist, other.getDist());
	}
	
}
