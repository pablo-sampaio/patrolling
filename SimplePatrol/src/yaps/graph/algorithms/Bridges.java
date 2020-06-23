package yaps.graph.algorithms;

import yaps.graph.Edge;
import yaps.graph.Graph;

import java.util.LinkedList;
import java.util.List;


//TODO: testar !!!!!!!!!!!

/**
 * Brute-force algorithm to find bridges in undirected graphs.
 * 
 * A bridge (also known as a cut-edge or an isthmus) is an edge whose
 * deletion increases the number of connected components. Equivalently,
 * an edge is a bridge if and only if it is not contained in any cycle
 * 
 * @author Pablo Sampaio
 */
public class Bridges {
	private Edge candidate;
	private boolean[] visited;

	public Bridges() {
		
	}
	
	//find the bridges (or cut-edges or isthmus)
	public List<Edge> findBridges(Graph g) {
		//TODO: garantir que � n�o-direcionado 
		
		List<Edge> bridges = new LinkedList<Edge>();
		List<Integer> neighbors;
		
		visited = new boolean[g.getNumVertices()];
		
		//a ideia consiste em escolher uma aresta e rodar uma DFS
		//para testar se a remo��o da aresta desconecta o grafo
		
		for (int v = g.getNumVertices() - 1; v >=0; v --) {
			neighbors = g.getSuccessors(v);
			
			for (Integer u : neighbors) {
				// para n�o analisar duas vezes a mesma aresta (j� que o grafo � n�o-direcionado)
				if (v < u) {
					candidate = new Edge(v,u);
					if (candidateDisconnects(g)) {
						bridges.add(candidate);
					}
				}
			}
		}
		
		return bridges;
	}
	
	protected boolean isCandidate(int v, int u) {
		Edge e = new Edge(v,u);
		return equalsIgnoreDirection(candidate, e);
	}
	
	private boolean equalsIgnoreDirection(Edge e1, Edge e2) {
		return (e1.getSource() == e2.getSource() && e1.getTarget() == e2.getTarget())
				|| (e1.getSource() == e2.getTarget() && e1.getTarget() == e2.getSource());
	}

	protected boolean candidateDisconnects(Graph g) {
		int order = g.getNumVertices();
		
		for (int v = 0; v < order; v++) {
			visited[v] = false;
		}
		
		dfs(g, 0);
		
		for (int v = 1; v < order; v++) {
			if (! visited[v]) {
				return true;
			}
		}
		
		return false;
	}
	
	protected void dfs(Graph g, int v) {
		List<Integer> succV = g.getSuccessors(v);

		visited[v] = true;
		
		for (int u : succV) {
			if (!visited[u] && !isCandidate(u,v)) {
				dfs(g, u);
			}
		}
		
	}
	
}
