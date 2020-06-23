package yaps.graph.algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.graph.GraphDataRepr;


public class StrongConnectivity {
	private boolean[] visited;
	//TODO: seguir o padrão

	public StrongConnectivity() {

	}
	
	public boolean isStronglyConnected(Graph graph) {
		return findStronglyConnectedComponents(graph).length == 1;
	}

	// algoritmo de Kosaraju
	public List<Integer>[] findStronglyConnectedComponents(Graph graph) {
		List<Integer>[] components;
		List<Integer> startSequence;
		Graph transpose;
		
		visited = new boolean[graph.getNumNodes()];
		
		startSequence = findStartSequence(graph);
		
		transpose = transposeGraph(graph);
		
		components = findComponents(transpose, startSequence);
		
		return components;
	}
	
	protected Graph transposeGraph(Graph g) {
		int numVertices = g.getNumNodes();
		Graph gt = new Graph(numVertices, GraphDataRepr.LISTS);

		List<Edge> outEdges;
		
		for (int v = 0; v < numVertices; v++) {
			outEdges = g.getOutEdges(v);
			for (Edge e : outEdges) {
				gt.addEdge(e.getTarget(), v, e.getLength(), e.isDirected());
			}
		}

		return gt;
	}
	
	protected List<Integer> findStartSequence(Graph graph) {
		LinkedList<Integer> sequence = new LinkedList<Integer>();
		
		for (int v = 0; v < visited.length; v++) {
			visited[v] = false;
		}
		
		for (int v = 0; v < visited.length; v++) {
			if (! visited[v]) {
				visitAndList(graph, v, sequence);
			}
		}		
		
		return sequence;
	}
	
	protected void visitAndList(Graph g, int v, LinkedList<Integer> list) {
		List<Integer> succV = g.getSuccessors(v);

		visited[v] = true;
		
		for (int u : succV) {
			if (! visited[u]) {
				visitAndList(g, u, list);
			}
		}
		
		list.addFirst(v);
	}
	
	@SuppressWarnings("unchecked")
	protected List<Integer>[] findComponents(Graph g, List<Integer> startSequence) {
		LinkedList<Integer> component;
		ArrayList<List<Integer>> componentList = new ArrayList<List<Integer>>();
		
		for (int v = 0; v < visited.length; v++) {
			visited[v] = false;
		}
		
		for (int v : startSequence) {
			if (! visited[v]) {
				component = new LinkedList<Integer>();
				visitAndList(g, v, component);
				componentList.add(component);
			}
		}
		
		LinkedList<Integer>[] componentArray = new LinkedList[componentList.size()];
		componentList.toArray(componentArray);
		
		return componentArray;
	}
	
}
