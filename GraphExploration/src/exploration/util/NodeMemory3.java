package exploration.util;



public class NodeMemory3 {
	private int[] nodes;   			//a mappping { exit (edge index) -> node id }
	private NodeExplorationStatus[] statusOfNodes; 	//a mapping { exit (edge index) -> status of node }
	
	private NodeExplorationStatus status;
	private int currToExpand; 		//next exit to be expanded (to find out the neighbor)
	private int currToExplore; 		//next exit to be explored
	
	public NodeMemory3(int size) {
		nodes = new int[size];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = -1;
		}
		currToExpand = 0;
		currToExplore = 0;
		statusOfNodes = new NodeExplorationStatus[size];
		status = NodeExplorationStatus.NOT_EXPANDED;
	}
	
	public boolean expansionStarted() {
		return status != NodeExplorationStatus.NOT_EXPANDED;
	}
	
	public int getNode(int exit) {
		return nodes[exit];
	}
	
	public int getExitTo(int nodeId) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] == nodeId) {
				return i;
			}
		}
		return -1;
	}

	public NodeExplorationStatus getStatus(int exit) {
		return statusOfNodes[exit];
	}
	
	/**
	 * Sets the mapping between the exit (i.e., index of an out edge of the source node) 
	 * to the target node. Setting the target node to -2 allows its value to be updated with
	 * #addData() method.
	 */
	public void setNode(int exit, int targetNode) {
		nodes[exit] = targetNode;
		status = NodeExplorationStatus.PARTIALLY_EXPANDED;
	}

	/**
	 * Increments the "exit" to the an index that: (i) is mapped to -1; (ii) is equals 
	 * to or higher than the current value of "current exit". (Therefore, if current exit
	 * is mapped to -1, it does not increment). 
	 */
	public int nextUnexpandedExit() {
		while (currToExpand < nodes.length && nodes[currToExpand] != -1) {
			currToExpand ++;
		}
		if (currToExpand == nodes.length) {
			currToExpand = -1;
			status = NodeExplorationStatus.EXPANDED_PARTIALLY_EXPLORED;
		}
		return currToExpand;
	}
	
	public int nextExitWithStatus(NodeExplorationStatus status, boolean updatePointerToNext) {
		int size = statusOfNodes.length;
		for (int i = 0; i < size; i++) {
			if (this.statusOfNodes[(this.currToExplore+i)%size] == status) {
				if (updatePointerToNext) {
					currToExplore = i + 1;
				}
				return i;
			}
		}
		return -1;
	}
	
	public void setStatus(int exit, NodeExplorationStatus status) {
		statusOfNodes[exit] = status;
	}
	
	public boolean fullyExpanded() {
		if (currToExpand == -1 ) {
			return true;
		} else {
			return nextUnexpandedExit() == -1;
		}
	}
	
	
//	public int alignThisTo(NeighborList2 other) {
//		return -1;
//	}
	
	
	public String toString() {
		StringBuilder str = new StringBuilder("[ ");
		for (int i = 0; i < nodes.length; i++) {
			if (i == currToExpand) {
				str.append('*');	
			}
			if (nodes[i] >= 0) {
				str.append("n" + nodes[i]);	
			} else {
				str.append("[" + nodes[i] + "]");
			}
			str.append("(" + statusOfNodes[i] + ")");
			str.append(' ');
		}
		str.append(']');
		return str.toString();
	}
	
}
