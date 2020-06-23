package exploration.fixed_order;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import exploration.util.NodeMemory;


/**
 * A DFS-based solution for 1 agent solving the <b>exploration problem with fixed-order exits</b>, as described in {@link MultiagentExplorationWithFixedOrder}, which
 * this class extends. <br><br>
 * 
 * The algorithm is an extension of the one implemented in {@link DfsExplorer1}, but without
 * the additional assumption described in that algorithm (i.e. the agent in DfsExplorer2 does know the exit from where it arrived at
 * its current node). Two implementations of the same behavior are given in the code.<br><br>
 * 
 * This algorithm seems to require <b>2x|E|</b> steps from the agent.
 * 
 * @author Pablo A. Sampaio
 */
public class DfsExplorer2 extends MultiagentExplorationWithFixedOrder {
	
	public DfsExplorer2() {
		super("DfsExplorer-2");
	}

	@Override
	public ExplorerAgentFixedOrder[] createTeam(int agents, int totalTime) {
		ExplorerAgentFixedOrder[] team = new ExplorerAgentFixedOrder[agents];
		for (int i = 0; i < agents; i++) {
			team[i] = new DfsExplorer2Agent(System.out);
		}
		return team;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}


class DfsExplorer2Agent extends ExplorerAgentFixedOrder {
	private int destinyNode_dbg; //used for debug only
	
	private Map<Integer,NodeMemory> navData; //mapping { node-> NeighborList }, shared with all agents
                                                //object NeighborList is basically a mapping with entries (exit number -> node id)

	private LinkedList<Integer> predecessorsStack;
	private boolean forwardMove;


	DfsExplorer2Agent(PrintStream teamLog) {
		super(teamLog);
	}

	@Override
	public void onStart(int initialNode, int initialNodeEdges) {
		print("START");
		this.predecessorsStack = new LinkedList<>();
		this.navData = new HashMap<>();
		navData.put(initialNode, new NodeMemory(initialNodeEdges));
		this.destinyNode_dbg = initialNode;
		this.forwardMove = false; //for avoiding setting navData in the first iteration
	}

	@Override
	public void onTurn(int nextTurn) {
		//does nothing
	}

	@Override
	public int onArrivalInNode(int nodeId, int nextTurn, int incomeEdge, int numEdges) {
		print("IN NODE " + nodeId + ", turn " + nextTurn);
		if (destinyNode_dbg != -1 && nodeId != destinyNode_dbg) { print("Error: unexpected neighbor - " + nodeId + " != " + destinyNode_dbg);	}
		
		print("- preds  : " + predecessorsStack);
		print("- navdata: " + navData.toString());

		return impl_1(nodeId, nextTurn, incomeEdge, numEdges); /*/
		return impl_2(nodeId, nextTurn, incomeEdge, numEdges); //*/
	}
	
	public int impl_1(int nodeId, int nextTurn, int entranceEdge, int numEdges) {	
		
		if ( forwardMove ) {
			int lastNode = predecessorsStack.getLast();
			NodeMemory lastNodeNeighbors = navData.get(lastNode);

			print("- navData[node " + lastNode + "] += (exit #" + lastNodeNeighbors.getCurrentPort() + " -> node " + nodeId + ")");
			lastNodeNeighbors.setNodeForCurrentPort(nodeId);
			lastNodeNeighbors.advanceToNextUnexploredPort();
		}
		
		NodeMemory neighbors = navData.get(nodeId);
		boolean newNode = false;
		
		//for an undiscovered node
		if (neighbors == null) {
			print("- new");
			newNode = true;
			neighbors = new NodeMemory(numEdges);
			navData.put(nodeId, neighbors);
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
				return -1; //stays in the same node
			}
			
			//backtracks
			int ancestorNode = predecessorsStack.removeLast();
			print("- complete, backtrack to " + ancestorNode);
			
			int nextExit = neighbors.getPortTo(ancestorNode);
			if (nextExit == -1) { print("Error: map is not symetrical!"); }

			destinyNode_dbg = ancestorNode;
			forwardMove = false;
			return nextExit; //its not always the same as 'entranceEdge' (if forward==false)
		
		} else if (forwardMove && !newNode) {
			//node already visited, incomplete, reached (again) in a forward move
			//backtracks
			int lastNode = predecessorsStack.removeLast();
			print("- already in visit, backtrack to " + lastNode);
			if (entranceEdge == -1) { print("Error: map is not symetrical ?!"); }
			
			destinyNode_dbg = lastNode;
			forwardMove = false;
			return entranceEdge;
			
		} else {
			//a new incomplete node, or an old incomplete node reached by backtrack
			int nextExit = neighbors.getCurrentPort();
			predecessorsStack.add(nodeId);
			print("- forward (exit #" + nextExit + ")");
			
			destinyNode_dbg = -1;
			forwardMove = true;
			return nextExit;
			
		}
		
	}

