package algorithms.realtime.weighted_extensions.experiments;

import java.io.IOException;

import algorithms.realtime.weighted_extensions.LRTAStarFull;
import algorithms.realtime.weighted_extensions.Normalization;
import algorithms.realtime.weighted_extensions.WNodeCountingMult;
import algorithms.realtime.weighted_extensions.WNodeCountingSum;
import algorithms.realtime.weighted_extensions.WVawMult;
import algorithms.realtime.weighted_extensions.WVawSum;
import algorithms.realtime_search.RealtimeSearchTeam;
import algorithms.realtime_search.basic.LRTAStar;
import algorithms.realtime_search.basic.LRTAStarUnitaryEdge;
import algorithms.realtime_search.basic.NodeCounting;
import algorithms.realtime_search.basic.Vaw;
import yaps.experimentation.ExperimentPerformer;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;


/**
 * Experiments comparing random choices and deterministic choices. 
 * 
 * @author Pablo Sampaio
 */
public class ExperMainArticleLARS2019 {

	public static void main(String[] args) throws IOException {
		ExperimentPerformer experiment = new ExperimentPerformer("Exper-final-LARS2019", 150000, 50001); //experimento principal
		//ExperimentPerformer experiment = new ExperimentPerformer("Exper-final-alternativo-ate-50000-LARS2019", 50000, 1); //experimento secundario
		
		// Original Strategies Node Counting, VAW, LRTAStar for unit edges
		experiment.addAlgorithm(new RealtimeSearchTeam(new NodeCounting(true)));
		experiment.addAlgorithm(new RealtimeSearchTeam(new Vaw(true)));
		experiment.addAlgorithm(new RealtimeSearchTeam(new LRTAStarUnitaryEdge(true)));
		
		addSelectedNewStrategies(experiment);
		//addTopNewStrategies(experiment);
		
		Graph graph1 = GraphFileUtil.read("maps\\map_islands.xml", GraphFileFormat.SIMPATROL);
		Graph graph2 = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL);
		Graph graph3 = GraphFileUtil.read("maps\\complete50.xml", GraphFileFormat.SIMPATROL);
		int[] numbersOfAgents = {1, 4, 7, 10, 13, 16};
		
		experiment.addMap("Islands", graph1, numbersOfAgents, 5);
		experiment.addMap("MapA", graph2, numbersOfAgents, 5);
		experiment.addMap("Complete50", graph3, numbersOfAgents, 5);
		
		experiment.setRepetitions(1);  //preferi aumentar a quantidade de posicoes iniciais por numero de agentes		
		experiment.runAllSimulations(true, false, "results");
		
		System.out.printf("Total simulation time: %ds", experiment.getRunningTimeInSecs());
	}
	
	private static void addSelectedNewStrategies(ExperimentPerformer experiment) {
		// General version of LRTA* -- not random
		experiment.addAlgorithm(new RealtimeSearchTeam(new LRTAStarFull(false)));
		
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
						new RealtimeSearchTeam(new WNodeCountingSum(false, normalizationEdge, normalizationCount)));
			}
		}
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WNodeCountingMult(false, normalizationEdge, normalizationCount)));
			}
		}
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WVawSum(false, normalizationEdge, normalizationCount)));
			}
		}
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WVawMult(false, normalizationEdge, normalizationCount)));
			}
		}
				
	}
	
	private static void addTopNewStrategies(ExperimentPerformer experiment) {
		//TODO!!
	}

}
