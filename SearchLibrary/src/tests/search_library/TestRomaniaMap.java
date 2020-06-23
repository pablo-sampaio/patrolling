package tests.search_library;

import search_library.methods.AStarSearch;
import search_library.methods.BreadthFirstSearch;
import search_library.methods.DepthFirstSearch;
import search_library.sample_problems.AIMABookRomaniaMap;
import search_library.sample_problems.RomaniaCities;

public class TestRomaniaMap {

	public static void main(String[] args) {

		AIMABookRomaniaMap map = new AIMABookRomaniaMap(RomaniaCities.ARAD);

		AStarSearch search = new AStarSearch();

		// RecursiveBestFirstSearch search = new RecursiveBestFirstSearch();

		// SMAStarSearch search = new SMAStarSearch();

		// BreadthFirstSearch search = new BreadthFirstSearch();

		// DepthFirstSearch search = new DepthFirstSearch();

		search.search(map);

	}

}
