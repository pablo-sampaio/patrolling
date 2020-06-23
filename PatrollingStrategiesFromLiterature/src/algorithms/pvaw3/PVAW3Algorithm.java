package algorithms.pvaw3;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;


/**
 * @author Rodrigo de Sousa
 */
public class PVAW3Algorithm extends SimpleMultiagentAlgorithm {
	
	private NodesMemories<PVAW3NodeMem> blackboard;
	private double p;
	private boolean leaderTest;
	private boolean herdTest;
	private int type;
	public final static int PVAW3_ZERO = 0;
	public final static int PVAW3 = 1;
	private Graph nGraph;

	public PVAW3Algorithm(Graph nGraph, double p, boolean leaderTest, boolean herdTest, int type) {
		//TODO
		super((type == PVAW3_ZERO? "Zr " : "") + "PVAW3");
		this.nGraph = nGraph;
		this.p = p;
		this.leaderTest = leaderTest;
		this.herdTest = herdTest;
		this.type = type;
	}

	@Override
	public void onSimulationEnd() {

	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int numAgents = positions.length;

		blackboard = new NodesMemories<PVAW3NodeMem>(g.getNumNodes());

		for (int i = 0; i < blackboard.size(); i++) {
			blackboard.set(i, new PVAW3NodeMem());
		}

		SimpleAgent[] agents = new SimpleAgent[numAgents];
		
		switch(type){
		
		case 0:
			for (int i = 0; i < numAgents; i++) {
				agents[i] = new PVAW3AgentZero(g, nGraph, blackboard, p, leaderTest, herdTest);
			}
			
			((PVAW3AgentZero) agents[0]).setLeader(true);
		case 1:
			for (int i = 0; i < numAgents; i++) {
				agents[i] = new PVAW3Agent(g, nGraph, blackboard, p, leaderTest, herdTest);
			}
			
			((PVAW3Agent) agents[0]).setLeader(true);
		}
		
		
		return agents;
	}

}
