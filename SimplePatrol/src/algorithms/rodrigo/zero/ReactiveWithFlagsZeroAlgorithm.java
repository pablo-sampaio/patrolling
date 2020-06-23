package algorithms.rodrigo.zero;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;
import algorithms.rodrigo.NodesMemories;

public class ReactiveWithFlagsZeroAlgorithm extends SimpleMultiagentAlgorithm {
	
	private NodesMemories<int[]> blackboard;
	private boolean updateAll;
	private boolean updateBeforeLeaving;
	
	public ReactiveWithFlagsZeroAlgorithm(String name, boolean synchronizeAllNodes, boolean updateTargetNodeBeforeLeaving) {
		super((updateTargetNodeBeforeLeaving? "EZr" : "Zr") + "(" + name + "," +(synchronizeAllNodes? "g" : "n") + ")" );
		this.updateAll = synchronizeAllNodes;
		this.updateBeforeLeaving = updateTargetNodeBeforeLeaving;
	}

	@Override
	public void onSimulationEnd() {

	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;
		
		blackboard = new NodesMemories<int[]>(g.getNumNodes());

		for (int i = 0; i < blackboard.size(); i++) {
			blackboard.set(i, new int[g.getNumNodes()]);
		}

		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new ReactiveWithFlagsZeroAgent(g, blackboard, updateAll, updateBeforeLeaving);
		}
		
		return agents;
	}
}
