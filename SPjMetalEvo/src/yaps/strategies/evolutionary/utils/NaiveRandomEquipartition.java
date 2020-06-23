package yaps.strategies.evolutionary.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.util.RandomUtil;

/**
 * This class implements the Naive Random method for calculating graph
 * equipartition.
 * 
 * @author V&iacute;tor Torre&atilde;o & Diogo Melo
 *
 */
public class NaiveRandomEquipartition extends GraphEquipartition {

	/**
	 * Creates a new instance of NaiveRandomEquipartition over the given graph and
	 * with the given centers.
	 * @param graph The Graph instance over which to build the Partition
	 * @param centers The nodes in the graph to serve as references
	 */
	public NaiveRandomEquipartition(Graph graph, List<Integer> centers) {
		super(graph, centers);
	}

	/**
	 * Calculates the partitions randomly according to the given centers.
	 * 
	 * @return A HashMap with a Set of nodes for each center
	 */
	@Override
	public HashMap<Integer, HashSet<Integer>> getPartitions() {
		HashMap<Integer, HashSet<Integer>> partition = new HashMap<Integer, HashSet<Integer>>(
				centers.size()); // This is the answer
		HashSet<Integer> addedNodes = new HashSet<Integer>(this.getGraph()
				.getNumNodes());
		// The above Set keeps record of which nodes where already added to the
		// partition

		// Initialize arrays
		for (Integer c : centers) {
			addedNodes.add(c);
			partition.put(c, new HashSet<Integer>());
			partition.get(c).add(c);
		}

		int k = 0;
		List<Integer> indexList = RandomUtil.shuffle(centers);
		List<Integer> graphNodes = GraphUtils.getNodeList(this.getGraph());

		while (addedNodes.size() < this.getGraph().getNumNodes()) {
			//Iterate until all nodes were added
			Integer source = indexList.get(k);
			k = (++k) % indexList.size();

			Integer destination = RandomUtil.chooseAtRandom(graphNodes);

			while (source.equals(destination)) {
				destination = RandomUtil.chooseAtRandom(graphNodes);
			}

			Path path = this.shortestPaths.getPath(source, destination);
			if (path != null) {
				addedNodes.addAll(path);
				partition.get(source).addAll(path);
			}

		}

		for (Integer c : centers) {
			while (partition.get(c).size() < 2) {
				Integer destination = RandomUtil.chooseAtRandom(graphNodes);
				Path path = this.shortestPaths.getPath(c, destination);
				partition.get(c).addAll(path);
			}
		}

		return partition;
	}

}
