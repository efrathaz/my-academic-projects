package graph;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

import agents.Agent;
import agents.HumanISISAgent;
import agents.HumanYazidiAgent;
import agents.ISISAgent;
import agents.SmartAgent;
import agents.USAAgent;
import agents.YazidiAgent;


public class Graph {
	
	public static Scanner scanner;

	private int iN; // number of nodes
	private int iIterNum; 
	private ArrayList<Node> aNodes;
	private ArrayList<Edge> aEdges;
	private LinkedList<Agent> lAgents;
	
	public Graph(){
		iN = 0;
		iIterNum = 0;
		aNodes = new ArrayList<Node>();
		aEdges = new ArrayList<Edge>();
		lAgents = new LinkedList<Agent>();
	}

	// getters and setters
	
	public int getN(){
		return iN;
	}
	
	public int getIterNum(){
		return iIterNum;
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
		iIterNum++;
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
	
	// create one USA agent and 2 other (Yazidi/ISIS) agents
	public LinkedList<Agent> addAgentsForPart1(int bombCost){
		
		int type, start = 0;
		int goal, id = 0;
		boolean readAgent = true;
		scanner = new Scanner(System.in);
		USAAgent obama = new USAAgent(id, 0, bombCost);
		lAgents.add(obama);
		
		while (readAgent){
			id++;
			System.out.println("\nSelect from menu:");
			System.out.println("      1 = add Greedy Yazidi refugee");
			System.out.println("      2 = add Human Yazidi refugee");
			System.out.println("      3 = add Greedy ISIS terrorist");
			System.out.println("      4 = add Human ISIS terrorist");
			System.out.println("      5 = done");
			System.out.print("> ");
			type = scanner.nextInt();
			if(type < 5){
				System.out.print("Select starting position (from 1 to " + start + "): ");
				start = scanner.nextInt();
				if (start < 1 || start > iN){
					System.out.println("no such position.");
					id--; // don't count this round - no agent was created
					continue;
				}
			}
			Agent a = null;
			switch (type){
				case 1:
					System.out.print("Select goal position (from 1 to " + iN + "): ");
					goal = scanner.nextInt();
					if (goal < 1 || goal > iN){
						System.out.println("no such position.");
						id--; // don't count this round - no agent was created
						continue;
					}
					a = new YazidiAgent(id, start, goal);
					lAgents.add(a);
					getNodeByID(start).addAgent(a);
					break;
				case 2:
					System.out.print("Select goal position (from 1 to " + iN + "): ");
					goal = scanner.nextInt();
					if (goal < 1 || goal > iN){
						System.out.println("no such position.");
						id--; // don't count this round - no agent was created
						continue;
					}
					a = new HumanYazidiAgent(id, start, goal, scanner);
					lAgents.add(a);
					getNodeByID(start).addAgent(a);
					break;
				case 3:
					a = new ISISAgent(id, start);
					lAgents.add(a);
					getNodeByID(start).addAgent(a);
					break;
				case 4:
					a = new HumanISISAgent(id, start, scanner);
					lAgents.add(a);
					getNodeByID(start).addAgent(a);
					break;
				case 5:
					readAgent = false;
					break;
				default:
					System.out.println(type + ": no such option.");
					id--; // don't count this round - no agent was created
					break;
			}			
		}
		return lAgents;
	}
	
	public LinkedList<Agent> addAgentsForPart2(int bombCost){
		
		int type, start = 0;
		int goal = 0, id = 0;
		boolean readAgent = true;
		scanner = new Scanner(System.in);
		
		while (readAgent){
			id++;
			System.out.println("\nSelect from menu:");
			System.out.println("      1 = add Greedy Heuristic Yazidi refugee");
			System.out.println("      2 = add A* Yazidi refugee");
			System.out.println("      3 = add RTA* Yazidi refugee");
			System.out.println("      4 = done");
			System.out.print("> ");
			type = scanner.nextInt();
			if (type < 4){
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
			}
			Agent a = null;
			switch (type){
				case 1:
					a = new SmartAgent(id, start, goal, "Greedy", Integer.MAX_VALUE);
					lAgents.add(a);
					getNodeByID(start).addAgent(a);
					break;
					
				case 2:
					a = new SmartAgent(id, start, goal, "A*", Integer.MAX_VALUE);
					lAgents.add(a);
					getNodeByID(start).addAgent(a);
					break;
					
				case 3:
					System.out.print("Select maximum number of expansions: ");
					int max = scanner.nextInt();
					a = new SmartAgent(id, start, goal, "RTA*", max);
					lAgents.add(a);
					getNodeByID(start).addAgent(a);
					break;
					
				case 4:
					readAgent = false;
					break;
					
				default:
					System.out.println(type + ": no such option.");
					id--; // don't count this round - no agent was created
					break;
			}			
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
