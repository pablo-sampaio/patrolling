package exploration.fixed_order;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import exploration.util.NodeMemory;


/**
 * A DFS-based solution for 1 agent solving the <b>exploration problem with fixed-order exits with an additional constraint</b>.<br><br> 
 * 
 * The problem is similar to the one described in class {@link MultiagentExplorationWithFixedOrder}, which this class extends.
 * 
 * Adicionalmente, porem, esta classe assume que o agente NAO sabe a saida 
 * (terminal de aresta) pela qual ele chegou ao seu no atual, no caso de uma aresta simetrica. A informacao chega 
 * para esta classe, mas nao e usada pelo algoritmo. <br><br>
 * 
 * Apesar de nao ser uma caracteristica exigida do problema, este algoritmo assume que o grafo e simetrico.<br><br>
 *
 * O problema pode modelar um robo movendo-se por um ambiente modelado como um grafo, onde os nos sao, 
 * potencialmente, pontos de bifurcacao do ambiente, onde o robo tem senso absoluto de direção (e.g. por meio de uma 
 * bussola, etc), que ele usa para ordenar as arestas em um sentido especifico (horário ou anti-horário). <br><br>
 * 
 * Aparentemente, este algoritmo requer que o agente de <b>4x|E|</b> passos. <br><br>
 * 
 * @see exploration.relative_order.MultiagentExplorationWithRelativeOrder
 * 
 * @author Pablo A. Sampaio
 */
public class DfsExplorer1 extends MultiagentExplorationWithFixedOrder {
	
	public DfsExplorer1() {
		super("DfsExplorer-1");
	}

	@Override
	public ExplorerAgentFixedOrder[] createTeam(int agents, int totalTime) {
		ExplorerAgentFixedOrder[] team = new ExplorerAgentFixedOrder[agents];
		for (int i = 0; i < agents; i++) {
			team[i] = new DfsExplorer1Agent(System.out);
		}
		return team;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

}


class DfsExplorer1Agent extends ExplorerAgentFixedOrder {
	private int destinyNode_dbg; //destiny node, used for debug only
	
	private Map<Integer,NodeMemory> navData;  //map { node-> ordered exits } 
	                                             //each array can be seen as a mapping { exit number -> node id }

	private LinkedList<Integer> predecessorsStack;
	private boolean forwardMove;


	DfsExplorer1Agent(PrintStream teamLog) {
		super(teamLog);
	}

	@Override
	public void onStart(int initialNode, int initialNodeNumEdges) {
		print("START");
		this.navData = new HashMap<>();
		this.predecessorsStack = new LinkedList<>();
		this.destinyNode_dbg = -1;
		this.forwardMove = false; //important for avoiding setting navData in the first iteration
	}

	@Override
	public void onTurn(int nextTurn) {
		//does nothing
	}

	@Override
	public int onArrivalInNode(int nodeId, int nextTurn, int _notUsed, int numEdges) {
		print("IN NODE " + nodeId);
		if (destinyNode_dbg != -1 && nodeId != destinyNode_dbg) { print("Error: unexpected neighbor - " + nodeId + " != " + destinyNode_dbg);	}
		
		print("- preds: " + predecessorsStack);
		print("- navdata: " + navData.toString());

		if ( forwardMove ) {
			int lastNode = predecessorsStack.getLast();
			NodeMemory lastNodeNeighbors = navData.get(lastNode);

			print("- navData[node " + lastNode + "] += (exit #" + lastNodeNeighbors.getCurrentPort() + " -> node " + nodeId + ")");
			lastNodeNeighbors.setNodeForCurrentPort(nodeId);
			lastNodeNeighbors.advanceToNextUnexploredPort();
		}
		
		NodeMemory neighbors = navData.get(nodeId);
		
		//for an undiscovered node
		if (neighbors == null) {
			print("- new");			
			neighbors = new NodeMemory(numEdges);
			navData.put(nodeId, neighbors);
			predecessorsStack.add(nodeId);
			destinyNode_dbg = -1;

			forwardMove = true;
			return neighbors.getCurrentPort();
			
		} else if (neighbors.isComplete()) {
			
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
			return nextExit;
		
		} else {
			//node already visited, but incomplete
			int nextExit = neighbors.getCurrentPort();
			predecessorsStack.add(nodeId);
			print("- forward (exit #" + nextExit + ")");
			
			destinyNode_dbg = -1;
			forwardMove = true;
			return nextExit;
		}
		
	}

}

