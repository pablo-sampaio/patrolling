package algorithms.edge_counting;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;

public class EdgeCountingAlgorithm extends SimpleMultiagentAlgorithm {
	
	//private AdjacencyMatrix<Integer> adjacencyMatrix;

	public EdgeCountingAlgorithm(String name){
		super(name);
	}
	
	@Override
	public void onSimulationEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;
		
//		adjacencyMatrix = new AdjacencyMatrix<Integer>(g.getNumNodes());
//		adjacencyMatrix.setAll(0);
		
		//to keep the next edge for each node
		NodesMemories<Integer> blackboard = new NodesMemories<>(g);
		
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			//agents[i] = new EdgeCountingAgent(g, adjacencyMatrix);
			agents[i] = new EdgeCountingAgent2(g, blackboard);
		}
		return agents;
	}

}
