package yaps.graph;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * This class represents <b>weighted graphs</b> (networks). Self-loops are allowed. Parallel edges 
 * are allowed only when using <b><code>GraphDataRepr.LISTS</code></b>.
 * <br><br>
 * Edges may be either <b>directed or undirected</b>. A graph may also have edges of both types. 
 * Undirected edges are similar to a pair of symmetric directed edges, but with the same 
 * identifiers (and the same lengths). 
 * <br><br>
 * In a graph with <b>N</b> nodes, nodes are represented with integers from 0 to N-1. If there 
 * are <b>M</b> edges, their identifiers range from 0 to M-1. Each undirected edge, although similar
 * to two directed edges, count only once.
 * 
 * @author Pablo A. Sampaio
 */
public class Graph {
	public static int NO_EDGE_LENGTH = 0; //TODO: usar Integer.MAX_VALUE? rever algoritmos de fluxo
	
	private int numNodes;
	private int numEdges;
	protected int numDirectedEdges;
	protected int numUndirectedEdges;

	private Edge[][]     matrix;      // adjacency matrix
	private List<Edge>[] adjacencies; // adjacency lists (one for each node)
	
	private List<Edge> edges;
	
	private  GraphDataRepr representation; // indicate which of the structures above are used
	
	private boolean parallelEdges;
	
	public Graph(int numVertices) {
		this(numVertices, GraphDataRepr.LISTS);
	}
	
	public List<Edge> getEdges() {
		return edges;
	}

	@SuppressWarnings("unchecked")
	public Graph(int numVertices, GraphDataRepr r) {
		this.numNodes = numVertices;
		this.edges = new LinkedList<Edge>();
		this.numEdges = this.numDirectedEdges = this.numUndirectedEdges = 0;
		this.representation = (r == null) ? GraphDataRepr.LISTS : r;
		this.parallelEdges = false;
		
		if (r != GraphDataRepr.LISTS) {
			matrix = new Edge[numVertices][numVertices];
		}
		if (r != GraphDataRepr.MATRIX) {
			adjacencies = new LinkedList[numVertices];
			for (int i = 0; i < numVertices; i++) {
				adjacencies[i] = new LinkedList<Edge>();
			}
		}
	}

	public int addDirectedEdge(int v, int u) {
		return addDirectedEdge(v, u, 1);
	}
	
	public int addDirectedEdge(int v, int u, int length) {
		if (existsEdge(v, u)) {
			this.parallelEdges = true;
			if (this.representation != GraphDataRepr.LISTS) { 
				throw new IllegalArgumentException("Edge " + v + "->" + u + " is parallel to an existing edge. "
						+ "Parallel edges can only be represented when using GraphDataRepr.LISTS");
			}
		}
		int id = this.numEdges;
		this.addEdgeInternal(id, v, u, length, true);
		this.numEdges ++;
		Edge ed = new Edge(-1,v, u, length, true);
		edges.add(ed);
		this.numDirectedEdges ++;
		return id;
	}
	
	public int addUndirectedEdge(int v, int u) {
		return addUndirectedEdge(v, u, 1);
	}
	
	public int addUndirectedEdge(int v, int u, int length) {
		if ( existsEdge(v, u) || existsEdge(u, v) ) {
			this.parallelEdges = true;
			if (this.representation != GraphDataRepr.LISTS) {
				throw new IllegalArgumentException("Edge " + v + "-" + u + " is parallel to an existing edge. "
						+ "Parallel edges can only be represented when using GraphDataRepr.LISTS");
			}
		}		
		int id = this.numEdges; 
		this.addEdgeInternal(id, v, u, length, false);
		if (v != u) { //avoids two entries for each self-loop
			this.addEdgeInternal(id, u, v, length, false);
		}
		this.numEdges ++;
		Edge ed = new Edge(-1,v, u, length, false);
		edges.add(ed);
		this.numUndirectedEdges ++;

		return id;
	}
	
	private void addEdgeInternal(int id, int v, int u, int length, boolean directed) {
		Edge edge = new Edge(id, v, u, length, directed);
		
		if (representation != GraphDataRepr.MATRIX) {
			adjacencies[v].add(edge);
		}
		if (representation != GraphDataRepr.LISTS) {
			matrix[v][u] = edge;
		}
	}
	
	public int addEdge(int v, int u, int length, boolean directed) {
		if (directed) {
			return addDirectedEdge(v, u, length);
		} else {
			return addUndirectedEdge(v, u, length);
		}
	}

