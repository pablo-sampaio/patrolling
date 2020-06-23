package algorithms.realtime.weighted_extensions;


public class WNodeCountingSquaredSum extends RSearchMethodWithNormalizations {

	public WNodeCountingSquaredSum(boolean breakTiesRandomly, Normalization edgeValueNormalization, Normalization nodeValueNormalization) {
		super("w-NC", "<+^2>", breakTiesRandomly, edgeValueNormalization, nodeValueNormalization);
	}

	@Override
	public double evaluateSuccessorOperator(double normalizedEdgeValue, double normalizedSuccValue) {
		return normalizedEdgeValue*normalizedEdgeValue + normalizedSuccValue*normalizedSuccValue;
	}
	
	@Override
	public double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime) {
		return nodeValue + 1;
	}
	
}