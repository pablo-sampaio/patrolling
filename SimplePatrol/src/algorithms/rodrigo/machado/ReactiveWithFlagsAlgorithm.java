package algorithms.rodrigo.machado;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;

public class ReactiveWithFlagsAlgorithm extends SimpleMultiagentAlgorithm {
	
	private NodesMemories<Integer> blackboard;
	
	public ReactiveWithFlagsAlgorithm(String name) {
		super(name);
	}

	@Override
	public void onSimulationEnd() {

	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;
		
		blackboard = new NodesMemories<Integer>(g.getNumNodes());

		for (int i = 0; i < blackboard.size(); i++) {
			blackboard.set(i, 0);
		}

		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new ReactiveWithFlagsAgent(g, blackboard);
		}
		return agents;
	}
}
