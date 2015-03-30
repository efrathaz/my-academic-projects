package main;

import graph.Graph;
import graph.Node;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import decision.Action;
import decision.ISISAtNode;
import decision.State;
import decision.Policy;
import decision.PolicyIteration;


public class Main {
	
	public static Scanner s;
	public static Graph graph;
	public static State currS;
	public static LinkedList<Integer> nodesWithProb;
	public static LinkedList<Integer> worldNodes;
	public static int numOfNodes;
	
	public static void main(String[] args) {
		
		graph = new Graph();
		nodesWithProb = new LinkedList<Integer>();
		
		s = new Scanner(System.in);
		parseGraph(args[0]);
		
//		graph.printGraph();
		
		PolicyIteration pIterator = new PolicyIteration(graph, nodesWithProb);
		Policy pi = pIterator.findMaxPolicy();
		
		System.out.println("BEST POLICY:");
		pi.print();
		
		currS = pi.getStates().getFirst();
		createWorldNodes();
		printWorldNodes();
		
		menu(pi);
	}
	
	
	private static void parseGraph(String arg){
		BufferedReader inputStream = null;
		try {
			inputStream = new BufferedReader(new FileReader(arg));
			String line;
			
			while ((line = inputStream.readLine()) != null){
				Scanner s = new Scanner(line);
				if (s.hasNext()){
					String type = s.next(); 
					switch(type){
						case "#N":
							numOfNodes = s.nextInt();
							graph.createNodes(numOfNodes);
							break;
						case "#V":
							int nodeID = s.nextInt();
							if (s.hasNextDouble()){
								double prob = s.nextDouble();
								graph.getNodeByID(nodeID).setISISprob(prob);
								if (prob > 0){
									nodesWithProb.add(nodeID);
								}
							}
							else{
								graph.getNodeByID(nodeID).setISISprob(0);
							}
							break;
						case "#E":
							graph.addEdge(s.nextInt(), s.nextInt(), Integer.parseInt(s.next().substring(1)));
							break;
						case "#Start":
							graph.setStart(s.nextInt());
							break;
						case "#Goal":
							graph.setGoal(s.nextInt());
							break;
					}
				}
				s.close();
			}
			graph.calcNeighbours();
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
			}
            catch (IOException ex) {
                ex.printStackTrace();
            }
		}
	}
	
	private static void createWorldNodes(){
		
		double prob, random;
		
		for (int i=0 ; i<numOfNodes; i++){
			random = Math.random();
			prob = graph.getNodes().get(i).getISISprob();
			if (prob != 0 && random >= prob){
				graph.getNodes().get(i).setWorldISIS(true);
			}
			else{
				graph.getNodes().get(i).setWorldISIS(false);
			}
		}
	}
	
	private static void printWorldNodes(){
		System.out.println("\nWorld ISIS status:");
		for (int i=0 ; i<numOfNodes; i++){
			System.out.println("V" + graph.getNodes().get(i).getID() + " " + graph.getNodes().get(i).isWorldISIS());
		}
	}
	
	private static void stepByPolicyChoice(){
		Action a = currS.getBestAction();
		LinkedList<State> bestStates = currS.getBestStates();
		boolean found = false;
		int i, j;
		
		if(currS.getLocation() == graph.getGoal()){
			System.out.println("At goal.");
			return;
		}
		
		if (bestStates.isEmpty()){
			System.out.println("No available actions.");
			return;
		}
		
		/// print
		System.out.print("current state: ");
		currS.print();
		System.out.println("bestStates for current state");
		for (i = 0 ; i < bestStates.size() && !found ; i++){
			bestStates.get(i).print();
		}
		System.out.println();
		///////
		
		for (i = 0 ; i < bestStates.size() && !found ; i++){
			State temp = bestStates.get(i);
			if (temp.getLocation() == a.getDest()){
				LinkedList<ISISAtNode> currISISinfo = currS.getISISinfo();
				LinkedList<ISISAtNode> tempISISinfo = temp.getISISinfo();
				
				for (j = 0 ; j < currISISinfo.size() ; j++){
					
					int currValue = currISISinfo.get(j).getValue();
					int tempValue = tempISISinfo.get(j).getValue();
					int tempNodeId = tempISISinfo.get(j).getNodeID();
					boolean ISISatTemp = graph.getNodes().get(tempNodeId-1).isWorldISIS();
					
					// if in the current state there is no terrorist at some node, 
					// than states where there is a terrorist at that node are irrelevant
					if ((currValue == 1 || currValue == 2) && currValue != tempValue){
						break;
					}
					else if(currValue == 3 && ((tempValue == 0 && ISISatTemp) || (tempValue == 1 && !ISISatTemp))){
						break;
					}
				}
				if (j == currISISinfo.size()){
					currS = temp;
					found = true;
				}
			}
		}
		if (!found){
			currS = bestStates.getFirst();
		}
		a.print();
	}
	
	private static void stepByUserChoice(Policy pi){
		
		LinkedList<Node> neighbours = graph.getNeighbours(currS.getLocation());
		int choise, dest, i, j;
		boolean found = false;
		
		/// print
		System.out.print("current state: ");
		currS.print();
		///////
		System.out.println("Please choose an action:");
		for (i = 0 ; i < neighbours.size() ; i++){
			System.out.println("    " + (i+1) + ") Traverse to " + neighbours.get(i).getID());
		}
		System.out.print("> ");
		choise = s.nextInt();
		dest = neighbours.get(choise-1).getID();
		if (graph.getEdgeWeight(currS.getLocation(), dest) < 0){
			System.out.println("No such option.");
			return;
		}
		
		// find next state
		LinkedList<State> states = pi.getStates();
		for (i = 0 ; i < states.size() ; i++){
			State tempS = states.get(i);
			if (tempS.getLocation() == dest){
				
				LinkedList<ISISAtNode> currISISinfo = currS.getISISinfo();
				LinkedList<ISISAtNode> tempISISinfo = tempS.getISISinfo();
				
				for (j = 0 ; j < currISISinfo.size() ; j++){
					
					int currValue = currISISinfo.get(j).getValue();
					int tempValue = tempISISinfo.get(j).getValue();
					int tempNodeId = tempISISinfo.get(j).getNodeID();
					boolean ISISatTemp = graph.getNodes().get(tempNodeId-1).isWorldISIS();
					
					// if in the current state there is no terrorist at some node, 
					// than states where there is a terrorist at that node are irrelevant
					if ((currValue == 1 || currValue == 2) && currValue != tempValue){
						break;
					}
					else if(currValue == 3 && ((tempValue == 0 && ISISatTemp) || (tempValue == 1 && !ISISatTemp))){
						break;
					}
				}
				if (j == currISISinfo.size()){
					currS = tempS;
					found = true;
					if (currS.getLocation() == graph.getGoal()){
						System.out.println("At goal!");
					}
					if (graph.getNodes().get(currS.getLocation()-1).isWorldISIS()){
						System.out.println("You are dead now.");
					}
				}
			}
		}
		
		if(!found){
			System.out.println("The action is not possible.");
		}
	}
	
	private static void menu(Policy pi){
		
		boolean done = false;
		
		while (!done){
		
			System.out.print("Please choose an option:\n"
					+ "1. Step accroding to Best Policy\n"
					+ "2. Step according to users's wish\n"
					+ "3. Quit\n"
					+ "> ");
			
			switch(s.nextInt()){
				case 1:
					stepByPolicyChoice();
					System.out.println();
					break;
				case 2:
					stepByUserChoice(pi);
					System.out.println();
					break;
				default:
					done = true;
					break;
			}
		}
	}
}
