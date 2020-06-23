package algorithms.zero_lookahead.experiments;

import java.io.IOException;

import algorithms.realtime_search.basic.LRTAStarUnitaryEdge;
import algorithms.realtime_search.basic.NodeCounting;
import algorithms.realtime_search.basic.Vaw;
import algorithms.zero_lookahead.ezr1_agents.EZr1Team;
import algorithms.zero_lookahead.ezr1x_agents.EZr1xTeam;
import algorithms.zero_lookahead.ezr2_agents.EZr2Team;
import algorithms.zero_lookahead.zr_agents.ZrTeam;
import yaps.experimentation.ExperimentPerformer;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;

/**
 * Experiments comparing EZr1, EZr1x and EZr2 compared to Zr and to EZr1 (to assess whether the other
 * methods are better or not). 
 * 
 * Conclusions: 
 *  - EZr1, EZr1x and EZr2 present clear improvement over Zr on all metrics.
 *  - Zr1x and Zr2 (with an advantage to Zr1x) present slight improvement over EZr1 in some settings, 
 *    but in general, it does not seem to be statistically significant.
 * 
 * @author Pablo Sampaio
 */
public class ExperCompareEZrVariants {

	public static void main(String[] args) throws IOException {
		ExperimentPerformer experiment = new ExperimentPerformer("Exper-zero-variants", 150000, 50001);
		
		addStrategies(experiment, true);
		
		Graph graph1 = GraphFileUtil.read("maps\\map_islands.xml", GraphFileFormat.SIMPATROL);
		graph1 = graph1.toUnitaryWeights();
		Graph graph2 = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL);
		graph2 = graph2.toUnitaryWeights();
		Graph graph3 = GraphFileUtil.read("maps\\complete50.xml", GraphFileFormat.SIMPATROL);
		graph3 = graph3.toUnitaryWeights();
		
		int[] numbersOfAgents = {1, 5, 10, 15};
		
		experiment.addMap("Islands", graph1, numbersOfAgents, 1);
		experiment.addMap("MapA", graph2, numbersOfAgents, 1);
		experiment.addMap("Complete50", graph3, numbersOfAgents, 2);
		
		experiment.setRepetitions(3);		
		experiment.runAllSimulations(true, false, "results");
		
		System.out.printf("Total simulation time: %ds", experiment.getRunningTimeInSecs());
	}
	
	private static void addStrategies(ExperimentPerformer experiment, boolean randomChoice) {
		boolean onlyNeighbor;
		
		for (int i = 0; i < 2; i ++) {
			onlyNeighbor = (i==0);
			experiment.addAlgorithm(new ZrTeam(new NodeCounting(randomChoice), onlyNeighbor));
			experiment.addAlgorithm(new ZrTeam(new Vaw(randomChoice), onlyNeighbor));
			experiment.addAlgorithm(new ZrTeam(new LRTAStarUnitaryEdge(randomChoice), onlyNeighbor));
		}
		
		for (int i = 0; i < 2; i ++) {
			onlyNeighbor = (i==0);
			experiment.addAlgorithm(new EZr1Team(new NodeCounting(randomChoice), onlyNeighbor));
			experiment.addAlgorithm(new EZr1Team(new Vaw(randomChoice), onlyNeighbor));
			experiment.addAlgorithm(new EZr1Team(new LRTAStarUnitaryEdge(randomChoice), onlyNeighbor));
		}
		
		for (int i = 0; i < 2; i ++) {
			onlyNeighbor = (i==0);
			experiment.addAlgorithm(new EZr1xTeam(new NodeCounting(randomChoice), onlyNeighbor));
			experiment.addAlgorithm(new EZr1xTeam(new Vaw(randomChoice), onlyNeighbor));
			experiment.addAlgorithm(new EZr1xTeam(new LRTAStarUnitaryEdge(randomChoice), onlyNeighbor));
		}
		
		for (int i = 0; i < 2; i ++) {
			onlyNeighbor = (i==0);
			experiment.addAlgorithm(new EZr2Team(new NodeCounting(randomChoice), onlyNeighbor));
			experiment.addAlgorithm(new EZr2Team(new Vaw(randomChoice), onlyNeighbor));
			experiment.addAlgorithm(new EZr2Team(new LRTAStarUnitaryEdge(randomChoice), onlyNeighbor));
		}
		
	}

}
