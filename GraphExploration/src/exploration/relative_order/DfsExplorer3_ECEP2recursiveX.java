package exploration.relative_order;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import exploration.util.NodeMemory0;


/**
 * A recursive version of ECEP2 {@link DfsExplorer3_ECEP2}. 
 * Requires more memory due to recursion. 
 * This version was proposed to prove properties.
 * 
 * @author Pablo A. Sampaio
 */
public class DfsExplorer3_ECEP2recursiveX extends MultiagentExplorationWithRelativeOrder {
	private PrintStream output;
	
	public DfsExplorer3_ECEP2recursiveX() {
		this(false);
	}
	
	public DfsExplorer3_ECEP2recursiveX(boolean verboseMode) {
		super("DfsExplorer-3-ECEP-v2-recursive-X");
		if (verboseMode) {
			this.output = System.out;
		}
	}

	@Override
	public ExplorerAgentRelativeOrder[] createTeam(int agents, int totalTime) {
		ExplorerAgentRelativeOrder[] team = new ExplorerAgentRelativeOrder[agents];
		Map<Integer,NodeMemory0> sharedNavData = new HashMap<>(); 
		
		for (int i = 0; i < agents; i++) {
			team[i] = new DfsExplorer3Agent_ECEP2_Recursive(this.output, sharedNavData);
		}
		return team;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}


class ECEPRecThread extends Thread {
	private int effectivePort;
	private NodeMemory0 nodeMemory;
	private int turn;
	private boolean exploring;
	private DfsExplorer3Agent_ECEP2_Recursive agent;
	
	ECEPRecThread(DfsExplorer3Agent_ECEP2_Recursive ag) {
		this.effectivePort = -1;
		this.nodeMemory = null;
		this.turn = 0;
		this.exploring = true;
		this.agent = ag;
	}
	
	@Override
	public void run() {
		try {

			while (this.nodeMemory == null) {
				Thread.yield();
			}
			
			NodeMemory0 V = this.nodeMemory;
			
			// visits the first neighbor (port 0)
			V.lastPort = 0;
			print("- forward (ref-port #0, eff-port #0)");
			NodeMemory0 X = traverse(0);
			DFS_visit(X, V.id);
			V.nav[0] = X.id;
			print("- navData += (ref-port #0 -> node " + X.id + ")");
			
			while ((V.lastPort + 1) < V.delta) {
				V.lastPort = V.lastPort + 1;
				print("- forward (ref-port #" + V.lastPort + ", eff-port #1)");
				X = traverse(1);
				
				DFS_visit(X, V.id);
				
				V.nav[V.lastPort] = X.id;
				print("- navData += (ref-port #" + V.lastPort + " -> node " + X.id + ")");
			}
			
			print("- complete, navdata: " + V.toString());
			print("=> FINISHED ALL! Turn " + this.turn);
			
			this.exploring = false;
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void DFS_visit(NodeMemory0 V, int prevNode) throws InterruptedException {
		boolean firstVisit = (V.lastPort == -1);
		
		if (! firstVisit) {
			print("- already visited");
			print("- backtrack (eff-port #0)");
			traverse(0);
			return;
		}

		print("- first time");
		V.lastPort = 0;
		V.nav[0] = prevNode;
		print("- navData += (ref-port #0 -> node " + prevNode + ")");

		NodeMemory0 X;
		while ((V.lastPort + 1) < V.delta) {
			V.lastPort = V.lastPort + 1;
			print("- forward (ref-port #" + V.lastPort + ", eff-port #1)");
			X = traverse(1);
			
			DFS_visit(X, V.id);
			
			V.nav[V.lastPort] = X.id;
			print("- navData += (ref-port #" + V.lastPort + " -> node " + X.id + ")");
		}
		
		print("- complete, navdata: " + V.toString());	
		//if V.delta = 1, it has visited no neighbor (in the loop), so must traverse 0
		print("- backtrack (ref-port #0, eff-port #" + (1 % V.delta) + ")");
		traverse(1 % V.delta);
	}

	private void print(String msg) {
		agent.print(msg);
	}
	
	private NodeMemory0 traverse(int port) throws InterruptedException {
		synchronized (this) {
			this.effectivePort = port;
			this.nodeMemory = null;
		}
		
		while (this.nodeMemory == null) {
			Thread.yield();
		}
		
		NodeMemory0 result;
		synchronized (this) {
			result = this.nodeMemory;
			this.nodeMemory = null;
			this.effectivePort = -1; //nem precisava
		}
		return result;
	}

	public int setNodeAndDecideNextPort(NodeMemory0 node, int nextTurn) throws InterruptedException {
		if (! this.exploring) {
			return -1;
		}
		
		synchronized (this) {
			assert (this.nodeMemory == null && this.effectivePort == -1);
			this.nodeMemory = node;
			this.effectivePort = -1; //nem precisava
			this.turn = nextTurn;
		}

		while (this.effectivePort == -1) {
			Thread.yield();
			if (! this.exploring) {
				return -1;
			}
		}
		
		int port;
		synchronized (this) {
			port = this.effectivePort;
			this.effectivePort = -1;
		}

		return port;
	}

}


class DfsExplorer3Agent_ECEP2_Recursive extends ExplorerAgentRelativeOrder {
	private Map<Integer,NodeMemory0> navData; //mapping { node --> indexed-exits } 
	                                          //the 'indexed-exits' identify the edges, but are also a mapping { canonical-port --> target-node }
											  //the canonical ports are the indexed order of the edges found when the agent first arrives in the node
											  //if the agent gets back later, coming from a different node, the ports change and need to be converted
	private ECEPRecThread ecep;
	
	DfsExplorer3Agent_ECEP2_Recursive(PrintStream teamLog, Map<Integer,NodeMemory0> sharedNodeData) {
		super(teamLog);
		this.navData = sharedNodeData;
	}

	@Override
	public void onStart(int initialNode, int initialNodeEdges) {
		print("START");
		ecep = new ECEPRecThread(this);
		ecep.start();
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
		
		int outPort = -1;
		try {
			outPort = ecep.setNodeAndDecideNextPort(nodeData, nextTurn);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return outPort;
	}
	
}
