package algorithms.grav_centralized;

import java.util.Locale;

import algorithms.grav_centralized.core.ForceCombination;
import algorithms.grav_centralized.core.ForcePropagation;
import algorithms.grav_centralized.core.MassGrowth;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.core.Algorithm;
import yaps.util.IdlenessManager;

public class GravitationalAgentsAlgorithm implements Algorithm {
	private String name;
	private IdlenessManager nodeIdlenesses;
	private GravityManager gravForces;
	
	public GravitationalAgentsAlgorithm(ForcePropagation forceProp,
			MassGrowth growth, double distExponent, ForceCombination comb) {
		this.gravForces = new GravityManager(forceProp, growth, distExponent, comb);
		this.nodeIdlenesses = new IdlenessManager();
		
	    String exponentStr = String.format(Locale.US, "%1.2f", distExponent);
	    this.name = "grav(" + forceProp + "," + growth + "," + exponentStr + "," + comb + ")";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void onSimulationStart(Graph graph, AgentPosition[] initialInfo, int totalTime) {
		this.gravForces.setup(graph, initialInfo);
		this.nodeIdlenesses.setup(graph);
	}
	
	@Override
	public void onTurn(int nextTurn, AgentTeamInfo team) {
		boolean recalculateGravities = true; //recalculates only for the first agent that is placed in a node
		AgentPosition agPosition;
		int agNextNode;
		
		for (int agentId = 0; agentId < team.getTeamSize(); agentId ++) {
			agPosition = team.getPosition(agentId);
			
			if (agPosition.inNode()) {
				if (recalculateGravities) {
					this.nodeIdlenesses.update(nextTurn, team);
					System.out.println("TURN: " + nextTurn);
					//System.out.println("Idlenesses updated: " + nodeIdlenesses);
					this.gravForces.update(this.nodeIdlenesses);
					recalculateGravities = false;
				}
				agNextNode = this.gravForces.selectGoalNode(agentId, agPosition.getCurrentNode());
				team.actGoto(agentId, agNextNode);
			}
		}
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}
