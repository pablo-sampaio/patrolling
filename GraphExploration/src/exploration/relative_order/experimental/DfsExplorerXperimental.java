package exploration.relative_order.experimental;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import exploration.relative_order.ExplorerAgentRelativeOrder;
import exploration.relative_order.MultiagentExplorationWithRelativeOrder;
import exploration.util.NodeMemory3;
import exploration.util.NodeExplorationStatus;
import yaps.util.Pair;


/**
 * This implements a DFS-based exploration strategy for the <b>exploration problem with relative-order exits</b>
 * (see {@link MultiagentExplorationWithRelativeOrder}).<br><br>
 * 
 * In this version, the first phase of a DFS-visit to a node consists in <i>"expanding"</i> the exits of the node 
 * by going back and forth, to find out all neighbors.<br><br>
 * 
 * After the expansion is complete, the second phase consists in "exploring" the unexplored neighbors. The next node 
 * to be visited is chosen by a selection based on the status of the node: not expanded, partially expanded, partially
 * explored. (Fully explored nodes are not chosen).
 * 
 * @author Pablo A. Sampaio
 */
public class DfsExplorerXperimental extends MultiagentExplorationWithRelativeOrder {
	
	public DfsExplorerXperimental() {
		super("DfsExplorerX-multi");
	}

	@Override
	public ExplorerAgentRelativeOrder[] createTeam(int agents, int totalTime) {
		ExplorerAgentRelativeOrder[] team = new ExplorerAgentRelativeOrder[agents];
		Map<Integer,NodeMemory3> sharedNavData = new HashMap<>(); 
		
		for (int i = 0; i < agents; i++) {
			team[i] = new DfsExplorerXperAgent(System.out, sharedNavData);
		}
		return team;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}


class DfsExplorerXperAgent extends ExplorerAgentRelativeOrder {
	private int destinyNode_dbg; //used for debug only
	
	private Map<Integer,NodeMemory3> navData; //map { node --> indexed-exits } 
	                                            //the 'indexed-exits' identify the edges, but are essentially a mapping { canonical-exit-index --> target-node }
											    //the canonical indexes of the edges are those when the agent first arrives in the node
											    //if the agent gets back to that node later by a different origin node, the indexes change and it is necessary to convert

	private Map<Integer,int[]> tempNavData;  //complements the mapping above with data acquired by the agent 
                                             //it is synchronized when the agent gets back to the node

	private LinkedList<Pair<Integer, Integer>> ancestorsStack; //pairs <lastNode, canonExit> 
	                                                           //indicates that, from 'lastNode', the agent crossed the edge 'canonExit'

	enum Move { EXPAND_FWD, EXPAND_BCK, FORWARD, BACKTRACK };
	private Move move;
	
	private int backReferenceExit; //in backward moves, keeps the canonical exit used when departed last time from the node to which it is returning
	                               //should not be used in forward moves
	private NodeExplorationStatus backLastNodeStatus = null; //in backward moves, keeps the status of the node from where it is returning


	DfsExplorerXperAgent(PrintStream teamLog, Map<Integer,NodeMemory3> sharedNodeData) {
		super(teamLog);
		this.navData = sharedNodeData;
	}

	@Override
	public void onStart(int initialNode, int initialNodeEdges) {
		print("START");
		this.ancestorsStack = new LinkedList<>();
		this.navData.put(initialNode, new NodeMemory3(initialNodeEdges));
		this.tempNavData = new HashMap<>();
		this.tempNavData.put(initialNode, new int[initialNodeEdges]);
		this.destinyNode_dbg = initialNode;
		this.backReferenceExit = 0;
		this.move = Move.EXPAND_BCK;
	}

	@Override
	public void onTurn(int nextTurn) {
		//does nothing
	}
	
	/*
	 * Sobre esta estratégia:
	 * - requer um esforço MUITO grande ajustar para que ela tenha sempre o status correto dos nos -- ainda tem bugs!
	 * - IDEIA 1: criar metodos backtrack_entrance(), backtrack(), etc. --> fiz no DF3-simplif
	 * - IDEIA 2: calcular com cuidado o staus de cada nó, setar tanto nos forwards, como nos backs
	 * (OK) - IDEIA 3: sobre o objeto NeighborList: criar no inicio sempre, passar por parametro, passar flag "new" ou 
	 *            setar nessa classe
	 * - IDEIA 4: setar o status exato no na classe Neighbors (para ajudar na ideia 2), simplificar os status (remover ANCESTOR)
	 */
	
