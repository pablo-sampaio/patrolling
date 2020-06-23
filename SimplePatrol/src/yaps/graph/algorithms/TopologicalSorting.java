package yaps.graph.algorithms;

import yaps.graph.Graph;

import java.util.LinkedList;
import java.util.List;


public class TopologicalSorting {
	private enum Color { WHITE, GREY, BLACK };
	private Color[] color;
	
	/**
	 * Sorts using depth-first-search
	 */
	public List<Integer> sort1(Graph graph) {
		LinkedList<Integer> order = new LinkedList<Integer>();
		
		color = new Color[graph.getNumNodes()];
		for (int v = 0; v < graph.getNumNodes(); v++) {
			color[v] = Color.WHITE;
		}

		for (int v = 0; v < graph.getNumNodes(); v++) {
			if (color[v] == Color.WHITE) {
				dfs(graph, v, order);
			}
		}
		
		return order;
	}
	
	private void dfs(Graph graph, int vertex, LinkedList<Integer> order) {
		color[vertex] = Color.GREY;
		
		for (int succ : graph.getSuccessors(vertex)) {
			if (color[succ] == Color.WHITE) {
				dfs(graph, succ, order);
			} else if (color[succ] == Color.GREY) {
				throw new UnsupportedOperationException("O grafo possui ciclo!");
			}
		}
		
		color[vertex] = Color.BLACK;
		order.addFirst(vertex);
	}
	
	/**
	 * Sorts using a specialized algorithm.
	 */
	public List<Integer> sort2(Graph graph) {
		LinkedList<Integer> order = new LinkedList<Integer>();
		
		LinkedList<Integer> sources = new LinkedList<Integer>();
		int numVertices = graph.getNumNodes();
		int indegree[] = new int[numVertices];
		
		for (int v = 0; v < numVertices; v++) {
			for (int u : graph.getSuccessors(v)) {
				indegree[u] ++;
			}
		}
		
		for (int v = 0; v < numVertices; v++) {
			if (indegree[v] == 0) {
				sources.add(v);
			}
		}
		
		while (! sources.isEmpty()) {
			int v = sources.remove();
			
			order.addLast(v);
			numVertices --;
			
			for (int u : graph.getSuccessors(v)) {
				indegree[u]--;
				if (indegree[u] == 0) {
					sources.add(u);
				}
			}
		}
		
		if (numVertices > 0) {
			throw new UnsupportedOperationException("O grafo possui ciclo!");
		}
		
		return order;
	}

}
