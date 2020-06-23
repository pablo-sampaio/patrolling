package algorithms.rodrigo;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import algorithms.rodrigo.experiments.AlgorithmReport;
import algorithms.rodrigo.experiments.BatchExperiment;
import algorithms.rodrigo.experiments.ChartUtil;
import algorithms.rodrigo.experiments.ExperimentReport;
import algorithms.rodrigo.experiments.Map;
import algorithms.rodrigo.experiments.MetricReport;
import algorithms.rodrigo.koenig.Img2Graph;
import algorithms.rodrigo.koenig.LRTAAlgorithm;
import algorithms.rodrigo.koenig.NodeCountingAlgorithm;
import algorithms.rodrigo.machado.ReactiveWithFlagsAlgorithm;
import yaps.graph.Graph;
import yaps.graph.generators.SimpleGraphGenerators;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;
import yaps.util.RandomUtil;

public class TestArtigo {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		experiment();
		// grid();

		// BalloonDFSViewer viewer = new
		// BalloonDFSViewer(GraphUtil.generateGridGraph(5, 5));
		// experiment2();

		//fromJSON();
		
		 //test();

	}

	public static void test() throws IOException {
		String filepath = "map_islands.xml initialTime 1 30 runs 50000 turns.json";
		String[] names = { "Intervalo M�ximo", "Intervalo Quadr�tico M�dio", "Desvio Padr�o das Frequ�ncias" };

		ExperimentReport ex = ExperimentReport.fromJSON(filepath);

		for (AlgorithmReport a : ex.getAlgorithmReports()) {
			for (int i = 0; i < a.getMetrics().size(); i++) {
				a.getMetrics().get(i).setName(names[i]);
			}
		}

		String json = ex.toJSON();

		PrintWriter pw = new PrintWriter(filepath);
		pw.write(json);
		pw.close();

	}

	public static void fromJSON() throws IOException, CloneNotSupportedException {
		float seriesLineStroke = 2f;
		float gridLineStroke = 1;
		float axisStroke = 1;
		float tickMarkStroke = 1;
		int tickFontSize = 30;
		int labelFontSize = 35;
		int legendFontSize = 30;
		double shapeScale = 1.5;
		Color backgroundColor = new Color(220,220,220);
		//backgroundColor = new Color(232,232,232);
		boolean xAxisLabelVisible = true;
		boolean yAxisLabelVisible = false;
		boolean chartTitleInsideChartArea = true;
		
		//
		
		boolean relative = true;
		boolean grayscale = false;
		
		boolean rankingIMax = false;
		boolean rankingIQM = false;
		boolean rankingSDF = true;
		boolean rankingGeral = false;
		
		int width = 800;
		int height = 600;

		int[] agentSets = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };

		List<String> excluded = new LinkedList<>();
		excluded.add("CR");
		excluded.add("ECount");
		excluded.add("PVAW3");
		excluded.add("Zr PVAW3");
		excluded.add("EZr2(LRTA*,g)");
		excluded.add("EZr2(Thrun,g)");
		excluded.add("EZr2(Wagner,g)");
		excluded.add("EZr2(LRTA*,n)");
		excluded.add("EZr2(Thrun,n)");
		excluded.add("EZr2(Wagner,n)");
		if(false){
			excluded.add("PVAW3");
			excluded.add("Zr PVAW3");
			excluded.add("Zr(NCount,g)");
			excluded.add("Zr(LRTA*,g)");
			excluded.add("Zr(Thrun,g)");
			excluded.add("Zr(Wagner,g)");
			excluded.add("Zr(RFlag,g)");
			excluded.add("EZr(NCount,g)");
			excluded.add("EZr1(LRTA*,g)");
			excluded.add("EZr1(Thrun,g)");
			excluded.add("EZr1(Wagner,g)");
			excluded.add("EZr(RFlag,g)");
			excluded.add("EZr2(LRTA*,g)");
			excluded.add("EZr2(Thrun,g)");
			excluded.add("EZr2(Wagner,g)");
			excluded.add("Zr(NCount,n)");
			excluded.add("Zr(LRTA*,n)");
			excluded.add("Zr(Thrun,n)");
			excluded.add("Zr(Wagner,n)");
			excluded.add("Zr(RFlag,n)");
			excluded.add("EZr(NCount,n)");
			excluded.add("EZr1(LRTA*,n)");
			excluded.add("EZr1(Thrun,n)");
			excluded.add("EZr1(Wagner,n)");
			excluded.add("EZr(RFlags,n)");
			excluded.add("EZr2(LRTA*,n)");
			excluded.add("EZr2(Thrun,n)");
			excluded.add("EZr2(Wagner,n)");
		}
		
		LinkedHashMap<String, String> mapNames = new LinkedHashMap<String, String>();
		mapNames.put("map_islands.xml initialTime 1", "Map Islands");
		/*mapNames.put("map_grid.xml initialTime 1", "Map Grid");
		mapNames.put("map_cicles_corridor.xml initialTime 1", "Map Cicles-Corridor");
		mapNames.put("map_a.xml initialTime 1", "Map A");*/
		
		LinkedHashMap<String, String> algs = new LinkedHashMap<String, String>();

		ExperimentReport exMapA = ExperimentReport.fromJSON("map_a.xml initialTime 1 30 runs 50000 turns.json");
		/*ExperimentReport exCircles = ExperimentReport.fromJSON("map_cicles_corridor.xml initialTime 1 30 runs 50000 turns.json");
		ExperimentReport exGrid = ExperimentReport.fromJSON("map_grid.xml initialTime 1 30 runs 50000 turns.json");
		ExperimentReport exMapIsland = ExperimentReport.fromJSON("map_islands.xml initialTime 1 30 runs 50000 turns.json");*/
		
		ArrayList<ExperimentReport> reports = new ArrayList<ExperimentReport>();
		reports.add(exMapA);
		/*reports.add(exCircles);
		reports.add(exGrid);
		reports.add(exMapIsland);*/
		
		double[] upperBounds;
		double upperBound;

		// IMax

		if (rankingIMax) {
			algs.clear();
			
			algs.put("EZr(RFlag,g)", "EZr(RFlag,g)");
			algs.put("EZr(RFlags,n)", "EZr(RFlag,n)");
			algs.put("EZr1(Thrun,g)", "EZr(Thrun,g)");
			algs.put("Wagner", "Wagner");
			
			//upperBounds = getHighestValueBoundsAcrossMaps(reports, algs);
			//upperBounds = getMeanValueBoundsAcrossMaps(reports, algs);
			//upperBound = upperBounds[0]/100;
			upperBound = 4.1;
			
			ChartUtil chartUtil = new ChartUtil(width, height, "# Agents", "Y", seriesLineStroke, gridLineStroke, axisStroke,
					tickMarkStroke, tickFontSize, labelFontSize, legendFontSize, shapeScale, backgroundColor,
					xAxisLabelVisible, yAxisLabelVisible, chartTitleInsideChartArea);
			
			chartUtil.generateCharts(mapNames, reports, width, height, algs, agentSets, relative, excluded, grayscale, upperBound, "Intervalo M�ximo");
			
		}

		// IQM

		else if (rankingIQM) {
			algs.clear();
			algs.put("EZr(RFlag,g)", "EZr(RFlag,g)");
			algs.put("EZr(RFlags,n)", "EZr(RFlag,n)");
			//algs.put("ECount", "ECount");
			//algs.put("CR", "CR");
			algs.put("EZr1(Thrun,n)", "EZr(Thrun,n)");
			algs.put("LRTA*", "LRTA*");
			//algs.put("EZr1(Thrun,g)", "EZr1(Thrun,g)");
			//algs.put("EZr1(LRTA*,g)", "EZr1(LRTA*,g)");
			//algs.put("EZr1(LRTA*,n)", "EZr1(LRTA*,n)");
			
			//upperBounds = getHighestValueBoundsAcrossMaps(reports, algs);
			//upperBounds = getMeanValueBoundsAcrossMaps(reports, algs);
			//upperBound = upperBounds[1]/100;
			upperBound = 1.51;
			
			ChartUtil chartUtil = new ChartUtil(width, height, "# Agents", "Y", seriesLineStroke, gridLineStroke, axisStroke,
					tickMarkStroke, tickFontSize, labelFontSize, legendFontSize, shapeScale, backgroundColor,
					xAxisLabelVisible, yAxisLabelVisible, chartTitleInsideChartArea);
			
			chartUtil.generateCharts(mapNames, reports, width, height, algs, agentSets, relative, excluded, grayscale, upperBound, "Intervalo Quadr�tico M�dio");
		}

		// Frequ�ncia
		
		else if (rankingSDF) {
			algs.clear();
			algs.put("NCount", "NCount");
			algs.put("EZr(NCount,g)", "EZr(NCount,g)");
			algs.put("Zr(NCount,g)", "Zr(NCount,g)");
			algs.put("Zr(Thrun,g)", "Zr(Thrun,g)");
			//algs.put("EZr2(Thrun,n)", "EZr2(Thrun,n)");
			//algs.put("Zr(Thrun,n)", "Zr(Thrun,n)");
			// algs.put("Thrun", "Thrun");
			
			//upperBounds = getHighestValueBoundsAcrossMaps(reports, algs);
			//upperBounds = getMeanValueBoundsAcrossMaps(reports, algs);
			//upperBound = upperBounds[2]/100;
			upperBound = 610;
			
			ChartUtil chartUtil = new ChartUtil(width, height, "# Agents", "Y", seriesLineStroke, gridLineStroke, axisStroke,
					tickMarkStroke, tickFontSize, labelFontSize, legendFontSize, shapeScale, backgroundColor,
					xAxisLabelVisible, yAxisLabelVisible, chartTitleInsideChartArea);
			
			chartUtil.generateCharts(mapNames, reports, width, height, algs, agentSets, relative, excluded, grayscale, upperBound, "Desvio Padr�o das Frequ�ncias");
		}
		
		else if (rankingGeral) {
			algs.clear();
			algs.put("NCount", "NCount");
			algs.put("LRTA*", "LRTA*");
			algs.put("Wagner", "Wagner");
			algs.put("Thrun", "Thrun");
			algs.put("RFlag", "RFlag");
			algs.put("CR", "CR");
			algs.put("ECount", "ECount");
			
			upperBounds = getHighestValueBoundsAcrossMaps(reports, algs);
			
			//exMapA.generateCharts(width, height, algs, agentSets, relative, excluded, grayscale, upperBounds[0]);
			//exCircles.generateCharts(width, height, algs, agentSets, relative, excluded, grayscale, upperBounds[0]);
			//exGrid.generateCharts(width, height, algs, agentSets, relative, excluded, grayscale, upperBounds[0]);
			//exMapIsland.generateCharts(width, height, algs, agentSets, relative, excluded, grayscale, upperBounds[0]);
		}

		

	}
	
	public static double[] getHighestValueBoundsAcrossMaps(ArrayList<ExperimentReport> reports, LinkedHashMap<String, String> algs){
		
		double[] worst = new double[reports.get(0).getAlgorithmReports().get(0).getMetrics().size()];
		
		for(int i =0; i < worst.length; i++){
			worst[i] = 0;
		}
		
		for(ExperimentReport report : reports){
			for(AlgorithmReport algReport : report.getAlgorithmReports()){
				if(algs.containsKey(algReport.getAlgorithmName())){
					for(int i = 0; i < algReport.getMetrics().size(); i++){
						MetricReport metricReport = algReport.getMetrics().get(i);
						double highestMean = metricReport.getHighestMean();

						if(worst[i] < highestMean){
							
							worst[i] = highestMean;
						}
					}
				}
			}
		}
		
		/*for (int a = 0; a < agentSets.length; a++) {
			double min = Double.MAX_VALUE;
			for (AlgorithmReport algReport : algorithmReports) {

				if (!excludeFromMin.contains(algReport.getAlgorithmName()) 
						&& algReport.getMetrics().get(i).getMean(a) < min) {
					min = algReport.getMetrics().get(i).getMean(a);
				}
			}
			best.add(min);
		}*/
		
		return worst;
	}
	
	public static double[] getMeanValueBoundsAcrossMaps(ArrayList<ExperimentReport> reports, LinkedHashMap<String, String> algs){
		int metricsSize = reports.get(0).getAlgorithmReports().get(0).getMetrics().size();
		
		double[] worst = new double[metricsSize];
		double[] sum = new double[metricsSize];
		double[] mean = new double[metricsSize];
		
		for(int i =0; i < worst.length; i++){
			worst[i] = 0;
			sum[i] = 0;
			mean[i] = 0;
		}
		
		for(ExperimentReport report : reports){
			for(AlgorithmReport algReport : report.getAlgorithmReports()){
				if(algs.containsKey(algReport.getAlgorithmName())){
					for(int i = 0; i < metricsSize; i++){
						MetricReport metricReport = algReport.getMetrics().get(i);
						double highestMean = metricReport.getHighestMean();

						if(worst[i] < highestMean){
							
							worst[i] = highestMean;
						}
					}
				}
			}
			for(int i = 0; i < metricsSize; i++){
				sum[i] += worst[i];
			}
		}
		
		for(int i = 0; i < metricsSize; i++){
			mean[i] = sum[i]/4;
			//System.out.println(mean[i]);
		}
		
		/*for (int a = 0; a < agentSets.length; a++) {
			double min = Double.MAX_VALUE;
			for (AlgorithmReport algReport : algorithmReports) {

				if (!excludeFromMin.contains(algReport.getAlgorithmName()) 
						&& algReport.getMetrics().get(i).getMean(a) < min) {
					min = algReport.getMetrics().get(i).getMean(a);
				}
			}
			best.add(min);
		}*/
		
		return mean;
	}

	public static void grid() {
		Graph graph = SimpleGraphGenerators.generateGridGraph(5, 5);
		System.out.println(graph.toString());
	}

	public static void experiment2() throws IOException {
		Graph graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		// Graph graph = GraphUtil.generateGridGraph(15, 16);
		// String graphName = "Grid 15x16";
		String graphName = "BDFS";
		String experimentName = graphName;

		int mapsNumber = 10;

		ArrayList<int[]> initialPositions = new ArrayList<int[]>();
		for (int i = 0; i < mapsNumber; i++) {
			int[] array = new int[(i + 1) * 2];
			for (int j = 0; j < array.length; j++) {
				array[j] = RandomUtil.chooseInteger(0, graph.getNumNodes() - 1);
			}
			initialPositions.add(array);
		}

		BatchExperiment experiment = new BatchExperiment();
		experiment.setExperimentName(experimentName);
		//experiment.addAlgorithm(new BalloonDFSAlgorithm(0.01));
		Map map = new Map(graphName, graph, initialPositions);
		experiment.setMap(map);
		experiment.setTime(500000);
		experiment.setInitialTime(50000);

		long begin = System.currentTimeMillis();
		experiment.start();
		long total = System.currentTimeMillis() - begin;

		System.out.println("Total = " + total);
		System.out.println("Simulation = " + experiment.getExperimentTime());
		experiment.toCsv("./" + experiment.getExperimentName() + ".csv");
	}

	public static void experiment() throws IOException {
		Graph graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		// Graph graph = GraphUtil.generateGridGraph(15, 16);
		// String graphName = "Grid 15x16";
		String graphName = "Office";
		String experimentName = graphName;

		int mapsNumber = 10;

		ArrayList<int[]> initialPositions = new ArrayList<int[]>();
		for (int i = 0; i < mapsNumber; i++) {
			int[] array = new int[(i + 1) * 2];
			for (int j = 0; j < array.length; j++) {
				array[j] = RandomUtil.chooseInteger(0, graph.getNumNodes() - 1);
			}
			initialPositions.add(array);
		}

		BatchExperiment experiment = new BatchExperiment();
		experiment.setExperimentName(experimentName);
		experiment.addAlgorithm(new NodeCountingAlgorithm("Node Counting"));
		//experiment.addAlgorithm(new EdgeCountingAlgorithm("Edge Counting"));
		experiment.addAlgorithm(new LRTAAlgorithm("LRTA Default"));
		experiment.addAlgorithm(new LRTAAlgorithm("LRTA Thrun's Rule", LRTAAlgorithm.THRUN_RULE));
		experiment.addAlgorithm(new LRTAAlgorithm("LRTA Wagner's Rule", LRTAAlgorithm.WAGNER_RULE));
		experiment.addAlgorithm(new ReactiveWithFlagsAlgorithm("Reactive With Flags"));
		//experiment.addAlgorithm(new BalloonDFSAlgorithm(0.01));
		// experiment.addAlgorithm(new PVAW3Algorithm(0.1, false, false,
		// PVAW3Algorithm.PVAW3));
		Map map = new Map(graphName, graph, initialPositions);
		experiment.setMap(map);
		experiment.setTime(50000);
		experiment.setInitialTime(15000);

		long begin = System.currentTimeMillis();
		experiment.start();
		long total = System.currentTimeMillis() - begin;

		System.out.println("Total = " + total);
		System.out.println("Simulation = " + experiment.getExperimentTime());
		experiment.toCsv("./" + experiment.getExperimentName() + ".csv");
	}

	public static void main2(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// Graph graph = GraphFileUtil.read("maps\\map_a.xml",
		// GraphFileFormat.SIMPATROL);
		int time = 50;
		Graph graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		int[] agentsNodes = new int[] { 5, 10, 20, 25 }; // 4 agents

		// System.out.println(graph);

		Simulator simulator = new Simulator();
		LRTAAlgorithm algorithm = new LRTAAlgorithm("Node Counting");
		// ReactiveWithFlagsAlgorithm algorithm = new
		// ReactiveWithFlagsAlgorithm("Reactive With Flags");

		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsNodes);
		simulator.setTotalTime(time);
		simulator.setAlgorithm(algorithm);

		simulator.run();

		showMetrics(simulator.getVisitsList(), graph.getNumNodes(), time);

	}

	private static void showMetrics(VisitsList visits, int nodes, int time) {
		System.out.println();
		System.out.println(visits);

		IntervalMetricsReport intervalReport = new IntervalMetricsReport(nodes, 1, time, visits);
		// IdlenessMetricsReport idlenessReport = new
		// IdlenessMetricsReport(nodes, 1, time, visits);

		System.out.println("Metricas:");
		System.out.printf(" - desvio padrao dos intervalos: %f \n", intervalReport.getStdDevOfIntervals());
		System.out.printf(" - intervalo quadratico medio: %.3f \n", intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.3f \n", intervalReport.getMaximumInterval());
		// System.out.printf(" - idleness max: %.3f \n",
		// idlenessReport.getMaxIdleness());
	}

}
