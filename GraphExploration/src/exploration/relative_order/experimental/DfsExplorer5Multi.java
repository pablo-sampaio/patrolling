package exploration.relative_order.experimental;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import exploration.util.NodeMemory;
import exploration.relative_order.ExplorerAgentRelativeOrder;
import exploration.relative_order.MultiagentExplorationWithRelativeOrder;
import exploration.util.NodeMemory2;
import yaps.util.Pair;


/**
 * Almost the same idea used in DfsExplorer3, but, in this version, the agents may revisit 
 * a node visited by another agent, exploring other neighbors and merging the partial navdatas.
 * 
 * @author Pablo A. Sampaio
 */
public class DfsExplorer5Multi extends MultiagentExplorationWithRelativeOrder {
	
	public DfsExplorer5Multi() {
		super("DfsExplorer-5-multi-new");
	}

	@Override
	public ExplorerAgentRelativeOrder[] createTeam(int agents, int totalTime) {
		ExplorerAgentRelativeOrder[] team = new ExplorerAgentRelativeOrder[agents];

		Map<Integer,NodeMemory2> sharedNavigationData = new HashMap<>();
		
		for (int i = 0; i < agents; i++) {
			team[i] = new DfsExplorer5Agent(System.out, sharedNavigationData);
		}
		return team;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}


class DfsExplorer5Agent extends ExplorerAgentRelativeOrder {
	/* These two attributes are supposed to be stored in the nodes. 
	 */
	private Map<Integer,NodeMemory2> navData; //mapping { node --> indexed-exits } 
	                                            //the 'indexed-exits' identify the edges, but are also a mapping { canonical-exit-index --> target-node }
											    //the canonical exit indexes are the exit indexes when the agent first arrives in the node
											    //if the agent gets back to that node later coming from different node, the indexes change and it is necessary to convert
	private Map<Integer,Pair<Integer, Integer>> explAncestorData; //keeps extra data per node, useful only during the exploration phase
	private Map<Integer,NodeMemory2> tempNavData; //used when reaches a node already in visit by other agent
	
	enum Move { START, FORWARD, BACKTRACK, FINISH, SHORT_FORWARD };

	/* The next attributes are really to be stored in the agents. This agent requires O(max-degree) memory space. 
	 */
	private Move currentMove;

	private Pair<Integer,Integer> explNextElement_fwd; 	//for fwd move: pair <node, node-exit> to be stored in the next node

	private int interruptedNode_bkt; 				//for bkt mode: pair from the last node (the exit will be used as reference exit)
	private int backtrackedExit_bkt; 	//for bkt move: the canonical exit used when departed last time from (next) node to where it is backtraking
	private int destinyNodeDbg_bkt; 	//used for debug only, in bkt moves
	

	DfsExplorer5Agent(PrintStream teamLog, Map<Integer,NodeMemory2> sharedNodeData) {
		super(teamLog);
		this.navData = sharedNodeData;
		this.explAncestorData = new HashMap<Integer,Pair<Integer, Integer>>(); //not shared
		this.tempNavData = new HashMap<Integer,NodeMemory2>(); //not shared
	}

	@Override
	public void onStart(int initialNode, int initialNodeEdges) {
		print("START");
		this.explNextElement_fwd = null;
		this.interruptedNode_bkt = -1;
		this.backtrackedExit_bkt = 0;
		this.destinyNodeDbg_bkt = -1;
		this.currentMove = Move.START;
	}

	@Override
	public void onTurn(int nextTurn) {
		//does nothing
	}
	
	@Override
	public int onArrivalInNode(int nodeId, int nextTurn, int numEdges) {
		print("IN NODE " + nodeId + ", turn " + nextTurn);
		assert (destinyNodeDbg_bkt != -1 && nodeId != destinyNodeDbg_bkt) : ("Error: unexpected neighbor - " + nodeId + " != " + destinyNodeDbg_bkt);
		
		print("- navdata: " + navData.toString());
		print("- temp navdata: " + tempNavData.toString());

		NodeMemory2 nodeData = navData.get(nodeId);
		
		if (nodeData == null) {
			nodeData = new NodeMemory2(numEdges);
			this.navData.put(nodeId, nodeData);
		}
		
		switch (this.currentMove) {
		case START:
			print("- start node");
			explAncestorData.put(nodeId, null); // only to detect "nodeId" as a visited node (see endForward)
			return beginShortForwardMove(nodeId, nodeData, 0);
		case FORWARD:
			return endForwardMove(nodeId, nodeData/*, firstTimeInNode*/, nextTurn);
		case BACKTRACK:
			return endBacktrack(nodeId, nodeData, nextTurn);
		case SHORT_FORWARD:
			return endShortForwardMove(nodeId, nodeData/*, firstTimeInNode*/, nextTurn);
		case FINISH:
			return -1;
		default:
			throw new Error("Unexpected case: " + this.currentMove);
		}
		
	}

