package algorithms.realtime.weighted_extensions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import algorithms.realtime_search.RealtimeSearchMethod;
import yaps.graph.Edge;
import yaps.util.RandomUtil;


/**
 * Abstract class for realtime search methods that evaluate the node (in the decision process)
 * by combining the weights of the edges and the basic node value in this way: <ol>
 * <li> normalize the edge length (using a normalization scheme given as a parameter),
 * <li> normalize the basic node value (using another given normalization scheme),
 * <li> then applies an operator (e.g. +) to combine both (normalized) values.
 * </ol>
 * This class allows to easily created extensions of the real time search methods that don't 
 * use the weights of the edges in the node evaluation and thus don't use it in the choice of 
 * the next node (probably because they were proposed for graphs with unit edges or for 
 * edges with identical costs). An example of such method is NodeCounting.
 * <br><br>
 * When neighbor nodes have similar evaluation, this class may brea ties with two methods: 
 * a random choice, or a deterministic choice, which consists in choosing the node with the 
 * minimum ID after the ID of last node used in a tie break. 
 * 
 * @author Pablo Sampaio
 */
public abstract class RSearchMethodWithNormalizations implements RealtimeSearchMethod {
	protected final String baseName;
	protected final String operatorStr;
	protected final boolean breakTiesRandomly;
	protected final Normalization edgeValueNormalization;
	protected final Normalization nodeValueNormalization;

	protected HashMap<Integer,Integer> lastNodeCache;  // maps from node id to last node used in a tie-break 

	public RSearchMethodWithNormalizations(String methodBaseName, String operatorRepresentation, boolean breakTiesRandomly, Normalization edgeValueNormalization, Normalization nodeValueNormalization) {
		this.baseName = methodBaseName;
		this.operatorStr = operatorRepresentation;
		this.breakTiesRandomly = breakTiesRandomly;
		if (! this.breakTiesRandomly) {
			this.lastNodeCache = new HashMap<>();
		}
		this.edgeValueNormalization = edgeValueNormalization;
		this.nodeValueNormalization = nodeValueNormalization;
	}

	public double evaluateSucessor(Edge outEdge, double succValue, double maxEdge, double minEdge, double maxSuccValue,
			double minSuccValue) {
		double normalizedEdge = this.edgeValueNormalization.normalize(outEdge.getLength(), maxEdge, minEdge);
		double normalizedSuccValue = this.nodeValueNormalization.normalize(succValue, maxSuccValue, minSuccValue);
		return evaluateSuccessorOperator(normalizedEdge, normalizedSuccValue);
	}

	public abstract double evaluateSuccessorOperator(double normalizedEdgeValue, double normalizedSuccValue);

	@Override
	public Edge choiceCriterion(List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		// Find max and min of edge and node
		double maxEdge = -Double.MAX_VALUE;
		double minEdge = Double.MAX_VALUE;
		double minValue = Double.MAX_VALUE;
		double maxValue = -Double.MAX_VALUE;

		for (Edge edge : nodeOutEdges) {
			if (edge.getLength() > maxEdge)
				maxEdge = edge.getLength();
			if (edge.getLength() < minEdge)
				minEdge = edge.getLength();

			int succ = edge.getTarget();
			if (valueOf[succ] < minValue) {
				minValue = valueOf[succ];
			}
			if (valueOf[succ] > maxValue) {
				maxValue = valueOf[succ];
			}
		}

		double minWValue = Double.MAX_VALUE;
		List<Edge> edgesWithMinWValue = new LinkedList<Edge>();

		for (Edge edge : nodeOutEdges) {
			double wval = evaluateSucessor(edge, valueOf[edge.getTarget()], maxEdge, minEdge, maxValue, minValue);
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
	
	@Override
	public String getName() {
		return this.baseName + (this.breakTiesRandomly?"(rand)":"") + "(" + this.edgeValueNormalization + "," + this.operatorStr + "," + this.nodeValueNormalization + ")";
	}

}