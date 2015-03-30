package bayesian;

import graph.Graph;
import graph.Node;

import java.util.ArrayList;
import java.util.LinkedList;

public class BayesianNetwork {

	public static final int RESOURCE = 1;
	public static final int TERRORIST = 2;
	public static final int DESTRUCTION = 3;
	
	public static double pt;
	public static double pd;
	public static double leakage;
	
	private Graph graph;
	private LinkedList<Variable> variables;
	
	public BayesianNetwork(Graph graph1, double pt1, double pd1, double leakage1){
		graph = graph1;
		pt = pt1;
		pd = pd1;
		leakage = leakage1;
		variables = new LinkedList<Variable>();
		createAllVars();
	}
	
	public LinkedList<Variable> getVariables(){
		return variables;
	}
	
	private void createAllVars(){
		
		for (int i = 0 ; i < graph.getN() ; i++){
			Variable ri = new Variable(1, (i+1));
			ri.createTable(graph.getNodeByID(ri.getNode()).getResourceProb());
			variables.add(ri);
		}
		
		for (int i = 0 ; i < graph.getN() ; i++){
			Variable ti = new Variable(2, (i+1));
			updateParents(ti);
			ti.createTable(graph.getNodeByID(ti.getNode()).getResourceProb());
			variables.add(ti);
		}
		
		for (int i = 0 ; i < graph.getN() ; i++){
			Variable di = new Variable(3, (i+1));
			updateParents(di);
			di.createTable(graph.getNodeByID(di.getNode()).getResourceProb());
			variables.add(di);
		}
	}
	
	private void updateParents(Variable vi){
		
		int type = vi.getType();
		int nodeId = vi.getNode();
		int neighbourID;
		int weight;
		int index;
		boolean addMe = false;
		Node node = graph.getNodeByID(nodeId);
		LinkedList<Node> neighbours = node.getNeighbours();
		Parent p;
		
		// create parents for neighbor nodes
		for (int i = 0 ; i < neighbours.size() ; i++){
			
			neighbourID = neighbours.get(i).getID();
			
			if(neighbourID > nodeId && !addMe){
				// create parent for the same node ID
				p = new Parent(variables.get((type-2)*graph.getN() + nodeId - 1), 0);
				vi.addParent(p);
				addMe = true;
			}
			
			
			weight = graph.getEdgeWeight(nodeId, neighbourID);
			weight = weight > 0 ? weight : 0; // case of getEdgeWeight(v,v)
			index = (type-2)*graph.getN() + neighbourID - 1;
			
			p = new Parent(variables.get(index), weight);
			vi.addParent(p);
		}
		
		if (!addMe){
			p = new Parent(variables.get((type-2)*graph.getN() + nodeId - 1), 0);
			vi.addParent(p);
		}
	}
	
	public void printBayesianNetwork(){
		
		Variable ri;
		Variable ti;
		Variable di;
		int n = graph.getN();
		ArrayList<Parent> parents;
		
		for (int i = 0 ; i < n ; i++){
			
			ri = variables.get(i);
			ti = variables.get(n+i);
			di = variables.get(2*n+i);
			
			// ri probabilty print
			System.out.println("VERTEX " + (i+1) + ": resource" +
								"\n\tP(resource) = " + trim(ri.getTableCell(0).getProbTrue()) +
								"\n\tP(no resource) = " + trim(ri.getTableCell(0).getProbFalse()) + "\n");
			
			// ti probabilty print
			parents = ti.getParents();
			System.out.print("VERTEX " + (i+1) + ": terrorist | ");
			for (int j = 0 ; j < parents.size()-1 ; j++){
				System.out.print("resources at " + parents.get(j).getNode() + ", ");
			}
			System.out.println("resources at " + parents.get(parents.size()-1).getNode());
			ti.printTable();
			
			// di probabilty print
			parents = di.getParents();
			System.out.print("VERTEX " + (i+1) + ": destruction | ");
			for (int j = 0 ; j < parents.size()-1 ; j++){
				System.out.print("terrorist at " + parents.get(j).getNode() + ", ");
			}
			System.out.println("terrorist at " + parents.get(parents.size()-1).getNode());
			di.printTable();
		}
	}
	
	private double trim(double number){
		return Math.round(number * 1000.0) / 1000.0;
	}
}
