package algorithms.rodrigo.experiments;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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

public class ExperimentReport {

	private String experimentName;
	private String mapName;
	private int[]  agentSets; //societies sizes
	private ArrayList<AlgorithmReport> algorithmReports;

	public ExperimentReport() {

	}

	public ExperimentReport(String experimentName, String mapName, int[] agentSets) {
		this.algorithmReports = new ArrayList<AlgorithmReport>();
		this.experimentName = experimentName;
		this.mapName = mapName;
		this.agentSets = agentSets;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public ArrayList<AlgorithmReport> getAlgorithmReports() {
		return algorithmReports;
	}

	public void setAlgorithmReports(ArrayList<AlgorithmReport> algorithmReports) {
		this.algorithmReports = algorithmReports;
	}

	public void addAlgorithmReport(AlgorithmReport algorithmReport) {
		this.algorithmReports.add(algorithmReport);
	}

	public static ExperimentReport fromJSON(String filepath) throws IOException {
		Gson gson = new Gson();
		String json = new String(Files.readAllBytes(Paths.get(filepath)));

		return gson.fromJson(json, ExperimentReport.class);
	}
	
	public String toJSON(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String json = gson.toJson(this);
		
		return json;
	}

	public void generateCharts(int width, int height, Map<String,String> algorithms, int[] agentNumbers, 
			boolean relativeValues, String metricName) {
		
		generateChartsReworked(width, height, algorithms, agentNumbers, relativeValues, Collections.EMPTY_LIST, false, null, metricName);
	}
	
	public void generateCharts(ArrayList<ExperimentReport> reports, int width, int height, Map<String,String> algorithms, int[] agentNumbers, 
			boolean relativeValues, List<String> excludeFromMin, boolean grayScale, Double rangeAxisUpperBound, String metricName) throws IOException, CloneNotSupportedException{
		
		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
		
		for(ExperimentReport report : reports){
			charts.add(report.generateChartsReworked(width, height, algorithms, agentNumbers, relativeValues, excludeFromMin, grayScale, rangeAxisUpperBound, metricName));
		}
		
		
		
	    
	    ////
	    
	    
	    
	    //f.getContentPane().repaint();
	    //f.getContentPane().paint(g);
	    boolean var = false;
	    if (var) {
			Graphics2D g = new EpsGraphics2D();
			LegendTitle legend = (LegendTitle) charts.get(0).getLegend().clone();
			
			
			int counter = 0;
			for (int i = 0; i < charts.size() / 2; i++) {
				for (int j = 0; j < charts.size() / 2; j++) {
					charts.get(counter).getLegend().setVisible(false);
					charts.get(counter).getTitle().setVisible(false);

					if (counter % 2 == 0) {
						charts.get(counter).getXYPlot().setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
						charts.get(counter).getXYPlot().getRangeAxis().setTickLabelsVisible(false);
						
						//charts.get(counter).getCategoryPlot().getRangeAxis().setLabel("Normalized Metric Value");
						//charts.get(counter).getCategoryPlot().getRangeAxis().setLa

						charts.get(counter).draw(g, new Rectangle(width * j, height * i, width, height));
						

					} else {
						charts.get(counter).draw(g, new Rectangle(width * j-6, height * i, width+80, height));
						
					}

					counter++;
				}
			}
			
			//////////////////
			/*System.out.println(charts.get(3).getXYPlot().getDomainAxis().getLabel());
			charts.get(2).getXYPlot().getDomainAxis().setLabel("A");
			charts.get(2).getXYPlot().getRangeAxis().setLabel("A");
			charts.get(3).getXYPlot().getDomainAxis().setLabel("AA");
			System.out.println(charts.get(3).getXYPlot().getDomainAxis().getLabel());*/
			/////////////////////
			
			//legend.setItemFont(new Font("Dialog", Font.PLAIN, 105));
			//legend.set
			int legendW = 724;
			int legendH = 50;
			legend.draw(g, new Rectangle2D.Double((double)(2 * width - 12 -legendW + 160)/ 2.0, (double)2.0 * height, legendW, legendH));
			//f.paint(g);
			//chart.draw(g, new Rectangle(width, height));
			Writer out;
			try {
				out = new FileWriter(mapName + " " + metricName + ".eps");
				out.write(g.toString());
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		if (!var) {
			JFrame f = new JFrame("Demo");
			f.setLayout(new GridLayout(0, 2));
			ChartPanel chartPanel1 = new ChartPanel(charts.get(0), width, height, width, height, width, height, false, false, false, false, false, false);
			ChartPanel chartPanel2 = new ChartPanel(charts.get(1), width, height, width, height, width, height, false, false, false, false, false, false);
			ChartPanel chartPanel3 = new ChartPanel(charts.get(2), width, height, width, height, width, height, false, false, false, false, false, false);
			ChartPanel chartPanel4 = new ChartPanel(charts.get(3), width, height, width, height, width, height, false, false, false, false, false, false);
			f.add(chartPanel1);
			f.add(chartPanel2);
			f.add(chartPanel3);
			f.add(chartPanel4);
			for (JFreeChart chart : charts) {
				//ChartPanel chartPanel = new ChartPanel(chart);

				//f.add(chartPanel);

			}
			
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.pack();
			f.setVisible(true);
			
			System.out.println(charts.get(0).getLegend().getBounds().getBounds().getWidth());
			System.out.println(charts.get(0).getLegend().getHeight());
		}
		
	}
	
	//Original
	public void generateCharts(int width, int height, Map<String,String> algorithms, int[] agentNumbers, 
			boolean relativeValues, List<String> excludeFromMin, boolean grayScale, Double rangeAxisUpperBound) {
		
		int metricsNumber = algorithmReports.get(0).getMetrics().size();
		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
		// ArrayList<DefaultCategoryDataset> datasets = new

		String xAxisLabel = "N�mero de Agentes";
		String yAxisLabel;
		
		for (int i = 0; i < metricsNumber; i++) {
			//DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
			ArrayList<Double> best = new ArrayList<Double>();
			
			yAxisLabel = algorithmReports.get(0).getMetrics().get(i).getName();

			for (int a = 0; a < agentSets.length; a++) {
				double min = Double.MAX_VALUE;
				for (AlgorithmReport algReport : algorithmReports) {

					if (!excludeFromMin.contains(algReport.getAlgorithmName()) 
							&& algReport.getMetrics().get(i).getMean(a) < min) {
						min = algReport.getMetrics().get(i).getMean(a);
					}
				}
				best.add(min);
			}

			for (String alg : algorithms.keySet()) {
				for (AlgorithmReport algReport : algorithmReports) {
					if (algReport.getAlgorithmName().equals(alg)) {
						for (int j = 0; j < agentSets.length; j++) {
							// dataset.addValue(algReport.getMetrics().get(i).getMean(j),
							// algReport.getAlgorithmName(),
							// this.map.getInitialPositions().get(j).length +
							// "");

							for (int k = 0; k < agentNumbers.length; k++) {
								if (agentSets[j] == agentNumbers[k]) {

									if (relativeValues) {
										dataset.add(algReport.getMetrics().get(i).getMean(j) / best.get(j), 
													algReport.getMetrics().get(i).getStdDev(j) / best.get(j),
													algorithms.get(alg), agentSets[j] + "");
										
										/*dataset.addValue(algReport.getMetrics().get(i).getMean(j) / best.get(j),
												algorithms.get(alg), agentSets[j] + "");*/
									} else {
										dataset.add(algReport.getMetrics().get(i).getMean(j), 
													algReport.getMetrics().get(i).getStdDev(j), 
													algorithms.get(alg),
													agentSets[j] + "");
										
										/*dataset.addValue(algReport.getMetrics().get(i).getMean(j) / best.get(j),
												algorithms.get(alg), agentSets[j] + "");*/
									}

								}
							}
						}
					}
				}
			}

			charts.add(ChartFactory.createLineChart(algorithmReports.get(0).getMetrics().get(i).getName(), 
					xAxisLabel, yAxisLabel, dataset, 
					PlotOrientation.VERTICAL, true, true, false));
		}
		
		//chartCustomization
		float seriesLineStroke = 2.5f;
		float gridLineStroke = 1;
		float axisStroke = 1;
		float tickMarkStroke = 1;
		int tickFont = 15;
		int labelFont = 25;
		
		Font yTickLabelFont = new Font("Dialog", Font.PLAIN, tickFont);
		Font xTickLabelFont = new Font("Dialog", Font.PLAIN, tickFont);
		Font xLabelFont = new Font("Dialog", Font.PLAIN, labelFont);
		Font legendFont = new Font("Dialog", Font.PLAIN, tickFont);
		

		for (JFreeChart chart : charts) {
			//chart.getCategoryPlot().setRenderer(new StatisticalLineAndShapeRenderer(true, true));
			//chart.getCategoryPlot().setRenderer(new LineAndShapeRenderer(true, true));
			
			CategoryPlot categoryPlot = chart.getCategoryPlot();
			//categoryPlot.setRenderer(new DefaultCategoryItemRenderer());
			categoryPlot.setRenderer(new StatisticalLineAndShapeRenderer(true, true));
			
			//categoryPlot.getRenderer().getLegendItem(0, 1);
			//LegendTitle legend = new LegendTitle(categoryPlot.getRenderer());
			
			if (grayScale) {
				GrayPaintScale paintScale = new GrayPaintScale(0, 5);
				categoryPlot.setBackgroundPaint(paintScale.getPaint(4));
				for (int i = 0; i < categoryPlot.getDataset().getRowCount(); i++) {
					categoryPlot.getRenderer().setSeriesPaint(i, paintScale.getPaint(i));
				} 
			}
			categoryPlot.getRenderer().setStroke(new BasicStroke(seriesLineStroke));
			
			categoryPlot.setRangeGridlineStroke(new BasicStroke(gridLineStroke));
			categoryPlot.setRangeGridlinesVisible(false);
			chart.getCategoryPlot().setRangeGridlinePaint(Color.BLACK);

			if (relativeValues) {
				NumberAxis yAxis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
				yAxis.setAxisLineStroke(new BasicStroke(axisStroke));
				yAxis.setTickMarkStroke(new BasicStroke(tickMarkStroke));
				yAxis.setTickMarkPaint(Color.BLACK);
				yAxis.setAxisLinePaint(Color.BLACK);
				yAxis.setTickLabelFont(yTickLabelFont);
				//yAxis.setTickLabelFont(yAxis.getTickLabelFont().deriveFont(Font.BOLD));
				
				if(rangeAxisUpperBound != null){
					System.out.println(yAxis.getUpperBound());
					//yAxis.setUpperBound(rangeAxisUpperBound);
					//yAxis.setUpperBound(1);
				}
				
				chart.getCategoryPlot().getRangeAxis().setLabel("");
				
				yAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
				yAxis.setAutoRangeIncludesZero(false);
				
				CategoryAxis xAxis = (CategoryAxis) chart.getCategoryPlot().getDomainAxis();
				xAxis.setAxisLineStroke(new BasicStroke(axisStroke));
				xAxis.setTickMarkStroke(new BasicStroke(tickMarkStroke));
				xAxis.setTickMarkPaint(Color.BLACK);
				xAxis.setAxisLinePaint(Color.BLACK);
				xAxis.setTickLabelFont(xTickLabelFont);
				xAxis.setLabelFont(xLabelFont);
				//xAxis.setTickLabelFont(yAxis.getTickLabelFont().deriveFont(Font.BOLD));
				
				
				//LogAxis logYAxis = new LogAxis(yAxis.getLabel());
				LogarithmicAxis logYAxis = new LogarithmicAxis(yAxis.getLabel());
				logYAxis.setAxisLineStroke(new BasicStroke(axisStroke));
				logYAxis.setTickMarkStroke(new BasicStroke(tickMarkStroke));
				logYAxis.setTickMarkPaint(Color.BLACK);
				logYAxis.setAxisLinePaint(Color.BLACK);
				
				
				//logYAxis.setAutoRangeIncludesZero(false);
				
				//logYAxis.setStandardTickUnits(LogarithmicAxis.createIntegerTickUnits());
				
				logYAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
				//System.out.println(yAxis.getTickLabelFont().toString());
				logYAxis.setLabelFont(yAxis.getLabelFont());
				//logYAxis.setLabel("Teste");
				logYAxis.setTickLabelFont(yAxis.getTickLabelFont());
				//logYAxis.setTickLabelFont(new Font("Arial", Font.BOLD, 25));
				//System.out.println(logYAxis.getTickLabelFont().toString());
				
				//logYAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			    //logYAxis.setBase(10);
				
				if(true){
					chart.getCategoryPlot().setRangeAxis(logYAxis);
				}
				//chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("Arial", Font.BOLD, 20));
				//((LogAxis) chart.getCategoryPlot().getRangeAxis()).setTickLabelFont(new Font("Arial", Font.BOLD, 20));
			    //logYAxis.setTickLabelFont(new Font("Arial", Font.BOLD, 10));
			    //chart.getCategoryPlot().setRenderer(new StatisticalLineAndShapeRenderer(true, true));
				
				
			} else {
				((NumberAxis) chart.getCategoryPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#0.000000"));
			}

			
			if(true){
				File file = new File(mapName + " " + chart.getTitle().getText() + ".png");

				try {
					ChartUtilities.saveChartAsPNG(file, chart, width, height);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				
				Graphics2D g = new EpsGraphics2D();
		        chart.draw(g, new Rectangle(width, height));
		        Writer out;
				try {
					out = new FileWriter(mapName + " " + chart.getTitle().getText() + ".eps");
					out.write(g.toString());
			        out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	
	public JFreeChart generateChartsNew(int width, int height, Map<String,String> algorithms, int[] agentNumbers, 
			boolean relativeValues, List<String> excludeFromMin, boolean grayScale, Double rangeAxisUpperBound, String metricName) {

		int metricsNumber = algorithmReports.get(0).getMetrics().size();
		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
		
		String title = "";
		
		if("map_islands.xml initialTime 1".equals(mapName)){
			title = "Island";
		}
		else if("map_grid.xml initialTime 1".equals(mapName)){
			title = "Grid";
		}
		else if("map_cicles_corridor.xml initialTime 1".equals(mapName)){
			title = "Cicles-Corridor";
		}
		else if("map_a.xml initialTime 1".equals(mapName)){
			title = "Map A";
		}
		
		String xAxisLabel = "# Agents";
		String yAxisLabel = "Normalized Metric Value";
		yAxisLabel = "";

		int metricIndex = -1;

		for(int i = 0; i < this.algorithmReports.get(0).getMetrics().size(); i++){
			if(this.algorithmReports.get(0).getMetrics().get(i).getName().equals(metricName)){
				metricIndex = i;
			}
		}

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		//DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
		ArrayList<Double> best = new ArrayList<Double>();
		//yAxisLabel = algorithmReports.get(0).getMetrics().get(metricIndex).getName();

		for(int i = 0; i < agentSets.length; i++){
			double min = Double.MAX_VALUE;
			for (AlgorithmReport algReport : algorithmReports) {

				if (!excludeFromMin.contains(algReport.getAlgorithmName()) 
						&& algReport.getMetrics().get(metricIndex).getMean(i) < min) {
					min = algReport.getMetrics().get(metricIndex).getMean(i);
				}
			}
			best.add(min);
		}

		for (String alg : algorithms.keySet()) {
			for (AlgorithmReport algReport : algorithmReports) {
				if (algReport.getAlgorithmName().equals(alg)) {
					for (int i = 0; i < agentSets.length; i++) {
						// dataset.addValue(algReport.getMetrics().get(i).getMean(j),
						// algReport.getAlgorithmName(),
						// this.map.getInitialPositions().get(j).length +
						// "");

						for (int j = 0; j < agentNumbers.length; j++) {
							if (agentSets[i] == agentNumbers[j]) {

								if (relativeValues) {
									/*dataset.add(algReport.getMetrics().get(metricIndex).getMean(i) / best.get(i), 
											algReport.getMetrics().get(metricIndex).getStdDev(i) / best.get(i),
											algorithms.get(alg), agentSets[i] + "");*/

									dataset.addValue(algReport.getMetrics().get(metricIndex).getMean(j) / best.get(j),
											algorithms.get(alg), agentSets[j] + "");
								} else {
									/*dataset.add(algReport.getMetrics().get(metricIndex).getMean(i), 
											algReport.getMetrics().get(metricIndex).getStdDev(i), 
											algorithms.get(alg),
											agentSets[i] + "");*/

									dataset.addValue(algReport.getMetrics().get(metricIndex).getMean(j) / best.get(j),
											algorithms.get(alg), agentSets[j] + "");
								}

							}
						}
					}
				}
			}
		}

		charts.add(ChartFactory.createLineChart(title, 
				xAxisLabel, yAxisLabel, dataset, 
				PlotOrientation.VERTICAL, true, true, false));

		//chartCustomization
		float seriesLineStroke = 2.5f;
		float gridLineStroke = 1;
		float axisStroke = 1;
		float tickMarkStroke = 1;
		int tickFont = 27;
		int labelFont = 35;
		int legendFontN = 30;

		Font yTickLabelFont = new Font("Dialog", Font.PLAIN, tickFont);
		Font xTickLabelFont = new Font("Dialog", Font.PLAIN, tickFont);
		Font xLabelFont = new Font("Dialog", Font.PLAIN, labelFont);
		Font legendFont = new Font("Dialog", Font.PLAIN, legendFontN);
		Font titleFont = new Font("Dialog", Font.PLAIN, labelFont);

		for (JFreeChart chart : charts) {
			//chart.getCategoryPlot().setRenderer(new StatisticalLineAndShapeRenderer(true, true));
			//chart.getCategoryPlot().setRenderer(new LineAndShapeRenderer(true, true));

			CategoryPlot categoryPlot = chart.getCategoryPlot();
			//categoryPlot.setRenderer(new DefaultCategoryItemRenderer());
			categoryPlot.setRenderer(new StatisticalLineAndShapeRenderer(true, true));
			
			//((AbstractRenderer) categoryPlot.getRenderer()).setBaseLegendShape(new Rectangle(30,30));
			chart.getTitle().setPosition(RectangleEdge.TOP);
			
			System.out.println(dataset.getColumnKey(1));
			categoryPlot.addAnnotation(new CategoryTextAnnotation(chart.getTitle().getText(), "1", 3.5));
			
			chart.getTitle().setFont(titleFont);
			chart.getLegend().setItemFont(legendFont);
			//categoryPlot.getRenderer().getLegendItem(0, 1);
			//LegendTitle legend = new LegendTitle(categoryPlot.getRenderer());

			if (grayScale) {
				GrayPaintScale paintScale = new GrayPaintScale(0, 5);
				categoryPlot.setBackgroundPaint(paintScale.getPaint(4));
				for (int i = 0; i < categoryPlot.getDataset().getRowCount(); i++) {
					categoryPlot.getRenderer().setSeriesPaint(i, paintScale.getPaint(i));
				} 
			}
			categoryPlot.getRenderer().setStroke(new BasicStroke(seriesLineStroke));

			categoryPlot.setRangeGridlineStroke(new BasicStroke(gridLineStroke));
			categoryPlot.setRangeGridlinesVisible(false);
			chart.getCategoryPlot().setRangeGridlinePaint(Color.BLACK);

			if (relativeValues) {
				NumberAxis yAxis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
				yAxis.setAxisLineStroke(new BasicStroke(axisStroke));
				yAxis.setTickMarkStroke(new BasicStroke(tickMarkStroke));
				yAxis.setTickMarkPaint(Color.BLACK);
				yAxis.setAxisLinePaint(Color.BLACK);
				yAxis.setTickLabelFont(yTickLabelFont);
				//yAxis.setTickLabelFont(yAxis.getTickLabelFont().deriveFont(Font.BOLD));

				if(rangeAxisUpperBound != null){
					
					//yAxis.setUpperBound(rangeAxisUpperBound);
					//yAxis.setFixedAutoRange(4);
					yAxis.setLowerBound(0.90);
					yAxis.setUpperBound(4);
					//yAxis.setAutoRangeStickyZero(false);
					System.out.println(yAxis.getUpperBound());
					//yAxis.setUpperBound(1);
				}

				//chart.getCategoryPlot().getRangeAxis().setLabel("Normalized Metric Value");

				yAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
				yAxis.setAutoRangeIncludesZero(false);

				CategoryAxis xAxis = (CategoryAxis) chart.getCategoryPlot().getDomainAxis();
				xAxis.setAxisLineStroke(new BasicStroke(axisStroke));
				xAxis.setTickMarkStroke(new BasicStroke(tickMarkStroke));
				xAxis.setTickMarkPaint(Color.BLACK);
				xAxis.setAxisLinePaint(Color.BLACK);
				xAxis.setTickLabelFont(xTickLabelFont);
				xAxis.setLabelFont(xLabelFont);
				//xAxis.setTickLabelFont(yAxis.getTickLabelFont().deriveFont(Font.BOLD));


				//LogAxis logYAxis = new LogAxis(yAxis.getLabel());
				LogarithmicAxis logYAxis = new LogarithmicAxis(yAxis.getLabel());
				logYAxis.setAxisLineStroke(new BasicStroke(axisStroke));
				logYAxis.setTickMarkStroke(new BasicStroke(tickMarkStroke));
				logYAxis.setTickMarkPaint(Color.BLACK);
				logYAxis.setAxisLinePaint(Color.BLACK);


				//logYAxis.setAutoRangeIncludesZero(false);

				//logYAxis.setStandardTickUnits(LogarithmicAxis.createIntegerTickUnits());

				logYAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
				//System.out.println(yAxis.getTickLabelFont().toString());
				logYAxis.setLabelFont(yAxis.getLabelFont());
				//logYAxis.setLabel("Teste");
				logYAxis.setTickLabelFont(yAxis.getTickLabelFont());
				//logYAxis.setTickLabelFont(new Font("Arial", Font.BOLD, 25));
				//System.out.println(logYAxis.getTickLabelFont().toString());

				//logYAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				//logYAxis.setBase(10);

				if(false){
					chart.getCategoryPlot().setRangeAxis(logYAxis);
				}
				//chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("Arial", Font.BOLD, 20));
				//((LogAxis) chart.getCategoryPlot().getRangeAxis()).setTickLabelFont(new Font("Arial", Font.BOLD, 20));
				//logYAxis.setTickLabelFont(new Font("Arial", Font.BOLD, 10));
				//chart.getCategoryPlot().setRenderer(new StatisticalLineAndShapeRenderer(true, true));


			} else {
				((NumberAxis) chart.getCategoryPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#0.000000"));
			}


			if(true){
				File file = new File(mapName + " " + chart.getTitle().getText() + ".png");

				try {
					ChartUtilities.saveChartAsPNG(file, chart, width, height);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {

				Graphics2D g = new EpsGraphics2D();
				chart.draw(g, new Rectangle(width, height));
				Writer out;
				try {
					out = new FileWriter(mapName + " " + chart.getTitle().getText() + ".eps");
					out.write(g.toString());
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return charts.get(0);

	}
	
	public JFreeChart generateChartsReworked(int width, int height, Map<String,String> algorithms, int[] agentNumbers, 
			boolean relativeValues, List<String> excludeFromMin, boolean grayScale, Double rangeAxisUpperBound, String metricName) {

		int metricsNumber = algorithmReports.get(0).getMetrics().size();
		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
		
		String title = "";
		
		if("map_islands.xml initialTime 1".equals(mapName)){
			title = "Island";
		}
		else if("map_grid.xml initialTime 1".equals(mapName)){
			title = "Grid";
		}
		else if("map_cicles_corridor.xml initialTime 1".equals(mapName)){
			title = "Cicles-Corridor";
		}
		else if("map_a.xml initialTime 1".equals(mapName)){
			title = "Map A";
		}
		
		String xAxisLabel = "# Agents";
		String yAxisLabel = "Normalized Metric Value";
		xAxisLabel = "";
		yAxisLabel = "";

		int metricIndex = -1;

		for(int i = 0; i < this.algorithmReports.get(0).getMetrics().size(); i++){
			if(this.algorithmReports.get(0).getMetrics().get(i).getName().equals(metricName)){
				metricIndex = i;
			}
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();

		///
		
		//DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		//DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
		ArrayList<Double> best = new ArrayList<Double>();
		//yAxisLabel = algorithmReports.get(0).getMetrics().get(metricIndex).getName();

		for(int i = 0; i < agentSets.length; i++){
			double min = Double.MAX_VALUE;
			for (AlgorithmReport algReport : algorithmReports) {

				if (!excludeFromMin.contains(algReport.getAlgorithmName()) 
						&& algReport.getMetrics().get(metricIndex).getMean(i) < min) {
					min = algReport.getMetrics().get(metricIndex).getMean(i);
				}
			}
			best.add(min);
		}

		for (String alg : algorithms.keySet()) {
			for (AlgorithmReport algReport : algorithmReports) {
				if (algReport.getAlgorithmName().equals(alg)) {
					
					XYSeries serie = new XYSeries(algReport.getAlgorithmName());
					
					for (int i = 0; i < agentSets.length; i++) {
						// dataset.addValue(algReport.getMetrics().get(i).getMean(j),
						// algReport.getAlgorithmName(),
						// this.map.getInitialPositions().get(j).length +
						// "");

						for (int j = 0; j < agentNumbers.length; j++) {
							if (agentSets[i] == agentNumbers[j]) {

								if (relativeValues) {
									/*dataset.add(algReport.getMetrics().get(metricIndex).getMean(i) / best.get(i), 
											algReport.getMetrics().get(metricIndex).getStdDev(i) / best.get(i),
											algorithms.get(alg), agentSets[i] + "");*/

									/*dataset.addValue(algReport.getMetrics().get(metricIndex).getMean(j) / best.get(j),
											algorithms.get(alg), agentSets[j] + "");*/
									
									serie.add(agentSets[j], algReport.getMetrics().get(metricIndex).getMean(j) / best.get(j));
								} else {
									/*dataset.add(algReport.getMetrics().get(metricIndex).getMean(i), 
											algReport.getMetrics().get(metricIndex).getStdDev(i), 
											algorithms.get(alg),
											agentSets[i] + "");*/

									/*dataset.addValue(algReport.getMetrics().get(metricIndex).getMean(j) / best.get(j),
											algorithms.get(alg), agentSets[j] + "");*/
									
									serie.add(agentSets[j], algReport.getMetrics().get(metricIndex).getMean(j) / best.get(j));
								}

							}
						}
					}
					
					dataset.addSeries(serie);
				}
			}
		}
		
		charts.add(ChartFactory.createXYLineChart(
	            title,      // chart title
	            xAxisLabel,                      // x axis label
	            yAxisLabel,                      // y axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL,
	            true,                     // include legend
	            true,                     // tooltips
	            false                     // urls
	        ));

		//chartCustomization
		float seriesLineStroke = 5.5f;
		float gridLineStroke = 1;
		float axisStroke = 1;
		float tickMarkStroke = 1;
		int tickFont = 27;
		int labelFont = 35;
		int legendFontN = 30;
		
		double shapeScale = 2;

		Font yTickLabelFont = new Font("Dialog", Font.PLAIN, tickFont);
		Font xTickLabelFont = new Font("Dialog", Font.PLAIN, tickFont);
		Font xLabelFont = new Font("Dialog", Font.PLAIN, labelFont);
		Font legendFont = new Font("Dialog", Font.PLAIN, legendFontN);
		Font titleFont = new Font("Dialog", Font.PLAIN, labelFont);

		for (JFreeChart chart : charts) {
			//chart.getCategoryPlot().setRenderer(new StatisticalLineAndShapeRenderer(true, true));
			//chart.getCategoryPlot().setRenderer(new LineAndShapeRenderer(true, true));

			XYPlot plot = chart.getXYPlot();
			//categoryPlot.setRenderer(new DefaultCategoryItemRenderer());
			
			//plot.setRenderer(new XYLineAndShapeRenderer());
			Custom custom = new Custom();
			custom.setBaseShapeScale(shapeScale);
			plot.setRenderer(custom);
			
			//plot.setRenderer(new StatisticalLineAndShapeRenderer(true, true));
			
			//((AbstractRenderer) categoryPlot.getRenderer()).setBaseLegendShape(new Rectangle(30,30));
			chart.getTitle().setPosition(RectangleEdge.TOP);
			chart.getTitle().setFont(titleFont);
			chart.getLegend().setItemFont(legendFont);
			
			//System.out.println(dataset.getColumnKey(1));
			//categoryPlot.addAnnotation(new CategoryTextAnnotation(chart.getTitle().getText(), "1", 3.5));
			plot.addAnnotation(new XYTitleAnnotation(0.98, 0.95, chart.getTitle(),RectangleAnchor.RIGHT));
			plot.setBackgroundPaint(new Color(232,232,232));
			
			//categoryPlot.getRenderer().getLegendItem(0, 1);
			//LegendTitle legend = new LegendTitle(categoryPlot.getRenderer());

			/*if (grayScale) {
				GrayPaintScale paintScale = new GrayPaintScale(0, 5);
				plot.setBackgroundPaint(paintScale.getPaint(4));
				for (int i = 0; i < plot.getDataset().getRowCount(); i++) {
					plot.getRenderer().setSeriesPaint(i, paintScale.getPaint(i));
				} 
			}*/
			plot.getRenderer().setStroke(new BasicStroke(seriesLineStroke));

			plot.setRangeGridlineStroke(new BasicStroke(gridLineStroke));
			plot.setRangeGridlinesVisible(false);
			plot.setDomainGridlinesVisible(false);
			plot.setRangeGridlinePaint(Color.BLACK);

			if (relativeValues) {
				NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
				yAxis.setAxisLineStroke(new BasicStroke(axisStroke));
				yAxis.setTickMarkStroke(new BasicStroke(tickMarkStroke));
				yAxis.setTickMarkPaint(Color.BLACK);
				yAxis.setAxisLinePaint(Color.BLACK);
				yAxis.setTickLabelFont(yTickLabelFont);
				//yAxis.setTickLabelFont(yAxis.getTickLabelFont().deriveFont(Font.BOLD));

				if(rangeAxisUpperBound != null){
					
					//yAxis.setUpperBound(rangeAxisUpperBound);
					//yAxis.setFixedAutoRange(4);
					yAxis.setLowerBound(0.90);
					yAxis.setUpperBound(4);
					//yAxis.setAutoRangeStickyZero(false);
					System.out.println(yAxis.getUpperBound());
					//yAxis.setUpperBound(1);
				}

				//chart.getCategoryPlot().getRangeAxis().setLabel("Normalized Metric Value");

				yAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
				yAxis.setAutoRangeIncludesZero(false);

				NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
				xAxis.setAxisLineStroke(new BasicStroke(axisStroke));
				xAxis.setTickMarkStroke(new BasicStroke(tickMarkStroke));
				xAxis.setTickMarkPaint(Color.BLACK);
				xAxis.setAxisLinePaint(Color.BLACK);
				xAxis.setTickLabelFont(xTickLabelFont);
				xAxis.setLabelFont(xLabelFont);
				//xAxis.setTickLabelFont(yAxis.getTickLabelFont().deriveFont(Font.BOLD));


				//LogAxis logYAxis = new LogAxis(yAxis.getLabel());
				LogarithmicAxis logYAxis = new LogarithmicAxis(yAxis.getLabel());
				logYAxis.setAxisLineStroke(new BasicStroke(axisStroke));
				logYAxis.setTickMarkStroke(new BasicStroke(tickMarkStroke));
				logYAxis.setTickMarkPaint(Color.BLACK);
				logYAxis.setAxisLinePaint(Color.BLACK);


				//logYAxis.setAutoRangeIncludesZero(false);

				//logYAxis.setStandardTickUnits(LogarithmicAxis.createIntegerTickUnits());

				logYAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
				//System.out.println(yAxis.getTickLabelFont().toString());
				logYAxis.setLabelFont(yAxis.getLabelFont());
				//logYAxis.setLabel("Teste");
				logYAxis.setTickLabelFont(yAxis.getTickLabelFont());
				//logYAxis.setTickLabelFont(new Font("Arial", Font.BOLD, 25));
				//System.out.println(logYAxis.getTickLabelFont().toString());

				//logYAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				//logYAxis.setBase(10);

				if(false){
					chart.getCategoryPlot().setRangeAxis(logYAxis);
				}
				//chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("Arial", Font.BOLD, 20));
				//((LogAxis) chart.getCategoryPlot().getRangeAxis()).setTickLabelFont(new Font("Arial", Font.BOLD, 20));
				//logYAxis.setTickLabelFont(new Font("Arial", Font.BOLD, 10));
				//chart.getCategoryPlot().setRenderer(new StatisticalLineAndShapeRenderer(true, true));


			} else {
				((NumberAxis) chart.getCategoryPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#0.000000"));
			}


			if(true){
				File file = new File(mapName + " " + chart.getTitle().getText() + ".png");

				try {
					ChartUtilities.saveChartAsPNG(file, chart, width, height);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {

				Graphics2D g = new EpsGraphics2D();
				chart.draw(g, new Rectangle(width, height));
				Writer out;
				try {
					out = new FileWriter(mapName + " " + chart.getTitle().getText() + ".eps");
					out.write(g.toString());
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return charts.get(0);

	}

	public int[] getAgentNumbers() {
		return agentSets;
	}

	public void setAgentNumbers(int[] agentSets) {
		this.agentSets = agentSets;
	}
	
	public int[] getAgentSets() {
		return agentSets;
	}

	public void setAgentSets(int[] agentSets) {
		this.agentSets = agentSets;
	}
}

