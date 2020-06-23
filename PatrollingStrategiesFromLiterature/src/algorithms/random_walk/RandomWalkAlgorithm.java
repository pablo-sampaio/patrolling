package algorithms.random_walk;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;

public class RandomWalkAlgorithm extends SimpleMultiagentAlgorithm {

	public RandomWalkAlgorithm() {
		super("Random");
	}

	@Override
	public void onSimulationEnd() {
		// does nothing
	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;

		SimpleAgent[] agents = new SimpleAgent[numAgents];

		for (int i = 0; i < numAgents; i++) {
			agents[i] = new RandomWalkAgent(g);
		}

		return agents;
	}

}
