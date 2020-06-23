package yaps.strategies.evolutionary.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.graph.algorithms.AllShortestPaths;
import yaps.strategies.evolutionary.experiments.EvoMATPLogger;

public class GraphUtils {
	
	public static List<Integer> getNodeList(Graph g) {
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		
		for(int i = 0; i < g.getNumNodes(); i++){
			nodes.add(i);
		}
		
		return (nodes);
	}
	
	/**
	 * Converts the given Graph into a String in the GraphViz's DOT format
	 * @return the String object containing the Graph in DOT format
	 */
	public static String toDOTString(Graph g) {
		StringBuilder sb = new StringBuilder();
		String edgeSeparator = "--";
		if (g.getNumDirectedEdges() > 0) {
			edgeSeparator = "->";
			sb.append("digraph {");
		} else {
			sb.append("graph {");
		}
		for (int node1 = 0; node1 < g.getNumNodes(); node1++) {
			for (int node2 = 0; node2 < g.getNumNodes(); node2++) {
				if (g.existsEdge(node1, node2)) {
					Edge edge = g.getEdge(node1, node2);
					sb.append(node1 + edgeSeparator + node2 + 
							"[label=\""+ edge.getLength() +"\"];");
				}
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static void toDOTFile(Graph g, File file) {
		try {
			if (!file.exists()) {
				file.getAbsoluteFile().getParentFile().mkdirs();
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(toDOTString(g).getBytes());
			fos.flush();fos.close();
		} catch (IOException e) {
			EvoMATPLogger.get().log(Level.SEVERE, e.getMessage());
		}
	}
	
	/**
	 * Bundle for Node and its distance to some other node
	 * @author Diogo Melo
	 *
	 */
	private static class NodeDistance{
		
		public NodeDistance(Integer node, double dist){
			this.node = node;
			this.dist = dist;
		}
		
		public Integer node;
		public double dist;
	}
	
	/**
	 * Returns a list of nodes of the given graph ordered by their distance to the source node
	 * @param g
	 * @param shortestPaths
	 * @param source
	 * @return
	 */
	public static List<Integer> getOrderedDistanceList(Graph g, AllShortestPaths shortestPaths, Integer source){

		LinkedList<NodeDistance> list = new LinkedList<NodeDistance>();
		
		for(Integer node: getNodeList(g)){
			if(source.equals(node)){
				continue;
			}
			orderedAddnode(new NodeDistance(node, shortestPaths.getDistance(source, node)), list);
		}
		
		List<Integer> out = new ArrayList<Integer>();
		
		while(!list.isEmpty()){
			out.add(list.removeFirst().node);
		}
		

		return out;
	}
	
	/**
	 * Inserts a NodeDistance object inside a list respecting an order
	 * @param v
	 * @param list
	 * @author Diogo Melo
	 */
	private static void orderedAddnode(NodeDistance v, List<NodeDistance> list){

		if(list.size() == 0){
			list.add(v);
			return;
		}

		int left = 0;
		int right = list.size();
		int midle = (left + right)/2;

		NodeDistance u = list.get(midle);

		while(left < right - 1){
			if(v.dist > u.dist){
				left = midle;
			}else{
				right = midle;
			}

			midle = (left + right)/2;
			u = list.get(midle);
		}

		if(v.dist > u.dist){
			list.add(midle + 1, v);
		}else{
			list.add(midle, v);
		}


	}

}
