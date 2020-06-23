package yaps.strategies.evolutionary.utils;

import java.util.List;

import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.graph.algorithms.AllShortestPaths;

/**
 * This class is a template for implementing different strategies for building
 * the agents' paths
 * 
 * @author V&iacute;tor Torre&atilde;o & Diogo Melo
 *
 */
public abstract class PathBuilder {
	
	public static final int NEAREST_INSERTION_PATH_BUILDER = 0;
	public static final int NEAREST_NEIGHBOR_PATH_BUILDER = 1;
	public static final int RANDOM_PATH_BUILDER = 2;
	
	public static final int INFINITE = Integer.MAX_VALUE / 2;
	
	private InducedSubGraph inducedSubgraph;
	
	public PathBuilder(Graph g, List<Integer> nodeSubset) {
		this.inducedSubgraph = new InducedSubGraph(nodeSubset, g);
	}
	
	public InducedSubGraph getInducedSubGraph() {
		return this.inducedSubgraph;
	}
	
	public abstract Path build();
	
	public static double distanceNodePath(int kNode, Path T, AllShortestPaths allPaths){

		if(T.contains(kNode)){
			return 0;
		}

		if(T.size() == 0){
			throw new IllegalArgumentException("Path of size 0.");
		}

		double d = allPaths.getDistance(T.getFirst(), kNode);

		for(int sNode: T){
			if(allPaths.getDistance(kNode, sNode) < d){
				d = allPaths.getDistance(kNode, sNode);
			}
		}

		if(d == PathBuilder.INFINITE){
			throw new IllegalArgumentException("Infinity distance node-path size.");
		}

		return d;
	}
	
	public static double lengthIncreace(int iNode, int jNode, int kNode, Path T, AllShortestPaths allPaths){
		if(!T.contains(iNode)){
			throw new IllegalArgumentException("The node does not exist in the path.");
		}

		if(!T.contains(jNode)){
			throw new IllegalArgumentException("The node does not exist in the path.");
		}

		if(allPaths.getPath(iNode, jNode) == null){
			throw new IllegalArgumentException("The nodes are not neighbors.");
		}

		return allPaths.getDistance(iNode, kNode) + allPaths.getDistance(kNode, jNode) - allPaths.getDistance(iNode, jNode);
	}

}
