package yaps.graph;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class SubGraph extends Graph {
	private Graph superGraph;
	
	private int[] superNodesToSubNodes;
	private int[] subNodesToSuperNodes;

	//TODO: permitir subconjunto das arestas? (passar os ids)
	//TODO: proibir adição de arestas (e ações de edição do grafo em geral)

	public SubGraph(Graph g, Set<Integer> nodeSet) {
		super(nodeSet.size());
		
		this.superGraph = g;
		this.superNodesToSubNodes = new int[g.getNumNodes()];
		this.subNodesToSuperNodes = new int[nodeSet.size()];
		
		int totalSuperNodes = this.superGraph.getNumNodes();
		int totalSubNodes = 0;
		int subNode;		
		
		for (int superNode = 0; superNode < totalSuperNodes; superNode++) {
			if (nodeSet.contains(superNode)) {				
				subNode = totalSubNodes;
				totalSubNodes++;				
				this.superNodesToSubNodes[superNode] = subNode;
				this.subNodesToSuperNodes[subNode] = superNode;
			} else {
				this.superNodesToSubNodes[superNode] = -1;
				
			}
		}

		//isto visa: (i) evitar adicionar duas vezes uma aresta não-direcionada; (ii) porém, permitindo arestas paralelas reais
		boolean[] closedEdges = new boolean[g.getNumEdges()];
		
		for (Integer superNode : nodeSet) {
			for (Edge e : this.superGraph.getOutEdges(superNode)) {
				if (closedEdges[e.getId()] == false) {
					closedEdges[e.getId()] = true;
				
					int src = this.toSubNode( e.getSource() );
					int tgt = this.toSubNode( e.getTarget() );
					if (tgt != -1) {
						super.addEdge(src, tgt, e.getLength(), e.isDirected());
					}
				}
				
			}
		}
		
		assert(totalSubNodes == nodeSet.size());		
	}

	public Graph getSuperGraph() {
		return superGraph;
	}

	public int toSuperNode(int subGraphNode) {
		return subNodesToSuperNodes[subGraphNode];
	}
	
	public int toSubNode(int superGraphNode) {
		return superNodesToSubNodes[superGraphNode];
	}	
	
	public static void main(String[] args) throws IOException {
		Graph graph = GraphFileUtil.readAdjacencyList("src/tests/graph/grafo-11.txt");
		
		HashSet<Integer> set = new HashSet<>();
		set.add(0); set.add(1); set.add(4); set.add(5); set.add(6);
		
		SubGraph subgraph = new SubGraph(graph, set);
		
		System.out.println("The subgraph:");
		System.out.println(subgraph);
		
		System.out.println("Node mapping (sub->super):\n");
		for (int n = 0; n < subgraph.getNumNodes(); n++) {
			System.out.printf("\t%d -> %d\n", n, subgraph.toSuperNode(n));
		}
	}
	
}
