package exploration.relative_order;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import exploration.util.NodeMemory;


/**
 * Based on the same idea used in the multi-agent versions of DfsExplorer3, with the same behavior,
 * but it is simpler -- e.g. the stack is no more required. <br><br>
 * 
 * In print messages and in code, "exits" are now called "ports", "reference (port)" is used instead
 * of "canonical (exit)", and "effective" is used instead of "relative". <br><br>
 * 
 * This agent requires O(1) memory space, and writes O(degree) data in each node.<br><br>
 * 
 * Published in <a href="http://dx.doi.org/10.1109/LARS-SBR-WRE48964.2019.00063">LARS/SBR 2019</a>.
 * 
 * @author Pablo A. Sampaio
 */
public class DfsExplorer3_ECEP extends MultiagentExplorationWithRelativeOrder {
	private PrintStream output;
	
	public DfsExplorer3_ECEP() {
		this(false);
	}
	
	public DfsExplorer3_ECEP(boolean verboseMode) {
		super("DfsExplorer-3-ECEP");
		if (verboseMode) {
			this.output = System.out;
		}
	}

	@Override
	public ExplorerAgentRelativeOrder[] createTeam(int agents, int totalTime) {
		ExplorerAgentRelativeOrder[] team = new ExplorerAgentRelativeOrder[agents];
		Map<Integer,NodeMemory> sharedNavData = new HashMap<>(); 
		
		for (int i = 0; i < agents; i++) {
			team[i] = new DfsExplorer3Agent_ECEP(this.output, sharedNavData);
		}
		return team;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}


class DfsExplorer3Agent_ECEP extends ExplorerAgentRelativeOrder {
	private Map<Integer,NodeMemory> navData; //mapping { node --> indexed-exits } 
	                                         //the 'indexed-exits' identify the edges, but are also a mapping { canonical-port --> target-node }
											 //the canonical ports are the indexed order of the edges found when the agent first arrives in the node
											 //if the agent gets back later, coming from a different node, the ports change and need to be converted
	private int lastNode;
	
	private enum ExplorationAction { START, FORWARD, BACKTRACK, FINISH };
	private ExplorationAction actionInProgress;

	DfsExplorer3Agent_ECEP(PrintStream teamLog, Map<Integer,NodeMemory> sharedNodeData) {
		super(teamLog);
		this.navData = sharedNodeData;
	}

	@Override
	public void onStart(int initialNode, int initialNodeEdges) {
		print("START");
		this.navData.put(initialNode, new NodeMemory(initialNodeEdges));
		this.lastNode = -1;
		this.actionInProgress = ExplorationAction.START;
	}

	@Override
	public void onTurn(int nextTurn) {
		//does nothing
	}
	
	@Override
	public int onArrivalInNode(int nodeId, int nextTurn, int numEdges) {
		print("IN NODE " + nodeId + ", turn " + nextTurn);

		NodeMemory nodeData = navData.get(nodeId);
		
		if (nodeData == null) {
			nodeData = new NodeMemory(numEdges);
			this.navData.put(nodeId, nodeData);
		}
		print("- navdata: " + navData.get(nodeId));
		
		// ATENCAO: para ficar igual ao artigo, falta acrescentar o peso da última aresta (diferença entre os turnos)
		int outPort = onArrivalInNodeInternal(nodeId, nodeData, nextTurn); 
		
		this.lastNode = nodeId;
		
		return outPort;
	}
	
	public int onArrivalInNodeInternal(int nodeId, NodeMemory nodeData, int nextTurn) {
		
		if ( actionInProgress == ExplorationAction.START ) {
			int nextReferencePort = nodeData.getCurrentPort(); //first time: returns 0
			
			nodeData.setInExploration(nextReferencePort); //this port/edge is being explored
			nodeData.advanceToNextUnexploredPort();
			print("- forward (ref-port #" + nextReferencePort + ", eff-port #" + nextReferencePort +")");
			nodeData.lastPort = nextReferencePort;
			actionInProgress = ExplorationAction.FORWARD;
			return nextReferencePort;
			
		} else if ( actionInProgress == ExplorationAction.FORWARD ) {
			return forwardArrive(nodeId, nodeData, nextTurn);
		
		} else if ( actionInProgress == ExplorationAction.BACKTRACK ) {
			return backtrackArrive(nodeId, nodeData, nextTurn);

		} else { //for finish or null
			return -1;
			
		}
	
	}

	private int forwardArrive(int nodeId, NodeMemory nodeData, int nextTurn) {
		boolean firstTime = !nodeData.fmarked && nodeData.getCurrentPort() == 0;
	
		//for an undiscovered node
		if (firstTime) {
			print("- first time");
			nodeData.fmarked = true;
			
			print("- navData += (ref-port #0 -> node " + lastNode + ")");
			nodeData.setNode(0, lastNode);
			nodeData.advanceToNextUnexploredPort();
		} 
		
		if (firstTime && !nodeData.isComplete()) {
			//a new (first-time) incomplete node
			int nextReferencePort = nodeData.getCurrentPort();
			nodeData.setInExploration(nextReferencePort); //this port/edge is being explored -- to be updated later
			nodeData.advanceToNextUnexploredPort();
			print("- forward (ref-port #" + nextReferencePort + ", eff-port #" + nextReferencePort + ")");
			nodeData.lastPort = nextReferencePort;
			
			actionInProgress = ExplorationAction.FORWARD;
			return nextReferencePort;
		
		} else {
			//print("- altready visited or complete, navdata: " + nodeData.toString());
			print(firstTime? "- complete, navdata: " + nodeData.toString() : "- already visited");
			print("- backtrack (eff-port #0)");
			
			actionInProgress = ExplorationAction.BACKTRACK;
			return 0;  //return through the 'entrance' port
			
		}
	}
	
	private int backtrackArrive(int nodeId, NodeMemory nodeData, int nextTurn) {
		int numEdges = nodeData.getNumNeighbors();
		
		print("- navData += (ref-port #" + nodeData.lastPort + " -> node " + lastNode + ")");
		nodeData.updateWith(nodeData.lastPort, lastNode);
		
		if (!nodeData.isComplete()) {
			//an ancestor incomplete node reached by backtrack
			int nextReferencePort = nodeData.getCurrentPort();
			int nextEffectivePort = (nextReferencePort - nodeData.lastPort + numEdges) % numEdges;
			
			nodeData.setInExploration(nextReferencePort); //this exit/edge is being explored -- to be updated in the synchronization
			nodeData.advanceToNextUnexploredPort();
			print("- forward (ref-port #" + nextReferencePort + ", eff-port #" + nextEffectivePort + ")");

			nodeData.lastPort = nextReferencePort;
			actionInProgress = ExplorationAction.FORWARD;
			return nextEffectivePort;

		} else if (nodeData.fmarked) {
			//backtracks
			int nextEffectivePort = (0 - nodeData.lastPort + numEdges) % numEdges; // calculation assuming reference port #0
			print("- complete, navdata: " + nodeData.toString());
			print("- backtrack (ref-port #0, eff-port #" + nextEffectivePort + ")");

			//nodeData.lastPort = 0; //not necessary -- will not be used
			actionInProgress = ExplorationAction.BACKTRACK;
			return nextEffectivePort; 

		} else {
			print("- complete, navdata: " + nodeData.toString());
			print("=> FINISHED ALL! Turn " + nextTurn);
			actionInProgress = ExplorationAction.FINISH;
			return -1; //stay in the same node
			
		}
	}

}
