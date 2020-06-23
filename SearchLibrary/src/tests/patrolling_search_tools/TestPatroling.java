package tests.patrolling_search_tools;

import java.util.LinkedList;

import patrolling_search_tools.SearchNodePatroling;
import search_library.methods.AStarSearch;
import yaps.graph.Graph;

public class TestPatroling {

	public static void main(String[] args) {

		Graph graph = new Graph(10);
		graph.addEdge(0, 3, 1, false);
		graph.addEdge(0, 4, 1, false);
		graph.addEdge(4, 3, 1, false);
		graph.addEdge(4, 5, 1, false);
		graph.addEdge(5, 1, 1, false);
		graph.addEdge(5, 6, 1, false);
		graph.addEdge(3, 2, 1, false);
		graph.addEdge(2, 1, 1, false);
		graph.addEdge(1, 6, 1, false);
		graph.addEdge(6, 7, 1, false);
		graph.addEdge(7, 8, 1, false);
		graph.addEdge(8, 2, 1, false);
		graph.addEdge(8, 9, 1, false);
		graph.addEdge(7, 9, 1, false);
		graph.addEdge(9, 0, 1, false); // Teste 1

		/*
		 * Graph graph = new Graph(30); graph.addEdge(0, 1, 1, false);
		 * graph.addEdge(1, 2, 1, false); graph.addEdge(2, 3, 1, false);
		 * graph.addEdge(3, 9, 1, false); graph.addEdge(9, 15, 1, false);
		 * graph.addEdge(15, 16, 1, false); graph.addEdge(16, 14, 1, false);
		 * graph.addEdge(16, 8, 1, false); graph.addEdge(14, 13, 1, false);
		 * graph.addEdge(13, 12, 1, false); graph.addEdge(13, 18, 1, false);
		 * graph.addEdge(18, 17, 1, false); graph.addEdge(18, 19, 1, false);
		 * graph.addEdge(19, 12, 1, false); graph.addEdge(12, 8, 1, false);
		 * graph.addEdge(8, 4, 1, false); graph.addEdge(4, 7, 1, false);
		 * graph.addEdge(4,10, 1, false); graph.addEdge(10, 5, 1, false);
		 * graph.addEdge(7, 6, 1, false); graph.addEdge(6, 11, 1, false);
		 * graph.addEdge(11, 10, 1, false); graph.addEdge(1, 4, 1, false);
		 * graph.addEdge(9, 8, 1, false);
		 * 
		 * //Teste 3 graph.addEdge(17, 20, 1, false); graph.addEdge(20, 22, 1,
		 * false); graph.addEdge(20, 21, 1, false); graph.addEdge(22, 27, 1,
		 * false); graph.addEdge(21, 23, 1, false); graph.addEdge(23, 24, 1,
		 * false); graph.addEdge(27, 24, 1, false); graph.addEdge(27,25, 1,
		 * false); graph.addEdge(24, 26, 1, false); graph.addEdge(25, 28, 1,
		 * false); graph.addEdge(24, 28, 1, false); graph.addEdge(24, 29, 1,
		 * false); graph.addEdge(29, 26, 1, false); graph.addEdge(22, 21, 1,
		 * false);
		 */

		/*
		 * Graph graph = new Graph(10); //Arvore 1 graph.addEdge(0, 1, 1,
		 * false); graph.addEdge(0, 2, 1, false); graph.addEdge(1, 3, 1, false);
		 * graph.addEdge(1, 4, 1, false); graph.addEdge(3, 6, 1, false);
		 * graph.addEdge(3, 7, 1, false); graph.addEdge(2, 5, 1, false);
		 * graph.addEdge(5, 8, 1, false); graph.addEdge(5, 9, 1, false);
		 */

		/*
		 * Graph graph = new Graph(20); //Arvore 3 graph.addEdge(0, 2, 1,
		 * false); graph.addEdge(0, 3, 1, false); graph.addEdge(0, 4, 1, false);
		 * graph.addEdge(0, 5, 1, false); graph.addEdge(2, 1, 1, false);
		 * graph.addEdge(2, 10, 1, false); graph.addEdge(2, 11, 1, false);
		 * graph.addEdge(10, 19, 1, false); graph.addEdge(10, 18, 1, false);
		 * graph.addEdge(10, 17, 1, false); graph.addEdge(4, 12, 1, false);
		 * graph.addEdge(12, 13, 1, false); graph.addEdge(12, 14, 1, false);
		 * graph.addEdge(13, 16, 1, false); graph.addEdge(14, 15, 1, false);
		 * graph.addEdge(5, 6, 1, false); graph.addEdge(5, 7, 1, false);
		 * graph.addEdge(6, 8, 1, false); graph.addEdge(7, 9, 1, false);
		 */

		LinkedList<Integer> agents = new LinkedList<Integer>();
		agents.add(0);
		agents.add(5);

		SearchNodePatroling node = new SearchNodePatroling(graph, agents);

		long tStart = System.currentTimeMillis();

		AStarSearch star = new AStarSearch(true,true);
		star.search(node);
		
		//BreadthFirstSearch bs = new BreadthFirstSearch(true,true);
		//bs.search(node);
		
		//DepthFirstSearch df = new DepthFirstSearch(-1,true,false);
		//df.search(node);

		//IterativeDeepeningSearch it = new IterativeDeepeningSearch(true,false);
		//it.search(node);
		
		// SMAStarSearch sma = new SMAStarSearch(20);
		// sma.search(node,true);

		// RecursiveBestFirstSearch rc = new RecursiveBestFirstSearch();
		// rc.search(node,true);

		 //UniformCostSearch us = new UniformCostSearch(true,false);
		 //us.search(node);

		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;

		System.out.println(elapsedSeconds);

	}

}
