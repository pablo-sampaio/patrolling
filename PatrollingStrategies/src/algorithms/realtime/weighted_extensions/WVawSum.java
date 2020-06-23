package algorithms.realtime.weighted_extensions;


public class WVawSum extends RSearchMethodWithNormalizations {

	public WVawSum(boolean breakTiesRandomly, Normalization edgeValueNormalization, Normalization nodeValueNormalization) {
		super("w-Vaw", "+", breakTiesRandomly, edgeValueNormalization, nodeValueNormalization);
	}

	@Override
	public double evaluateSuccessorOperator(double normalizedEdgeValue, double normalizedSuccValue) {
		return normalizedEdgeValue + normalizedSuccValue;
	}

	@Override
	public double valueUpdateRule(double nodeValue, double distance, double succValue, int currentTime) {
		return currentTime;
	}

}
