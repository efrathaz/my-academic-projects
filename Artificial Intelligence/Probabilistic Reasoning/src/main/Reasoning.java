package main;

import java.util.LinkedList;

import bayesian.BayesianNetwork;
import bayesian.Evidence;
import bayesian.Variable;
import graph.Graph;

public class Reasoning {

	private Graph world;
	private BayesianNetwork bn;
	private LinkedList<Evidence> initEvidences;
	private Enumeration en;
	
	public Reasoning(Graph w, BayesianNetwork b, LinkedList<Evidence> evs, Enumeration enumer){
		world = w;
		bn = b;
		initEvidences = evs;
		en = enumer;
	}
	
	private double trim(double number){
		return Math.round(number * 1000.0) / 1000.0;
	}
	
	private double[] checkTerrorist(int i){
		Variable ti = bn.getVariables().get(world.getN()+i);
		return en.run(ti, initEvidences, bn);
	}
	
	
	public void question1(){
		
		int n = world.getN();
		String result = "";
		double[] probs = new double[2];
		
		for (int i = 0 ; i < n ; i++){
			probs = checkTerrorist(i);
			result = result + "P(not t" + (i+1) + ") = " + trim(probs[0]) + ", P(t" + (i+1) + ") = " + trim(probs[1]) + "\n";
		}
		System.out.println(result);
	}
	
	
	private LinkedList<Evidence> createEvidences(LinkedList<Variable> rest){
		LinkedList<Evidence> evs = new LinkedList<Evidence>();
		
		for (int i = 0 ; i < initEvidences.size() ; i++){
			evs.add(initEvidences.get(i));
		}
		
		for (int i = 0 ; i < rest.size() ; i++){
			evs.add(new Evidence(rest.get(i).getType(), rest.get(i).getNode(), 0));
		}
		
		return evs;
	}
	
	
	public void question2(LinkedList<Integer> path){
		
		double prob;
		
		if (!world.isPath(path)){
			System.out.println("No such path\n");
		}
		else {
			prob = calcPathProb(path);
			System.out.println("The probability of no terrorists in this path is " + trim(prob) + "\n");
		}
	}
	
	
	public void question3(int src, int goal){
		
		double tempProb, maxProb = 0;
		LinkedList<Integer> tempPath, maxPath = null;
		LinkedList<LinkedList<Integer>> allPaths = world.findAllPaths(src, goal);
		
		for (int i = 0 ; i < allPaths.size() ; i++){
			tempPath = allPaths.get(i);
			tempProb = calcPathProb(tempPath);
			
			if (tempProb > maxProb){
				maxProb = tempProb;
				maxPath = tempPath;
			}
		}
		System.out.println("The path with the highest probability of being free from terrorists is:");
		printPath(maxPath);
		System.out.println("(probability = " + trim(maxProb) + ")\n");
	}
	
	private double calcPathProb(LinkedList<Integer> path){
		
		LinkedList<Variable> vars = new LinkedList<Variable>();
		LinkedList<Evidence> evs;
		Variable first;
		double prob = 1;
		
		for (int i = 0 ; i < path.size() ; i++){
			vars.add(bn.getVariables().get(world.getN()+path.get(i)-1));
		}
		
		while (!vars.isEmpty()){
			first = vars.remove(0);
			evs = createEvidences(vars);
			prob *= en.run(first, evs, bn)[0];
		}
		return prob;
	}
	
	private void printPath(LinkedList<Integer> path){
		
		for (int i = 0 ; i < path.size() ; i++){
			System.out.print(path.get(i) + " ");
		}
	}
}
