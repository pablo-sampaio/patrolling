package algorithms.realtime.weighted_extensions;

import java.util.LinkedList;
import java.util.List;

import yaps.graph.Edge;
import yaps.util.RandomUtil;

public class InterVawGeneral extends RSearchMethodWithNormalizations {
	protected boolean useDivision;

	public InterVawGeneral(boolean breakTiesRand, Normalization edgeValueNormalization, boolean division, Normalization nodeValueNormalization) {
		super("InterVaw", (division?"/":"*-"), breakTiesRand, edgeValueNormalization, nodeValueNormalization);
		this.useDivision = division;
	}

	@Override
	public Edge choiceCriterion(List<Edge> nodeOutEdges, double[] valueOf, int currentTime) {
		// Find max and min of edge and node
		double maxEdge = -Double.MAX_VALUE;
		double minEdge = Double.MAX_VALUE;
		double minValue = Double.MAX_VALUE;
		double maxValue = -Double.MAX_VALUE;
		double value = 0;

		for (Edge edge : nodeOutEdges) {
			if (edge.getLength() > maxEdge)
				maxEdge = edge.getLength();
			if (edge.getLength() < minEdge)
				minEdge = edge.getLength();

			int succ = edge.getTarget();
			value = (currentTime - valueOf[succ] + 1);

			if (value < minValue) {
				minValue = value;
			}
			if (value > maxValue) {
				maxValue = value;
			}
		}
		
		double minWValue = Double.MAX_VALUE;
		List<Edge> edgesWithMinWValue = new LinkedList<Edge>();

		for (Edge edge : nodeOutEdges) {
			int succ = edge.getTarget();
			value = (currentTime - valueOf[succ] + 1);
			double wval = evaluateSucessor(edge, value, maxEdge, minEdge, maxValue, minValue);
			
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
			return super.getMinNeighborAfterLast(edgesWithMinWValue, currentTime);
		}
	}

	@Override
	public double evaluateSuccessorOperator(double normalizedEdgeValue, double normalizedSuccValue) {
		// obs.: we also tried 'e+(-n)' and e+(1/n), but had worse results
		if (useDivision) {
			return normalizedEdgeValue / normalizedSuccValue;
		} else {
			return normalizedEdgeValue * (-normalizedSuccValue);
		}
	}

	@Override
	public double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime) {
		return currentTime;
	}

}
