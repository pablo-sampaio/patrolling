package tests.search_library;

import search_library.AbstractSearchNode;
import search_library.methods.BreadthFirstSearch;
import search_library.methods.SMAStarSearch;
import search_library.sample_problems.The8Puzzle;

public class Test8Puzzle {

	public static void main(String[] args) {
		int matrix[][] = { { 6, 5, 1 }, { 4, 7, 8 }, { 0, 3, 2 } };
		int matrix2[][] = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };

		long tStart = System.currentTimeMillis();

		The8Puzzle puzzle = new The8Puzzle(matrix, matrix2);

		// RecursiveBestFirstSearch rbfs = new RecursiveBestFirstSearch();
		// AbstractSearchNode sol = rbfs.search(puzzle, true);

		SMAStarSearch sma = new SMAStarSearch(100);
		AbstractSearchNode sol = sma.search(puzzle);

		// BreadthFirstSearch bs = new BreadthFirstSearch();
		// AbstractSearchNode sol = bs.search(puzzle, true);

		// AStarSearch as = new AStarSearch();
		// AbstractSearchNode sol = as.search(puzzle, true);

		// DepthFirstSearch df = new DepthFirstSearch();
		// AbstractSearchNode sol = df.search(puzzle, true);

		// IterativeDeepeningSearch is = new IterativeDeepeningSearch();
		// AbstractSearchNode sol = is.search(puzzle, true);

		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;

		/*
		 * LinkedList<AbstractSearchNode> t = (LinkedList<AbstractSearchNode>)
		 * sol .getSolutionPath();
		 * 
		 * for (int i = 0; i < t.size(); i++) {
		 * System.out.println("------------");
		 * System.out.println(t.get(i).toString());
		 * System.out.println("F_COST: " + t.get(i).getFutureCostEstimate());
		 * System.out.println("------------"); }
		 * 
		 * System.out.println(elapsedSeconds);
		 */

	}

}
