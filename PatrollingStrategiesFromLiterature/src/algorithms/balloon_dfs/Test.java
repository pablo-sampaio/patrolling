package algorithms.balloon_dfs;

import java.io.IOException;

import algorithms.rodrigo.koenig.Img2Graph;
import yaps.graph.Graph;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.simulator.core.Simulator;


/**
 * @author Rodrigo de Sousa
 */
public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		balloonTest();

	}
	
	public static void balloonTest() throws IOException {
		// TODO Auto-generated method stub
		/*Graph graph = GraphFileUtil.read("maps\\map_a.xml",
				GraphFileFormat.SIMPATROL);*/
		Graph graph = Img2Graph.img2Graph("./maps/office.bmp", 20, 20);
		int time = 50000;
		// Graph graph = GraphUtil.generateGridGraph(7, 7);
		int[] agentsNodes = new int[] { 5, 10, 20, 25 }; // 4 agents

		Simulator simulator = new Simulator();
		BalloonDFSAlgorithm algorithm = new BalloonDFSAlgorithm(0.01);

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

		IntervalMetricsReport intervalReport = new IntervalMetricsReport(nodes,
				1, time, visits);
		// IdlenessMetricsReport idlenessReport = new
		// IdlenessMetricsReport(nodes, 1, time, visits);

		System.out.println("Metricas:");
		System.out.printf(" - desvio padrao dos intervalos: %f \n",
				intervalReport.getStdDevOfIntervals());
		System.out.printf(" - intervalo quadratico medio: %.3f \n",
				intervalReport.getQuadraticMeanOfIntervals());
		System.out.printf(" - intervalo maximo: %.3f \n",
				intervalReport.getMaximumInterval());
		// System.out.printf(" - idleness max: %.3f \n",
		// idlenessReport.getMaxIdleness());
	}

}
