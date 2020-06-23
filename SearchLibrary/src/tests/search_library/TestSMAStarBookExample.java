package tests.search_library;

import search_library.AbstractSearchNode;
import search_library.methods.SMAStarSearch;
import search_library.sample_problems.SMAStarBookExample;

public class TestSMAStarBookExample {

	public static void main(String[] args) {
		SMAStarBookExample initialNode = new SMAStarBookExample();
		SMAStarSearch smaSearch = new SMAStarSearch(3, true);

		AbstractSearchNode sx = smaSearch.search(initialNode);

		System.out.println(sx.getSolutionPath());
	}

}
