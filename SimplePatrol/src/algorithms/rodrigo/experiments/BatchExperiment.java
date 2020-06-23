package algorithms.rodrigo.experiments;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;

import yaps.metrics.FrequencyMetricsReport;
import yaps.metrics.IntervalMetricsReport;
import yaps.simulator.core.Simulator;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;
import yaps.util.DoubleList;
import yaps.util.cool_table.CoolTable;
import yaps.util.cool_table.CoolTableList;


public class BatchExperiment {

	private String experimentName;
	private ArrayList<SimpleMultiagentAlgorithm> algorithms;
	private Map map;
	private int executions;
	private int time;
	private ExperimentReport experimentReport;
	private int initialTime;
	private String filePath;
	private long experimentTime;

	public BatchExperiment(String experimentName, ArrayList<SimpleMultiagentAlgorithm> algorithms, int executions, int time, int initialTime) {
		this.experimentName = experimentName;
		this.algorithms = algorithms;
		this.executions = executions;
		this.time = time;
	}

	public BatchExperiment() {
		this.algorithms = new ArrayList<SimpleMultiagentAlgorithm>();
		this.executions = 1;
		this.time = 1;
	}

	public void start() {

		long startTime = System.currentTimeMillis() / 1000;

		int count = 0;

		this.experimentReport = new ExperimentReport(experimentName, map.getName(), map.getAgentSets());

		for (SimpleMultiagentAlgorithm algorithm : algorithms) {

			AlgorithmReport algorithmReport = new AlgorithmReport(algorithm.getName());
			MetricReport maxInterval = new MetricReport("Max. Interval");
			MetricReport quadraticMeanInterval = new MetricReport("QMI");
			MetricReport frequencyStandardDeviation = new MetricReport("SDF");

			algorithmReport.addMetric(maxInterval);
			algorithmReport.addMetric(quadraticMeanInterval);
			algorithmReport.addMetric(frequencyStandardDeviation);

			this.experimentReport.addAlgorithmReport(algorithmReport);

			for (int[] initialPositions : this.map.getInitialPositions()) {

				DoubleList maxIntervalValues = new DoubleList();
				DoubleList quadraticMeanIntervalValues = new DoubleList();
				DoubleList frequencyStandardDeviationValues = new DoubleList();

				maxInterval.addValues(maxIntervalValues);
				quadraticMeanInterval.addValues(quadraticMeanIntervalValues);
				frequencyStandardDeviation.addValues(frequencyStandardDeviationValues);

				for (int i = 0; i < executions; i++) {

					Simulator simulator = new Simulator();

					simulator.setGraph(map.getGraph());
					simulator.setAgentsInitialNodes(initialPositions);
					simulator.setTotalTime(time);
					simulator.setAlgorithm(algorithm);

					simulator.run();

					IntervalMetricsReport intervalReport = new IntervalMetricsReport(this.map.getGraph().getNumNodes(), initialTime, time, simulator.getVisitsList());

					FrequencyMetricsReport frequencyReport = new FrequencyMetricsReport(this.map.getGraph().getNumNodes(), initialTime, time, simulator.getVisitsList());

					maxIntervalValues.add(intervalReport.getMaximumInterval());
					quadraticMeanIntervalValues.add(intervalReport.getQuadraticMeanOfIntervals());
					frequencyStandardDeviationValues.add(frequencyReport.getStdDevOfFrequencies());

				}

				count++;
				System.out.println(this.map.getName() + " run: " + count);

			}

		}

		this.experimentTime = System.currentTimeMillis() / 1000 - startTime;
	}

	public void addAlgorithm(SimpleMultiagentAlgorithm algorithm) {
		this.algorithms.add(algorithm);
	}

	public ArrayList<SimpleMultiagentAlgorithm> getAlgorithms() {
		return algorithms;
	}

//	public void setAlgorithms(ArrayList<SimpleMultiagentAlgorithm> algorithms) {
//		this.algorithms = algorithms;
//	}

