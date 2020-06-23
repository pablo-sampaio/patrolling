package algorithms.rodrigo.koenig;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;

public class NodeCountingAlgorithm extends SimpleMultiagentAlgorithm {
	
	private NodesMemories<Integer> blackboard;

	public NodeCountingAlgorithm(String name){
		super(name);
	}
	
	@Override
	public void onSimulationEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;
		
		blackboard = new NodesMemories<Integer>(g.getNumNodes());
		
		for(int i = 0; i < blackboard.size(); i++){
			blackboard.set(i, 0);
		}
		
		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new NodeCountingAgent(g, blackboard);
		}
		return agents;
	}

}
