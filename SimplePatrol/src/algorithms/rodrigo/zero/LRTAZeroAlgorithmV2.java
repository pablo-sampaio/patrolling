package algorithms.rodrigo.zero;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;

public class LRTAZeroAlgorithmV2 extends SimpleMultiagentAlgorithm{

	private NodesMemories<int[]> blackboard;
	private int valueUpdateRule;
	private boolean updateAllNodes;
	private boolean updateBeforeLeaving;
	public static final int DEFAULT = 0;
	public static final int WAGNER_RULE = 1;
	public static final int THRUN_RULE = 2;

	public LRTAZeroAlgorithmV2(String name, int valueUpdateRule, boolean synchronizeAllNodes, boolean updateTargetNodeBeforeLeaving) {
		super((updateTargetNodeBeforeLeaving? "EZr2" : "Zr") + "(" + name + "," +(synchronizeAllNodes? "g" : "n") + ")" );
		this.valueUpdateRule = valueUpdateRule;
		this.updateAllNodes = synchronizeAllNodes;
		this.updateBeforeLeaving = updateTargetNodeBeforeLeaving;
	}
	
	public LRTAZeroAlgorithmV2(String name, boolean synchronizeAllNodes, boolean updateTargetNodeBeforeLeaving) {
		super((updateTargetNodeBeforeLeaving? "EZr2" : "Zr") + "(" + name + "," +(synchronizeAllNodes? "g" : "n") + ")" );
		this.valueUpdateRule = DEFAULT;
		this.updateAllNodes = synchronizeAllNodes;
		this.updateBeforeLeaving = updateTargetNodeBeforeLeaving;
	}
	
	@Override
	public void onSimulationEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		
		blackboard = new NodesMemories<int[]>(g.getNumNodes());
		
		int numAgents = positions.length;

		for (int i = 0; i < blackboard.size(); i++) {
			blackboard.set(i, new int[g.getNumNodes()]);
		}

		SimpleAgent[] agents = new SimpleAgent[numAgents];
		for (int i = 0; i < numAgents; i++) {
			agents[i] = new LRTAZeroAgentV2(g, blackboard, valueUpdateRule, updateAllNodes, updateBeforeLeaving);
		}
		return agents;
	}

}
