package exploration.util;

import yaps.util.Pair;

public class NodeMemory2 {
	private static final int NOT_EXPLORED = -1;
	private static final int IN_EXPLORATION = -2;
	
	private int[] nodes;   //map { exit (edge index) -> node id }
	private int currIndex; //next exit to be set (with the target node of the edge)
	
	private boolean unexplored;
	
	public NodeMemory2(int size) {
		nodes = new int[size];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = -1;
		}
		currIndex = 0;
		unexplored = true;
	}
	
	public int getCurrentExit() {
		return currIndex;
	}
	
	public int getNumNeighbors() {
		return this.nodes.length;
	}
	
	public boolean isUnexplored() {
		return this.unexplored;
	}
	
	/**
	 * Sets the mapping between the exit (i.e., index of an out edge of the source node) 
	 * to the target node. Setting the target node to -2 allows its value to be updated with
	 * #addData() method.
	 */
	public void setNode(int exit, int targetNode) {
		assert this.nodes[exit] < 0 || this.nodes[exit] == targetNode;
		unexplored = false;
		nodes[exit] = targetNode;
	}
	
	public void setInExploration(int exit) {
		unexplored = false;
		if (nodes[exit] == -1) {  //this was necessary starting in DFS-5 (because of the short-fwds)
			nodes[exit] = IN_EXPLORATION;
		}
	}

	/**
	 * Increments the "current exit" to the next index that: (i) is mapped to node -1; (ii) is equals 
	 * or higher than the current value of "current exit". (Therefore, if current exit is mapped to -1, 
	 * it does not increment). 
	 */
	public void advanceToNextOpenExit() {
		if (currIndex == nodes.length - 1 || currIndex == -1) {
			currIndex = -1;
		} else {
			while (currIndex < nodes.length && nodes[currIndex] != NOT_EXPLORED) {
				currIndex ++;
			}
			if (currIndex == nodes.length) {
				currIndex = -1;
			}
		}
	}
	
	public boolean isComplete() {
		return currIndex == -1;
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
	
	public String toString() {
		StringBuilder str = new StringBuilder("[ ");
		for (int i = 0; i < nodes.length; i++) {
			if (i == currIndex) {
				str.append('*');	
			}
			str.append(nodes[i]);
			str.append(' ');
		}
		str.append(']');
		return str.toString();
	}

	//TODO: melhorar: e se os dois forem complementares ?
	public Pair<Boolean,Integer> alignAndMerge(NodeMemory2 tempNavData) {
		assert this.nodes.length == tempNavData.nodes.length;
		
		int numNodes = this.nodes.length;
		int index = 0;
		int tempIndex = 0;
		
		loop1: while (true) {
			if (this.nodes[index] >= 0) {
				for (tempIndex = 0; tempIndex < numNodes; tempIndex++) {
					if (this.nodes[index] == tempNavData.nodes[tempIndex]) {
						break loop1; 
					}
				}
			}			
			index ++;
			if (index >= nodes.length) {
				return new Pair<>(false, -1);
			}
		}

		int node;
		for (int i = 0; i < nodes.length; i ++) {
			node = tempNavData.nodes[(tempIndex + i) % numNodes];
			if (node >= 0) {
				this.nodes[(index + i) % numNodes] = node;
			}
		}
		
		this.advanceToNextOpenExit();
		return new Pair<>(true, (index - tempIndex + numNodes) % numNodes);
	}

	
}
