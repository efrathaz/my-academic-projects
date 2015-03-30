package graph;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;


public class Graph {
	
	public static Scanner scanner;

	private int iN; // number of nodes
	private int iStart;
	private int iGoal;
	private ArrayList<Node> aNodes;
	private ArrayList<Edge> aEdges;
	
	public Graph(){
		iN = 0;
		iStart = 0;
		iGoal = 0;
		aNodes = new ArrayList<Node>();
		aEdges = new ArrayList<Edge>();
		scanner = new Scanner(System.in);
	}

	// getters and setters
	
	public int getN(){
		return iN;
	}
	
	public int getStart(){
		return iStart;
	}
	
	public void setStart(int s){
		iStart = s;
	}
	
	public int getGoal(){
		return iGoal;
	}
	
	public void setGoal(int t){
		iGoal = t;
	}
	
	public ArrayList<Node> getNodes(){
		return aNodes;
	}
	
	
	// methods
	
	public LinkedList<Node> getNeighbours(int v){
		return getNodeByID(v).getNeighbours();
	}
	
	public Node getNodeByID(int id){
		for (int i = 0 ; i < aNodes.size() ; i++){
			if (aNodes.get(i).getID() == id){
				return aNodes.get(i);
			}
		}
		return null;
	}
	
	public double getISISprobByID(int id){
		Node node = getNodeByID(id);
		return node.getISISprob();
	}
	
	public void createNodes(int n){
		iN = n;
		// create N nodes
		for (int i = 1 ; i < n+1 ; i++){
			aNodes.add(new Node(i));
		}
	}
	
	public int getEdgeWeight(int u, int v){
		Edge edge = null;
		for (int i = 0 ; i < aEdges.size() ; i++){
			edge = aEdges.get(i);
			if ((edge.getU() == u && edge.getV() == v) || (edge.getU() == v && edge.getV() == u)){
				return edge.getWeight();
			}
		}
		return -1;
	}
	
	public void addEdge(int u, int v, int w){
		Edge e = new Edge(u, v, w);
		aEdges.add(e);
	}
	
	public void calcNeighbours(){
		for (int i = 0 ; i < aNodes.size() ; i++){
			int nodeID = aNodes.get(i).getID();
			LinkedList<Node> neighbours = new LinkedList<Node>();
			for (int j = 0 ; j < aEdges.size() ; j++){
				Edge e = aEdges.get(j);
				if (e.getU() == nodeID){
					neighbours.add(getNodeByID(e.getV()));
				}
				else if(e.getV() == nodeID){
					neighbours.add(getNodeByID(e.getU()));
				}
			}
			aNodes.get(i).setNeighbours(neighbours);
		}
	}
	
	public void dijkstra(int source, Func func){
		resetVerticesDistancesAndParents();
		Node src = getNodeByID(source);
		src.setDist(0);
		PriorityQueue<Node> vertexQueue = new PriorityQueue<Node>();
		vertexQueue.add(src);
		while (!vertexQueue.isEmpty()) {
			Node u = vertexQueue.poll();
			LinkedList<Node> neighbours = u.getNeighbours();
			for (int i = 0; i < neighbours.size(); i++){
				Node v = u.getNeighbours().get(i); 
				int weight = getEdgeWeight(u.getID(), v.getID());
				int costThroughU = u.getDist() + func.calcWeight(weight);
				if (costThroughU < v.getDist()) {
					vertexQueue.remove(v);
					v.setDist(costThroughU);
					v.setNodeParent(u);
					vertexQueue.add(v);
				}
			}
		}
	}
	
	public LinkedList<Integer> findPath(int src, int trg, Func func){
		LinkedList<Integer> path = new LinkedList<Integer>();
		dijkstra(src, func);
		
		// build path
		Node goal = getNodeByID(trg);
		if (goal.getDist() != Integer.MAX_VALUE){
			Node temp = goal;
			while(temp != null){
				path.addFirst(temp.getID());
				temp = temp.getNodeParent();
			}
			path.removeFirst();
		}
		return path;
	}
	
	public void resetVerticesDistancesAndParents(){
		for(Node node : aNodes){
			node.setDist(Integer.MAX_VALUE);
			node.setNodeParent(null);
			node.setVisited(false);
		}
	}
	
	public void printGraph(){
		System.out.println("Number of vertices: " + iN);
		for (int i = 0 ; i < aEdges.size() ; i++){
			System.out.println("[" + i + "] edge: (" + aEdges.get(i).getU() + "," + aEdges.get(i).getV() + ") weight: " + aEdges.get(i).getWeight());
		}
	}

	
	public LinkedList<LinkedList<Integer>> findAllPaths(int src, int goal){
		
		LinkedList<LinkedList<Integer>> allPaths = new LinkedList<LinkedList<Integer>>();
		LinkedList<Integer> visited = new LinkedList<Integer>();
        
		visited.add(src);
		breadthFirst(allPaths, visited, goal);
        
        return allPaths;
	}
	
	private void breadthFirst(LinkedList<LinkedList<Integer>> allPaths, LinkedList<Integer> visited, int goal) {
        LinkedList<Node> nodes = getNeighbours(visited.getLast());
        // examine adjacent nodes
        for (Node node : nodes) {
            if (visited.contains(node.getID())) {
                continue;
            }
            if (node.getID() == goal) {
                visited.add(node.getID());
                allPaths.add(makePath(visited));
                visited.removeLast();
                break;
            }
        }
        // in breadth-first, recursion needs to come after visiting adjacent nodes
        for (Node node : nodes) {
            if (visited.contains(node.getID()) || (node.getID() == goal)) {
                continue;
            }
            visited.addLast(node.getID());
            breadthFirst(allPaths, visited, goal);
            visited.removeLast();
        }
    }
	
	private LinkedList<Integer> makePath(LinkedList<Integer> visited){
		
		LinkedList<Integer> path = new LinkedList<Integer>();
			
		for (int i = 0 ; i < visited.size() ; i++){
			path.add(visited.get(i));
		}
		
		return path;
	}
	
	public boolean isPath(LinkedList<Integer> path){
		
		for (int i = 0 ; i < path.size()-1 ; i++){
			if (getEdgeWeight(path.get(i), path.get(i+1)) == -1){
				return false;
			}
		}
		
		return true;
	}
}
