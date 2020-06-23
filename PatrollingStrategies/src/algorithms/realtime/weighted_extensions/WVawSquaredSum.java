package algorithms.realtime.weighted_extensions;


public class WVawSquaredSum extends RSearchMethodWithNormalizations {

	public WVawSquaredSum(boolean breakTiesRandomly, Normalization edgeValueNormalization, Normalization nodeValueNormalization) {
		super("w-Vaw", "<+^2>", breakTiesRandomly, edgeValueNormalization, nodeValueNormalization);
	}

	@Override
	public double evaluateSuccessorOperator(double normalizedEdgeValue, double normalizedSuccValue) {
		return normalizedEdgeValue*normalizedEdgeValue + normalizedSuccValue*normalizedSuccValue;
	}

	@Override
	public double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime) {
		return currentTime;
	}

}
