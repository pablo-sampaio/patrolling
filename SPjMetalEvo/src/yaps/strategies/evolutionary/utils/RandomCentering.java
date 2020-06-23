package yaps.strategies.evolutionary.utils;

import java.util.ArrayList;
import java.util.List;

import yaps.graph.Graph;
import yaps.util.RandomUtil;

/**
 * This class implements the random strategy for creating centers
 * 
 * @author V&iacute;tor Torre&atilde;o & Diogo Melo
 *
 */
public class RandomCentering extends Centering {

	public RandomCentering(Graph graph, int numOfAgents) {
		super(graph, numOfAgents);
	}

	@Override
	public List<Integer> calculateCenters() {
		ArrayList<Integer> agentList = new ArrayList<Integer>();
		for(int i = 0; i < this.getNumAgents(); i++) {
			int agent = RandomUtil.chooseInteger(0, this.getGraph().getNumNodes() -1);
			while(agentList.contains(agent)){
				agent = RandomUtil.chooseInteger(0, this.getGraph().getNumNodes() -1);
			}
			agentList.add(agent);
		}
		return agentList;
	}

}
