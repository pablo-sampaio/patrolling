package yaps.experimentation;

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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

import yaps.util.DoubleList;


/**
 * Class for drawing graphs with the results of the experiments.
 * 
 * @author Rodrigo de Sousa
 *
 */
public class ChartUtil {
	
	int width;
	int height;
	float seriesLineStroke;
	float gridLineStroke;
	float axisStroke;
	float tickMarkStroke;
	int tickFontSize;
	int labelFontSize;
	int legendFontSize;
	double shapeScale;
	Color backgroundColor;
	boolean xAxisLabelVisible;
	boolean yAxisLabelVisible;
	boolean chartTitleInsideChartArea;
	
	String xAxisLabel;
	String yAxisLabel;

	public ChartUtil() {
		seriesLineStroke = 5.5f;
		gridLineStroke = 1;
		axisStroke = 1;
		tickMarkStroke = 1;
		tickFontSize = 27;
		labelFontSize = 35;
		legendFontSize = 30;
		shapeScale = 2;
		backgroundColor = new Color(220,220,220);
		//backgroundColor = new Color(232,232,232);
		xAxisLabelVisible = false;
		yAxisLabelVisible = false;
		chartTitleInsideChartArea = true;
	}
	
	public ChartUtil(int width, int height, String xAxisLabel, String yAxisLabel, float seriesLineStroke, float gridLineStroke, float axisStroke,
			float tickMarkStroke, int tickFontSize, int labelFontSize, int legendFontSize, double shapeScale, Color backgroundColor,
			boolean xAxisLabelVisible, boolean yAxisLabelVisible, boolean chartTitleInsideChartArea) {
		this.width = width;
		this.height = height;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.seriesLineStroke = seriesLineStroke;
		this.gridLineStroke = gridLineStroke;
		this.axisStroke = axisStroke;
		this.tickMarkStroke = tickMarkStroke;
		this.tickFontSize = tickFontSize;
		this.labelFontSize = labelFontSize;
		this.legendFontSize = legendFontSize;
		this.shapeScale = shapeScale;
		this.backgroundColor = backgroundColor;
		this.xAxisLabelVisible = xAxisLabelVisible;
		this.yAxisLabelVisible = yAxisLabelVisible;
		this.chartTitleInsideChartArea = chartTitleInsideChartArea;
	}
	