	public int endForwardMove(int nodeId, NodeMemory2 nodeData, int nextTurn) {
		int referenceExit = 0; //relative indexing == canonical indexing
		int lastNode = this.explNextElement_fwd.first;

		//TODO: ficou desnecessario?? porque um FWD acontece apos um Short-FWD, que faz isso
		print("- temp[node " + lastNode + "] += (can-exit #" + explNextElement_fwd.second + " -> node " + nodeId + ")");

		//for an undiscovered node
		boolean firstTime = nodeData.isUnexplored();
		if (firstTime) {
			print("- first time");
			print("- navData[node " + nodeId + "] += (can-exit #0 -> node " + lastNode + ")");
			nodeData.setNode(0, lastNode);
			nodeData.advanceToNextOpenExit();
		} 

		boolean visitedByMe = this.explAncestorData.containsKey(nodeId);
		if (nodeData.isComplete() || visitedByMe) {
			//complete node (may be a "first-time" node iff it has only 1 neighbor)
			//or node already visited, incomplete, reached again
			print("- complete (in fwd move) or in visit by me");
			return beginBacktrack(true, nodeId, nodeData, this.explNextElement_fwd, 0);
		
		} else if (!firstTime) {
			//node already visited by other agent, incomplete, reached again
			print("- node already in exploration by other agent");
			
			if (nodeData.getExitTo(lastNode) >= 0) {
				print("- auto-aligned using last node");
				referenceExit = nodeData.getExitTo(lastNode);
			} else {
				print("- creating temp navdata");
				NodeMemory2 tmpNodeData = new NodeMemory2(nodeData.getNumNeighbors());
				this.tempNavData.put(nodeId, tmpNodeData);
				tmpNodeData.setNode(0, lastNode);
				tmpNodeData.advanceToNextOpenExit();
				nodeData = tmpNodeData;  //para fazer o forward abaixo, mas nao escreve na memoria compartilhada
			}

			// abordagem lazy (segue com um FWD e deixar para fazer o merge depois)
			
			this.explAncestorData.put(nodeId, this.explNextElement_fwd);  //store backtrack data (only in agent memory)
			return beginForwardMove(nodeId, nodeData, referenceExit);     //faz um forward (pode ser com o tmpNodeData)
			
		} else {
			//a new (first-time) incomplete node
			this.explAncestorData.put(nodeId, this.explNextElement_fwd);  //store backtrack data (only in agent memory)
			return beginShortForwardMove(nodeId, nodeData, referenceExit);

		}
		
	}

	public int endShortForwardMove(int nodeId, NodeMemory2 nodeData, int nextTurn) {
		int lastNode = this.explNextElement_fwd.first;

		print("- temp[node " + lastNode + "] += (can-exit #" + explNextElement_fwd.second + " -> node " + nodeId + ")");

		// for an undiscovered node -- nao seta nada porque:
		// - este agente vai retornar a este nó, e precisa tratá-lo como unexplored
		// - para deixar o no aberto a ser explorado por outro agente que chegar antes 

		// IDEIAS de melhorias -- informar (ao antecessor) se este já foi visitado ou se esta completo, 
		//      encerrar um no completado (testar se pode fazer merge), etc

		return beginBacktrack(true, nodeId, nodeData, this.explNextElement_fwd, 0);
	}

