package search;

import java.util.Comparator;

public class ComparatorByF implements Comparator<StateNode> {

	@Override
	public int compare(StateNode s1, StateNode s2) {
		return s1.getF() - s2.getF();
	}
}
