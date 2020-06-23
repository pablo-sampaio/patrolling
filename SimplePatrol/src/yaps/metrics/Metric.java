package yaps.metrics;

/**
 * An interface to simplify calculating the metrics. 
 * <br><br>
 * Obs.: This is not the most efficient way to calculate multiple metrics 
 * (e.g. average and maximum interval) for the same input.
 * 
 * @author Pablo A. Sampaio
 */
public enum Metric {
	MAXIMUM_INTERVAL {
		@Override
		public double calculate(VisitsList visits, int numNodes, int start, int end) {
			IntervalMetricsReport report = new IntervalMetricsReport(numNodes, start, end, visits);
			return report.getMaximumInterval();
		}
	},	
	QUADR_MEAN_OF_INTERVALS {
		@Override
		public double calculate(VisitsList visits, int numNodes, int start, int end) {
			IntervalMetricsReport report = new IntervalMetricsReport(numNodes, start, end, visits);
			return report.getQuadraticMeanOfIntervals();
		}
	},	
	AVERAGE_INTERVAL {
		@Override
		public double calculate(VisitsList visits, int numNodes, int start, int end) {
			IntervalMetricsReport report = new IntervalMetricsReport(numNodes, start, end, visits);
			return report.getAverageInterval();
		}
	},	
	STD_DEV_OF_INTERVALS {
		@Override
		public double calculate(VisitsList visits, int numNodes, int start, int end) {
			IntervalMetricsReport report = new IntervalMetricsReport(numNodes, start, end, visits);
			return report.getStdDevOfIntervals();
		}
	},	
	STD_DEV_OF_FREQUENCIES {
		@Override
		public double calculate(VisitsList visits, int numNodes, int start, int end) {
			FrequencyMetricsReport report = new FrequencyMetricsReport(numNodes, start, end, visits);
			return report.getStdDevOfFrequencies();
		}
	};

	public abstract double calculate(VisitsList visits, int numNodes, int start, int end);
	
}
