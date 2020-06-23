package tests.algorithms;

import java.io.IOException;

import algorithms.tree_balance.EdgeLengthBalance;
import algorithms.tree_balance.NodeBalance;
import algorithms.tree_balance.TreeBalanceDfs;
import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;


public class TestTreeBalanceDfs {
	
	public static void main(String[] args) throws IOException {
		Graph g = GraphFileUtil.readAdjacencyList("tmp\\tree1");
		//System.out.println(g);

		System.out.println("NODE-BALANCE:");
		TreeBalanceDfs dfs = new TreeBalanceDfs(g, new NodeBalance());
		dfs.compute();
		dfs.printTable();
		
		System.out.println();
		System.out.println("EDGE-LENGTH:");
		TreeBalanceDfs dfs2 = new TreeBalanceDfs(g, new EdgeLengthBalance());
		dfs2.compute();
		dfs2.printTable();

	}
	
	public static void testPartition(TreeBalanceDfs dfs) {
		for (int i = 1; i < 4; i++) {
			for (int j = 1; j < 4; j++) {
				System.out.printf("Aresta para particionar %d:%d - %s\n", i, j, dfs.partition(i,j));				
			}
		}
	}
	
}
