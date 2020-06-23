package yaps.util;

import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.multiagent.SimpleAgent;

/**
 * This class can be used by algorithms to calculate the "instantaneous idlenesses" 
 * of the nodes in each turn. <br><br>
 * 
 * Typical setup: <code><br><br><b>
 *   idlenesses = new IdlenessManager();<br>
 *   idlenesses.setup(graph);
 * </code><br><br></b>
 * 
 * Then, on each turn (at least when an agent arrives in a node), call: <code><br><br><b>
 *   idlenesses.update(currentTurn); <br></b>
 *   
 * @author Pablo A. Sampaio
 */
public class IdlenessManager {
	private int[] idleness; //instantaneous idlenesses per node
	private int lastUpdate; //the last time the idlenesses were updated
	
	public IdlenessManager() {
		this.lastUpdate = Integer.MIN_VALUE;
	}
	
	/**
	 * Returns the instantaneous idleness of the given node (in the turn given
	 * in the last call to update()).  
	 */
	public synchronized int getCurrentIdleness(int node) {
		return idleness[node];
	}
	
	/**
	 * Should be called prior to using update() or getCurrentIdlenesses(). 
	 */
	public void setup(Graph g) {
		this.idleness = new int[g.getNumNodes()];
		this.lastUpdate = 0;
	}
	
	/**
	 * Updates the current idlenesses of all nodes. Can be called on every turn, or 
	 * only in turns where at least one agent is placed in a node.
	 * Should be used only by centralized algorithms.
	 */
	public void update(int turn, AgentTeamInfo teamInfo) {
		int numNodes = idleness.length;
		boolean[] hasAgentIn = new boolean[numNodes]; 

		AgentPosition agPosition;
		for (int ag = 0; ag < teamInfo.getTeamSize(); ag++) {
			agPosition = teamInfo.getPosition(ag);
			if (agPosition.inNode()) {
				hasAgentIn[agPosition.getCurrentNode()] = true;
			}
		}
		
		int deltaTime = turn - this.lastUpdate;
		for (int node = 0; node < numNodes; node++) {
			if (hasAgentIn[node]) {
				this.idleness[node] = 1;  //can be used 0 or 1 (there are different advantages in each approach)
			} else {
				this.idleness[node] += deltaTime;
			}
		}
		
		this.lastUpdate = turn;
	}

	/**
	 * Should be called by each agent, only when the agent has arrived in a node.
	 * It has this behaviour: <ol>
	 * <li> On the first call (in that turn) increments the idlenesses of all nodes, but "resets"
	 * the idleness of the node of the agent. 
	 * <li> On the following calls (done by other agents), simply "resets" the idleness of the node of 
	 * each agent.
	 * </ol>
	 */
	public synchronized void updateForAgent(int turn, int agentCurrNode) {
		if (this.lastUpdate > turn) {
			throw new Error("Invalid update.");
		
		} else if (this.lastUpdate < turn) {
			int deltaTime = turn - this.lastUpdate;
			
			int numNodes = idleness.length;
			for (int node = 0; node < numNodes; node++) { //increments deltaTime in the idlenesses of all nodes
				this.idleness[node] += deltaTime;
			}
			
			this.idleness[agentCurrNode] = 1;
			this.lastUpdate = turn; //this ensures that it enters in this block only once per turn
		
		} else {
			this.idleness[agentCurrNode] = 1;
		
		}
	}
	
	/**
	 * Returns the pair { min-idleness, max-idleness }.
	 */
	public synchronized double[] getBoundsOfIdlenesses() {
		double[] boundIdlenesses = new double[]{ Double.MAX_VALUE, 0.0d }; 
		int currIdleness;
		
		for (int v = 0; v < idleness.length; v++) {
			currIdleness = this.idleness[v];
			if (currIdleness < boundIdlenesses[0]) {
				boundIdlenesses[0] = currIdleness;
			}
			if (currIdleness > boundIdlenesses[1]) {
				boundIdlenesses[1] = currIdleness;
			}
		}
		return boundIdlenesses;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder("[ ");
		for (int node = 0; node < idleness.length; node++) {
			builder.append(idleness[node]);
			builder.append(", ");
		}
		builder.append("]");
		return builder.toString();
	}
	
}
