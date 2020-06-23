package yaps.strategies.evolutionary.utils;

import java.util.List;

import yaps.graph.Graph;
import yaps.graph.algorithms.AllShortestPaths;
import yaps.util.RandomUtil;

/**
 * This class implements the approximated maximum distance strategy for
 * calculating centers
 * 
 * @author V&iacute;tor Torre&atilde;o & Diogo Melo
 */
public class MaximumDistanceCentering extends Centering {

	private static final int MAXIMUM_NUMBER_OF_ITERATIONS = 100;

	private int maxNumberOfIterations;
	private AllShortestPaths shortestPaths;

	public MaximumDistanceCentering(Graph graph, int numOfAgents) {
		this(graph, numOfAgents, MAXIMUM_NUMBER_OF_ITERATIONS);
	}

	public MaximumDistanceCentering(Graph graph, int numOfAgents,
			int maxNumberOfIterations) {
		super(graph, numOfAgents);
		this.maxNumberOfIterations = maxNumberOfIterations;
		this.shortestPaths = new AllShortestPaths(this.getGraph());
		this.shortestPaths.compute();
	}

	public int getMaxNumberOfIterations() {
		return maxNumberOfIterations;
	}

	public void setMaxNumberOfIterations(int maxNumberOfIterations) {
		this.maxNumberOfIterations = maxNumberOfIterations;
	}

	@Override
	public List<Integer> calculateCenters() {
		List<Integer> nodeArray = RandomUtil.randomChoose(
				this.getNumAgents(),
				GraphUtils.getNodeList(this.getGraph())
			);

		while (this.maxNumberOfIterations-- > 0) {
			int i = RandomUtil.chooseInteger(0, nodeArray.size() - 1);
			double dMin = sumDistancies(nodeArray, this.shortestPaths);
			Integer n = nodeArray.get(i), nBest;
			nBest = n;
			List<Integer> neigbors = this.getGraph().getSuccessors(n);

			for (int j = 0; j < neigbors.size(); j++) {
				Integer nj = neigbors.get(j);
				boolean contains = false;
				for (int k = 0; k < nodeArray.size(); k++) {
					if (nodeArray.get(k) == nj) {
						contains = true;
						break;
					}
				}

				if (contains) {
					continue;
				}

				nodeArray.set(i, nj);
				double d = sumDistancies(nodeArray, this.shortestPaths);
				nodeArray.set(i, n);

				if (d > dMin) {
					dMin = d;
					nBest = nj;
				}

			}
			nodeArray.set(i, nBest);

		}
		return nodeArray;
	}

	private static double sumDistancies(List<Integer> nis, AllShortestPaths p) {

		double distance = 0;

		for (int i = 0; i < nis.size() - 1; i++) {
			for (int j = i + 1; j < nis.size(); j++) {
				distance += p.getDistance(nis.get(i), nis.get(j));
			}
		}

		return distance;
	}

}
