package algorithms.rodrigo.experiments;

import java.util.ArrayList;

public class AlgorithmReport {

	private String algorithmName;
	private ArrayList<MetricReport> metrics;

	public AlgorithmReport(String algorithmName) {
		this.algorithmName = algorithmName;
		this.metrics = new ArrayList<MetricReport>();
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public ArrayList<MetricReport> getMetrics() {
		return metrics;
	}

	public void setMetrics(ArrayList<MetricReport> metrics) {
		this.metrics = metrics;
	}

	public void addMetric(MetricReport metric) {
		this.metrics.add(metric);
	}

}
