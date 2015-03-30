package decision;

public class ISISAtNode {

	private int nodeID;
	private int value; // 1 = True, 2 = False, 3 = Unknown
	
	public ISISAtNode(int id, int v){
		nodeID = id;
		value = v;
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean equals(ISISAtNode other){
		if (nodeID == other.getNodeID() && value == other.getValue()){
			return true;
		}
		return false;
	}
}
