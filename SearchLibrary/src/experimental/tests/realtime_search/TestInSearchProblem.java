package experimental.tests.realtime_search;

import java.io.IOException;

import experimental.realtime_search.RealtimeSearchMethod;
import experimental.realtime_search.RealtimeSearchTeam;
import experimental.realtime_search.basic.LRTAStar;
import experimental.realtime_search.basic.RTAStar;
import experimental.realtime_search.basic.Vaw;
import yaps.graph.Graph;
import yaps.simulator.core.Simulator;


public class TestInSearchProblem {

	public static void main(String[] args) throws IOException {
		Graph graph = createGraph();
		int[] agentsInitialPositions = new int[]{ 0 }; //1 agent
		
		int NUM_EXECUTIONS = 3;
		int totalTime = 18;

		RealtimeSearchMethod searchRule = new RTAStar(graph, 2, true, 5);
		RealtimeSearchTeam algorithm = new RealtimeSearchTeam(searchRule, 5);

		System.out.println("ALGORITHM: " + algorithm.getName() + "\n");

		for (int i = 1; i <= NUM_EXECUTIONS; i ++) {
			Simulator simulator = new Simulator();

			simulator.setGraph(graph);
			simulator.setAgentsInitialNodes(agentsInitialPositions);
			simulator.setTotalTime(totalTime);
			simulator.setAlgorithm(algorithm);

			System.out.printf("\n === RUN #%d === \n\n", i);
			
			simulator.run();
			
			algorithm = new RealtimeSearchTeam(searchRule, 5, algorithm.getNodeValues());
		}
		
	}

	private static Graph createGraph() {
		Graph g = new Graph(6);
		g.addDirectedEdge(0, 1, 2);
		g.addDirectedEdge(0, 2, 2);
		g.addDirectedEdge(1, 2, 2);
		g.addDirectedEdge(1, 3, 3);
		g.addDirectedEdge(2, 4, 3);
		g.addDirectedEdge(2, 3, 2);
		g.addDirectedEdge(4, 3, 3);
		g.addDirectedEdge(3, 5, 1);
		g.addDirectedEdge(4, 5, 5);
		return g;
	}


}
