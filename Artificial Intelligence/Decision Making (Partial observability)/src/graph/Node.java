package graph;
import java.util.LinkedList;

public class Node implements Comparable<Node>{

	private int iID; // ID is a number between 1 to n
	private LinkedList<Node> lNeighbours;
	private int iDist;
	private Node nodeParent;
	private boolean bVisited;
	private double ISISprob;
	private boolean worldISIS;
	
	public Node(int id){
		iID = id;
		lNeighbours = new LinkedList<Node>();
		iDist = 0;
		nodeParent = null;
		bVisited = false;
		ISISprob = 0;
		worldISIS = false;
	}
	
	// getters and setters
	
	public int getID(){
		return iID;
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
	
	public boolean isWorldISIS() {
		return worldISIS;
	}

	public void setWorldISIS(boolean status) {
		this.worldISIS = status;
	}

	public double getISISprob() {
		return ISISprob;
	}

	public void setISISprob(double ISISprob) {
		this.ISISprob = ISISprob;
	}
	
	// methods
	
	@Override
	public int compareTo(Node other) {
		return Integer.compare(iDist, other.getDist());
	}

	
}