	public void generateCharts(Map<String,String> chartTitles, ArrayList<ExperimentReport> reports, int width, int height, Map<String,String> algorithms, int[] agentNumbers, 
			boolean relativeValues, List<String> excludeFromMin, boolean grayScale, Double rangeAxisUpperBound, String metricName) throws IOException, CloneNotSupportedException{
		
		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
		
		for(ExperimentReport report : reports){
			charts.add(generateChartsReworked(chartTitles.get(report.getMapSettings().getName()), report, algorithms, agentNumbers, relativeValues, excludeFromMin, grayScale, rangeAxisUpperBound, metricName));
		}
		
	    boolean var = true;
	    if (var) {
			Graphics2D g = new EpsGraphics2D();
			LegendTitle legend = (LegendTitle) charts.get(0).getLegend();
			
			for(int i = 0; i < charts.size(); i++){
				charts.get(i).getLegend().setVisible(false);
			}
			
			////Rendering
			ChartRenderingInfo baseInfo = new ChartRenderingInfo();
			ChartRenderingInfo comparingInfo = new ChartRenderingInfo();
			
			charts.get(0).getXYPlot().getDomainAxis().setLabel("");
			charts.get(1).getXYPlot().getDomainAxis().setLabel("");
			charts.get(0).getXYPlot().setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			charts.get(0).getXYPlot().getRangeAxis().setTickLabelsVisible(false);
			charts.get(2).getXYPlot().setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			charts.get(2).getXYPlot().getRangeAxis().setTickLabelsVisible(false);
			
			charts.get(0).draw(g, new Rectangle2D.Double(0, 0, width, height), baseInfo);
			double baseWidth = baseInfo.getPlotInfo().getDataArea().getWidth();
			double baseHeight = baseInfo.getPlotInfo().getDataArea().getHeight();
			
			charts.get(3).draw(g, new Rectangle2D.Double(width, height, width, height), comparingInfo);
			double comparingWidth = comparingInfo.getPlotInfo().getDataArea().getWidth();
			double comparingHeight = comparingInfo.getPlotInfo().getDataArea().getHeight();
			double widthDiff = baseWidth - comparingWidth;
			double heightDiff = baseHeight - comparingHeight;
			
			charts.get(3).draw(g, new Rectangle2D.Double(width, height, width + widthDiff, height + heightDiff));
			charts.get(1).draw(g, new Rectangle2D.Double(width, 0, width + widthDiff, height));
			charts.get(2).draw(g, new Rectangle2D.Double(0, height, width, height + heightDiff));
			////
			
			double legendW = legend.arrange(g).getWidth();
			double legendH = legend.arrange(g).getHeight();
			legend.draw(g, new Rectangle2D.Double( width - (legendW-widthDiff)/2.0, 2*height+heightDiff, legendW, legendH));
			
			Writer out;
			try {
				out = new FileWriter(metricName + ".eps");
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
			
			//System.out.println(charts.get(0).getLegend().getBounds().getBounds().getWidth());
			//System.out.println(charts.get(0).getLegend().getHeight());
		}
		
	}
	
	public JFreeChart generateChartsReworked(String chartTitle, ExperimentReport experimentReport, Map<String,String> algorithms, int[] agentNumbers, 
			boolean relativeValues, List<String> excludeFromMin, boolean grayScale, Double rangeAxisUpperBound, String metricName) {
		
		List<AlgorithmReport> algorithmReports = experimentReport.getAlgorithmReports();
		//int[] agentSets = experimentReport.getAgentSets();
		List<Integer> agentSets = new ArrayList<Integer>(experimentReport.getNumbersOfAgents());

		int metricsNumber = algorithmReports.get(0).getMetrics().size();
		ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
		
		/*String xAxisLabel = "# Agents";
		String yAxisLabel = "Normalized Metric Value";
		xAxisLabel = "";
		yAxisLabel = "";*/

		int metricIndex = -1;

		for(int i = 0; i < algorithmReports.get(0).getMetrics().size(); i++){
			if(algorithmReports.get(0).getMetrics().get(i).getName().equals(metricName)){
				metricIndex = i;
			}
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();

		///
		
		//DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		//DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
		ArrayList<Double> best = new ArrayList<Double>();
		//yAxisLabel = algorithmReports.get(0).getMetrics().get(metricIndex).getName();

		for(int i = 0; i < agentSets.size(); i++){
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
					
					XYSeries serie = new XYSeries(algorithms.get(alg));
					
					//TODO: ver comentario no outro metodo -- estes dois arrays muito provavelmente sao iguais...
					for (int i = 0; i < agentSets.size(); i++) {
						for (int j = 0; j < agentNumbers.length; j++) {
							if (agentSets.get(i) == agentNumbers[j]) {
								if (relativeValues) {
	
									serie.add((int)agentSets.get(j), algReport.getMetrics().get(metricIndex).getMean(j) / best.get(j));
			
								} else {
									
									serie.add((int)agentSets.get(j), algReport.getMetrics().get(metricIndex).getMean(j));
								}

							}
						}
					}
					
					dataset.addSeries(serie);
				}
			}
		}
		
		if(!xAxisLabelVisible){
			xAxisLabel = "";
		}
		
		if(!yAxisLabelVisible){
			yAxisLabel = "";
		}
	
		charts.add(ChartFactory.createXYLineChart(
	            chartTitle,		// chart title
	            xAxisLabel,		// x axis label
	            yAxisLabel,		// y axis label
	            dataset,		// data
	            PlotOrientation.VERTICAL,
	            true,			// include legend
	            true,			// tooltips
	            false			// urls
	        ));

		//chartCustomization
		String font = "sans-serif";
		Font yTickLabelFont = new Font(font, Font.PLAIN, tickFontSize);
		Font xTickLabelFont = new Font(font, Font.PLAIN, tickFontSize);
		Font xLabelFont = new Font(font, Font.PLAIN, labelFontSize);
		Font legendFont = new Font(font, Font.PLAIN, legendFontSize);
		Font titleFont = new Font(font, Font.PLAIN, labelFontSize);
		
		//Custom custom = new Custom();
		//custom.setBaseShapeScale(shapeScale);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		
		for (JFreeChart chart : charts) {
				
			chart.getTitle().setVisible(!chartTitleInsideChartArea);
			//chart.getLegend().setVisible(false);
			
			XYPlot plot = chart.getXYPlot();
			plot.setRenderer(renderer);
			
			//chart.getTitle().setPosition(RectangleEdge.TOP);
			chart.getTitle().setFont(titleFont);
			//chart.getTitle().setBorder(1, 1, 1, 1);
			//chart.getTitle().setBackgroundPaint(Color.white);
			chart.getLegend().setItemFont(legendFont);
			//chart.get
			//System.out.println(custom.lookupLegendShape(0).getBounds2D().toString());
			
			for(int i = 0; i < plot.getSeriesCount();i++){
				Shape shape = renderer.lookupLegendShape(i);
				AffineTransform at = new AffineTransform();
				at.scale(shapeScale, shapeScale);
				shape = at.createTransformedShape(shape);
				renderer.setSeriesShape( i, shape);
			}
			
			XYTitleAnnotation titleAnnotation = new XYTitleAnnotation(0.98, 0.75, chart.getTitle(), RectangleAnchor.RIGHT);
			plot.addAnnotation(titleAnnotation);
			plot.setBackgroundPaint(backgroundColor);
			plot.setOutlineVisible(false);
			
			//categoryPlot.getRenderer().getLegendItem(0, 1);
			//LegendTitle legend = new LegendTitle(categoryPlot.getRenderer());

			/*if (grayScale) {
				GrayPaintScale paintScale = new GrayPaintScale(0, 5);
				plot.setBackgroundPaint(paintScale.getPaint(4));
				for (int i = 0; i < plot.getDataset().getRowCount(); i++) {
					plot.getRenderer().setSeriesPaint(i, paintScale.getPaint(i));
				} 
			}*/
			
			renderer.setStroke(new BasicStroke(seriesLineStroke));

			plot.setRangeGridlineStroke(new BasicStroke(gridLineStroke));
			plot.setRangeGridlinesVisible(false);
			plot.setDomainGridlinesVisible(false);
			//plot.setRangeGridlinePaint(Color.BLACK);

			if (relativeValues) {
				NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
				yAxis.setAxisLineStroke(new BasicStroke(axisStroke));
				yAxis.setTickMarkStroke(new BasicStroke(tickMarkStroke));
				yAxis.setTickMarkPaint(Color.BLACK);
				yAxis.setAxisLinePaint(Color.BLACK);
				yAxis.setTickLabelFont(yTickLabelFont);
				//yAxis.setTickLabelFont(yAxis.getTickLabelFont().deriveFont(Font.BOLD));
			

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
				
				if(rangeAxisUpperBound != null){
					
					//yAxis.setUpperBound(4.1);
					logYAxis.setUpperBound(rangeAxisUpperBound);
					logYAxis.setLowerBound(0.9);
					yAxis.setUpperBound(rangeAxisUpperBound);
					yAxis.setLowerBound(0.9);

					//yAxis.setFixedAutoRange(4);
					//yAxis.setUpperMargin(0.1);
					//yAxis.setAutoRangeStickyZero(false);
					System.out.println(yAxis.getUpperBound());
					//yAxis.setUpperBound(1);
				}

				if(true){
					chart.getXYPlot().setRangeAxis(logYAxis);
				}
				//chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("Arial", Font.BOLD, 20));
				//((LogAxis) chart.getCategoryPlot().getRangeAxis()).setTickLabelFont(new Font("Arial", Font.BOLD, 20));
				//logYAxis.setTickLabelFont(new Font("Arial", Font.BOLD, 10));
				//chart.getCategoryPlot().setRenderer(new StatisticalLineAndShapeRenderer(true, true));


			} else {
				((NumberAxis) chart.getCategoryPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#0.000000"));
			}

			
			if(true){
				File file = new File(experimentReport.getMapSettings().getName() + " " + chart.getTitle().getText() + ".png");

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
					out = new FileWriter(experimentReport.getMapSettings().getName() + " " + chart.getTitle().getText() + ".eps");
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

}