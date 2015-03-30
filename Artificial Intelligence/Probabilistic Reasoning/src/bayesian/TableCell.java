package bayesian;

public class TableCell {

	private double probTrue;  // Pr(v)
	private double probFalse; // Pr(!v)
	
	public TableCell(double prob){
//		probTrue = Math.round(prob * 1000.0) / 1000.0;
//		probFalse = Math.round((1-prob) * 1000.0) / 1000.0;
		probTrue = prob;
		probFalse = 1 - prob;
	}

	public double getProbTrue() {
		return probTrue;
	}

	public double getProbFalse() {
		return probFalse;
	}
	
}
