package algorithms.edge_counting;

import java.util.LinkedList;
import java.util.List;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.RandomUtil;

public class EdgeCountingAgent1 extends SimpleAgent{
	
	private Graph graph;
	private AdjacencyMatrix<Integer> adjacencyMatrix;
	private int originNode;

	public EdgeCountingAgent1(Graph g, AdjacencyMatrix<Integer> adjacencyMatrix){
		super(System.out);
		this.graph = g;
		this.adjacencyMatrix = adjacencyMatrix;
		this.originNode = -1;
		
	}
	
	@Override
	public void onStart() {
		// does nothing

	}

	@Override
	public void onTurn(int nextTurn) {
		// does nothing

	}

	@Override
	public int onArrivalInNode(int nextTurn) {		
		int currentNode = position.getCurrentNode();
		
		if(originNode != -1){
			adjacencyMatrix.set(originNode, currentNode, adjacencyMatrix.get(originNode, currentNode) + 1);
		}
		
		originNode = position.getCurrentNode();
		
		List<Edge> neighbors = graph.getOutEdges(currentNode);
		
		int lessVisits = adjacencyMatrix.get(currentNode, neighbors.get(0).getOtherEndNode(currentNode));
		
		for(Edge e : neighbors){
			if(adjacencyMatrix.get(currentNode, e.getOtherEndNode(currentNode)) < lessVisits){
				lessVisits = adjacencyMatrix.get(currentNode, e.getOtherEndNode(currentNode));
			}
			
		}
		
		List<Integer> ties = new LinkedList<Integer>();
		
		for(Edge e : neighbors){
			if(adjacencyMatrix.get(currentNode, e.getOtherEndNode(currentNode)) == lessVisits){
				
				ties.add(e.getOtherEndNode(currentNode));
				
			}
			
		}
		
		return RandomUtil.chooseAtRandom(ties);
	}

}
