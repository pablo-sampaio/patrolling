package exploration.util;

/**
 * Used in DFS versions 1 to 3.
 * 
 * @author Pablo Sampaio
 */
public class NodeMemory {
	private static final int NOT_EXPLORED = -1;
	private static final int IN_EXPLORATION = -2;
	
	public boolean fmarked; //node was first visited in a forward move (excludes start nodes)
	public int lastPort;
	
	private int[] nodes;   //map { exit (edge index) -> node id }
	private int currPort;  //next exit to be set (with the target node of the edge)
	
	public NodeMemory(int size) {
		fmarked = false;
		lastPort = -1;
		
		nodes = new int[size];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = NOT_EXPLORED;
		}
		currPort = 0;
	}
	
	public int getCurrentPort() {
		return currPort;
	}
	
	public void setNodeForCurrentPort(int nodeId) {
		nodes[currPort] = nodeId;
	}
	
	public int getNumNeighbors() {
		return this.nodes.length;
	}
	
	/*
	 * Sets the mapping between the exit (i.e., index of an out edge of the source node) 
	 * to the target node. Setting the target node to -2 allows its value to be updated with
	 * #addData() method.
	 */
	public void setNode(int exit, int targetNode) {
		nodes[exit] = targetNode;
	}
	
	public void setInExploration(int exit) {
		nodes[exit] = IN_EXPLORATION;
	}

	/**
	 * Increments the "current port" to the next index that: (i) is mapped to node -1; and 
	 * (ii) is equals or higher than its current value. 
	 * If current port is mapped to -1, it does not increment. 
	 */
	public void advanceToNextUnexploredPort() {
		if (currPort == nodes.length - 1 || currPort == -1) {
			currPort = -1;
		} else {
			while (currPort < nodes.length && nodes[currPort] != NOT_EXPLORED) {
				currPort ++;
			}
			if (currPort == nodes.length) {
				currPort = -1;
			}
		}
	}
	
	public boolean isComplete() {
		return currPort == -1;
	}
	
	public int getNode(int exit) {
		return nodes[exit];
	}
	
	public int getPortTo(int nodeId) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] == nodeId) {
				return i;
			}
		}
		return -1;
	}
	
	public void updateWith(int exit, int targetNode) {
		if (this.nodes[exit] == IN_EXPLORATION) {
			this.nodes[exit] = targetNode;
		} else {
			throw new Error("Exit is not in exploration");
		}
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder("[ ");
		for (int i = 0; i < nodes.length; i++) {
			if (i == currPort) {
				str.append('*');	
			}
			str.append(nodes[i]);
			str.append(' ');
		}
		str.append(']');
		return str.toString();
	}

}
