package yaps.graph.generators;

import java.util.ArrayList;
import java.util.List;

import yaps.graph.Graph;
import yaps.graph.GraphDataRepr;
import yaps.util.RandomUtil;


/**
 * This class offers static methods to generate graphs with varying features.
 * These methods can be wrapped in subclasses of RandomGraphGenerator later, if needed. 
 * 
 * Useful reference: http://algs4.cs.princeton.edu/41graph/GraphGenerator.java.html
 * 
 * @author Pablo A. Sampaio
 */
public class SimpleGraphGenerators {

	//TODO: rever estes geradores, garantir conexidade, quando poss�vel
	
	//by Rodrigo
	public static Graph generateGridGraph(int width, int height) {
		Graph graph = new Graph(width * height);
		int v;
		int u;

		for (int i = 0; i < width * height; i++) {

			v = i;

			if (v - width >= 0) {
				u = v - width;
				graph.addUndirectedEdge(v, u, 1);
			}

			if (v != 0 && v % width != 0) {
				u = v - 1;
				graph.addUndirectedEdge(v, u, 1);
			}

		}

		return graph;
	}
	

	public static Graph generateUndirected(int numVertices, int minDegree, int maxDegree) {
		return generateUndirectedWeighted(numVertices, minDegree, maxDegree, 1, 1);
	}

	public static Graph generateUndirectedWeighted(int numVertices, int minDegree, int maxDegree, int minWeight, int maxWeight) {
		Graph g = new Graph(numVertices, GraphDataRepr.LISTS);
		
		int[] degree = new int[numVertices];
		
		int randomDegree;
		int neighbor, weight;
		boolean added;
				
		for (int v = 0; v < numVertices; v++) {
			randomDegree = RandomUtil.chooseInteger(minDegree, maxDegree);
			//System.out.printf("RandomDeg[%s] = %s\n", v, randomDegree);
			
			while (degree[v] < randomDegree && v < numVertices-1) {
				neighbor = RandomUtil.chooseInteger(v+1, numVertices-1);
				weight = RandomUtil.chooseInteger(minWeight, maxWeight);
		
				added = false;
				
				for (int x = neighbor; x < numVertices; x++) {
					if (degree[x] < maxDegree && !g.existsEdge(v, x)) {
						g.addUndirectedEdge(v, x, weight);
						degree[v] ++;
						degree[x] ++;
						added = true;
						break;
					}
				}
				
				if (!added) {
					randomDegree --;
				}
			}
		}
		
		return g;
	}
	
	public static Graph generateDirected(int numVertices, int minOutDegree, int maxOutDegree) {
		return generateDirectedWeighted(numVertices, minOutDegree, maxOutDegree, 1, 1);
	}
	
	public static Graph generateDirectedWeighted(int numVertices, int minOutDegree, int maxOutDegree, int minWeight, int maxWeight) {
		Graph g = new Graph(numVertices, GraphDataRepr.LISTS);
		
		int[] outdegree = new int[numVertices];
		
		int randomDegree;
		int neighbor, neighborInc;
		int weight;
		boolean added;
				
		for (int v = 0; v < numVertices; v++) {
			randomDegree = RandomUtil.chooseInteger(minOutDegree, maxOutDegree);
			//System.out.printf("RandomDeg[%s] = %s\n", v, randomDegree);
			
			while (outdegree[v] < randomDegree) {
				neighbor = RandomUtil.chooseInteger(0, numVertices-1);
				weight = RandomUtil.chooseInteger(minWeight, maxWeight);
		
				added = false;
				
				for (int inc = 0; inc < numVertices; inc++) {
					neighborInc = (neighbor + inc) % numVertices;
					
					if (v != neighborInc && !g.existsEdge(v,neighborInc)) {
						g.addDirectedEdge(v, neighborInc, weight);
						outdegree[v] ++;
						added = true;
						break;
					}
				}
				
				if (!added) {
					randomDegree --;
				}
			}
		}
		
		return g;
	}
	
	public static Graph generateComplete(int numVertices) {
		return generateCompleteWeighted(numVertices, 1, 1);
	}
	
	public static Graph generateCompleteWeighted(int numVertices, int minWeight, int maxWeight) {
		Graph graph = new Graph(numVertices, GraphDataRepr.LISTS);
		int capacity;
		
		for (int v = 0; v < numVertices; v++) {
			for (int x = v+1; x < numVertices; x++) {
				capacity = RandomUtil.chooseInteger(minWeight, maxWeight);
				
				graph.addUndirectedEdge(v, x, capacity);
			}
		}
		
		return graph;
	}
	
	public static Graph generateUndirectedEulerian(int numVertices, int avgDegree) {
		return generateUndirectedWeightedEulerian(numVertices, avgDegree, avgDegree, 1, 1);
	}
	
	//TODO: testar se resolver bug antigo - alguns n�s com grau �mpar eram gerados
	//TODO: usar t�cnica mais simples - acrescentar todos os n�s, criar algumas repeti��es e, depois, criar um passeio c�clico
	/**
	 * A non-proved method to generate an undirected eulerian graph with weights in the edges.
	 * Although this may be not very common, some vertices may be generated with less neighbors than 'minDegree'.  
	 */
	public static Graph generateUndirectedWeightedEulerian(int numVertices, int minDegree, int maxDegree, int minWeight, int maxWeight) {
		Graph g = new Graph(numVertices, GraphDataRepr.LISTS);
		
		minDegree += (minDegree % 2); //assure evenness
		maxDegree += (maxDegree % 2); //assure evenness
		
		System.out.println("MinDegree: " + minDegree);
		System.out.println("MaxDegree: " + maxDegree);
		
		int[] degree = new int[numVertices];
		
		int randomDegree;
		int neighbor, weight;
		boolean added;
				
		for (int v = 0; v < numVertices; v++) {
			randomDegree = 2 * RandomUtil.chooseInteger(minDegree/2, maxDegree/2); // always even

			// "v" may already have a higher degree
			if (degree[v] > randomDegree) {
				
				// if degree is odd, randomDegree is its successor (an even number)
				if ((degree[v] % 2) == 1) {
					randomDegree = degree[v] + 1;
				} else {
					randomDegree = degree[v];
				}
			
			}

			System.out.printf("RandomDeg[%s] = %s\n", v, randomDegree);
			
			List<Integer> newNeighbors = new ArrayList<>(2);
			while (degree[v] < randomDegree) {
				neighbor = RandomUtil.chooseInteger(v+1, numVertices-1);
		
				added = false;
				
				for (int x = neighbor; x < numVertices; x++) {
					if (degree[x] < maxDegree && !g.existsEdge(v, x)) {
						//g.addUndirectedEdge(v, x, weight);
						newNeighbors.add(x);
						//degree[v] ++;
						//degree[x] ++;
						added = true;
						break;
					}
				}
				
				if (!added) {
					randomDegree -= 2;
				
				} else if (newNeighbors.size() % 2 == 0) { //added a node in this iteration and completed a pair					
					for (Integer xis : newNeighbors) {
						weight = RandomUtil.chooseInteger(minWeight, maxWeight);
						g.addUndirectedEdge(v, xis, weight);
						degree[v] ++;
						degree[xis] ++;
					}
					newNeighbors.clear();
				}
				
			}
		}
		
		return g;
	}


}
