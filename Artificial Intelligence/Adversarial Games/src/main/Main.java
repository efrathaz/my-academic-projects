package main;
import graph.Graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import agents.Agent;


public class Main {
	
	public static int partOfGame;
	public static int T;
	public static int cutoff;
	public static Scanner s;
	
	public static void main(String[] args) {
		
		// initialization
		s = new Scanner(System.in);
		initialize();
		Graph world = parseGraph(args[0]);
		
		LinkedList<Agent> agents;
		switch(partOfGame){
			case 1:
				agents = world.addAgentsForPart1();
				break;
			case 2:
				agents = world.addAgentsForPart2();
				break;
			default:
				agents = world.addAgentsForPart3();
				break;
		}
		
		Simulator worldSim = new Simulator(world);
		
		System.out.println();
		world.printState();
		
		worldSim.simulate();
		
		// print scores
		printScores(agents);
		/*
		Minimax m = new Minimax();
		m.initialize();
		m.alphaBetaDecision();
		
		*/
	}
	
	private static void initialize(){
		System.out.print("Please choose a part of game (1, 2 or 3):\n> ");
		partOfGame = s.nextInt();
		System.out.print("Please choose a maximal number of actions per agent:\n> ");
		T = s.nextInt();
		System.out.print("Please choose a cutoff:\n> ");
		cutoff = s.nextInt();
	}
	
	private static Graph parseGraph(String arg){
		Graph g = new Graph(T, cutoff);
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
							g.createNodes(s.nextInt());
							break;
						case "#V":
							int nodeID = s.nextInt();
							g.getNodeByID(nodeID).setFoodUnits(Integer.parseInt(s.next().substring(1)));
							break;
						case "#E":
							g.addEdge(s.nextInt(), s.nextInt(), Integer.parseInt(s.next().substring(1)));
							break;
					}
				}
				s.close();
			}
			g.calcNeighbours();
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
		return g;
	}
	
	private static void printScores(LinkedList<Agent> lAgents){
		System.out.println("Scores:");
		for (int i = 0 ; i < lAgents.size() ; i++){
			Agent agent = lAgents.get(i);
			int score = agent.getScore();
			if (score == Integer.MAX_VALUE){
				System.out.println("\tAgent " + i + ": S = ∞, Number of actions: " + agent.numOfActions());
			}
			else if (score == Integer.MIN_VALUE){
				System.out.println("\tAgent " + i + ": S = -∞, Number of actions: " + agent.numOfActions());
			}
			else {
				System.out.println("\tAgent " + i + ": S = " + score + ", Number of actions: " + agent.numOfActions());
			}
			System.out.println("\t\tActions: " + agent.getActions());
		}
	}
}
