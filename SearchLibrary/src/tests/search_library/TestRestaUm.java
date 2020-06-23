package tests.search_library;

import java.util.List;

import search_library.AbstractSearchNode;
import search_library.methods.DepthFirstSearch;
import search_library.methods.SMAStarSearch;
import search_library.sample_problems.RestaUmSearchNode;

public class TestRestaUm {

	public static void main1(String[] args) {
		RestaUmSearchNode initialState = new RestaUmSearchNode();
		System.out.println(initialState);

		List<AbstractSearchNode> children = initialState.expand();
		System.out.println("FILHOS: " + children.size());

		for (AbstractSearchNode c : children) {
			System.out.println(c);
		}
	}

	public static void main(String[] args) {
		RestaUmSearchNode initialState = new RestaUmSearchNode();
		//SMAStarSearch searchAlgorithm = new SMAStarSearch(20);
		DepthFirstSearch searchAlgorithm = new DepthFirstSearch(-1, false, true);

		AbstractSearchNode result = searchAlgorithm.search(initialState);

		showFullSolution(result);
	}
	
	public static void showFullSolution(AbstractSearchNode result) {
		if (result.getFatherNode() != null) {
			showFullSolution(result.getFatherNode());
			System.out.println("--");
		}
		System.out.println(result);
	}

}
