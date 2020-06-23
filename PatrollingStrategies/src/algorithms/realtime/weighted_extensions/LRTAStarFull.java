package algorithms.realtime.weighted_extensions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import algorithms.realtime_search.RealtimeSearchMethod;
import yaps.graph.Edge;
import yaps.util.RandomUtil;


/**
 * LRTA*
 * 
 * @author Pablo Sampaio
 */
public class LRTAStarFull implements RealtimeSearchMethod {
	protected final boolean breakTiesRandomly;
	protected HashMap<Integer,Integer> lastNodeCache;  // maps from node id to last node chosen on a tie 

	public LRTAStarFull(boolean breakTiesRandomly) {
		this.breakTiesRandomly = breakTiesRandomly;
		if (! this.breakTiesRandomly) {
			this.lastNodeCache = new HashMap<>();
		}
	}

	@Override
	public Edge choiceCriterion(List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		double minWValue = Double.MAX_VALUE;
		List<Edge> edgesWithMinWValue = new LinkedList<Edge>();

		for (Edge edge : nodeOutEdges) {
			double wval = evaluateSucessor(edge, valueOf[edge.getTarget()]);
			if (wval < minWValue) {
				minWValue = wval;
				edgesWithMinWValue.clear();
				edgesWithMinWValue.add(edge);
			} else if (wval == minWValue) {
				edgesWithMinWValue.add(edge);
			}
		}
		//System.out.printf(" - min count: %.2f (edges: %s) \n", minC, edgesWithMinValue);

		if (this.breakTiesRandomly) {
			return RandomUtil.chooseAtRandom(edgesWithMinWValue);
		} else {
			if (currentTime == 1) {
				this.lastNodeCache.clear();  //gambiarra para zerar os valores entre execucoes distintas
			}
	
			return getMinNeighborAfterLast(edgesWithMinWValue, currentTime);
		}
	}
	
	protected Edge getMinNeighborAfterLast(List<Edge> edgesWithMinValue, int currentTime) {
		if (currentTime == 1) {
			this.lastNodeCache.clear();  //gambiarra para zerar os valores entre execucoes distintas
		}
		
		int sourceNode = edgesWithMinValue.get(0).getSource();
		int lastNode = this.lastNodeCache.containsKey(sourceNode)? this.lastNodeCache.get(sourceNode) : -1;

		Edge minNeighborId = null;
		Edge minNeighborIdAfterLast = null;
		
		for (int i = 0; i < edgesWithMinValue.size(); i++) {
			Edge edge = edgesWithMinValue.get(i);
			if (edge.getTarget() > lastNode 
					&& (minNeighborIdAfterLast == null || edge.getTarget() < minNeighborIdAfterLast.getTarget())) {
				minNeighborIdAfterLast = edge;
			}
			if (minNeighborId == null || edge.getTarget() < minNeighborId.getTarget()) {
				minNeighborId = edge;
			}
		}

		Edge chosenEdge;
		if (minNeighborIdAfterLast != null) {
			chosenEdge = minNeighborIdAfterLast;
		} else {
			chosenEdge = minNeighborId;
		}
		
		this.lastNodeCache.put(sourceNode, chosenEdge.getTarget());
		return chosenEdge;
	}
	
	public double evaluateSucessor(Edge outEdge, double succValue) {
		return outEdge.getLength() + succValue;
	}

	@Override
	public double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime) {
		double nextNodeValue = distance + succValue;
		return Math.max(nodeValue, nextNodeValue);
	}

	@Override
	public String getName() {
		return "Full-LRTA*" + (this.breakTiesRandomly?"(rand)":"");
	}

}
