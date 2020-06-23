package algorithms.machado.cr;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;

public class ConscientiousReactiveAlgorithm extends SimpleMultiagentAlgorithm {

	public ConscientiousReactiveAlgorithm() {
		super("CR");
	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		CrAgent[] agents = new CrAgent[positions.length];
		for (int i = 0; i < agents.length; i++) {
			agents[i] = new CrAgent(g, i, positions[i].getCurrentNode());
		}
		return agents;
	}

	@Override
	public void onSimulationEnd() {
		// does nothing
	}

}
