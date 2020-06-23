package yaps.graph.algorithms;

import yaps.graph.Graph;
import yaps.graph.GraphDataRepr;
import yaps.graph.Path;


/**
 * Calculates the shortest paths between any pair of nodes.
 * The graph should not have negative cycles. 
 * 
 * @author Pablo A. Sampaio
 */
public class AllShortestPaths extends GraphAlgorithm {
	//outputs
	private int[][] distance;
	private int[][] predecessor;
	private int[][] successor;

	public AllShortestPaths(Graph g) {
		super(g);
	}
	
	public void compute() {
		int numVertices = graph.getNumNodes();

		distance = new int[numVertices][numVertices];
		predecessor = new int[numVertices][numVertices];
		successor = new int[numVertices][numVertices];

		for (int v = 0; v < numVertices; v ++) {
			for (int u = 0; u < numVertices; u++) {
				if (v == u) {
					distance[v][u] = 0;
					predecessor[v][u] = -1;
					successor  [v][u] = -1;
				
				} else if (graph.existsEdge(v, u)) {
					distance[v][u] = graph.getEdgeLength(v,u);
					predecessor[v][u] = v;
					successor  [v][u] = u;
					
				} else {
					distance[v][u] = INFINITE;
					predecessor[v][u] = -1;
					successor  [v][u] = -1;
				}
			}
		}
		
		// escolhe vertice intermediario
		for (int k = 0; k < numVertices; k++) {
			// escolhe origem
	  		for (int i = 0; i < numVertices; i++) {
				// escolhe destino
	    		for (int j = 0; j < numVertices; j++) {
	    			// se i-->k + k-->j for menor do que o caminho atual i-->j
	      			if ((distance[i][k] + distance[k][j]) < distance[i][j]) {
	        			// entao reduz a distancia do caminho i-->j fazendo i-->k-->j
	        			distance[i][j] = distance[i][k] + distance[k][j];
	        			predecessor[i][j] = predecessor[k][j];
	        			successor  [i][j] = successor[i][k];
	      			}
	    		}
	  		}
		}

	}
	
	/**
	 * Returns "true" iff there is a path from 'source' to 'destiny'.
	 */
	public boolean existsPath(int source, int destiny) {
		return distance[source][destiny] != INFINITE;
	}

	/**
	 * Returns the path from 'source' to 'destiny' or null if no such path exists.
	 */
	public Path getPath(int source, int destiny) {
		Path path;
		
		if (distance[source][destiny] != INFINITE) {
			path =  new Path(this.graph);
			getPathInternal(source, destiny, path);
		} else {
			path = null;
		}

		return path;
	}

	private void getPathInternal(int source, int destiny, Path path) {
		if (destiny == source) {
			path.add(destiny);
		} else {
			getPathInternal(source, predecessor[source][destiny], path);
			path.add(destiny);
		}
	}
	
	/**
	 * Returns the node immediately before 'destiny' in the minimum-cost path from 'source' to 'destiny'.
	 * Nodes 'source' and 'destiny' can't be the same.
	 */
	public int getDestinyPredecessor(int source, int destiny) {
		return predecessor[source][destiny];
	}
	
	/**
	 * Returns the node immediately after 'source' in the minimum-cost path from 'source' to 'destiny'.
	 * Nodes 'source' and 'destiny' can't be the same.
	 */
	public int getSourceSuccessor(int source, int destiny) {
		return successor[source][destiny];
	}
	
	/**
	 * Returns the minimum distance from 'source' to 'destiny'.
	 */
	public int getDistance(int source, int destiny) {
		if (distance[source][destiny] == INFINITE) {
			return Integer.MAX_VALUE;
		} else {
			return distance[source][destiny];
		}
	}

	/**
	 * Returns a graph with the edges representing the shortest paths 
	 * and the weights of the edges given by the minimum distance.
	 */
	public Graph toDistancesGraph() {
		int order = this.distance.length;
		Graph graph = new Graph(order, GraphDataRepr.MIXED);
		
		boolean directed = this.graph.getNumDirectedEdges() > 0;
		
		for (int v = 0; v < order; v++) {
			for (int x = 0; x < order; x++) {
				if (distance[v][x] != INFINITE) {
					if (directed) {
						graph.addDirectedEdge(v, x, distance[v][x]);
					} else if (v <= x) {
						graph.addUndirectedEdge(v, x, distance[v][x]);
					}
				}
			}
		}
		
		return graph;
	}
	
}
