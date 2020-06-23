package algorithms.rodrigo.refactoring;

import java.util.List;

import algorithms.rodrigo.NodesMemories;

public abstract class AgentModule<T> {
	
	public AgentModule() {
		
	}
	
	public abstract void initNodesMemory(NodesMemories<T> nodeMemory);
	
	public abstract int decisionRule(List<Integer> neighbors, NodesMemories<T> nodesMemory, NodesMemories<T> agentMemory, int time);
	
	public abstract void updateRule(int currentNode, NodesMemories<T> nodesMemory, NodesMemories<T> agentMemory, int time);
	
	public abstract void synchronize(List<Integer> neighbors, NodesMemories<T> nodeMemory, NodesMemories<T> agentMemory, int currentNode, boolean all);

}