	public int getNumNodes() {
		return this.numNodes;
	}
	public int getNumVertices() {
		return this.numNodes;
	}

	/**
	 * Returns the total number of edges (either directed or undirected).
	 */
	public int getNumEdges() {
		return this.numEdges;
	}
	
	/**
	 * Returns the number of directed edges.
	 */
	public int getNumDirectedEdges() {
		return this.numDirectedEdges;
	}
	
	/**
	 * Returns the total number of undirected edges. Although they appear in both
	 * directions (like a pair of symmetric directed edges), they count only once.
	 */
	public int getNumUndirectedEdges() {
		return this.numUndirectedEdges;
	}

	//otimizado para: matrix, mixed
	public boolean existsEdge(int v, int u) {
		if (representation != GraphDataRepr.LISTS) {
			return matrix[v][u] != null;
		} else {
			return adjacencies[v].contains(new Edge(v,u));
		}
	}

	//otimizado para: matrix, mixed
	public int getEdgeLength(int source, int target) {
		if (representation != GraphDataRepr.LISTS) {
			if (matrix[source][target] == null) {
				return NO_EDGE_LENGTH;
			}
			return matrix[source][target].getLength();
		
		} else {
			//TODO: lan�ar exce��o se o grafo tiver aresta paralela?
			Edge edgeCopy = new Edge(source,target);
			for (Edge edge : adjacencies[source]) {
				if (edge.equals(edgeCopy)) {
					return edge.getLength();
				}
			}
			return NO_EDGE_LENGTH;
		}
	}

	public Edge getEdge(int source, int target) {
		if (representation != GraphDataRepr.LISTS) {
			return matrix[source][target];
		
		} else {
			//TODO: lan�ar exce��o se o grafo tiver aresta paralela?
			Edge edgeCopy = new Edge(source,target);
			for (Edge edge : adjacencies[source]) {
				if (edge.equals(edgeCopy)) {
					return edge;
				}
			}
			return null;
		}
	}
	
	//otimizado para: lists
	//obs.: getOutEdges is more memory-efficient and may be faster
	//mesma ordem retornada pelo getOutEdges()
	public List<Integer> getSuccessors(int node) {
		List<Integer> succ = new LinkedList<Integer>();
		
		if (representation != GraphDataRepr.MATRIX) {
			for (int i = 0; i < adjacencies[node].size(); i++) {
				succ.add(adjacencies[node].get(i).getTarget());
			}
			
		} else {
			for (int u = 0; u < matrix.length; u++) {
				if (matrix[node][u] != null) {
					succ.add(u);
				}
			}
		}
		
		return succ;
	}
	
	//otimizado para: lists 
	/**
	 * Return the undirected edges incident to the given node and
	 * the directed edges that point away from the node. In both
	 * cases the edges informs the given node as its "source" node.  
	 */
	public List<Edge> getOutEdges(int source) {
		List<Edge> outEdges;
		
		if (representation != GraphDataRepr.MATRIX) {
			outEdges = Collections.unmodifiableList(adjacencies[source]);
			
		} else {
			outEdges = new LinkedList<Edge>();
			for (int u = 0; u < matrix.length; u++) {
				if (matrix[source][u] != null) {
					outEdges.add(matrix[source][u]);
				}
			}
		}
		
		return outEdges;
	}

	public int getOutEdgesNum(int source) {
		int outEdgesNum = 0;
		
		if (representation != GraphDataRepr.MATRIX) {
			outEdgesNum = adjacencies[source].size();
			
		} else {
			for (int u = 0; u < matrix.length; u++) {
				if (matrix[source][u] != null) {
					outEdgesNum ++;
				}
			}
		}
		
		return outEdgesNum;
	}
	
	/**
	 * Indicates whether the graph has or not parallel edges. Two edges are considered <b>parallel</b> 
	 * iff they satisfy one of these conditions: 
	 * <ul> 
	 * <li>They are two directed edges and both have a common source node and a common target node.
	 * <li>One of them is an undirected edge (and the other may be any kind of edge) and they have the 
	 * same pair of endnodes (same source and same target; or the target of one is the source of the 
	 * other and vice-versa).
	 * </ul>
	 * Parallel edges can only be represented when using GraphDataRepr.LISTS.
	 */
	public boolean hasParallelEdges() {
		return this.parallelEdges;
	}

	//� complicado, por conta das arestas paralelas
//	public boolean isSymmetrical() {		
//		for (int v = 0; v < getNumNodes(); v++) {
//			for (Edge edge : this.getOutEdges(v)) {
//				if (! existsEdge(edge.getTarget(), edge.getSource()) 
//						|| getEdgeLength(edge.getTarget(), edge.getSource()) != getEdgeLength(edge.getSource(), edge.getTarget())) {
//					return false;
//				}
//			}
//		}
//		
//		return true;
//	}

