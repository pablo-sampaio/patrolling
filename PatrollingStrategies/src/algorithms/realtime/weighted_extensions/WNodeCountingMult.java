package algorithms.realtime.weighted_extensions;


public class WNodeCountingMult extends RSearchMethodWithNormalizations {

	/**
	 * Ties may be broken either randomly or by using a circular tie-break method.
	 */
	public WNodeCountingMult(boolean breakTiesRandomly, Normalization edgeValueNormalization, Normalization nodeValueNormalization) {
		super("w-NC", "x", breakTiesRandomly, edgeValueNormalization, nodeValueNormalization);
	}

	@Override
	public double evaluateSuccessorOperator(double normalizedEdgeValue, double normalizedSuccValue) {
		return normalizedEdgeValue * normalizedSuccValue;
	}

	@Override
	public double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime) {
		return nodeValue + 1;
	}

}