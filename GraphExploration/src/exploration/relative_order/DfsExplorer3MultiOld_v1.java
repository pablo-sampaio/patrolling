package exploration.relative_order;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import exploration.util.NodeMemory;
import yaps.util.Pair;


/**
 * The same idea used in DfsExplorer3, but, in this version, each agent can only write to the node where it 
 * is located (i.e. they are really implemented as zero-range local agents). This allows the use of multiple 
 * agents working cooperatively (although the cooperation is not the most efficient one).<br><br> 
 * 
 * When the agent does a forward move "last-node -> current-node", upon arrival at current-node, as it cannot 
 * write in last-node, the agent keeps the navigation information "(#exit, current-node)" in a temporary 
 * memory, that is synchronized to last-node's memory when the agent backtracks.<br>
 * 
 * This version is prior to ECEP.
 * 
 * @author Pablo A. Sampaio
 */
public class DfsExplorer3MultiOld_v1 extends MultiagentExplorationWithRelativeOrder {
	
	public DfsExplorer3MultiOld_v1() {
		super("DfsExplorer-3-multi-old-1");
	}

	@Override
	public ExplorerAgentRelativeOrder[] createTeam(int agents, int totalTime) {
		ExplorerAgentRelativeOrder[] team = new ExplorerAgentRelativeOrder[agents];
		Map<Integer,NodeMemory> sharedNavData = new HashMap<>(); 
		
		for (int i = 0; i < agents; i++) {
			team[i] = new DfsExplorer3AgentMultiOld1(System.out, sharedNavData);
		}
		return team;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}


class DfsExplorer3AgentMultiOld1 extends ExplorerAgentRelativeOrder {
	private int destinyNode_dbg; //used for debug only
	
	private Map<Integer,NodeMemory> navData; //mapping { node --> indexed-exits } 
	                                            //the 'indexed-exits' identify the edges, but are also a mapping { canonical-exit-index --> target-node }
											    //the canonical exit indexes are the exit indexes when the agent first arrives in the node
											    //if the agent gets back to that node later coming from different node, the indexes change and it is necessary to convert

	private Map<Integer,Pair<Integer,Integer>> tempNavData;  //complements the mapping above with data acquired by the agent in further nodes
															 //map { node -> (#node-exit, target-node) }; a pair is enough because only one "forward" visit to each "node" may occur
															 //it is synchronized with the memory in "node" when the agent backtracks to it

	private LinkedList<Pair<Integer, Integer>> predecessorsStack; //pairs (lastNode, canonExit) 
	                                                              //indicates that, from 'lastNode', the agent crossed the edge 'canonExit'

	/* This agent requires O(V) memory space.
	 * For 1 agent, both tempNavData and predecessorsStack can easily be stored in each node,
	 * reducing the requirement for space in agent's memory to O(max-degree). 
	 * For multiple agents, adaptation to store these data in the node are more tricky but
	 * are also possible. See the class with similar name. 
	 */
	
	private boolean forwardMove;
	private int nextNodeReferenceExit; //this exit index is used to cyclically shift the current relative exit indexes of the next node, thus converting from the current relative indexing to the canonical indexing in that node 
									   //if the next move is a backtrack (forwardMove == false), keeps the canonical exit used when departed last time from next node (this value is obtained from the stack)
	                                   //otherwise, it is zero (so canonical indexing = relative indexing)


	DfsExplorer3AgentMultiOld1(PrintStream teamLog, Map<Integer,NodeMemory> sharedNodeData) {
		super(teamLog);
		this.navData = sharedNodeData;
	}

	@Override
	public void onStart(int initialNode, int initialNodeEdges) {
		print("START");
		this.predecessorsStack = new LinkedList<>();
		this.navData.put(initialNode, new NodeMemory(initialNodeEdges));
		this.tempNavData = new HashMap<>();
		this.destinyNode_dbg = initialNode;
		this.forwardMove = false; //for avoiding setting navData in the first iteration
		this.nextNodeReferenceExit = 0;
	}

	@Override
	public void onTurn(int nextTurn) {
		//does nothing
	}
	
	@Override
	public int onArrivalInNode(int nodeId, int nextTurn, int numEdges) {
		print("IN NODE " + nodeId + ", turn " + nextTurn);
		if (destinyNode_dbg != -1 && nodeId != destinyNode_dbg) { print("Error: unexpected neighbor - " + nodeId + " != " + destinyNode_dbg);	}
		
		print("- preds  : " + predecessorsStack);
		print("- navdata: " + navData.toString());

		NodeMemory nodeData = navData.get(nodeId);
		boolean unexplored = false;
		
		if (nodeData == null) {
			unexplored = true;
			nodeData = new NodeMemory(numEdges);
			this.navData.put(nodeId, nodeData);
		}
		
		return onArrivalInNodeInternal(nodeId, nodeData, unexplored, nextTurn, numEdges);
	}
	
