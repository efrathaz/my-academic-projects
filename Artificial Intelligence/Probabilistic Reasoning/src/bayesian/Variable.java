package bayesian;

import java.util.ArrayList;

public class Variable {

	private int type;
	private int node;
	private ArrayList<Parent> parents;
	private ArrayList<TableCell> table; // a table representing possible assignments for the parents
										// for example, the values of Pr(v) and Pr(!v) for the assignment (F, T, F) will be at index 2 in table
	
	public Variable(int type, int node){
		this.type = type;
		this.node = node;
		parents = new ArrayList<Parent>();
		table = new ArrayList<TableCell>();
	}
	
	public int getType(){
		return type;
	}
	
	public int getNode(){
		return node;
	}
	
	public ArrayList<TableCell> getTable(){
		return table;
	}
	
	public TableCell getTableCell(int index){
		return table.get(index);
	}
	
	public ArrayList<Parent> getParents(){
		return parents;
	}
	
	public void addParent(Parent parent){
		parents.add(parent);
	}
	
	public ArrayList<TableCell> createTable(double prob){
		
		int numOfParents = parents.size();
		int numOfAssignments = (int)Math.pow(2, numOfParents);
		
		// in case of ri
		if (numOfParents == 0){
			table.add(new TableCell(prob));
		}
		// in case of ti or di
		else{
			if (type == 2){
				table.add(new TableCell(BayesianNetwork.leakage)); // for the case where all parents are false = leakage
			}
			else {
				table.add(new TableCell(0.0));
			}
			
			for (int i = 1 ; i < numOfAssignments ; i++){
				
				// if i is a power of 2 - meaning only one parent is True
				if ((i & (i - 1)) == 0){
					int indexOfParent = (int)(Math.log(i) / Math.log(2));
					table.add(new TableCell(calcFormulaValue(parents.get(indexOfParent))));
				}
				// more than one parent is True
				else{
					table.add(new TableCell(calcNoisyOrValue(i)));
				}
			}
		}
		return table;
	}
	
	private double calcFormulaValue(Parent parent){
		
		int weight = parent.getWeight();
		double tmpMax = ((double)(4 - weight)) / (double)5;
		double max = tmpMax > 0 ? tmpMax : 0;

		if(parent.getType() == BayesianNetwork.RESOURCE){
			return BayesianNetwork.pt * max;
		}
		
		return BayesianNetwork.pd * max;
	}
	
	private double calcNoisyOrValue(int index){
		
		double multipleProb = 1;
		// for every case in which only one parent is true
		for (int parent = 0 ; parent < parents.size() ; parent++){
			if ((index & (int)Math.pow(2, parent)) != 0){
				multipleProb *= table.get((int)Math.pow(2, parent)).getProbFalse();
			}
		}
		return (1-multipleProb);
	}

	public void printTable() {
		
		String s;
		String myType, parentType;
		
		if(type == 2){
			myType = "terr";
			parentType = "res";
		}
		else {
			myType = "dest";
			parentType = "terr";
		}
		
		for (int i = 0 ; i < table.size() ; i++){
			s = "";
			for (int j = 0 ; j < parents.size() ; j++){
				if(isFalse(i, j)){
					s = s + "not ";
				}
				
				s = s + parentType + " " + parents.get(j).getNode();
				if (j < parents.size()-1){
					s = s + ", ";
				}
			}
			System.out.println("\tP(" + myType + " | " + s + ") = " + trim(table.get(i).getProbTrue()));
			System.out.println("\tP(no " + myType + " | " + s + ") = " + trim(table.get(i).getProbFalse()) + "\n");
		}
	}
	
	private boolean isFalse(int index, int digit){
		if((index & (int)Math.pow(2, digit)) == 0){
			return true;
		}
		return false;
	}
	
	public void printParents(){
		System.out.println("Parents:");
		for(int j = 0 ; j < parents.size() ; j++){
			parents.get(j).print();
		}
		System.out.println();
	}
	
	public void print(){
		if (type == 1){
			System.out.println("Variable r" + node);
		}
		else if(type == 2){
			System.out.println("Variable t" + node);
		}
		else {
			System.out.println("Variable d" + node);
		}
	}
	
	private double trim(double number){
		return Math.round(number * 1000.0) / 1000.0;
	}
}
