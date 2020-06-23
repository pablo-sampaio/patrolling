package yaps.simulator.multiagent;

import java.util.LinkedList;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.core.Algorithm;


/**
 * A patrolling algorithm that creates and manages agents that run in separate threads.
 * 
 * @author Pablo A. Sampaio
 */
public abstract class ThreadedMultiagentAlgorithm implements Algorithm {
	private String strategyName;
	private ThreadAgent[] agents;

	/**
	 * An unique name that identifies the algorithm and its parametrization
	 * (which we may call the "multiagent strategy"). 
	 */
	public String getName() {
		return strategyName;
	}

	/**
	 * This method must be overridden by subclasses (i.e., specific algorithms) to
	 * return an array of appropriate subclasses of AbstractAgent. 
	 */
	public abstract ThreadAgent[] createTeam(AgentPosition[] initialInfo, Graph g, int time);


	@Override
	public final void onSimulationStart(Graph graph, AgentPosition[] initialPos, int totalTime) {
		int numAgents = initialPos.length;
		
		this.agents = createTeam(initialPos, graph, totalTime);
		if (this.agents.length != numAgents) {
			throw new Error("Subclass returned a wrong number of agents.");
		}
		
		for (int id = 0; id < numAgents; id++) {
			this.agents[id].setTurnInfo(0, initialPos[id]);
			this.agents[id].setIdentifier(id);
			this.agents[id].startAgent();
		}		
	}

	@Override
	public final void onTurn(int nextTurn, AgentTeamInfo teamInfo) {
		int numAgents = teamInfo.getTeamSize();
		AgentPosition position;
		
		//agents that must act on this turn (e.g., because they are in a node)
		LinkedList<ThreadAgent> pendingAgents = new LinkedList<ThreadAgent>();
		
		for (int id = 0; id < numAgents; id++) {
			position = teamInfo.getPosition(id);
			if (position.inNode()) {
				pendingAgents.add(this.agents[id]);
			}
			this.agents[id].setTurnInfo(nextTurn, position);
		}
		
		LinkedList<ThreadAgent> nextPendingAgents = new LinkedList<ThreadAgent>(); //for the next iteration
		LinkedList<ThreadAgent> temp;
		
		while (!pendingAgents.isEmpty()) {
			nextPendingAgents.clear();
			
			for (ThreadAgent agent : pendingAgents) {
				if (agent.acted) {
					if (agent.nextAction == -1) {
						teamInfo.actStop(agent.identifier);
					} else {
						teamInfo.actGoto(agent.identifier, agent.nextAction);
					}
				} else {
					nextPendingAgents.add(agent);
				}
			}
			temp = pendingAgents;
			pendingAgents = nextPendingAgents;
			nextPendingAgents = temp;

			try {
				// wait some time
				Thread.sleep(50);  //0.05s
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("End of iteration for turn " + nextTurn);
	}

	@Override
	public final void onSimulationEnd() {
		System.out.println("Requesting to stop agents...");
		for (int id = 0; id < this.agents.length; id++) {
			this.agents[id].stopAgent();
		}
		System.out.println("Waiting agents to finish...");
		for (int id = 0; id < this.agents.length; id++) {
			this.agents[id].join();
		}
		System.out.println("All agents finished.");
	}
	
}