	public int onArrivalInNodeInternal(int nodeId, NodeMemory nodeData, boolean firstTime, int nextTurn, int numEdges) {
		int referenceExit;
		
		if ( forwardMove ) {
			referenceExit = 0; //relative indexing == canonical indexing
			
			int lastNode = predecessorsStack.getLast().first;
			int lastCanonExit = predecessorsStack.getLast().second;
			print("- temp[node " + lastNode + "] += (can-exit #" + lastCanonExit + " -> node " + nodeId + ")");
			storeTempNavData(lastNode, lastCanonExit, nodeId);
		
		} else {
			referenceExit = this.nextNodeReferenceExit;

			print("- synchronizing data for " + nodeId);
			syncTempNavData(nodeData, nodeId);
			print("- navdata updated: " + navData.toString());
		}
		
		//for an undiscovered node
		if (firstTime) {
			int lastNode = predecessorsStack.getLast().first;
			print("- first time");
			print("- navData[node " + nodeId + "] += (can-exit #0 -> node " + lastNode + ")");
			nodeData.setNode(0, lastNode);
			nodeData.advanceToNextUnexploredPort();
		} 

		if (nodeData.isComplete()) {
			if (forwardMove) {
				//complete node, reached (again) in a forward move
				//backtracks through exit 0 (in a forward move, the reference is not properly set in this case)
				Pair<Integer,Integer> element = predecessorsStack.removeLast(); 
				int ancestorNode = element.first;
				print("- complete (fwd move), backtrack to " + ancestorNode +" (rel-exit #0)");

				destinyNode_dbg = ancestorNode;
				this.nextNodeReferenceExit = element.second;
				forwardMove = false;
				return 0;  //return through the 'entrance' edge
			}
			
			if (predecessorsStack.isEmpty()) {
				print("=> FINISHED ALL! Turn " + nextTurn);
				forwardMove = false;
				return -1; //stay in the same node
			}
			
			//backtracks
			Pair<Integer,Integer> element = predecessorsStack.removeLast(); 
			int ancestorNode = element.first;
			
			int nextCanonExit = nodeData.getPortTo(ancestorNode); //its the same as the 'entrance edge' (#0) if 'forwardMove == true'
			int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges); 
			if (nextCanonExit == -1) { print("Error: map is not symetrical!"); }
			print("- complete (bck move), backtrack to node " + ancestorNode + " (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");

			destinyNode_dbg = ancestorNode;
			this.nextNodeReferenceExit = element.second;
			forwardMove = false;
			return nextRelatExit; 
		
		} else if (forwardMove && !firstTime) {
			//node already visited, incomplete, reached (again) in a forward move
			
			//the "multi" versions of the algorithms for relative order are different mainly in this situation
			//this version simply backtracks (like DFS 3)

			Pair<Integer,Integer> element = predecessorsStack.removeLast(); 
			int lastNode = element.first;
			print("- already in visit, backtrack to " + lastNode +" (rel-exit #0)");
			
			destinyNode_dbg = lastNode;
			this.nextNodeReferenceExit = element.second;
			forwardMove = false;
			return 0;  //return through the 'entrance' edge
			
		} else {
			//a new (first-time) incomplete node, or an ancestor incomplete node reached by backtrack
			int nextCanonExit = nodeData.getCurrentPort();
			int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges);
			predecessorsStack.add(new Pair<>(nodeId, nextCanonExit));
			
			nodeData.setInExploration(nodeData.getCurrentPort()); //this exit/edge is being explored -- to be updated in the synchronization
			nodeData.advanceToNextUnexploredPort();
			print("- forward (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");
			
			destinyNode_dbg = -1;
			forwardMove = true;
			return nextRelatExit;
			
		}
		
	}
	
	private int toRelativeExit(int canonicalExit, int referenceExit, int numEdges) {
		return (canonicalExit - referenceExit + numEdges) % numEdges; // term "+ numEdges" is to avoid negative values
	}
	
	private void storeTempNavData(int node, int nodeExit, int targetNode) {
		this.tempNavData.put(node, new Pair<Integer,Integer>(nodeExit, targetNode));
	}

	private void syncTempNavData(NodeMemory nodeData, int node) {
		Pair<Integer,Integer> pair = this.tempNavData.get(node);
		if (pair != null) {
			nodeData.updateWith(pair.first, pair.second);
			this.tempNavData.remove(node);
		}
	}

}