	public Map getMap() {
		return this.map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public int getExecutions() {
		return executions;
	}

	public void setExecutions(int executions) {
		this.executions = executions;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public ExperimentReport getExperimentReport() {
		return this.experimentReport;
	}

	public long getExperimentTime() {
		return this.experimentTime;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public int getInitialTime() {
		return initialTime;
	}

	public void setInitialTime(int initialTime) {
		this.initialTime = initialTime;
	}
	
	public void toJSON(String filepath) throws FileNotFoundException{	
		String json = experimentReport.toJSON();
		
		PrintWriter pw = new PrintWriter(filepath);
		pw.write(json);
		pw.close();
	}

	public void toCsv(String filePath) throws IOException {
		this.filePath = filePath;

		CoolTableList tableList = new CoolTableList(experimentReport.getExperimentName() + " - " + experimentReport.getMapName());

		for (AlgorithmReport algReport : experimentReport.getAlgorithmReports()) {

			for (MetricReport metric : algReport.getMetrics()) {

				CoolTable meanTable;
				CoolTable stdDevTable;

				if (tableList.hasTable(metric.getName() + " - Mean")) {
					meanTable = tableList.getTable(metric.getName() + " - Mean");
				} else {
					meanTable = new CoolTable(metric.getName() + " - Mean");
					tableList.add(meanTable);
				}

				if (tableList.hasTable(metric.getName() + " - StdDev")) {
					stdDevTable = tableList.getTable(metric.getName() + " - StdDev");
				} else {
					stdDevTable = new CoolTable(metric.getName() + " - StdDev");
					tableList.add(stdDevTable);
				}

				for (int i = 0; i < this.map.getInitialPositions().size(); i++) {

					int meanCol = meanTable.addColumn("" + this.map.getInitialPositions().get(i).length);
					int meanRow = meanTable.addRow(algReport.getAlgorithmName());
					meanTable.set(meanRow, meanCol, String.format("%,f", metric.getMean(i)));

					int stdDevCol = stdDevTable.addColumn("" + this.map.getInitialPositions().get(i).length);
					int stdDevRow = stdDevTable.addRow(algReport.getAlgorithmName());
					stdDevTable.set(stdDevRow, stdDevCol, String.format("%,f", metric.getStdDev(i)));

				}

			}

		}

		tableList.exportToCsv(filePath);

	}

	public void chart(String xAxisLabel, String yAxisLabel) {
		int metricsNumber = experimentReport.getAlgorithmReports().get(0).getMetrics().size();
		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
		//ArrayList<DefaultCategoryDataset> datasets = new ArrayList<DefaultCategoryDataset>();

		for (int i = 0; i < metricsNumber; i++) {
			//DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
			
			for(AlgorithmReport algReport : experimentReport.getAlgorithmReports()){
				
				for(int j = 0; j < this.map.getInitialPositions().size(); j++){
					//dataset.addValue(algReport.getMetrics().get(i).getMean(j), algReport.getAlgorithmName(), this.map.getInitialPositions().get(j).length + "");
					dataset.add(algReport.getMetrics().get(i).getMean(j), algReport.getMetrics().get(i).getStdDev(j), algReport.getAlgorithmName(), this.map.getInitialPositions().get(j).length + "");
					
				}
				
			}
			
			charts.add(ChartFactory.createLineChart(experimentReport.getAlgorithmReports().get(0).getMetrics().get(i).getName(), xAxisLabel, yAxisLabel, 
					dataset, PlotOrientation.VERTICAL, true, true, false));
		}

		
		for(JFreeChart chart : charts){
			
			//chart.getCategoryPlot().setRenderer(new DefaultCategoryItemRenderer());
			chart.getCategoryPlot().setRenderer(new StatisticalLineAndShapeRenderer(true, true));
			//((NumberAxis)chart.getCategoryPlot().getRangeAxis()).setNumberFormatOverride(NumberFormat.getPercentInstance());
			((NumberAxis)chart.getCategoryPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#0.000000"));
			
			File file = new File(chart.getTitle().getText()+".png");
			
			try {
				ChartUtilities.saveChartAsPNG(file, chart, 800, 600);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	public void openResults() throws IOException {
		Desktop dt = Desktop.getDesktop();
		dt.open(new File(filePath));
	}
	
}