	public int endBacktrack(int nodeId, NodeMemory2 nodeData, int nextTurn) {
		int referenceExit = this.backtrackedExit_bkt;
		
		print("- synchronizing data for " + nodeId);
		
		if (this.tempNavData.containsKey(nodeId)) {
			NodeMemory2 tmpNodeData = this.tempNavData.get(nodeId);
			tmpNodeData.setNode(this.backtrackedExit_bkt, this.interruptedNode_bkt);
			
			print("- trying to align real and temp navdata: " + nodeData + " " + tmpNodeData);
			Pair<Boolean,Integer> mergeResult = nodeData.alignAndMerge(tmpNodeData);
			
			boolean merged = mergeResult.first;
			if (merged) {
				print("  * merged navdata: " + navData.toString());
				tempNavData.remove(nodeId);
				print("  * reference: " + mergeResult.second);
				referenceExit += mergeResult.second;			
			} else {
				print("  * not merged");
				nodeData = tmpNodeData;
			}

		} else  {
			nodeData.setNode(this.backtrackedExit_bkt, this.interruptedNode_bkt);
			print("- navdata updated: " + navData.toString());
		}
		
		if (nodeData.isComplete()) {
			if (this.explAncestorData.get(nodeId) == null) {
				return finishAction(nextTurn);
			}
			//backtracks
			print("- complete (bck move)");
			Pair<Integer,Integer> ancestorInfo = this.explAncestorData.remove(nodeId); //read and erase data from current node
			return beginBacktrack(false, nodeId, nodeData, ancestorInfo, referenceExit);
		} else {
			//an incomplete node
			return beginForwardMove(nodeId, nodeData, referenceExit);
		}
		
	}
	
	private int beginForwardMove(int nodeId, NodeMemory2 nodeData, int referenceExit) {
		int nextCanonExit = nodeData.getCurrentExit();
		int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, nodeData.getNumNeighbors());
		
		nodeData.setInExploration(nodeData.getCurrentExit()); //this exit/edge is being explored -- to be updated in the synchronization
		nodeData.advanceToNextOpenExit();
		print("- forward (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");
		
		this.explNextElement_fwd = new Pair<>(nodeId, nextCanonExit);
		this.currentMove = Move.FORWARD;
		destinyNodeDbg_bkt = -1;
		this.interruptedNode_bkt = -1;
		this.backtrackedExit_bkt = -1;
		return nextRelatExit;
	}

	private int beginShortForwardMove(int nodeId, NodeMemory2 nodeData, int referenceExit) {
		int nextCanonExit = nodeData.getCurrentExit();
		int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, nodeData.getNumNeighbors());
		
		print("- short-forward (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");
		
		this.explNextElement_fwd = new Pair<>(nodeId, nextCanonExit);
		this.currentMove = Move.SHORT_FORWARD;
		destinyNodeDbg_bkt = -1;
		this.interruptedNode_bkt = -1;
		this.backtrackedExit_bkt = -1;
		return nextRelatExit;
	}
	
	private int beginBacktrack(boolean afterForward, int nodeId, NodeMemory2 nodeData, Pair<Integer,Integer> ancestorInfo, int referenceExit) {
		int ancestorNode = ancestorInfo.first;
		int nextRelatExit;

		if (afterForward) {
			nextRelatExit = 0;  //return through the 'entrance' edge (for already-visited nodes reached in a forward move, because the reference is not properly set in this case)
			print("- backtrack to " + ancestorNode +" (rel-exit #0)");	
		} else {
			int nextCanonExit = nodeData.getExitTo(ancestorNode);
			nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, nodeData.getNumNeighbors()); 
			if (nextCanonExit == -1) { print("Error: map is not symetrical!"); }
			print("- backtrack to " + ancestorNode + " (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");
		}
			
		destinyNodeDbg_bkt = ancestorNode; //for debugging only
		this.interruptedNode_bkt = nodeId;
		this.backtrackedExit_bkt = ancestorInfo.second;
		this.currentMove = Move.BACKTRACK;
		this.explNextElement_fwd = null;
		return nextRelatExit; 
	}

	private int finishAction(int turn) {
		print("=> FINISHED ALL! Turn " + turn);
		this.currentMove = Move.FINISH;
		return -1; //stay in the same node
	}


	private int toRelativeExit(int canonicalExit, int referenceExit, int numEdges) {
		return (canonicalExit - referenceExit + numEdges) % numEdges; // term "+ numEdges" is to avoid negative values
	}
	
}
