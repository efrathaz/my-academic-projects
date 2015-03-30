package search;

import java.util.Comparator;

public class ComparatorByH implements Comparator<StateNode> {

	@Override
	public int compare(StateNode s1, StateNode s2) {
		return s1.getH() - s2.getH();
	}

}
