package main;

import java.util.LinkedList;

import bayesian.BayesianNetwork;
import bayesian.Evidence;
import bayesian.Parent;
import bayesian.Variable;

public class Enumeration {

	
	public double[] run(Variable x, LinkedList<Evidence> evs, BayesianNetwork bn){
		
		double[] result = new double[2];
		LinkedList<Variable> newVars;
		LinkedList<Evidence> newEv;
		
		for (int i = 0 ; i < 2 ; i++){
			newVars = copyVars(bn.getVariables());
			newEv = copyEvs(evs);
			newEv.add(new Evidence(x.getType(), x.getNode(), i));
			
			result[i] = enumerateAll(newVars, newEv);
		}
		
		return normalize(result);
//		return result;
	}

	
	private Double enumerateAll(LinkedList<Variable> vars, LinkedList<Evidence> evs) {
		
		int firstValue = 0;
		double result = 0;
		boolean found = false;
		Variable first;
		LinkedList<Evidence> evsTrue, evsFalse;
		LinkedList<Variable> varsTrue, varsFalse;
		
		if (vars.isEmpty()){
			return 1.0;
		}
		
		first = vars.remove(0);
		
		// check if there is an evidence of "first"
		for (int i = 0 ; i < evs.size() && !found; i++){
			if ((evs.get(i).getType() == first.getType()) && (evs.get(i).getNode() == first.getNode())){
				found = true;
				firstValue = evs.get(i).getValue();
			}
		}
		
		if (found){
			double prob = calcProb(first, evs, firstValue);
			double recResult = 0;
			
			if (prob != 0){
				recResult = enumerateAll(vars, evs);
			}
			result = prob * recResult;
		}
		else {
			// create arguments for recursive call with first = 0
			double probFalse = calcProb(first, evs, 0);
			double recResultFalse = 0;
			varsFalse = copyVars(vars);
			evsFalse = copyEvs(evs);
			evsFalse.add(new Evidence(first.getType(), first.getNode(), 0));
			
			// create arguments for recursive call with first = 1
			double probTrue = calcProb(first, evs, 1);
			double recResultTrue = 0;
			varsTrue = copyVars(vars);
			evsTrue = copyEvs(evs);
			evsTrue.add(new Evidence(first.getType(), first.getNode(), 1));
			
			if (probFalse != 0){
				recResultFalse = enumerateAll(varsFalse, evsFalse);
			}
			
			if (probTrue != 0){
				recResultTrue = enumerateAll(varsTrue, evsTrue);
			}
			result = probFalse * recResultFalse + probTrue * recResultTrue;
		}
		return result;
	}

	
	private double calcProb(Variable y, LinkedList<Evidence> evs, int yValue){
		
		int value, entry = 0;
		boolean found = false;
		Parent currParent;
		Evidence currEvidence;
		
		for (int i = 0 ; i < y.getParents().size(); i++){
			currParent = y.getParents().get(i);
			for (int j = 0 ; j < evs.size() && !found; j++){
				currEvidence = evs.get(j);
				if ((currEvidence.getType() == currParent.getType()) && (currEvidence.getNode() == currParent.getNode())){
					found = true;
					value = evs.get(j).getValue();
					entry += value*Math.pow(2, i);
				}
			}
			found = false;
		}
		
		if (yValue == 1){
			return y.getTableCell(entry).getProbTrue();
		}
		return y.getTableCell(entry).getProbFalse();
	}
	
	
	private double[] normalize(double[] result) {
		
		double sum = result[0] + result[1];

		for (int i = 0 ; i < 2 ; i++){
			result[i] /= sum;
		}
		
		return result;
	}
	
	
	private LinkedList<Evidence> copyEvs(LinkedList<Evidence> e1) {
		
		LinkedList<Evidence> e2 = new LinkedList<Evidence>();
		
		for (int i = 0 ; i < e1.size() ; i++){
			e2.add(e1.get(i));
		}
		
		return e2;
	}
	
	
	private LinkedList<Variable> copyVars(LinkedList<Variable> v1) {
		
		LinkedList<Variable> v2 = new LinkedList<Variable>();
		
		for (int i = 0 ; i < v1.size() ; i++){
			v2.add(v1.get(i));
		}
		
		return v2;
	}
	
	public static void printEvidences(LinkedList<Evidence> evidences){
		//TODO: delete function
		System.out.println("EVIDENCES:");
		for (int i = 0 ; i < evidences.size() ; i++){
			evidences.get(i).print();
		}
	}
	
	public static void printVariables(LinkedList<Variable> vars){
		//TODO: delete function
		System.out.println("VARIABLES:");
		for (int i = 0 ; i < vars.size() ; i++){
			vars.get(i).print();
		}
	}
}
