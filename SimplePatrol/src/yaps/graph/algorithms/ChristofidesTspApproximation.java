package yaps.graph.algorithms;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.graph.SubGraph;


/**
 * Approximation algorithm for the <b>TSP</b> (Travelling Salesman Problem) and for the <b>TSPM</b> 
 * (variation of TSP in which multiple visits to an node are allowed) with the approximation factor 1.5. 
 * <br><br>
 * If the graph is complete and metric, it gives a solution to the TSP with approximation factor 1.5, 
 * i.e., at most, 50% bigger than the optimum solution (cycle).
 * <br><br>
 * If the graph is not complete or not metric, it solves the TSPM (not sure if the approximation factor
 * remains 1.5 for the TSPM in this case) <br><br>
 *  
 * @author Pablo A. Sampaio
 */
public class ChristofidesTspApproximation extends GraphAlgorithm {
	private Path tspCycle;

	public ChristofidesTspApproximation(Graph g) {
		super(g);
	}
	
	public void compute() {
		// 1. torna o grafo completo e metrico (se ja nao for)
		AllShortestPaths shortestPaths = new AllShortestPaths(this.graph);
		shortestPaths.compute();
		
		Graph augmentedGraph = shortestPaths.toDistancesGraph();
		
		// 2. calcula a arvore espalhada minima
		MinimumSpanningTree minTree = new MinimumSpanningTree(augmentedGraph);
		minTree.compute();
		Graph tree = minTree.getMinimumTree();
		System.out.println("MST :\n" + tree);
		System.out.println("MST cost : " + minTree.getMinimumTreeCost());
		
		// 3. forma um subgrafo so com os nos ímpares
		Set<Integer> oddDegList = new TreeSet<Integer>(); 

		for (int node = 0; node < tree.getNumNodes(); node++) {
			if (tree.getOutEdges(node).size() % 2 == 1) {
				oddDegList.add(node);
			}
		}
		System.out.println("Total de nos de grau impar: " + oddDegList.size());
		//System.out.println("Nos de grau impar: " + oddDegList);

		SubGraph oddGraph = new SubGraph(augmentedGraph, oddDegList);
		
		// 4. emparelha os nós ímpares (com custo mínimo) e acrescenta na árvore
		WeightedMatching matchingAlgorithm = new WeightedMatching(oddGraph, false, true);
		
		matchingAlgorithm.compute();
		
		List<Edge> newEdges = matchingAlgorithm.getMatching();
		System.out.println("Matching: ");
		
		for (Edge edge : newEdges) {
			int superNodeSrc = oddGraph.toSuperNode(edge.getSource());
			int superNodeTgt = oddGraph.toSuperNode(edge.getTarget());
			tree.addUndirectedEdge(superNodeSrc, superNodeTgt, edge.getLength());
			System.out.printf("(%d, %d) ", superNodeSrc, superNodeTgt);
		}
		System.out.println();
		
		// 5. forma o ciclo euleriano
		EulerianCircuit eulerianCirc = new EulerianCircuit(tree);		
		eulerianCirc.compute();		
		Path cycle = eulerianCirc.getCircuit();
		System.out.println("Ciclo euleriano: " + cycle);
		
		// 6. cria os atalhos (gera uma solucao para o grafo aumentado)
		this.tspCycle = shortcutPath(cycle, augmentedGraph);

		// 7. expande os menores caminhos (gera uma solucao para o grafo original)
		this.tspCycle = this.tspCycle.expandUsingShortestPaths(shortestPaths);
	}
	
	public Path getSolution() {
		return this.tspCycle;
	}

	private Path shortcutPath(Path cycle, Graph graph) {
		Path newPath = new Path(graph);
		boolean[] visited = new boolean[graph.getNumNodes()];
		
		int node;
		for (int i = 0; i < cycle.size() - 1; i++) {
			node = cycle.get(i);
			if (! visited[node]) {
				newPath.add(node);
				visited[node] = true;
			}
		}
		newPath.add(cycle.getFirst()); //start node
		
		return newPath;
	}

}
