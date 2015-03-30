package main;
import graph.Graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import bayesian.BayesianNetwork;
import bayesian.Evidence;
import bayesian.Variable;


public class Main {
	
	public static Scanner s;
	public static Graph world;
	public static BayesianNetwork bn;
	public static LinkedList<Evidence> initEvidences;
	public static Enumeration en;
	public static Reasoning rs;
	
	public static void main(String[] args) {
		
		s = new Scanner(System.in);
		world = parseGraph(args[0]);
		
		en = new Enumeration();
		initEvidences = new LinkedList<Evidence>();
		buildBayesianNetwork();
		bn.printBayesianNetwork();
		
		rs = new Reasoning(world, bn, initEvidences, en);
		menu();
		
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
							double prob = s.nextDouble();
							g.getNodeByID(nodeID).setResourceProb(prob);
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
	
	
	private static void buildBayesianNetwork(){
		double pt, pd, leakage;
		
		System.out.print("Please choose pt:\n> ");
		pt = s.nextDouble();
		System.out.print("Please choose pd:\n> ");
		pd = s.nextDouble();
		System.out.print("Please choose leakage:\n> ");
		leakage = s.nextDouble();
		bn = new BayesianNetwork(world, pt, pd, leakage);
	}
	
	
	private static void menu(){
		
		boolean done = false;
		
		while (!done){
		
			System.out.print("Please choose an option:\n"
					+ "1. Reset evidences\n"
					+ "2. Add new evidence\n"
					+ "3. Do probablistic reasoning\n"
					+ "4. Print evidences\n"
					+ "5. Quit\n"
					+ "> ");
			
			switch(s.nextInt()){
				case 1:
					initEvidences.clear();
					System.out.println();
					break;
				case 2:
					readEvidence();
					System.out.println();
					break;
				case 3:
					reasoningMenu();
					break;
				case 4:
					printEvidences();
					break;
				default:
					done = true;
					break;
			}
		}
	}
	
	
	private static void readEvidence() {
		
		int type, node, value = 0;
		String input;
		System.out.print("Please insert an evidence in the following form: <r/t/d><nodeId> = <0/1>\n> ");
		input = s.next();
		type = input.charAt(0);
		node = Integer.parseInt(input.substring(1));
		s.next(); // =
		value = s.nextInt();
		
		switch (type){
			case ('r'):
				type = 1;
				break;
			case ('t'):
				type = 2;
				break;
			case ('d'):
				type = 3;
				break;
			default:
				System.out.println("invalid variable type");
		}
		
		if (type == 1){
			Variable v = bn.getVariables().get(node-1);
			if ((v.getTableCell(0).getProbTrue() == 0 && value == 1) ||
					(v.getTableCell(0).getProbTrue() == 1 && value == 0)){
				System.out.print("invalid value for ");
				v.print();
				return;
			}
		}
		initEvidences.add(new Evidence(type, node, value));
	}
	
	
	private static void reasoningMenu(){
		
		int q;
		
		System.out.print("Please choose a question:\n"
				+ "1. What is the probability that each of the vertices contains terrorists?\n"
				+ "2. What is the probability that a certain path (set of edges) is free from terrorists?\n"
				+ "3. What is the path from a given location to a goal that has the highest probability of being free from terrorists?\n"
				+ "> ");
		q = s.nextInt();
		
		switch (q){
			case(1):
				rs.question1();
				break;
			case(2):
				rs.question2(readPath());
				break;
			case(3):
				readSrcGoal();
				break;
			default:
				System.out.println("No such option.");
				break;
		}
		
	}

	
	private static LinkedList<Integer> readPath() {
		
		int length;
		LinkedList<Integer> path = new LinkedList<Integer>();
		
		System.out.print("Please choose path length: ");
		length = s.nextInt();
		
		System.out.print("Please choose a path: ");
		
		while(length > 0){
			path.add(s.nextInt());
			length--;
		}
		
		return path;
	}

	
	private static void readSrcGoal() {
		
		int src, goal;
		
		System.out.print("Please choose source node: ");
		src = s.nextInt();
		
		System.out.print("Please choose goal node: ");
		goal = s.nextInt();
		
		rs.question3(src, goal);
	}
	
	
	public static void printEvidences(){
		
		if (initEvidences.isEmpty()){
			System.out.println("NO EVIDENCES");
		}
		else{
			System.out.println("EVIDENCES:");
			for (int i = 0 ; i < initEvidences.size() ; i++){
				initEvidences.get(i).print();
			}
		}
		System.out.println();
	}
	
}
