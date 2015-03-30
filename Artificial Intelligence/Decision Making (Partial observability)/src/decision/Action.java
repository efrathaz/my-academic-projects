package decision;

public class Action {
	
	private int srcNode;
	private int destNode;
	
	public Action(int source, int destination){
		srcNode = source;
		destNode = destination;
	}
	
	public int getSrc(){
		return srcNode;
	}
	
	public int getDest(){
		return destNode;
	}
	
	public void print() {
		System.out.println("(" + srcNode + "-->" + destNode + ")");
	}
}
