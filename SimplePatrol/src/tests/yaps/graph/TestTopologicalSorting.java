package tests.yaps.graph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import yaps.graph.Graph;
import yaps.graph.GraphDataRepr;
import yaps.graph.GraphFileUtil;
import yaps.graph.algorithms.TopologicalSorting;


public class TestTopologicalSorting {

	//TODO: mover para uma classe de cria��o de grafos
	public static void createDAG(int numVertices, String fileName) throws FileNotFoundException {
		PrintStream out = new PrintStream(fileName);
		
		out.println(numVertices);

		for (int v = 0; v < numVertices; v++) {
			for (int succ = 0; succ < v; succ++) {
				if (succ != 0 && (v % succ) == 0) {
					out.print(succ + " 1  ");
				}
			}
			out.println("-1 -1");
		}

		out.close();
		System.out.println("Arquivo salvo.");
	}
	
	
	public static void main(String[] args) throws IOException {
		createDAG(7, "tmp\\DAG.txt");
		
		Graph graph = GraphFileUtil.readAdjacencyList("tmp\\DAG.txt", GraphDataRepr.MIXED);
				
		TopologicalSorting topSort = new TopologicalSorting();
		long time;
		
		try {
			System.out.println("Resultado 1: ");
			time = System.currentTimeMillis();
			System.out.println(topSort.sort1(graph));
			System.out.println(" => tempo: " + (System.currentTimeMillis() - time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("Resultado 2: ");
			time = System.currentTimeMillis();
			System.out.println(topSort.sort2(graph));
			System.out.println(" => tempo: " + (System.currentTimeMillis() - time));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
