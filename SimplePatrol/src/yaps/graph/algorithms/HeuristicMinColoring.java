package yaps.graph.algorithms;

import java.util.LinkedList;

import yaps.graph.Graph;
import yaps.util.priority_queue.BinHeapPQueue;
import yaps.util.priority_queue.PQueue;
import yaps.util.priority_queue.PQueueElement;


/**
 * Heuristic methods for finding "low" node-colorings of a graph. 
 * All the methods are variations of sequential coloring.
 *  
 * @author Pablo A. Sampaio
 */
public class HeuristicMinColoring extends GraphAlgorithm {
	private int maxColor;
	private int[] coloring;
	
	private static int NOT_COLORED   =  0; // vertex not colored and not reached by the algorithm 
	private static int TO_BE_COLORED = -1; // vertex not colored but already reached

	
	public HeuristicMinColoring(Graph g) {
		super(g);
	}
	
	/**
	 * Returns the total number of colors used. This is an estimate
	 * (an upper-bound, indeed) for the cromatic number of the graph. 
	 */
	public int getColorsUsed() {
		return maxColor;
	}

	/**
	 * Returns the color given to a node, in range {1, ..., getColorsUsed()}. 
	 */
	public int getColor(int v) {
		return coloring[v];
	}	
	
	/**
	 * Colora��o sequencial baseada na pesquisa em largura. <br>
	 * Retorna uma estimativa do n�mero crom�tico.
	 */
	public int bfsColoring() {
		int numVertices = graph.getNumNodes();
		
		maxColor = 0;
		coloring = new int[numVertices]; 
		for (int i = 0; i < numVertices; i++) {
			coloring[i] = NOT_COLORED;
		}
		
		for (int start = 0; start < numVertices; start++) {
			if (coloring[start] == NOT_COLORED) {
				bfsColoringInternal(start);
			}
		}
		
		return maxColor;
	}
	
	// faz a pesquisa em largura a partir do v�rtice "start" 
	private void bfsColoringInternal(int start) {
		int v;
		
		LinkedList<Integer> queue = new LinkedList<Integer>(); 
		
		queue.add(start);
		coloring[start] = TO_BE_COLORED;
		
		while (! queue.isEmpty()) {
			v = queue.pollFirst();
			
			coloring[v] = giveMinColor(v);
			
			if (coloring[v] > maxColor) {
				maxColor = coloring[v];
			}
			
			for (Integer neighbor : graph.getSuccessors(v)) {
				if (coloring[neighbor] == NOT_COLORED) {
					queue.add(neighbor);
					coloring[neighbor] = TO_BE_COLORED;
				}
			}
		}
		
	}
	
	// Colore o v�rtice "v" usando n�meros inteiros maiores que zero
	// como cores. A cor escolhida (e retornada) � o menor n�mero que
	// n�o causa choque com os vizinhos
	private int giveMinColor(int v) {
		int color = 0;
		boolean colorIsUnique;
		
		//ideia: criar array de boolean das cores usadas e escolher a menor posicao nao usada
		
		do {
			color ++;			
			colorIsUnique = true;

			for (Integer neighbor : graph.getSuccessors(v)) {
				if (coloring[neighbor] == color) {
					colorIsUnique = false;
					break;
				}
			}
		
		} while (!colorIsUnique);
		
		return color;
	}
	
	/**
	 * Colora��o sequencial em que s�o coloridos primeiro os v�rtices
	 * com menos vizinhos n�o-coloridos.
	 * 
	 * Retorna uma estimativa do n�mero crom�tico.
	 */
	public int leastConstrainedFirstColoring() {
		PQueue<NodeInfo> frontier = new BinHeapPQueue<NodeInfo>(graph.getNumNodes());
		NodeInfo[] vertices = new NodeInfo[graph.getNumNodes()];
		
		for (int v = 0; v < graph.getNumNodes(); v++) {
			vertices[v] = new NodeInfo(v, graph.getOutEdges(v).size());			
			frontier.add(vertices[v]);
		}

		coloring = new int[graph.getNumNodes()]; // initialized with zero (an invalid color)
		maxColor = 0;
		
		int v;

		while (! frontier.isEmpty()) {
			v = frontier.removeMinimum().id;
			
			coloring[v] = giveMinColor(v);

			if (coloring[v] > maxColor) {
				maxColor = coloring[v];
			}

			for (Integer neighbor : graph.getSuccessors(v)) {
				if (coloring[neighbor] == 0) {
					vertices[neighbor].remainingDegree --;
					frontier.decreaseKey(vertices[neighbor]);
				}
			}
		}
		
		return maxColor;
	}
	
	/**
	 * Colora��o sequencial em que, dentre os v�rtices j� atingidos
	 * pelo algoritmo, s�o coloridos primeiro aqueles com menos
	 * vizinhos n�o-coloridos.
	 * 
	 * Retorna uma estimativa do n�mero crom�tico.
	 */
	public int leastConstrainedFirstColoringX() {
		int numVertices = graph.getNumNodes();
		
		maxColor = 0;
		coloring = new int[numVertices]; 
		
		NodeInfo[] vertices = new NodeInfo[numVertices];

		for (int v = 0; v < coloring.length; v++) {
			vertices[v] = new NodeInfo(v, graph.getOutEdges(v).size());
			coloring[v] = NOT_COLORED;
		}
		
		NodeInfo start;
		int unvisited = numVertices;
		
		while (unvisited > 0) {
			start = new NodeInfo(-1, Integer.MAX_VALUE);
			
			for (int v = 0; v < graph.getNumNodes(); v++) {
				if (coloring[v] == NOT_COLORED &&
						vertices[v].remainingDegree < start.remainingDegree) {
					start = vertices[v];
				}
			}
			
			unvisited = leastConstrainedFirstColoringXInternal(start, vertices, unvisited);
		}		
		
		return maxColor;
	}
	
	// usado por leastConstrainedFirstColoringX
	private int leastConstrainedFirstColoringXInternal(NodeInfo start, NodeInfo[] vertices, int unvisited) {
		int v;
		PQueue<NodeInfo> frontier = new BinHeapPQueue<NodeInfo>(unvisited);
		
		frontier.add(start);
		coloring[start.id] = TO_BE_COLORED;

		while (! frontier.isEmpty()) {
			v = frontier.removeMinimum().id;
			unvisited --;
			
			coloring[v] = giveMinColor(v);

			if (coloring[v] > maxColor) {
				maxColor = coloring[v];
			}

			for (Integer neighbor : graph.getSuccessors(v)) {
				vertices[neighbor].remainingDegree --;

				if (coloring[neighbor] == NOT_COLORED) {
					frontier.add(vertices[neighbor]);
					coloring[neighbor] = TO_BE_COLORED;

				} else if (coloring[neighbor] == TO_BE_COLORED) {
					frontier.decreaseKey(vertices[neighbor]);
					
				}
				
			}
		}
		
		return unvisited;
	}

	// classe auxiliar, faz a PQueue ordenar pela 
	// quantidade de vizinhos n�o-coloridos
	class NodeInfo extends PQueueElement {
		private int id;
		private int remainingDegree;
		
		NodeInfo(int v, int degree) {
			id = v;
			remainingDegree = degree;
		}

		@Override
		public double getKey() {
			return remainingDegree;
		}
	}
	
}
