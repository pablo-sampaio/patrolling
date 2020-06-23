package exploration.relative_order;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import exploration.util.NodeMemory0;


/**
 * A monolithic and somewhat simplified version of ECEP {@link DfsExplorer3_ECEP}. 
 * This version was proposed to prove properties.
 * 
 * @author Pablo A. Sampaio
 */
public class DfsExplorer3_ECEP2 extends MultiagentExplorationWithRelativeOrder {
	private PrintStream output;
	
	public DfsExplorer3_ECEP2() {
		this(false);
	}
	
	public DfsExplorer3_ECEP2(boolean verboseMode) {
		super("DfsExplorer-3-ECEP-v2-X");
		if (verboseMode) {
			this.output = System.out;
		}
	}

	@Override
	public ExplorerAgentRelativeOrder[] createTeam(int agents, int totalTime) {
		ExplorerAgentRelativeOrder[] team = new ExplorerAgentRelativeOrder[agents];
		Map<Integer,NodeMemory0> sharedNavData = new HashMap<>(); 
		
		for (int i = 0; i < agents; i++) {
			team[i] = new DfsExplorer3Agent_ECEP_v2(this.output, sharedNavData);
		}
		return team;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}


class DfsExplorer3Agent_ECEP_v2 extends ExplorerAgentRelativeOrder {
	private Map<Integer,NodeMemory0> navData; //mapping { node --> indexed-exits } 
	                                         //the 'indexed-exits' identify the edges, but are also a mapping { canonical-port --> target-node }
											 //the canonical ports are the indexed order of the edges found when the agent first arrives in the node
											 //if the agent gets back later, coming from a different node, the ports change and need to be converted
	private int initialNode;
	private int prevNode;
	
	private enum ExplorationAction { START, FORWARD, BACKTRACK, FINISH };
	private ExplorationAction actionInProgress;

	DfsExplorer3Agent_ECEP_v2(PrintStream teamLog, Map<Integer,NodeMemory0> sharedNodeData) {
		super(teamLog);
		this.navData = sharedNodeData;
	}

	@Override
	public void onStart(int initialNode, int initialNodeEdges) {
		print("START");
		this.navData.put(initialNode, new NodeMemory0(initialNode, initialNodeEdges));
		this.initialNode = -1;
		this.prevNode = -1;
		this.actionInProgress = ExplorationAction.START;
	}

	@Override
	public void onTurn(int nextTurn) {
		//does nothing
	}
	
	@Override
	public int onArrivalInNode(int nodeId, int nextTurn, int numEdges) {
		print("IN NODE " + nodeId + ", turn " + nextTurn);

		NodeMemory0 nodeData = navData.get(nodeId);
		
		if (nodeData == null) {
			nodeData = new NodeMemory0(nodeId, numEdges);
			this.navData.put(nodeId, nodeData);
		}
		print("- navdata: " + navData.get(nodeId));
		
		// ATENCAO: para ficar igual ao artigo, falta acrescentar o peso da última aresta (diferença entre os turnos)
		int outPort = onArrivalInNodeInternal(nodeData, prevNode, nextTurn); 
		
		this.prevNode = nodeId;

		return outPort;
	}
	
	public int onArrivalInNodeInternal(NodeMemory0 node, int previousNode, int nextTurn) {
		int port = -1;  // next port in the current effective configuration
		ExplorationAction previousAction = this.actionInProgress;
		
		if ( previousAction == ExplorationAction.START ) {
			this.initialNode = node.id;
			port = 0;
			//node.nav[port] = NodeMemory0.IN_EXPLORATION;
			node.lastPort = port;
			print("- forward (ref-port #0, eff-port #0)");
			this.actionInProgress = ExplorationAction.FORWARD;
			return port;
			
		} else if ( previousAction == ExplorationAction.FORWARD ) {
			boolean firstTime = (node.lastPort == -1); //node.nav[0] == NodeMemory0.UNEXPLORED);
			
			//for an undiscovered node
			if (firstTime) {
				print("- first time");
				print("- navData += (ref-port #0 -> node " + previousNode + ")");
				node.nav[0] = previousNode;
			} 
			
			if (firstTime && node.delta > 1) {
				//a new (first-time) incomplete node
				//node.nav[port] = NodeMemory0.IN_EXPLORATION;
				node.lastPort = 1;
				print("- forward (ref-port #1, eff-port #1)");
				this.actionInProgress = ExplorationAction.FORWARD;
				return 1;
			
			} else {
				print(firstTime? "- complete, navdata: " + node.toString() : "- already visited");
				print("- backtrack (eff-port #0)");
				this.actionInProgress = ExplorationAction.BACKTRACK;
				return 0;  //return through the 'entrance' port
			}
		
		} else if ( previousAction == ExplorationAction.BACKTRACK ) {
			print("- navData += (ref-port #" + node.lastPort + " -> node " + previousNode + ")");
			node.nav[node.lastPort] = previousNode;
			
			int refPort = node.lastPort + 1; //next reference port, corresponds precisely to effective port #1
			
			if (refPort < node.delta) {  // in this case, I think I can prove that: node.nav[refPort] == UNEXPLORED
				//an ancestor incomplete node reached by backtrack
				//node.nav[refPort] = NodeMemory0.IN_EXPLORATION;
				node.lastPort = refPort;
				print("- forward (ref-port #" + refPort + ", eff-port #1)");
				this.actionInProgress = ExplorationAction.FORWARD;
				return 1;
			
			} else if (node.id != this.initialNode) {
				//backtracks
				print("- complete, navdata: " + node.toString());
				print("- backtrack (ref-port #0, eff-port #1)");
				this.actionInProgress = ExplorationAction.BACKTRACK;
				return 1; 
			
			} else {
				print("- complete, navdata: " + node.toString());
				print("=> FINISHED ALL! Turn " + nextTurn);
				this.actionInProgress = ExplorationAction.FINISH;
				return -1; //stay in the same node 
			}

		} else { //for finish or null
			return -1;
			
		}
	
	}

}
