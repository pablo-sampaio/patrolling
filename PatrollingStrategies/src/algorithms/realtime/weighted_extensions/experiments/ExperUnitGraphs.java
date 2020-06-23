package algorithms.realtime.weighted_extensions.experiments;

import java.io.IOException;

import algorithms.realtime.weighted_extensions.LRTAStarFull;
import algorithms.realtime.weighted_extensions.Normalization;
import algorithms.realtime.weighted_extensions.WNodeCountingMult;
import algorithms.realtime.weighted_extensions.WNodeCountingSum;
import algorithms.realtime.weighted_extensions.WVawMult;
import algorithms.realtime.weighted_extensions.WVawSum;
import algorithms.realtime_search.RealtimeSearchTeam;
import algorithms.realtime_search.basic.LRTAStarUnitaryEdge;
import algorithms.realtime_search.basic.NodeCounting;
import algorithms.realtime_search.basic.Vaw;
import yaps.experimentation.ExperimentPerformer;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;


/**
 * Experiments in graphs with unit edges to observe if the new weighted strategies perform similar to 
 * their original counterparts (proposed for graphs with unit edges)..
 * 
 * We observed that the results of the new weighted strategies are very similar to the original ones,
 * except in SDF where variations (for better or worse) were observed. In QMI, they were specially 
 * similar. 
 * 
 * @author Pablo Sampaio
 */
public class ExperUnitGraphs {

	public static void main(String[] args) throws IOException {
		ExperimentPerformer experiment = new ExperimentPerformer("Exper-unit-edges", 150000, 50001);
		
		// Original Strategies Node Counting, VAW, LRTAStar for unit edges
		experiment.addAlgorithm(new RealtimeSearchTeam(new NodeCounting(true)));
		experiment.addAlgorithm(new RealtimeSearchTeam(new Vaw(true)));
		experiment.addAlgorithm(new RealtimeSearchTeam(new LRTAStarUnitaryEdge(true)));
		
		addSelectedNewStrategies(experiment, true);
		
		Graph graph1 = GraphFileUtil.read("maps\\map_islands.xml", GraphFileFormat.SIMPATROL);
		Graph graph2 = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL);
		Graph graph3 = GraphFileUtil.read("maps\\complete50.xml", GraphFileFormat.SIMPATROL);
		graph1 = graph1.toUnitaryWeights();
		graph2 = graph2.toUnitaryWeights();
		graph3 = graph3.toUnitaryWeights();
		
		int[] numbersOfAgents = {1, 6, 11, 16};
		
		experiment.addMap("Islands", graph1, numbersOfAgents, 3);
		experiment.addMap("MapA", graph2, numbersOfAgents, 3);
		experiment.addMap("Complete50", graph3, numbersOfAgents, 3);
		
		experiment.setRepetitions(1);  //preferi aumentar a quantidade de posicoes iniciais por numero de agentes		
		experiment.runAllSimulations(true, false, "results");
	
		System.out.println();
		System.out.printf("Total simulation time: %ds", experiment.getRunningTimeInSecs());
	}
	
	private static void addSelectedNewStrategies(ExperimentPerformer experiment, boolean rand) {
		// General version of LRTA*
		experiment.addAlgorithm(new RealtimeSearchTeam(new LRTAStarFull(rand)));
		
		Normalization[] selectedNormalizations = new Normalization[] {
				Normalization.NONE, 
				Normalization.PROPORTIONAL_0_1, 
				Normalization.PROPORTIONAL_TO_MIN,
				Normalization.DIFFERENCE_TO_MIN 
		};
		
		// Variation of all normalizations in each class
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WNodeCountingSum(rand, normalizationEdge, normalizationCount)));
			}
		}
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WNodeCountingMult(rand, normalizationEdge, normalizationCount)));
			}
		}
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WVawSum(rand, normalizationEdge, normalizationCount)));
			}
		}
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WVawMult(rand, normalizationEdge, normalizationCount)));
			}
		}
				
	}

}
