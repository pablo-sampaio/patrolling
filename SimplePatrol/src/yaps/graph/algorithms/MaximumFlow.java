package yaps.graph.algorithms;

import yaps.graph.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Calculates the Max-flow using Edmonds-Karp algorithm.
 *  
 * @author Pablo A. Sampaio
 */
public class MaximumFlow extends GraphAlgorithm {
	// informação do grafo
	private int numNodes;
	
	// propriedades do fluxo
	private int totalFlow;
	private int[][] flow;
	private int[][] residualCapacity;
	
	// propriedades usadas na pesquisa em extensão
	private boolean[] visited;
	private int[] parent;
	private int[] minCapacity;
	
	// usados para impedir que passe mais de 1 fluxo por cada vértice
	private int[] inflow;   //TODO: da para usar o proprio flow ??
	private int[] auxInflow;	
 
	
	public MaximumFlow(Graph g) {
		super(g);
	}

	public Graph getFlow() {
		return getFlow(GraphDataRepr.LISTS);
	}
	
	public Graph getFlow(GraphDataRepr r) {
		Graph g = new Graph(flow.length, r);
		
		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				if (flow[i][j] > 0) { 
					g.addDirectedEdge(i, j, flow[i][j]);
				}
			}
		}
		
		return g;
	}
	
	public int getFlowValue() {
		return totalFlow;
	}
	
	public int compute(int source, int sink) {
		return compute(source, sink, false);
	}
	
	/**
	 * Runs Edmonds-Karp algorithm with O(V^3 * E) complexity, with the lengths of 
	 * the edges cast to integer and interpreted as capacities.
	 * <br><br>
	 * If "visitOnce" is set, only one augmenting path can pass per node. 
	 */
	protected int compute(int source, int sink, boolean visitOnce) {
		numNodes = graph.getNumNodes();
		
		totalFlow = 0;
		flow = new int[numNodes][numNodes];
		residualCapacity = new int[numNodes][numNodes];
		visited = new boolean[numNodes];
		parent = new int[numNodes];
		minCapacity = new int[numNodes];
	
		if (visitOnce) {
			inflow = new int[numNodes];
			auxInflow = new int[numNodes];
		}
 
		List<Edge> outV;
		for (int v = 0; v < numNodes; v++) {
			outV = graph.getOutEdges(v);
			for (Edge edge : outV) {
				residualCapacity[v][edge.getTarget()] = (int)edge.getLength();
				//TODO: guardar listas de predecessores aqui?
			}
		}
		
		double increment;
		int v, pred;
 
		while (findPath(source, sink, visitOnce)) {
			/*System.out.print(">> ");
			printPath(source, sink);
			System.out.println();//*/
			
			increment = minCapacity[sink];
			totalFlow += increment;
			
			v = sink;
			while (v != source) {
				pred = parent[v];
				flow[pred][v] += increment;
				flow[v][pred] -= increment;
				residualCapacity[pred][v] -= increment;
				residualCapacity[v][pred] += increment;
				if (visitOnce) {
					inflow[v] = auxInflow[v];
				}
				v = pred;
			}
			
			// debug
			/*if (visitOnce) {
				for (int i = 0; i < inflow.length; i++)
					System.out.print(inflow[i] + " ");
				System.out.println();
			}//*/

		}
		
		return totalFlow;
	}
	
	protected boolean findPath(int source, int sink, boolean visitOnce) {
		if (visitOnce) {
			return BFS_visit_once(source, sink);
		} else {
			return BFS(source, sink);
		}
	}
 
	// Breadth First Search in O(V^2)
	// TODO: dá para melhorar usando as listas de adjacencias do grafo original?
	protected boolean BFS(int source, int sink) {
		for (int i = 0; i < numNodes; i++) {
			visited[i] = false;
			minCapacity[i] = Integer.MAX_VALUE;
		}
		
		LinkedList<Integer> queue = new LinkedList<Integer>();
		int v;

		queue.add(source);
		visited[source] = true;
		
		while (! queue.isEmpty()) {
			v = queue.poll();
			
			for (int u = 0; u < numNodes; u++) {
				if (!visited[u] && residualCapacity[v][u] > 0) {
					minCapacity[u] = Math.min(minCapacity[v], residualCapacity[v][u]);
					parent[u] = v;
					visited[u] = true;
					if (u == sink) return true;
					queue.addLast(u);
				}
			}
		}
		
		return false;
	}

	// Breadth First Search that avoids choosing a path
	// that passes through a previously visited vertex. 
	// Assumes that all arcs have unitary capacities.
	protected boolean BFS_visit_once(int source, int sink) {
		
		for (int i = 0; i < numNodes; i++) {
			visited[i] = false;;
			minCapacity[i] = 1;
			auxInflow[i] = inflow[i];
		}
		
		LinkedList<Integer> queue = new LinkedList<Integer>();
		int v;

		queue.add(source);
		visited[source] = true;
		
		while (! queue.isEmpty()) {
			v = queue.poll();
			//System.out.println("Escolhido: " + v);
			
			for (int u = 0; u < numNodes; u++) {
				if (! visited[u])
					if (auxInflow[v] > 1  && flow[v][u] < 0) {
						//System.out.printf("   (%s,%s) - retorno\n", v, u);						
						parent[u] = v;
						visited[u] = true;
						auxInflow[v] -= 1;
						if (u == sink) return true;
						queue.addLast(u);
					
					} else if (auxInflow[v] <= 1 && residualCapacity[v][u] > 0) {
						//System.out.printf("   (%s,%s) - adiante\n", v, u);
						parent[u] = v;
						visited[u] = true;
						auxInflow[u] += 1;
						if (u == sink) return true;
						queue.addLast(u);
					
					}
			}
		}
		
		return false;
	}
	
	protected void printPath(int source, int dest) {
		if (dest == source) {
			System.out.print(dest);
		} else if (dest != -1) {
			printPath(source, parent[dest]);
			System.out.print("->" + dest);
		}
	}
	
}


