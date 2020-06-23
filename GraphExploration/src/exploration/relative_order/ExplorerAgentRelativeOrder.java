package exploration.relative_order;

import java.io.PrintStream;


public abstract class ExplorerAgentRelativeOrder {
	protected int identifier;

	private PrintStream log;

	public ExplorerAgentRelativeOrder() {
		this.identifier = -1;
		this.log = null;
	}
	
	public ExplorerAgentRelativeOrder(PrintStream teamLog) {
		this.identifier = -1;
		this.log = teamLog;
	}
	
	public int getIdentifier() {
		return this.identifier;
	}
	
	/**
	 * Called by the simulator (as a call back) immediately before starting the simulation. 
	 * Informs the agent the initial node and its number of out edges (=number of exits).
	 */
	public abstract void onStart(int initialNode, int initialNodeEdges);

	/**
	 * Called on each turn, only to inform the agent that a new turn is starting.
	 */
	public abstract void onTurn(int nextTurn);
	
	/**
	 * Called when the agent arrives on a node, after calling "onTurn" for all agents.<br><br>
	 * 
	 * Should return an integer 'n' from the set {0, ..., numEdges} representing that the agent
	 * will cross the 'n'-th edge in relative ordering of the edges that depends
	 * on the last node visited by the agent.<br><br>
	 * 
	 * The edges (exits) are always indexed by a cyclic rotation of a fixed list of neighbors, 
	 * so that the entrance edge (that leads to the node from where the agent came) 
	 * is always 0. <br><br>
	 * 
	 * Returning -1 lets the agent on the same node for one turn.
	 */
	public abstract int onArrivalInNode(int nodeId, int nextTurn, int numEdges);
	
	//used only by MultiagentExplorationWithRelativeOrder
	final void setIdentifier(int id) {
		this.identifier = id;
	}
	
	/**
	 * Prints a message in the log, with an automatic line break in the end.
	 */
	protected synchronized void print(String msg) {
		if (log != null)
			log.printf("[Agent %d]: %s\n", identifier, msg);
	}

}