	@Override
	public int onArrivalInNode(int nodeId, int nextTurn, int numEdges) {
		print("IN NODE " + nodeId + ", turn " + nextTurn);
		if (destinyNode_dbg != -1 && nodeId != destinyNode_dbg) { print("Error: unexpected neighbor - " + nodeId + " != " + destinyNode_dbg);	}
		
		print("- preds  : " + ancestorsStack);
		print("- navdata: " + navData.toString());
		
		NodeMemory3 nodeData = this.navData.get(nodeId);
		if (nodeData == null) {
			nodeData = new NodeMemory3(numEdges);
			this.navData.put(nodeId, nodeData);
		}
		
		int[] tempData = tempNavData.get(nodeId);
		if (tempData == null) {
			tempData = new int[numEdges];
			tempNavData.put(nodeId, tempData);
		}
		
		return onArrivalInNodeInternal(nodeId, nodeData, nextTurn, numEdges);
	}

	public int onArrivalInNodeInternal(int nodeId, NodeMemory3 nodeData, int nextTurn, int numEdges) {
		int referenceExit = 0; //relative indexing == canonical indexing (attention: in forward moves to expanded nodes, this is not the correct reference exit)
		
		if ( move == Move.FORWARD || move == Move.EXPAND_FWD ) {
			int lastNode = ancestorsStack.getLast().first;
			int lastCanonExit = ancestorsStack.getLast().second;
			print("- temp[node " + lastNode + "] += (can-exit #" + lastCanonExit + " -> node " + nodeId + ")");  //TODO: no forward � desnecess�rio
			storeTempNavData(lastNode, lastCanonExit, nodeId);
			
			int exitToLast = nodeData.getExitTo(lastNode);
			if (move == Move.FORWARD && exitToLast > -1) {
				nodeData.setStatus(exitToLast , NodeExplorationStatus.DIRECT_ANCESTOR);
			} else if (move == Move.EXPAND_FWD && exitToLast > -1) {
				nodeData.setStatus(exitToLast , NodeExplorationStatus.PARTIALLY_EXPANDED);
			}
		
		} else if (move == Move.BACKTRACK || move == Move.EXPAND_BCK) {
			referenceExit = this.backReferenceExit;

			if (backLastNodeStatus == null) { print("Error: status of last node was not set (start node?)");	}
			nodeData.setStatus(referenceExit, backLastNodeStatus); // #referenceExit was the one used (in this node) to reach the node from where the agent is backtracking

			print("- synchronizing data for " + nodeId);
			syncTempNavData(nodeId);
			print("- navdata updated: " + nodeId + "=" + nodeData.toString());
			
		}

		backLastNodeStatus = null;
		
		if (move == Move.EXPAND_FWD) {
			Pair<Integer,Integer> element = ancestorsStack.removeLast(); 
			int ancestorNode = element.first;
			print("- expand-bck to node " + ancestorNode +" (rel-exit #0)");
			
			if (! nodeData.expansionStarted()) {
				this.backLastNodeStatus = NodeExplorationStatus.NOT_EXPANDED;
			} else if (isAncestor(nodeId)) {
				this.backLastNodeStatus = NodeExplorationStatus.DIRECT_ANCESTOR;
			} else if (fullyExplored(nodeData)) {
				this.backLastNodeStatus = NodeExplorationStatus.FULLY_EXPLORED;
			} else if (nodeData.fullyExpanded()) {
				this.backLastNodeStatus = NodeExplorationStatus.EXPANDED_PARTIALLY_EXPLORED;
			} else {
				this.backLastNodeStatus = NodeExplorationStatus.PARTIALLY_EXPANDED;
			}
	
			destinyNode_dbg = ancestorNode;
			this.backReferenceExit = element.second;
			move = Move.EXPAND_BCK;
			return 0;  //return through the 'entrance' edge
		
		} else if (move == Move.EXPAND_BCK) {
			
			if (nodeData.fullyExpanded()) { //TODO: no futuro, fazer isso quando faltar um para ficar expanded
				print("- expansion completed");
				
				if (fullyExplored(nodeData)) {
					//backtracks to ancestor
					Pair<Integer,Integer> element = ancestorsStack.removeLast(); 
					int ancestorNode = element.first;
					
					int nextCanonExit = nodeData.getExitTo(ancestorNode); //its not (necessarily) the same as the 'entrance edge' (#0)
					int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges); 
					if (nextCanonExit == -1) { print("Error: map is not symetrical!"); }
					print("- fully explored (in exp-bck move), backtrack to node " + ancestorNode + " (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");

					this.backLastNodeStatus = NodeExplorationStatus.FULLY_EXPLORED;
					destinyNode_dbg = ancestorNode;
					this.backReferenceExit = element.second;
					this.move = Move.BACKTRACK;
					return nextRelatExit; 

				
				} else {
					//forward
					int nextCanonExit = chooseNextForwardExit(nodeData);
					int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges);
					ancestorsStack.add(new Pair<>(nodeId, nextCanonExit));
					//neighbors.setStatus(nextCanonExit, NodeExplorationStatus.);
					
					print("- forward, from exp-bck (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");
					
					destinyNode_dbg = -1;
					this.move = Move.FORWARD;
					return nextRelatExit;
				}
			}
			
