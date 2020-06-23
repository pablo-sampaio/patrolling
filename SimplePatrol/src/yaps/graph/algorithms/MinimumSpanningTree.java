package yaps.graph.algorithms;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.graph.GraphDataRepr;
import yaps.util.priority_queue.BinHeapPQueue;
import yaps.util.priority_queue.PQueue;
import yaps.util.priority_queue.PQueueElement;


/**
 * Prim's algorithm for finding a minimum cost spanning tree of an undirected graphs.
 * 
 * @author Pablo A. Sampaio
 */
public class MinimumSpanningTree extends GraphAlgorithm {
	private Graph mcsTree; //the minimum-cost spanning tree
	private int mcsTreeCost;
	
	public MinimumSpanningTree(Graph g) {
		super(g);
		reset(g);
	}
	
	public void reset(Graph g) {
		this.graph = g;
		this.mcsTree = new Graph(g.getNumNodes(), GraphDataRepr.LISTS);
		this.mcsTreeCost = -1;
	}

	public void compute() {
		if (mcsTreeCost != -1.0d) {
			return;
		}
		if (graph.getNumDirectedEdges() > 0) {
			throw new IllegalArgumentException("Graph with directed edges not allowed.");
		}
		
	    int numNodes = graph.getNumNodes();

		boolean[] chosen = new boolean[numNodes];
		PQueue<NodeInfo> frontier = new BinHeapPQueue<NodeInfo>(numNodes);
		NodeInfo[] nodeInfo = new NodeInfo[numNodes];

		int root = 0; //can be any other node...		
		for (int node = 0; node < numNodes; node ++) {
			chosen[node] = false;
			nodeInfo[node] = new NodeInfo(node, (node == root)? 0 : INFINITE);			
			frontier.add(nodeInfo[node]);
		}
		//System.out.printf("\n[PRIM, root=%d]\n", root);

		int u;
		int edgeCost;

		this.mcsTreeCost = 0;
		
	    while (! frontier.isEmpty()) {
	    	u = frontier.removeMinimum().id;
	    	chosen[u] = true;
	    	//System.out.printf(" > u=%d\n", u);

	    	if (u != root) {
	    		edgeCost = nodeInfo[u].costToLink;
	    		this.mcsTree.addUndirectedEdge(u, nodeInfo[u].parent, edgeCost);
	    		this.mcsTreeCost += edgeCost;
	    		//System.out.printf("   - INSERIDA: (%d,%d), peso: %5.2f\n", nodeInfo[u].parent, u, edgeCost);
	    	}

	    	for (Edge edge : graph.getOutEdges(u)) {
	    		int neighbor = edge.getTarget();
				edgeCost = edge.getLength();

	    		if (!chosen[neighbor] && (edgeCost < nodeInfo[neighbor].costToLink)) {
	    			nodeInfo[neighbor].changeLink(u, edgeCost);
	    			frontier.decreaseKey(nodeInfo[neighbor]);
	    			//System.out.printf("   - mudou: vertice %d / peso:%5.2f\n", neighbor, edgeCost);
	    		}
			}
	    }
	    
	}
	
	public Graph getMinimumTree() {
		return this.mcsTree;				
	}
	
	public int getMinimumTreeCost() {
		return this.mcsTreeCost;
	}

	// classe auxiliar, faz a PQueue ordenar pelo
	// custo de ligar � �rvore
	class NodeInfo extends PQueueElement {
		private int id;
		
		private int parent;
		private int costToLink;
		
		NodeInfo(int nodeId, int cost) {
			id = nodeId;
			parent = -1;
			costToLink = cost;
		}
		
		void changeLink(int parentNode, int edgeCost) {
			this.parent = parentNode;
			this.costToLink = edgeCost;
		}

		@Override
		public double getKey() {
			return costToLink;
		}
	}
}
