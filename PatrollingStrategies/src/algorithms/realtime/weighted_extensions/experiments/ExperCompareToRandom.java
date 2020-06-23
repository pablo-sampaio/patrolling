package algorithms.realtime.weighted_extensions.experiments;

import java.io.IOException;

import algorithms.realtime.weighted_extensions.Normalization;
import algorithms.realtime.weighted_extensions.WNodeCountingMult;
import algorithms.realtime.weighted_extensions.WNodeCountingSum;
import algorithms.realtime.weighted_extensions.WVawMult;
import algorithms.realtime.weighted_extensions.WVawSum;
import algorithms.realtime_search.RealtimeSearchTeam;
import algorithms.realtime_search.basic.NodeCounting;
import algorithms.realtime_search.basic.Vaw;
import yaps.experimentation.ExperimentPerformer;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;

/**
 * Experiments comparing random choices and deterministic choices with all normalizations. 
 * 
 * Conclusions: 
 *  - Island - deterministic strategies are better on average (on QMI and SDF)
 *  - Map A - deterministic strategies are better on average (on QMI and SDF), 
 *        but deterministic and non-deterministic versions of VAW on SDF are tied
 *  - Complete - deterministic wNC strategies are better on average (on QMI and SDF), 
 *        but non-deterministic wVAW are slightly better
 * 
 * @author Pablo Sampaio
 */
public class ExperCompareToRandom {

	public static void main(String[] args) throws IOException {
		ExperimentPerformer experiment = new ExperimentPerformer("Exper-compare-to-random", 150000, 50001);
		
		// Original Strategies Node Counting, VAW
		experiment.addAlgorithm(new RealtimeSearchTeam(new NodeCounting(true)));
		experiment.addAlgorithm(new RealtimeSearchTeam(new Vaw(true)));

		addNewStrategies(experiment, true);
		addNewStrategies(experiment, false);
		
		Graph graph1 = GraphFileUtil.read("maps\\map_islands.xml", GraphFileFormat.SIMPATROL);
		Graph graph2 = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL);
		Graph graph3 = GraphFileUtil.read("maps\\complete50.xml", GraphFileFormat.SIMPATROL);
		int[] numbersOfAgents = {1, 5, 10, 15};
		
		experiment.addMap("Islands", graph1, numbersOfAgents, 3);
		experiment.addMap("MapA", graph2, numbersOfAgents, 3);
		experiment.addMap("Complete50", graph3, numbersOfAgents, 3);
		
		experiment.setRepetitions(1);		
		experiment.runAllSimulations(true, false, "results");
		
		System.out.printf("Total simulation time: %ds", experiment.getRunningTimeInSecs());
	}
	
	private static void addNewStrategies(ExperimentPerformer experiment, boolean randomChoice) {
		
		// Variation of all normalizations in each class
		for (Normalization normalizationEdge : Normalization.values()) {
			for (Normalization normalizationCount : Normalization.values()) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WNodeCountingSum(randomChoice, normalizationEdge, normalizationCount)));
			}
		}
		for (Normalization normalizationEdge : Normalization.values()) {
			for (Normalization normalizationCount : Normalization.values()) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WNodeCountingMult(randomChoice, normalizationEdge, normalizationCount)));
			}
		}
		for (Normalization normalizationEdge : Normalization.values()) {
			for (Normalization normalizationCount : Normalization.values()) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WVawSum(randomChoice, normalizationEdge, normalizationCount)));
			}
		}
		for (Normalization normalizationEdge : Normalization.values()) {
			for (Normalization normalizationCount : Normalization.values()) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WVawMult(randomChoice, normalizationEdge, normalizationCount)));
			}
		}
				
	}

}
