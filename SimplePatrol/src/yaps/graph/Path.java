package yaps.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import yaps.graph.algorithms.AllShortestPaths;


/**
 * Represents a path as a list of nodes, with repetitions of nodes allowed 
 * (i.e. its not necessarily a simple path). 
 *
 * @author Pablo A. Sampaio
 */
@SuppressWarnings("serial")
public class Path extends LinkedList<Integer> {
	private Graph graph;

//it may be useful, but should be avoided
//	public Path() {
//		super();
//	}
	
	public Path(Graph g) {
		super();
		this.graph = g;
	}
	
//	public Path(Collection<Integer> c) {
//		this(null, c);
//	}
	
	public Path(Graph g, Collection<Integer> c) {
		super(c);
		this.graph = g;
	}
	
	public void setGraph(Graph g) {
		this.graph = g;
	}
	
	public Graph getGraph() {
		return this.graph;
	}

	/**
	 * Tests if the (directed) edges used in this path really exist in the graph.
	 */
	public boolean isValid() {
		for (int i = 1; i < this.size(); i++) {
			if (! graph.existsEdge(this.get(i-1), this.get(i)) ) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Calculates the cost of the path in the given graph. 
	 * Returns -1 if this is not a valid path in the graph.
	 */
	public int getCost() {
		int cost = 0;
		
		for (int i = 1; i < this.size(); i++) {
			if (! graph.existsEdge(this.get(i-1), this.get(i)) ) {
				throw new IllegalArgumentException("grafo nao permite caminho de " + this.get(i-1) + " a " + this.get(i));
			}
			cost += graph.getEdgeLength(this.get(i-1), this.get(i));
		}
		
		return cost;
	}
	
	/**
	 * Tests if the start and end vertex of the path are the same 
	 * i.e. tests if this is a closed path (not necessarily simple).
	 */
	public boolean isCycle() {
		return (super.getFirst() == super.getLast());
	}
	
	/**
	 * Calculates the cost of the path in the given graph, considering that each (u,v) 
	 * edge has the cost of the shortest path from u to v. <br>
	 * Returns -1 if one the paths is not possible in the graph (e.g. if the graph is 
	 * not strongly connected).
	 * <br><br>
	 * It should return the same value as: <br>
	 * expandShortestPaths(graph).getCost() .
	 */
	public double getCostExpandingShortestPaths(AllShortestPaths shortest) {
		int n = this.size();
		int totalCost = 0;
		double distance;
		
		for (int i = 1; i < n; i++) {
			distance = shortest.getDistance(this.get(i-1), this.get(i));
			
			if (distance == Integer.MAX_VALUE) {
				throw new IllegalArgumentException("grafo nao permite caminho de " + this.get(i-1) + " a " + this.get(i));
			}
			
			totalCost += distance;
		}

		return totalCost;
	}
	
	/**
	 * Expand the given path by exchanging each edge (u,v) by the shortest 
	 * path from u to v. <br> 
	 * Returns null if one of the paths is not possible in the graph (e.g. if 
	 * the graph is not strongly connected).
	 */
	public Path expandUsingShortestPaths(AllShortestPaths shortest) {
		Path realPath = new Path(shortest.getGraph());	
		
		realPath.add(this.get(0));

		List<Integer> partialPath;

		for (int i = 1; i < this.size(); i++) {
			partialPath = shortest.getPath(this.get(i-1), this.get(i));
			
			if (partialPath == null) {
				throw new IllegalArgumentException("grafo nao permite caminho de " + this.get(i-1) + " a " + this.get(i));
			}
			
			partialPath.remove(0);
			realPath.addAll(partialPath);
		}
		
		return realPath;
	}	
	
	/**
	 * If it is a cycle (i.e. the start and end nodes are the same), rotates it
	 * to start with the first occurrence of the given vertex. Otherwise, returns null.
	 */
	public Path rotateCycle(int index) {
		if (!this.isCycle()) {
			return null;
		}
		
		Path newPath = new Path(this.graph);
		
		//from the index to the end
		for (int i = index; i < this.size(); i++) {
			newPath.add(this.get(i));
		}
		//from the second node (the first was added in the previous 'for', since it is the last node)
		//to the index (which is added a second time)
		for (int i = 1; i <= index; i++) {
			newPath.add(this.get(i));
		}
		
		return newPath;
	}

	public String toString() {
		return super.toString();
	}
	
//	public static void main(String[] args) {
//		Path p = new Path();
//		
//		p.add(0); p.add(2); p.add(4); p.add(5); p.add(0);
//		
//		System.out.println(p.isCycle());
//		System.out.println(p.rotateCycle(2));
//	}

}
