package algorithms.rodrigo.refactoring;

import java.io.PrintStream;
import java.util.List;
import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;

public class ExtendedZeroRangeAgent<T> extends ZeroRangeAgent<T> {

	public ExtendedZeroRangeAgent(AgentModule<T> agentModule, Graph graph,
			NodesMemories<NodesMemories<T>> ambientMemory, boolean synchronizeAll) {
		super(agentModule, ambientMemory, graph, synchronizeAll);
		
	}
	
	public ExtendedZeroRangeAgent(PrintStream teamLog, AgentModule<T> agentModule, Graph graph,
			NodesMemories<NodesMemories<T>> ambientMemory, boolean synchronizeAll) {
		super(teamLog, agentModule, ambientMemory, graph, synchronizeAll);
		
	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		int currentNode = position.getCurrentNode();
		NodesMemories<T> currentNodeMemory = ambientMemory.get(currentNode);
		int targetNode;
		
		// get neighbors
		List<Integer> nodeNeighbors = graph.getSuccessors(currentNode);
		
		agentModule.synchronize(nodeNeighbors, currentNodeMemory, agentMemory, currentNode, synchronizeAll); // synchronize the memory of the agent with the memory of the node

		targetNode = agentModule.decisionRule(nodeNeighbors, currentNodeMemory, agentMemory, time);
		
		// update current node
		agentModule.updateRule(currentNode, currentNodeMemory, agentMemory, time);

		// update target node (on both agent memory and node memory) before leaving current node
		agentModule.updateRule(targetNode, currentNodeMemory, agentMemory, time);
		
		return targetNode;
	}

}