	//otimizado para: listas
	public Graph toUnitaryWeights() {
		return toUnitaryWeights(GraphDataRepr.LISTS);
	}
	
	//otimizado para: listas
	public Graph toUnitaryWeights(GraphDataRepr r) {
		int numVertices = getNumVertices();
		Graph gu = new Graph(numVertices, r);

		List<Edge> outEdges;		
		for (int v = 0; v < numVertices; v++) {
			outEdges = getOutEdges(v);
			
			for (Edge e : outEdges) {
				if (e.isDirected()) {
					gu.addDirectedEdge(v, e.getTarget(), 1);
				} else if (v <= e.getTarget()) {
					gu.addUndirectedEdge(v, e.getTarget(), 1);
				}
			}
		}
		
		return gu;
	}

	public GraphDataRepr getDataRepr() {
		return representation;
	}
	
	//TODO: rever, proibir sair do tipo LISTS se houver arestas paralelas
	@SuppressWarnings("unchecked")
	public void changeRepresentation(GraphDataRepr newRepresentation) {
		GraphDataRepr oldRepresetation = getDataRepr();
		if (oldRepresetation == newRepresentation) {
			return;
		}
		if (oldRepresetation == GraphDataRepr.LISTS && this.hasParallelEdges()) {
			throw new UnsupportedOperationException("Cannot change from GraphDataRepr.LISTS because the graph has parallel edges.");
		}
		
		List<Edge>[] adj_ = null;
		Edge[][] mat_ = null;
		
		int numVertices = getNumNodes();
		List<Edge> outEdges;

		if (newRepresentation != GraphDataRepr.LISTS 
				&& oldRepresetation == GraphDataRepr.LISTS) {
			mat_ = new Edge[numVertices][numVertices];
		}
		if (newRepresentation != GraphDataRepr.MATRIX
				&& oldRepresetation == GraphDataRepr.MATRIX) {
			adj_ = new LinkedList[numVertices];
			for (int i = 0; i < numVertices; i++) {
				adj_[i] = new LinkedList<Edge>();
			}
		}
		
		for (int v = 0; v < numVertices; v++) {
			outEdges = getOutEdges(v);
			for (Edge e : outEdges) {
				if (mat_ != null) {
					mat_[v][e.getTarget()] = e;
				}
				if (adj_ != null) {
					adj_[v].add(e);
				}
			}
		}
		
		if (representation == GraphDataRepr.MATRIX) {
			this.adjacencies = null;
		}
		if (representation == GraphDataRepr.LISTS) {
			this.matrix = null;
		}
		if (adj_ != null) {
			this.adjacencies = adj_;
		}
		if (mat_ != null) {
			this.matrix = mat_;
		}
		
		this.representation = newRepresentation;
	}
	
	//TODO: rever, por conta das arestas paralelas
	public boolean equals(Object o) {
		if (! (o instanceof Graph)) {
			return false;
		}
		
		Graph other = (Graph)o;
		int numVertices = this.getNumNodes();
		
		if (numVertices != other.getNumNodes()) {
			return false;
		}
		if (this.getNumEdges() != other.getNumEdges()) {
			return false;
		}
		
		for (int v = 0; v < numVertices; v++) {
			for (int u = 0; u < numVertices; u++) {
				if (this.getEdgeLength(v, u) 
						!= other.getEdgeLength(v, u)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (representation != GraphDataRepr.LISTS) {
			builder.append("\n");
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix.length; j++) {
					builder.append(" ");
					builder.append(matrix[i][j]);
				}
				builder.append("\n");
			}
		
		}
		if (representation != GraphDataRepr.MATRIX) {
			builder.append("\n");
			for (int u = 0; u < adjacencies.length; u++) {
				builder.append("Adj[");
				builder.append(u);
				builder.append("] = ");
				builder.append(adjacencies[u]);
				builder.append("\n");
			}
		}
		
		return builder.toString();
	}
	
	public Graph getClone() {
		Graph g = new Graph(this.numNodes, this.representation);
		for (Edge e : this.getEdges()) {
			if (e.isDirected()) {
				g.addDirectedEdge(e.getSource(), e.getTarget(), e.getLength());
			} else {
				g.addUndirectedEdge(e.getSource(), e.getTarget(), e.getLength());
			}
		}		
		return g;
	}

}
