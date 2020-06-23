package yaps.simulator.core;

/**
 * This class holds information about agent's position. 
 * <br>
 * The agent can be either in a node or traversing an edge. Methods inNode() and
 * indEdge() indicate where it is. 
 * 
 * @author Pablo A. Sampaio
 */
public class AgentPosition implements Cloneable {
	int origin;
	int destination; 
	
	//used when the the agent is in an edge
	int distance;		
	
	public AgentPosition(int node) {
		setNodePosition(node);
	}
	
	public AgentPosition(int edgeSource, int edgeTarget, int displacement) {
		setEdgePosition(edgeSource, edgeTarget, displacement);
	}
	
	/**
	 * Used by the simulator when the agent arrives in a node. 
	 */
	void setNodePosition(int node) {
		this.origin = this.destination = node;
		this.distance = 0;
	}

	/**
	 * Used by the simulator when the agent starts to traverse an edge. 
	 */
	void setEdgePosition(int source, int target, int distance) {
		this.origin = source;
		this.destination = target;
		this.distance = distance;
	}
	
	public AgentPosition clone() {
		try {
			return (AgentPosition)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean inNode() {
		return origin == destination;
	}
	
	public boolean inEdge() {
		return origin != destination;
	}

	/**
	 * Indicates the node where the agent is. Can only be used if the agent is in a node.
	 */
	public int getCurrentNode() {
		if (origin == destination) {
			return origin;
		} else {
			throw new Error("Agent is not in a node.");
		}
	}

	/**
	 * It is the node that the agent is "leaving behind".
	 * <br><br> 
	 * The returned node is usually the node from where the agent started to traverse the edge. 
	 * The only exception is the case in which the agent changes direction in the middle of the edge
	 * (in this case, the returned node is the previous destination).
	 * <br><br>
	 * Can only be called if the agent is in an edge.    
	 */
	public int getOrigin() {
		if (origin != destination) {
			return origin;
		} else {
			throw new Error("Agent is not in an edge.");
		}
	}
	
	/**
	 * The node to where the agent is going.
	 * <br><br> 
	 * Can only be called if the agent is in an edge.    
	 */
	public int getDestination() {
		if (origin != destination) {
			return destination;
		} else {
			throw new Error("Agent is not in an edge.");
		}
	}

	/**
	 * Indicates how far is the agent from the node returned by getOrigin().  
	 */
	public int getDistance() {
		if (origin != destination) {
			return distance;
		} else {
			throw new Error("Agent is not in an edge.");
		}
	}
	
	public String toString() {
		if (inNode()) {
			return "node(" + getCurrentNode() + ")";
		} else {
			return "edge(" + getOrigin() + ", " + getDestination() 
					+ ", dist=" + getDistance() + ")";
		}
	}
	
}
