package yaps.graph.algorithms;

import java.util.LinkedList;

import yaps.graph.Graph;


public class WeakConnectivity {
	
	public WeakConnectivity() {

	}
	
	//TODO: rever - o grafo precisa ser direcionado
	public boolean testWeaklyConnectedness(Graph graph) {
		int order = graph.getNumNodes();
		
		if (order == 0) {
			return false;
		}
		
		boolean[] visited = new boolean[order];
		LinkedList<Integer> queue = new LinkedList<Integer>();
		int v;
		
		queue.add(0);
		visited[0] = true;
		
		while (! queue.isEmpty()) {
			v = queue.poll();
			
			for (int u : graph.getSuccessors(v)) {
				if (! visited[u]) {
					queue.addLast(u);
					visited[u] = true;
				}
			}
			
		}
		
		for (v = 0; v < order; v++) {		
			if (! visited[v]) {
				return false;
			}
		}
		
		return true;
	}
	
	
	
}
