package yaps.experimentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import yaps.util.cool_table.CoolTable;
import yaps.util.cool_table.CoolTableList;


/**
 * This class keeps all the results of simulations run by {@link ExperimentPerformer} in a specific map 
 * (for all algorithms, with all the metrics set for the experiment). <br><br>
 * 
 * The results are kept as a list of instances of {@link AlgorithmReport}. 
 *  
 * @author Pablo Sampaio
 * @author Rodrigo de Sousa
 *
 */
public class ExperimentReport {
	private String experimentName;
	private MapSettings mapSettings;
	private List<AlgorithmReport> algorithmReports;

	ExperimentReport(String experimentName, MapSettings map) {
		this.algorithmReports = new ArrayList<AlgorithmReport>();
		this.experimentName = experimentName;
		this.mapSettings = map;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public List<AlgorithmReport> getAlgorithmReports() {
		return algorithmReports;
	}

	public void addAlgorithmReport(AlgorithmReport algorithmReport) {
		this.algorithmReports.add(algorithmReport);
	}
	
	public MapSettings getMapSettings() {
		return this.mapSettings;
	}

	public SortedSet<Integer> getNumbersOfAgents() {
		return mapSettings.getNumbersOfAgents();
	}
	
	public static ExperimentReport loadFromJsonFile(String filepath) throws IOException {
		Gson gson = new Gson();
		String json = new String(Files.readAllBytes(Paths.get(filepath)));

		return gson.fromJson(json, ExperimentReport.class);
	}
	
	public void saveJsonFile(String filepath) throws IOException {	
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(this);
		
		PrintWriter pw = new PrintWriter(filepath);
		pw.write(json);
		pw.close();
	}

	public void saveCsvFile(String filePath) throws IOException {
		CoolTableList tableList = new CoolTableList(this.getExperimentName() + " - " + mapSettings.getName());
		
		for (AlgorithmReport algReport : this.getAlgorithmReports()) {

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

				int index = 0;
				for (int agents : mapSettings.getNumbersOfAgents()) {
					int meanCol = meanTable.addColumn("" + agents);
					int meanRow = meanTable.addRow(algReport.getAlgorithmName());
					meanTable.set(meanRow, meanCol, String.format("%,f", metric.getMean(index)));

					int stdDevCol = stdDevTable.addColumn("" + agents);
					int stdDevRow = stdDevTable.addRow(algReport.getAlgorithmName());
					stdDevTable.set(stdDevRow, stdDevCol, String.format("%,f", metric.getStdDev(index)));
					index ++;
				}

			}

		}

		if (! filePath.endsWith(".csv")) {
			filePath = filePath + ".csv";
		}
		tableList.exportToCsv(filePath);
	}

	public void saveChartFiles(String fileNamePrefix, String xAxisLabel, String yAxisLabel) throws IOException {
		int metricsNumber = algorithmReports.get(0).getMetrics().size();
		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
		//ArrayList<DefaultCategoryDataset> datasets = new ArrayList<DefaultCategoryDataset>();

		for (int m = 0; m < metricsNumber; m++) {
			//DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
			
			for(AlgorithmReport algReport : algorithmReports){
				
				int indexAgents = 0;
				for (int agents : mapSettings.getNumbersOfAgents()) {
					//dataset.addValue(algReport.getMetrics().get(i).getMean(j), algReport.getAlgorithmName(), this.map.getInitialPositions().get(j).length + "");
					dataset.add(algReport.getMetrics().get(m).getMean(indexAgents), algReport.getMetrics().get(m).getStdDev(indexAgents), algReport.getAlgorithmName(), agents + "");
					indexAgents++;
				}
				
			}
			
			charts.add(ChartFactory.createLineChart(algorithmReports.get(0).getMetrics().get(m).getName(), xAxisLabel, yAxisLabel, 
					dataset, PlotOrientation.VERTICAL, true, true, false));
		}
		
		for(JFreeChart chart : charts){
			//chart.getCategoryPlot().setRenderer(new DefaultCategoryItemRenderer());
			chart.getCategoryPlot().setRenderer(new StatisticalLineAndShapeRenderer(true, true));
			//((NumberAxis)chart.getCategoryPlot().getRangeAxis()).setNumberFormatOverride(NumberFormat.getPercentInstance());
			((NumberAxis)chart.getCategoryPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#0.000000"));
			
			File file = new File(fileNamePrefix + "-" + chart.getTitle().getText()+".png");
			
			ChartUtilities.saveChartAsPNG(file, chart, 800, 600);
		}
	}

}

