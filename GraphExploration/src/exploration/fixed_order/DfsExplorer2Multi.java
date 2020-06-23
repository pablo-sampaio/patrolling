package exploration.fixed_order;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import exploration.util.NodeMemory;
import yaps.util.Pair;


/**
 * The same idea used in {@link DfsExplorer2}, but, in this version, each agent can only write to the node 
 * where it is located (i.e., this is a zero-range agent). When the agent does a forward move 
 * <i>"last-node -> current-node"</i>, it keeps the navigation information <b>(#last-node-exit, current-node)</b> 
 * in a temporary memory of the agent, that is synchronized when it backtracks to "last-node".
 * 
 * @author Pablo A. Sampaio
 */
public class DfsExplorer2Multi extends MultiagentExplorationWithFixedOrder {
	
	public DfsExplorer2Multi() {
		super("DfsExplorer2-multi");
	}

	@Override
	public ExplorerAgentFixedOrder[] createTeam(int agents, int totalTime) {
		ExplorerAgentFixedOrder[] team = new ExplorerAgentFixedOrder[agents];
		Map<Integer,NodeMemory> sharedNavData = new HashMap<>(); 
		
		for (int i = 0; i < agents; i++) {
			team[i] = new DfsExplorer2WithNodeMemAgent(System.out, sharedNavData);
		}
		return team;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}


class DfsExplorer2WithNodeMemAgent extends ExplorerAgentFixedOrder {
	private int destinyNode_dbg; //used for debug only
	
	private Map<Integer,NodeMemory> nodesNavData; //mapping { node-> NeighborList }, shared with all agents
	                                                 //object NeighborListX is basically a mapping { exit -> node id }

	private Map<Integer,Pair<Integer,Integer>> tempNavData;  //complements the mapping above with data acquired by the agent in further nodes
															 //map { node -> (#node-exit, target-node) }; a pair is enough because only one "forward" visit to each "node" may occur
															 //it is synchronized with the memory in "node" when the agent backtracks to it

	private LinkedList<Integer> predecessorsStack;
	private boolean forwardMove;
	private int lastExitEdge; //used only in forward moves


	DfsExplorer2WithNodeMemAgent(PrintStream teamLog, Map<Integer,NodeMemory> sharedNodeData) {
		super(teamLog);
		this.nodesNavData = sharedNodeData;
	}

	@Override
	public void onStart(int initialNode, int initialNodeEdges) {
		print("START");
		this.lastExitEdge = -1;
		this.predecessorsStack = new LinkedList<>();
		this.nodesNavData.put(initialNode, new NodeMemory(initialNodeEdges));
		this.tempNavData = new HashMap<>();
		this.destinyNode_dbg = initialNode;
		this.forwardMove = false; //for avoiding setting navData in the first iteration
	}

	@Override
	public void onTurn(int nextTurn) {
		//does nothing
	}

	@Override
	public int onArrivalInNode(int nodeId, int nextTurn, int entranceEdge, int numEdges) {
		print("IN NODE " + nodeId + ", turn " + nextTurn);
		if (destinyNode_dbg != -1 && nodeId != destinyNode_dbg) { print("Error: unexpected neighbor - " + nodeId + " != " + destinyNode_dbg);	}
		
		print("- preds  : " + predecessorsStack);
		print("- navdata: " + nodesNavData.toString());

		if ( forwardMove ) {
			int lastNode = predecessorsStack.getLast();
			print("- temp[node " + lastNode + "] += (exit #" + lastExitEdge + " -> node " + nodeId + ")");
			storeTempNavData(lastNode, this.lastExitEdge, nodeId);
		} else {
			print("- synchronizing data for " + nodeId);
			syncTempNavData(nodeId);
			print("- navdata updated: " + nodesNavData.toString());
		}
		
		NodeMemory neighbors = nodesNavData.get(nodeId);
		boolean newNode = false;
		
		//for an undiscovered node
		if (neighbors == null) {
			print("- new");
			newNode = true;
			neighbors = new NodeMemory(numEdges);
			nodesNavData.put(nodeId, neighbors);
			//tempNavData.put(nodeId, new int[numEdges]); //always create a 'temp' entry together with the main entry
		}
		
		if (forwardMove) {
			int lastNode = predecessorsStack.getLast();
			print("- navData[node " + nodeId + "] += (exit #" + entranceEdge + " -> node " + lastNode + ")");
			if (neighbors.getNode(entranceEdge) != -1 && neighbors.getNode(entranceEdge) != lastNode) { print("Error: inconsistence in income " + entranceEdge + " " + neighbors); }
			neighbors.setNode(entranceEdge, lastNode);
			neighbors.advanceToNextUnexploredPort();
		}

		if (neighbors.isComplete()) {
			
			if (predecessorsStack.isEmpty()) {
				print("=> FINISHED ALL! Turn " + nextTurn);
				forwardMove = false;
				lastExitEdge = -1;
				return -1; //stays in the same node
			}
			
			//backtracks
			int ancestorNode = predecessorsStack.removeLast();
			print("- complete, backtrack to " + ancestorNode);
			
			int nextExit = neighbors.getPortTo(ancestorNode);
			if (nextExit == -1) { print("Error: map is not symetrical!"); }

			destinyNode_dbg = ancestorNode;
			forwardMove = false;
			lastExitEdge = -1;
			return nextExit; //its not always the same as the 'entranceEdge' (if forward==false)
		
		} else if (forwardMove && !newNode) {
			//node already visited, incomplete, reached (again) in a forward move
			//backtracks
			int lastNode = predecessorsStack.removeLast();
			print("- already in visit, backtrack to " + lastNode);
			if (entranceEdge == -1) { print("Error: map is not symetrical ?!"); }
			
			destinyNode_dbg = lastNode;
			forwardMove = false;
			lastExitEdge = -1;
			return entranceEdge;
			
		} else {
			//a new incomplete node, or an old incomplete node reached by backtrack
			int nextExit = neighbors.getCurrentPort();
			predecessorsStack.add(nodeId);
			
			neighbors.setNodeForCurrentPort(-2); //special code to tell that this exit/edge is being explored
			neighbors.advanceToNextUnexploredPort();
			lastExitEdge = nextExit;
			print("- forward (exit #" + nextExit + ")");
			
			destinyNode_dbg = -1;
			forwardMove = true;
			return nextExit;
			
		}
		
	}

	private void storeTempNavData(int node, int nodeExit, int targetNode) {
		this.tempNavData.put(node, new Pair<Integer,Integer>(nodeExit, targetNode));
	}

	private void syncTempNavData(int node) {
		NodeMemory nodeData = this.nodesNavData.get(node);
		Pair<Integer,Integer> pair = this.tempNavData.get(node);
		if (pair != null) {
			nodeData.updateWith(pair.first, pair.second);
			this.tempNavData.remove(node);
		}
	}

	/*private void storeTempNavData(int node, int nodeExit, int targetNode) {
		int[] tempNeighbors = this.tempNavData.get(node);
		tempNeighbors[nodeExit] = targetNode;
	}

	private void syncTempNavData(int node) {
		int[] tempNeighbors = this.tempNavData.get(node);
		NeighborListX neighbors = this.nodesNavData.get(node);
		neighbors.updateWith(tempNeighbors);
	}*/	

}
