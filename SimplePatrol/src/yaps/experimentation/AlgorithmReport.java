package yaps.experimentation;

import java.util.ArrayList;

/**
 * This class keeps the results of simulations run by {@link ExperimentPerformer} for a specific 
 * algorithm in a specific map, for multiple metrics. The results for each metric are kept 
 * in an instance of {@link MetricReport}. 
 *  
 * @author Pablo Sampaio
 * @author Rodrigo de Sousa
 *
 */
public class AlgorithmReport {
	private String algorithmName;
	private ArrayList<MetricReport> metrics;

	AlgorithmReport(String algorithmName) {
		this.algorithmName = algorithmName;
		this.metrics = new ArrayList<MetricReport>();
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public ArrayList<MetricReport> getMetrics() {
		return metrics;
	}

	public void addMetricReport(MetricReport metric) {
		this.metrics.add(metric);
	}

}