	public int impl_2(int nodeId, int nextTurn, int incomeEdge, int numExits) {	
		
		if ( forwardMove ) { //arrived in this node by a FORWARD MOVE (i.e., exploring an unknown edge/exit) 
			
			int lastNode = predecessorsStack.getLast();
			NodeMemory lastNodeNeighbors = navData.get(lastNode);

			lastNodeNeighbors.setNodeForCurrentPort(nodeId);
			print("- navData[node " + lastNode + "] += (exit #" + lastNodeNeighbors.getCurrentPort() + " -> node " + nodeId + ")");
			lastNodeNeighbors.advanceToNextUnexploredPort();
		
			NodeMemory neighbors = navData.get(nodeId);
			boolean newNode = false;

			//for a new (undiscovered) node
			if (neighbors == null) {
				print("- new");
				newNode = true;
				neighbors = new NodeMemory(numExits);
				navData.put(nodeId, neighbors);
			}
			
			print("- navData[node " + nodeId + "] += (exit #" + incomeEdge + " -> node " + lastNode + ")");
			if (neighbors.getNode(incomeEdge) != -1 && neighbors.getNode(incomeEdge) != lastNode) { print("Error: inconsistence in income " + incomeEdge + " " + navData.get(nodeId)); }
			neighbors.setNode(incomeEdge, lastNode);

			if (neighbors.isComplete() || !newNode) {
				//backtracks
				print("- complete? " + neighbors.isComplete());
				print("- backtrack to " + lastNode);
				predecessorsStack.removeLast();
				destinyNode_dbg = lastNode;
				if (incomeEdge == -1) { print("Error: map is not symetrical!"); }
				
				forwardMove = false;
				return incomeEdge;
			
			} else {
				//incomplete new node
				predecessorsStack.add(nodeId);
				destinyNode_dbg = -1;
				
				forwardMove = true;
				return neighbors.getCurrentPort();			
			}
		
		} else { //BACKWARD MOVE (i.e., backtracked from a node because it is already partially or fully explored)
			
			NodeMemory neighbors = navData.get(nodeId);
			
			if (neighbors.isComplete()) {			
				if (predecessorsStack.isEmpty()) {
					print("=> FINISHED ALL! Turn " + nextTurn);				
					forwardMove = false;
					return -1; //stays in the same node
				}
				
				//backtracks
				int lastNode = predecessorsStack.removeLast();
				print("- complete, backtrack to " + lastNode);
				destinyNode_dbg = lastNode;
				if (neighbors.getPortTo(lastNode) == -1) { print("Error: map is not symetrical!"); }

				forwardMove = false;
				return neighbors.getPortTo(lastNode); //its not always the same as 'incomeEdge'
			
			} else {
				//an incomplete node
				predecessorsStack.add(nodeId);
				destinyNode_dbg = -1;
				
				forwardMove = true;
				return neighbors.getCurrentPort();				
			}

		}
		
	}

}
