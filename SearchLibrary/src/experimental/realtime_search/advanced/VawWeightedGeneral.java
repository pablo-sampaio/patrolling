package experimental.realtime_search.advanced;

import java.util.LinkedList;
import java.util.List;

import experimental.realtime_search.RealtimeSearchMethod;
import yaps.graph.Edge;
import yaps.util.RandomUtil;

//TODO: Esta é a classe NodecountingWeighted -- adaptar para Vaw!!!
public class VawWeightedGeneral implements RealtimeSearchMethod {
	private Normalization nodeValueNormalization;
	private Normalization edgeNormalization;
	private boolean operationIsSum;
	private boolean breakTiesRandomly;

	public VawWeightedGeneral(Normalization normNodeValue, Normalization normEdge, boolean sum, boolean breakTiesRand) {
		this.breakTiesRandomly = breakTiesRand;
		this.nodeValueNormalization = normNodeValue;
		this.edgeNormalization = normEdge;
		this.operationIsSum = sum;
	}
	
	public double evaluateSucessor(Edge outEdge, double succValue, double minCount, double maxCount, double minEdge, double maxEdge) {
		double normalizedCount = this.nodeValueNormalization.normalize(succValue, maxCount, minCount);
		double normalizedEdge = this.edgeNormalization.normalize(outEdge.getLength(), maxEdge, minEdge);
		
		if (operationIsSum) {
			return normalizedCount + normalizedEdge;
		} else {
			return normalizedCount * normalizedEdge;
		}
	}

	@Override
	public double nextValue(Edge chosenEdge, List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		return valueOf[chosenEdge.getSource()] + 1;
	}
	
	@Override
	public final Edge choiceCriterion(List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		double minCount = Double.MAX_VALUE;
		double maxCount = - Double.MAX_VALUE;

		double minEdge = Double.MAX_VALUE;
		double maxEdge = - Double.MAX_VALUE;
		
		//encontrar minimo e maximo
		for (Edge edge : nodeOutEdges){
			int succ = edge.getTarget();
			if (valueOf[succ] < minCount){
				minCount = valueOf[succ];
			} 
			if (valueOf[succ] > maxCount){
				maxCount = valueOf[succ];
			}
			
			if (edge.getLength() < minEdge) {
				minEdge = edge.getLength();
			}
			if (edge.getLength() > maxEdge) {
				maxEdge = edge.getLength();
			}
		}
		
		List<Edge> edgesWithMinValue = new LinkedList<Edge>();
		double minValue = Double.MAX_VALUE;

		//avalia o menor (avaliacao pode ser normalizada com o minimo e o maximo)
		for (Edge edge : nodeOutEdges){
			double val = this.evaluateSucessor(edge, valueOf[edge.getTarget()], minCount, maxCount, minEdge, maxEdge);
			if (val < minValue){
				minValue = val;
				edgesWithMinValue.clear();
				edgesWithMinValue.add(edge);
			} else if (val == minValue){
				edgesWithMinValue.add(edge);
			}
		}
		System.out.printf(" - minimum value: %.3f (edges: %s) \n", minValue, edgesWithMinValue);
		
		if (breakTiesRandomly) {
			return RandomUtil.chooseAtRandom(edgesWithMinValue);
		} else {
			return edgeWithMinTarget(edgesWithMinValue); //pega aresta cujo vertice de destino tem o menor "id"
		}
	}

	private Edge edgeWithMinTarget(List<Edge> edges) {
		Edge edgeMinTarget = edges.get(0);
		for (int i = 1; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			if (edge.getTarget() < edgeMinTarget.getTarget()) {
				edgeMinTarget = edge;
			}
		}
		return edgeMinTarget;
	}

}
