package exploration.relative_order;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import exploration.util.NodeMemory;
import yaps.util.Pair;


/**
 * A DFS-based solution for 1 agent solving the <b>exploration problem with relative-order exits</b> 
 * described in {@link MultiagentExplorationWithRelativeOrder}, which this class extends. <br><br>
 *
 * @author Pablo A. Sampaio
 */
public class DfsExplorer3SingleAgent extends MultiagentExplorationWithRelativeOrder {
	
	public DfsExplorer3SingleAgent() {
		super("DfsExplorer-3-single");
	}

	@Override
	public ExplorerAgentRelativeOrder[] createTeam(int agents, int totalTime) {
		ExplorerAgentRelativeOrder[] team = new ExplorerAgentRelativeOrder[agents];
		for (int i = 0; i < agents; i++) {
			team[i] = new DfsExplorer3Agent(System.out);
		}
		return team;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}


class DfsExplorer3Agent extends ExplorerAgentRelativeOrder {
	private int destinyNode_dbg; //used for debug only
	
	private Map<Integer,NodeMemory> navData; //map { node --> exits } 
	                                         //the 'indexed-exits' identify the edges, but are essentially a mapping { canonical-exit-index --> target-node } 
											 //the canonical exit indexes are the exit numbers when the agent first arrives in the node
											 //if the agent gets back to that node later coming from different node, the indexes change and it is necessary to convert

	private LinkedList<Pair<Integer, Integer>> predecessorsStack; //pairs (lastNode, canonExit) 
	                                                              //indicates that, from 'lastNode', the agent crossed the edge 'canonExit'

	private boolean forwardMove;
	private int nextNodeReferenceExit; //this exit index is used to cyclically shift the current relative exit indexes of the next node, thus converting from the current relative indexing to the canonical indexing in that node 
									   //if the next move is a backtrack (forwardMove == false), keeps the canonical exit used when departed last time from next node (this value is obtained from the stack)
	                                   //otherwise, it is zero (so canonical indexing = relative indexing)


	DfsExplorer3Agent(PrintStream teamLog) {
		super(teamLog);
	}

	@Override
	public void onStart(int initialNode, int initialNodeEdges) {
		print("START");
		this.predecessorsStack = new LinkedList<>();
		this.navData = new HashMap<>();
		this.navData.put(initialNode, new NodeMemory(initialNodeEdges));
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

		int referenceExit = 0; //relative indexing == canonical indexing
		
		if ( forwardMove ) {
			int lastNode = predecessorsStack.getLast().first;
			NodeMemory lastNodeNeighbors = navData.get(lastNode);

			print("- navData[node " + lastNode + "] += (can-exit #" + lastNodeNeighbors.getCurrentPort() + " -> node " + nodeId + ")");
			lastNodeNeighbors.setNodeForCurrentPort(nodeId);
			lastNodeNeighbors.advanceToNextUnexploredPort();
		
		} else {
			referenceExit = this.nextNodeReferenceExit;

		}
		
		NodeMemory neighbors = navData.get(nodeId);
		boolean firstTime = false;
		
		//for an undiscovered node
		if (neighbors == null) {
			int lastNode = predecessorsStack.getLast().first;
			print("- first time");
			firstTime = true;
			neighbors = new NodeMemory(numEdges);

			print("- navData[node " + nodeId + "] += (can-exit #0 -> node " + lastNode + ")");
			neighbors.setNode(0, lastNode);
			neighbors.advanceToNextUnexploredPort();
			
			navData.put(nodeId, neighbors);
		} 

		if (neighbors.isComplete()) {
			if (forwardMove) {
				//complete, reached (again) in a forward move
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
			
			int nextCanonExit = neighbors.getPortTo(ancestorNode); //its the same as the 'entrance edge' (#0) if 'forwardMove == true'
			int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges); 
			if (nextCanonExit == -1) { print("Error: map is not symetrical!"); }
			print("- complete (bck move), backtrack to node " + ancestorNode + " (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");

			destinyNode_dbg = ancestorNode;
			this.nextNodeReferenceExit = element.second;
			forwardMove = false;
			return nextRelatExit; 
		
		} else if (forwardMove && !firstTime) {
			//node already visited, incomplete, reached (again) in a forward move
			//backtracks
			Pair<Integer,Integer> element = predecessorsStack.removeLast(); 
			int lastNode = element.first;
			print("- already in visit, backtrack to " + lastNode +" (rel-exit #0)");
			
			destinyNode_dbg = lastNode;
			this.nextNodeReferenceExit = element.second;
			forwardMove = false;
			return 0;  //return through the 'entrance' edge
			
		} else {
			//a new incomplete node, or an ancestor node reached by backtrack
			int nextCanonExit = neighbors.getCurrentPort();
			int nextRelatExit = this.toRelativeExit(nextCanonExit, referenceExit, numEdges);
			predecessorsStack.add(new Pair<>(nodeId, nextCanonExit));
			print("- forward (can-exit #" + nextCanonExit + ", rel-exit #" + nextRelatExit + ")");
			
			destinyNode_dbg = -1;
			forwardMove = true;
			return nextRelatExit;
			
		}
		
	}
	
	/**
	 * Converts the (desired) canonical exit index to the current relative exit index. An example of exit index conversion:<br><ul>
	 * <li> Suppose that an agent goes from x to y, in a forward move, through x's canonical exit #1. Then, the stack will hold the pair (y, #1).  
	 * <li> When the backtrack from y to x occurs at any time, the x's indexes are all changed, e.g. the exit #0 will now point to y (instead of exit #1).
	 * <li> To convert from the canonical indexing of the exits to the current relative indexing, the value #1 (obtained from the stack) will be used as a 
	 *      negative cyclic offset, so that that canonical exit C is converted to the relative index given by <code><b>(C - #1) mod numEdges</code></b>.
	 * <li> If the agents wants to go to y again, it converts the canonical exit #1 (as used in the first move) to the relative exit (#1 - #1) mod numEdges = #0,
	 *      as expected. 
	 */
	private int toRelativeExit(int canonicalExit, int referenceExit, int numEdges) {
		return (canonicalExit - referenceExit + numEdges) % numEdges; // term "+ numEdges" is to avoid negative values
	}

	//not used
//	private int toCanonicalExit(int relativeExit, int referenceExit, int numEdges) {
//		return (referenceExit + relativeExit) % numEdges;
//	}

}
