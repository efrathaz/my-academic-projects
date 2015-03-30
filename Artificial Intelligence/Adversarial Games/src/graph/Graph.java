package graph;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

import agents.Agent;
import agents.AlphaBetaMaximax;
import agents.AlphaBetaMinimax;
import agents.HumanISISAgent;
import agents.HumanYazidiAgent;
import agents.ISISMinimax;
import agents.SemiCooperative;


public class Graph {
	
	public static Scanner scanner;

	private int iN; // number of nodes
	private int iMaxIterNum;
	private int iCutoff;
	private ArrayList<Node> aNodes;
	private ArrayList<Edge> aEdges;
	private LinkedList<Agent> lAgents;
	
	public Graph(int t, int cutoff){
		iN = 0;
		iMaxIterNum = t;
		iCutoff = cutoff;
		aNodes = new ArrayList<Node>();
		aEdges = new ArrayList<Edge>();
		lAgents = new LinkedList<Agent>();
		scanner = new Scanner(System.in);
	}

	// getters and setters
	
	public int getN(){
		return iN;
	}
	
	public int getMaxIterNum(){
		return iMaxIterNum;
	}
	
	public int getCutoff(){
		return iCutoff;
	}
	
	public ArrayList<Node> getNodes(){
		return aNodes;
	}
	
	public LinkedList<Agent> getAgents(){
		return lAgents;
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
	
	public void incIterNum(){
		iMaxIterNum++;
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
	
	public LinkedList<Agent> addAgentsForPart1(){
		
		int type, id = 0;
		int goal = 0, start = 0;
		int food = 0;
		
		while (id < 2){
			System.out.println("\nSelect from menu:");
			System.out.println("      1 = add Game Tree Search Yazidi Agent");
			System.out.println("      2 = add Human Yazidi Agent");
			System.out.println("      3 = add Game Tree Search ISIS Agent");
			System.out.println("      4 = add Human ISIS Agent");
			System.out.print("> ");
			type = scanner.nextInt();
			
			if(type < 1 || type > 4){
				System.out.println(type + ": no such option.");
				id--; // don't count this round - no agent was created
				continue;
			}
			
			if(type < 5){
				System.out.print("Select starting position (from 1 to " + iN + "): ");
				start = scanner.nextInt();
				if (start < 1 || start > iN){
					System.out.println("no such position.");
					id--; // don't count this round - no agent was created
					continue;
				}
			}
			if(type < 3){
				System.out.print("Select goal position (from 1 to " + iN + "): ");
				goal = scanner.nextInt();
				if (goal < 1 || goal > iN){
					System.out.println("no such position.");
					id--; // don't count this round - no agent was created
					continue;
				}
			}
			food = getNodeByID(start).getFoodUnits();
			getNodeByID(start).setFoodUnits(0);
			Agent a = null;
			switch (type){
				case 1:
					a = new AlphaBetaMinimax(id, start, goal, food);
					break;
				case 2:
					a = new HumanYazidiAgent(id, start, goal, food, scanner);
					break;
				case 3:
					a = new ISISMinimax(id, start);
					break;
				default:
					a = new HumanISISAgent(id, start, scanner);
					break;
			}
			lAgents.add(a);
			getNodeByID(start).addAgent(a);
			id++;
		}
		return lAgents;
	}
	

	public LinkedList<Agent> addAgentsForPart2(){
		
		int id = 0;
		int goal = 0, start = 0;
		int food = 0;
		
		while (id < 2){
			System.out.println("For agent " + id + ":");
			System.out.print("Select starting position (from 1 to " + iN + "): ");
			start = scanner.nextInt();
			if (start < 1 || start > iN){
				System.out.println("no such position.");
				id--; // don't count this round - no agent was created
				continue;
			}
			System.out.print("Select goal position (from 1 to " + iN + "): ");
			goal = scanner.nextInt();
			if (goal < 1 || goal > iN){
				System.out.println("no such position.");
				id--; // don't count this round - no agent was created
				continue;
			}
			food = getNodeByID(start).getFoodUnits();
			getNodeByID(start).setFoodUnits(0);
			Agent a = new SemiCooperative(id, start, goal, food);
			lAgents.add(a);
			getNodeByID(start).addAgent(a);
			id++;
		}
		return lAgents;
	}
	
	public LinkedList<Agent> addAgentsForPart3(){
		
		int id = 0;
		int goal = 0, start = 0;
		int food = 0;
		
		while (id < 2){
			System.out.println("For agent " + id + ":");
			System.out.print("Select starting position (from 1 to " + iN + "): ");
			start = scanner.nextInt();
			if (start < 1 || start > iN){
				System.out.println("no such position.");
				id--; // don't count this round - no agent was created
				continue;
			}
			System.out.print("Select goal position (from 1 to " + iN + "): ");
			goal = scanner.nextInt();
			if (goal < 1 || goal > iN){
				System.out.println("no such position.");
				id--; // don't count this round - no agent was created
				continue;
			}
			food = getNodeByID(start).getFoodUnits();
			getNodeByID(start).setFoodUnits(0);
			Agent a = new AlphaBetaMaximax(id, start, goal, food);
			lAgents.add(a);
			getNodeByID(start).addAgent(a);
			id++;
		}
		return lAgents;
	}
	
	public void printGraph(){
		System.out.println("Number of vertices: " + iN);
		for (int i = 0 ; i < aEdges.size() ; i++){
			System.out.println("[" + i + "] edge: (" + aEdges.get(i).getU() + "," + aEdges.get(i).getV() + ") weight: " + aEdges.get(i).getWeight());
		}
	}
	
	public void printState(){
		System.out.println("-------State-------");
		System.out.println("Nodes:");
		for (int i = 0 ; i < aNodes.size() ; i++){
			aNodes.get(i).print();
		}
		System.out.println("-------------------");
	}
}
