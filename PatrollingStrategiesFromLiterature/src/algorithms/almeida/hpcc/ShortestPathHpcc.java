package algorithms.almeida.hpcc;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import yaps.graph.Edge;
import yaps.graph.algorithms.GraphAlgorithm;
import yaps.util.priority_queue.BinHeapPQueue;
import yaps.util.priority_queue.PQueue;
import yaps.util.priority_queue.PQueueElement;


/**
 * An adapted version of Dijkstra's shortest path algorithm used by HPCC strategy.
 * 
 * @author Pablo A. Sampaio
 */
class ShortestPathHpcc extends GraphAlgorithm {
	private HpccStrategy hpccStrategy;
	private double[] boundsOfEdges;   // 2-sized array: { min-edge-distance, max-edge-distance }
	
	ShortestPathHpcc(HpccStrategy hpcc) {
		super(hpcc.getGraph());
		this.hpccStrategy = hpcc;
		
		this.boundsOfEdges = new double[]{ Double.MAX_VALUE, 0.0d };
		for (int v = 0; v < graph.getNumNodes(); v++) {
			List<Edge> edgeList = graph.getOutEdges(v);
			for (Edge e : edgeList) {
				if (e.getLength() < boundsOfEdges[0]) {
					boundsOfEdges[0] = e.getLength();
				}
				if (e.getLength() > boundsOfEdges[1]) {
					boundsOfEdges[1] = e.getLength();
				}
			}
		}
	}
	
	Queue<Integer> compute(int sourceNode, int targetNode) {
	    int numNodes = graph.getNumNodes();

		NodeInfo[] nodeInfo = new NodeInfo[numNodes];
		for (int node = 0; node < numNodes; node ++) {
			nodeInfo[node] = new NodeInfo(node, (node == sourceNode)? 0.0d : Double.POSITIVE_INFINITY);
		}

		PQueue<NodeInfo> frontier = new BinHeapPQueue<NodeInfo>(nodeInfo);

		int current;
		double succCost;

	    while (! frontier.isEmpty()) {
	    	current = frontier.removeMinimum().id;
	    	//System.out.printf(" > u=%d\n", u);

	    	if (current == targetNode) {
                return createPath(nodeInfo, sourceNode, targetNode);
	    	}

	    	for (Edge edge : graph.getOutEdges(current)) {
	    		int succ = edge.getTarget();
	    		int edgeDistance = graph.getEdgeLength(current, succ);
				
	    		succCost = nodeInfo[current].cost + hpccStrategy.calculateNodeValue(succ, current, edgeDistance, this.boundsOfEdges);

	    		if (succCost < nodeInfo[succ].cost) {
	    			nodeInfo[succ].changePath(current, succCost);
	    			frontier.decreaseKey(nodeInfo[succ]);
	    			//System.out.printf("   - mudou: vertice %d / peso: %5.2f\n", neighbor, edgeCost);
	    		}
			}
	    }
	    
	    return null;
	}
	
	private Queue<Integer> createPath(NodeInfo[] nodeInfo, int sourceNode, int targetNode) {
		LinkedList<Integer> path = new LinkedList<Integer>();
		
		NodeInfo currNode = nodeInfo[targetNode];
		path.addFirst(currNode.id);
		
		while (currNode.pred != -1) {
			currNode = nodeInfo[currNode.pred];
			path.addFirst(currNode.id);
		}		
		
		assert(sourceNode == currNode.id);
		return path;
	}

	// classe auxiliar, faz a PQueue ordenar pelo custo
	private class NodeInfo extends PQueueElement {
		private final int id;
		private int pred;
		private double cost;
		
		NodeInfo(int nodeId, double cost) {
			this.id = nodeId;
			this.pred = -1;
			this.cost = cost;
		}
		
		void changePath(int parentNode, double newCost) {
			this.pred = parentNode;
			this.cost = newCost;
		}

		@Override
		public double getKey() {
			return cost;
		}
	}
	
}