			int nextCanonExit = nodeData.nextUnexpandedExit();
			int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges);
			ancestorsStack.add(new Pair<>(nodeId, nextCanonExit));
			
			nodeData.setNode(nextCanonExit, -2); //special code to tell that this exit/edge is being expanded -- updated in the synchronization
			print("- expand-fwd (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");
			
			destinyNode_dbg = -1;
			move = Move.EXPAND_FWD;
			return nextRelatExit;
			
		} else if (move == Move.FORWARD) {
			boolean newNode = false;
			int lastNode = ancestorsStack.getLast().first;
			
			//for an undiscovered node
			if (! nodeData.expansionStarted()) {
				print("- not expanded");
				newNode = true;
				referenceExit = 0;

				print("- navData[node " + nodeId + "] += (can-exit #0 -> node " + lastNode + ")");
				nodeData.setNode(0, lastNode);
				nodeData.setStatus(0, NodeExplorationStatus.DIRECT_ANCESTOR);
//				tempNavData.put(nodeId, new int[numEdges]); //always create a 'temp' entry together with the main entry
			
			} else {
				referenceExit = nodeData.getExitTo(lastNode);
				
			}
			
			if (fullyExplored(nodeData) || referenceExit < 0) { //referenceExit < 0 occurs when this node is not fully expanded (so it has not the exit to the last node)
				//complete, reached in a forward move (may be a revisit or a new node)
				//backtracks through exit 0 
				Pair<Integer,Integer> element = ancestorsStack.removeLast(); 
				int ancestorNode = element.first;
				print("- complete (fwd move), backtrack to " + ancestorNode +" (rel-exit #0)");

				if (referenceExit < 0) {
					this.backLastNodeStatus = NodeExplorationStatus.PARTIALLY_EXPANDED;
				} else {
					this.backLastNodeStatus = NodeExplorationStatus.FULLY_EXPLORED;
				}
				destinyNode_dbg = ancestorNode;
				this.backReferenceExit = element.second;
				this.move = Move.BACKTRACK;
				return 0;  //return through the 'entrance' edge
			
			} else if (newNode) {
				//a new node (not expanded and not explored)
				//start expansion of the node
				int nextCanonExit = nodeData.nextUnexpandedExit();
				int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges); //referenceExit is 0 here
				ancestorsStack.add(new Pair<>(nodeId, nextCanonExit));
				
				nodeData.setNode(nextCanonExit, -2); //special code to tell that this exit/edge is being explored -- updated in the synchronization
				//neighbors.nextUnexpandedExit();
				print("- start expand-fwd (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");
				
				destinyNode_dbg = -1;
				this.move = Move.EXPAND_FWD;
				return nextRelatExit;
				
			} else {
				//a fully (or, at least, reasonably) expanded node, but not fully explored
				int nextCanonExit = chooseNextForwardExit(nodeData);
				int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges);
				ancestorsStack.add(new Pair<>(nodeId, nextCanonExit));
				
				print("- forward, from fwd (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");
				
				destinyNode_dbg = -1;
				this.move = Move.FORWARD;
				return nextRelatExit;

			}

		} else /*if (move == Move.BACKTRACK)*/ {
			
			if (fullyExplored(nodeData)) {
				if (ancestorsStack.isEmpty()) {
					print("=> FINISHED ALL! Turn " + nextTurn);
					destinyNode_dbg = nodeId;
					this.backLastNodeStatus = NodeExplorationStatus.FULLY_EXPLORED;
					this.move = Move.BACKTRACK;
					return -1; //stay in the same node
				}
				
				//backtracks
				Pair<Integer,Integer> element = ancestorsStack.removeLast(); 
				int ancestorNode = element.first;
				
				int nextCanonExit = nodeData.getExitTo(ancestorNode); //its not (necessarily) the same as the 'entrance edge' (#0)
				int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges); 
				if (nextCanonExit == -1) { print("Error: map is not symetrical!"); }
				print("- fully explored (in a backtrack), backtrack to node " + ancestorNode + " (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");

				this.backLastNodeStatus = NodeExplorationStatus.FULLY_EXPLORED;
				destinyNode_dbg = ancestorNode;
				this.backReferenceExit = element.second;
				this.move = Move.BACKTRACK;
				return nextRelatExit; 
			
			} else {
				//an ancestor, not fully explored, but fully expanded, reached by backtrack
				int nextCanonExit = chooseNextForwardExit(nodeData);
				int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges);
				ancestorsStack.add(new Pair<>(nodeId, nextCanonExit));
				print("- forward, from back (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");
				
				destinyNode_dbg = -1;
				this.move = Move.FORWARD;
				return nextRelatExit;
				
			}

		}
		
	}
	
	private boolean fullyExplored(NodeMemory3 neighbors) {
		if (neighbors.nextExitWithStatus(NodeExplorationStatus.NOT_EXPANDED, false) >= 0) {
			return false;
		}
		if (neighbors.nextExitWithStatus(NodeExplorationStatus.EXPANDED_PARTIALLY_EXPLORED, false) >= 0) {
			return false;
		}
		if (neighbors.nextExitWithStatus(NodeExplorationStatus.PARTIALLY_EXPANDED, false) >= 0) {
			return false;
		}
		if (neighbors.nextExitWithStatus(null, false) >= 0) { //TODO: create UNKNWON (and initialize it)
			return false;
		}
		return true;
	}

	//Ideias para o forward: 
	//		1) Mais conservadora: so vai para NOT_EXPANDED
	//		2) Mais ousada: FORWARD em outra sa�da do no atual, seguindo certa prioridade dos status

	private int chooseNextForwardExit(NodeMemory3 nodeData) {
		//TODO: melhoria: não (nunca) escolher um antecessor direto ou indireto do proprio agente
		
		int nextExit;
		nextExit = nodeData.nextExitWithStatus(NodeExplorationStatus.NOT_EXPANDED, false);
		if (nextExit >= 0) {
			return nextExit;
		}
		nextExit = nodeData.nextExitWithStatus(NodeExplorationStatus.EXPANDED_PARTIALLY_EXPLORED, false);
		if (nextExit >= 0) {
			return nextExit;
		}
		nextExit = nodeData.nextExitWithStatus(NodeExplorationStatus.PARTIALLY_EXPANDED, false);
		if (nextExit >= 0) {
			return nextExit;
		}
		nextExit = nodeData.nextExitWithStatus(null, false);
		if (nextExit >= 0) {
			return nextExit;
		}
		return -1;
	}
	
	private boolean isAncestor(int nodeId) {
		for (Pair<Integer,Integer> element : this.ancestorsStack) {
			if (element.first == nodeId) {
				return true;
			}
		}
		return false;
	}

	private int toRelativeExit(int canonicalExit, int referenceExit, int numEdges) {
		return (canonicalExit - referenceExit + numEdges) % numEdges; // term "+ numEdges" is to avoid negative values
	}
	
	private void storeTempNavData(int node, int nodeExit, int targetNode) {
		int[] tempNeighbors = this.tempNavData.get(node);
		tempNeighbors[nodeExit] = targetNode;
	}

	private void syncTempNavData(int node) {
		int[] tempNeighbors = this.tempNavData.get(node);
		NodeMemory3 neighbors = this.navData.get(node);
		
		int dbgCnt = 0;
		for (int i = 0; i < tempNeighbors.length; i++) {
			if (tempNeighbors[i] != -1 && neighbors.getNode(i) == -2) {
				neighbors.setNode(i, tempNeighbors[i]);
				tempNeighbors[i] = -1;
				dbgCnt ++;
			}
		}
		print("- updated " + dbgCnt + " entries");
	}

}
