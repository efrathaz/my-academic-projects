package bayesian;

public class Parent {

	private Variable parent;
	private int weight;
	
	public Parent(Variable p, int w){
		parent = p;
		weight = w;
	}

	public Variable getParent() {
		return parent;
	}

	public int getWeight() {
		return weight;
	}

	public int getType() {
		return parent.getType();
	}
	
	public int getNode() {
		return parent.getNode();
	}
	
	public void print(){
		System.out.print("Parent weight = " + weight + ", ");
		parent.print();
	}
}
