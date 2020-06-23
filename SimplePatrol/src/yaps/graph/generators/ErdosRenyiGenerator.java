package yaps.graph.generators;

import yaps.graph.Graph;
import yaps.util.RandomUtil;

/**
 * Erdos-Renyi generator. Creates each possible undirected edge with probability "p".
 * Additionally (this is not part of the original method), the edges can be weighted
 * with weights uniformly randomly generated in a given range. 
 * 
 * @author Pablo A. Sampaio
 *
 */
public class ErdosRenyiGenerator implements GraphGenerator {
	private int numberOfNodes;
	private double probability;
	
	private int minWeight;
	private int maxWeight;
	
	public ErdosRenyiGenerator(int nodes, double p) {
		this.numberOfNodes = nodes;
		if (p > 1.0d) {
			p = 1.0d;
		} else if (p < 0.0d) {
			p = 0.0d;
		}
		this.probability = p;
		this.minWeight = this.maxWeight = 1;
	}

	public ErdosRenyiGenerator(double p, int minWeight, int maxWeight) {
		if (p > 1.0d) {
			p = 1.0d;
		} else if (p < 0.0d) {
			p = 0.0d;
		}
		this.probability = p;
		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
	}

	@Override
	public void setNumberOfNodes(int nodes) {
		this.numberOfNodes = nodes;
	}

	@Override
	public int getNumberOfNodes() {
		return this.numberOfNodes;
	}
	
	@Override
	public Graph generate() {
		Graph g = new Graph(numberOfNodes);
		int weight;
		
		for (int src = 0; src < numberOfNodes; src++) {
			for (int tgt = src + 1; tgt < numberOfNodes; tgt++) {
				if (RandomUtil.chooseDouble() <= probability) {
					weight = RandomUtil.chooseInteger(minWeight, maxWeight);
					g.addUndirectedEdge(src, tgt, weight);
				}
			}
		}
		return g;
	}

}

