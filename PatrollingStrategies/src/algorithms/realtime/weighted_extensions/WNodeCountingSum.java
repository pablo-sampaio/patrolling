package algorithms.realtime.weighted_extensions;


public class WNodeCountingSum extends RSearchMethodWithNormalizations {

	public WNodeCountingSum(boolean breakTiesRandomly, Normalization edgeValueNormalization, Normalization nodeValueNormalization) {
		super("w-NC", "+", breakTiesRandomly, edgeValueNormalization, nodeValueNormalization);
	}

	@Override
	public double evaluateSuccessorOperator(double normalizedEdgeValue, double normalizedSuccValue) {
		return normalizedEdgeValue + normalizedSuccValue;
	}
	
	@Override
	public double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime) {
		return nodeValue + 1;
	}

}