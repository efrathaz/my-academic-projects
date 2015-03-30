package graph;

public class Edge {

	private int iU;
	private int iV;
	private int iWeight;
	
	public Edge(int u, int v, int w){
		iU = u;
		iV = v;
		iWeight = w;
	}
	
	// getters
	
	public int getU(){
		return iU;
	}
	
	public int getV(){
		return iV;
	}
	
	public int getWeight(){
		return iWeight;
	}
}
