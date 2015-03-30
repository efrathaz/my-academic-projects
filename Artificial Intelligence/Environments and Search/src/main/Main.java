package main;
import graph.Graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import agents.Agent;
import agents.SmartAgent;


public class Main {
	
	public static final int f = 1;
	
	public static void main(String[] args) {
		
		// initialization
		Graph world = parseGraph(args[0]);
		int bombCost = Integer.parseInt(args[1]);
		int PartOfGame = Integer.parseInt(args[2]);
		
		LinkedList<Agent> agents;
		if (PartOfGame == 1){
			agents = world.addAgentsForPart1(bombCost);
		}
		else {
			agents = world.addAgentsForPart2(bombCost);
		}
		
		Simulator worldSim = new Simulator(world);
		
		System.out.println();
		world.printState();
		
		worldSim.simulate();
		
		// print scores
		printScores(agents);
	}
	
	private static Graph parseGraph(String arg){
		Graph g = new Graph();
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
			if (agent instanceof SmartAgent){
				int t = ((SmartAgent) agent).getExpansionNum();
				int performance = f * score + t;
				if (score == -1){
					System.out.println("\tAgent " + i + ": S = infinity, T = " + t + ", P = infinity"
							+ ", Number of actions: " + agent.numOfActions());
				}
				else {
				System.out.println("\tAgent " + i + ": S = " + score + ", T = " + t + ", P = " + performance 
						+ ", Number of actions: " + agent.numOfActions());
				}
			}
			else if (score == -1){
				System.out.println("\tAgent " + i + ": S = infinity, Number of actions: " + agent.numOfActions());
			}
			else {
				System.out.println("\tAgent " + i + ": S = " + score + ", Number of actions: " + agent.numOfActions());
			}
			System.out.println("\t\tActions: " + agent.getActions());
		}
		
	}
}
