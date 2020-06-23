package algorithms.rodrigo.refactoring;

import java.io.PrintStream;
import java.util.List;

import algorithms.rodrigo.NodesMemories;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;

public class OneRangeAgent<T> extends SimpleAgent {
	
	private Graph graph;
	private AgentModule<T> agentModule;
	private NodesMemories<T> nodesMemory;
	private NodesMemories<T> agentMemory;
	private int time = 0;

	public OneRangeAgent(AgentModule<T> agentModule, NodesMemories<T> nodesMemory, Graph graph) {
		this.agentModule = agentModule;
		this.nodesMemory = nodesMemory;
		this.graph = graph;
	}

	public OneRangeAgent(PrintStream teamLog, AgentModule<T> agentModule, NodesMemories<T> nodesMemory, Graph graph) {
		super(teamLog);
		this.agentModule = agentModule;
		this.nodesMemory = nodesMemory;
		this.graph = graph;
	}

	@Override
	public void onStart() {
		this.agentMemory = new NodesMemories<T>(this.graph.getNumNodes());
	}

	@Override
	public void onTurn(int nextTurn) {
		time++;
	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		
		int currentNode = position.getCurrentNode();
		
		// get neighbors
		List<Integer> neighbors = graph.getSuccessors(currentNode);
		
		//get target node from the module decision rule
		int targetNode = agentModule.decisionRule(neighbors, nodesMemory, agentMemory, time);
		
		//update memories by using the module's update rule
		agentModule.updateRule(currentNode, nodesMemory, agentMemory, time);
		
		return targetNode;
	}

}
