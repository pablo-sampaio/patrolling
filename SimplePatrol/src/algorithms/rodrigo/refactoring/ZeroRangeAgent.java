package algorithms.rodrigo.refactoring;

import java.io.PrintStream;
import java.util.List;
import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;

public class ZeroRangeAgent<T> extends SimpleAgent {
	
	protected Graph graph;
	protected NodesMemories<NodesMemories<T>> ambientMemory;
	protected NodesMemories<T> agentMemory;
	protected int time = 0;
	protected AgentModule<T> agentModule;
	protected boolean synchronizeAll;

	public ZeroRangeAgent(AgentModule<T> agentModule, NodesMemories<NodesMemories<T>> ambientMemory, Graph graph, boolean synchronizeAll) {
		this.agentModule = agentModule;
		this.graph = graph;
		this.ambientMemory = ambientMemory;
		this.synchronizeAll = synchronizeAll;
	}

	public ZeroRangeAgent(PrintStream teamLog, AgentModule<T> agentModule, NodesMemories<NodesMemories<T>> ambientMemory, Graph graph, boolean synchronizeAll) {
		super(teamLog);
		this.agentModule = agentModule;
		this.graph = graph;
		this.ambientMemory = ambientMemory;
		this.synchronizeAll = synchronizeAll;
	}

	@Override
	public void onStart() {
		this.agentMemory = new NodesMemories<T>(graph.getNumNodes());
	}

	@Override
	public void onTurn(int nextTurn) {
		time++;

	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		int currentNode = position.getCurrentNode();
		NodesMemories<T> currentNodeMemory = ambientMemory.get(currentNode);
		int targetNode;
		
		// get neighbors
		List<Integer> nodeNeighbors = graph.getSuccessors(currentNode);
		
		//sync memories
		agentModule.synchronize(nodeNeighbors, currentNodeMemory, agentMemory, currentNode, synchronizeAll); // synchronize the memory of the agent with the memory of the node

		//get target node from the module decision rule
		targetNode = agentModule.decisionRule(nodeNeighbors, currentNodeMemory, agentMemory, time);
		
		//update memories by using the module's update rule
		agentModule.updateRule(currentNode, currentNodeMemory, agentMemory, time);
		
		return targetNode;
	}

}
