package tests.yaps.graph;

import java.io.IOException;

import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;
import yaps.graph.Path;
import yaps.graph.algorithms.AllShortestPaths;


public class TestGraphLibrary {

	public static void main(String[] args) throws IOException {
		testEditing(); 
		//testReadingAndShortestPaths();
		//testFileFormatConversion(); 
	}

	private static void testFileFormatConversion() throws IOException {
		String[] maps = { "map_cicles_corridor", "map_city_traffic", "map_grid",
				"map_islands", "map_random_directed_1", "map_random_directed_2" };
		
		for (String mapName : maps) {
			GraphFileUtil.convert("maps/" + mapName + ".xml", GraphFileFormat.SIMPATROL, 
					"tmp/" + mapName + ".adj", GraphFileFormat.ADJACENCY_LIST);
			System.out.println("Converted " + mapName);
		}
		
		System.out.println("Ok");
	}

	private static void testReadingAndShortestPaths() throws IOException {
		Graph graph = GraphFileUtil.readAdjacencyList("src/tests/graph/island11");
		
		//imprime o grafo na forma de matriz e de listas de adjacencias
		System.out.println(graph);

		//calcula os menores caminhos
		AllShortestPaths minPaths = new AllShortestPaths(graph);
		minPaths.compute();
		
		int origin = 2;  //you may choose any other node
		
		for (int destiny = 0; destiny < graph.getNumNodes(); destiny++) {
			Path path = minPaths.getPath(origin, destiny);
			System.out.printf("Menor caminho de n%d para n%d: %s, custo: %s\n", 
								origin, destiny, path, path.getCost());
		}
	}

	private static void testEditing() {
		Graph graph = new Graph(5);
		
		graph.addDirectedEdge(0, 1, 10);
		graph.addDirectedEdge(0, 4, 50);
		graph.addDirectedEdge(1, 4, 20);
		graph.addDirectedEdge(1, 2, 40);
		graph.addDirectedEdge(1, 3, 30);
		graph.addUndirectedEdge(2, 3, 12);
		
		System.out.println(graph);
	}
}
