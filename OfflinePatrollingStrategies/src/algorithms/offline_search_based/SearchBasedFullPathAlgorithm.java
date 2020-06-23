package algorithms.offline_search_based;

import java.util.LinkedList;
import java.util.List;

import search_library.AbstractSearchNode;
import search_library.methods.AStarSearch;
import search_library.methods.BreadthFirstSearch;
import search_library.methods.DepthFirstSearch;
import search_library.methods.IterativeDeepeningSearch;
import search_library.methods.RecursiveBestFirstSearch;
import search_library.methods.SMAStarSearch;
import search_library.methods.UniformCostSearch;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.FullPathAlgorithm;
import yaps.util.lazylists.LazyList;

/**
 * This class represents a implementation of the FullPathAlgorithm for the
 * search problem.<br>
 * In this class simulator can use any of the search algorithms from the
 * searching library.
 * 
 * @author Alison Carrera
 *
 */

public class SearchBasedFullPathAlgorithm implements FullPathAlgorithm {
	private Graph graph;
	private AgentPosition[] initialInfo;
	private SearchNodePatroling patrol;
	private AbstractSearchNode result;
	private int totalTime;
	private AlgorithmTypes type;

	public SearchBasedFullPathAlgorithm(AlgorithmTypes type) {
		this.type = type;

	}

	public String getName() {
		return "search";
	}

	@Override
	public void onSimulationStart(Graph graph, AgentPosition[] initialInfo, int totalTime) {
		this.graph = graph;
		this.initialInfo = initialInfo;
		this.totalTime = totalTime;

		LinkedList<Integer> agents = new LinkedList<Integer>();

		for (int i = 0; i < initialInfo.length; i++) {
			agents.add(this.initialInfo[i].getCurrentNode());
		}

		patrol = new SearchNodePatroling(this.graph, agents);

		// Execute the specified search algorithm.

		if (type == AlgorithmTypes.A_STAR) {

			AStarSearch star = new AStarSearch();
			result = star.search(patrol);

		} else if (type == AlgorithmTypes.BFS) {
			BreadthFirstSearch breadth = new BreadthFirstSearch();
			result = breadth.search(patrol);

		} else if (type == AlgorithmTypes.DFS) {

			DepthFirstSearch dfs = new DepthFirstSearch();
			result = dfs.search(patrol);

		} else if (type == AlgorithmTypes.ITERATIVE_DEEPENING_SEARCH) {

			IterativeDeepeningSearch ite = new IterativeDeepeningSearch();
			result = ite.search(patrol);

		} else if (type == AlgorithmTypes.RECURSIVE_BFS) {

			RecursiveBestFirstSearch rc = new RecursiveBestFirstSearch();
			result = rc.search(patrol);

		} else if (type == AlgorithmTypes.SMA) {

			SMAStarSearch sma = new SMAStarSearch(1000);
			result = sma.search(patrol);

		} else if (type == AlgorithmTypes.UNIFORM_COST_SEARCH) {

			UniformCostSearch uni = new UniformCostSearch();
			result = uni.search(patrol);

		}

	}

	@Override
	public List<Integer> getAgentTrajectory(int agent) {

		List<AbstractSearchNode> result2 = result.getSolutionPath();

		List<Integer> path = new LinkedList<Integer>();

		for (AbstractSearchNode showResult : result2) {

			SearchNodePatroling temp = (SearchNodePatroling) showResult;
			int node = temp.getAgentNode().get(agent);
			path.add(node);

		}

		// PAS:Ajeitar para grafos valorados.

		List l = LazyList.toLazyList(path).repeatUntilSize(totalTime);

		System.out.println(" >> trajectory: " + l);

		return path;

	}

}
