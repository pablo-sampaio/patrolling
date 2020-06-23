package yaps.graph;


/**
 * Represents a directed edge (or arc) "source -> target".
 * 
 * @author Pablo A. Sampaio
 */
public class Edge {
	private int identifier;
	
	private int sourceId;
	private int targetId;
	
	private int length;
	private boolean directed;
	
	Edge(int ident, int source, int target, int weight, boolean directed) {
		this.identifier = ident;
		this.sourceId = source;
		this.targetId = target;
		this.length = weight;
		this.directed = directed;
	}

	Edge(int ident, int source, int target, int weight) {
		this(ident, source, target, weight, true);
	}

	public Edge(int source, int target, int weight, boolean directed) {
		this(-1, source, target, weight, true);
	}

	public Edge(int source, int target) {
		this(-1, source, target, 1, true);
	}
	
	/**
	 * The unique sequential identifier of this edge. If the edge is undirected,
	 * there is another instance of this class in the same graph, with the same id,
	 * but with the endnodes (source and target) exchanged.
	 */
	public int getId() {
		return this.identifier;
	}

	/**
	 * Returns the node from which this edge "departs".
	 * Also called "head" node.  
	 */
	public int getSource() {
		return sourceId;
	}

	/**
	 * Returns the node in which this edge "arrives".
	 * Also called "tail" node.  
	 */
	public int getTarget() {
		return targetId;
	}
	
	public int getOtherEndNode(int anEndNode) {
		if (anEndNode == sourceId) {
			return targetId;
		} else if (anEndNode == targetId) {
			return sourceId;
		} else {
			throw new Error("Invalid end-node: " + anEndNode);
		}
	}

	public int getLength() {
		return length;
	}
	
	public boolean isDirected() {
		return this.directed;
	}
	
	/**
	 * Tests if the endings of the edges are the same. If the edge is undirected, it ignores
	 * the order of the endnodes (i.e. the source of one edge may match the target of the other).
	 * Does not check identifier and length. It is useful for searching edges. 
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Edge)) {
			return false;
		}
		
		Edge other = (Edge)o;

		/* PAS: este método tem que comparar sem olhar a 'direcionalidade' da aresta,
		 * por conta do uso (implicito) feito deste metodo em funcoes como Graph.existsEdge() 
		 * Alternativa: criar uma classe só para este tipo de comparação (EdgeMatcher, digamos) 
		 */ 
		
		return this.sourceId == other.sourceId && this.targetId == other.targetId;
	}

	public boolean equalsDetailed(Object o) {
		if (!(o instanceof Edge)) {
			return false;
		}
		
		Edge other = (Edge)o;

		if (this.directed != other.directed) {
			return false;
		} else if (this.directed) {
			return this.sourceId == other.sourceId && this.targetId == other.targetId;
		} else {
			return (this.sourceId == other.sourceId && this.targetId == other.targetId)
					|| (this.sourceId == other.targetId && this.targetId == other.sourceId);
		}
	}
	
	public String toString() {
		if (this.directed) {
			return /*"e" + this.identifier +*/ "(n" + this.sourceId + "->" + "n" + this.targetId + ", " + this.length +")";
		} else {
			return /*"e" + this.identifier +*/ "(n" + this.sourceId + "--" + "n" + this.targetId + ", " + this.length +")";
		}
	}
	
}
