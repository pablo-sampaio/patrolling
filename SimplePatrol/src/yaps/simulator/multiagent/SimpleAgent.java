package yaps.simulator.multiagent;

import java.io.PrintStream;

import yaps.simulator.core.AgentPosition;


public abstract class SimpleAgent {
	protected int identifier;
	protected AgentPosition position;

	private PrintStream log; //TODO: fazer um log melhor

	public SimpleAgent() {
		this.identifier = -1;
		this.log = null;
	}
	
	public SimpleAgent(PrintStream teamLog) {
		this.identifier = -1;
		this.log = teamLog;
	}
	
	public int getIdentifier() {
		return this.identifier;
	}
	
	public AgentPosition getPosition() {
		return position;
	}
	
	/**
	 * Called immediately before starting the simulation.
	 */
	public abstract void onStart();

	/**
	 * Called on each turn, only to inform the agent that a new turn is starting.
	 */
	public abstract void onTurn(int nextTurn);
	
	/**
	 * Called when the agent arrives on a node, after calling "onTurn" for all agents.
	 * Should return a node that is neighbor to the current node, to indicate the next
	 * destination of the agent. May return the same node to stand still.
	 */
	public abstract int onArrivalInNode(int nextTurn);
	
	//used only by SimpleMultiagentAlgorithm
	final void setIdentifier(int id) {
		this.identifier = id;
	}

	//used only by SimpleMultiagentAlgorithm
	final void setPosition(AgentPosition agentInfo) {
		this.position = agentInfo;
	}
	
	/**
	 * Prints a message in the log.
	 */
	protected void print(String msg) {
		if (log != null)
			log.printf("[Agent %d]: %s\n", identifier, msg);
	}

}
