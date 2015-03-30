package bayesian;

public class Evidence {

	private int type;
	private int node;
	private int value;
	
	public Evidence(int t, int n, int v){
		type = t;
		node = n;
		value = v;
	}
	
	public int getType(){
		return type;
	}
	
	public int getNode(){
		return node;
	}
	
	public int getValue(){
		return value;
	}
	
	public void print(){
		
		if (type == 1){
			System.out.println("r" + node + " = " + value);
		}
		else if(type == 2){
			System.out.println("t" + node + " = " + value);
		}
		else {
			System.out.println("d" + node + " = " + value);
		}
	}
	
}
