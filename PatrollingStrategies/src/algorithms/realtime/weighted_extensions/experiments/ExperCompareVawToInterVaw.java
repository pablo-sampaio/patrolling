package algorithms.realtime.weighted_extensions.experiments;

import java.io.IOException;

import algorithms.realtime.weighted_extensions.InterVawGeneral;
import algorithms.realtime.weighted_extensions.Normalization;
import algorithms.realtime.weighted_extensions.WVawMult;
import algorithms.realtime.weighted_extensions.WVawSum;
import algorithms.realtime_search.RealtimeSearchTeam;
import algorithms.realtime_search.basic.Vaw;
import yaps.experimentation.ExperimentPerformer;
import yaps.graph.Graph;
import yaps.graph.GraphFileFormat;
import yaps.graph.GraphFileUtil;

/**
 * Experiments comparing random choices and deterministic choices with all normalizations. 
 * 
 * Conclusions with older version of InterVaw that included e+(-n), e+(1/n), e*(-n) and e*(1/n): 
 *  - Compared to similar normalizations: '+' versions show degraded performance with some few cases
 *    of improvement; some * showed good or very good improvement.
 *  - Compared to the minimum value of all wVAWs: in general, rare or few cases of improvement.
 * 
 * As a result, InterVAW was implemented in a simpler form, removing +. The performance was similar 
 * to the old one.
 * 
 * @author Pablo Sampaio
 */
public class ExperCompareVawToInterVaw {

	public static void main(String[] args) throws IOException {
		Normalization[] selectedNormalizations = new Normalization[] {
				Normalization.NONE, 
				Normalization.DIFFERENCE_TO_MIN, 
				Normalization.PROPORTIONAL_1_2, 
				Normalization.PROPORTIONAL_TO_MIN_X
		};

		ExperimentPerformer experiment = new ExperimentPerformer("Exper-compare-Vaw-Intervaw", 150000, 50001);
		
		// Original Strategy VAW
		experiment.addAlgorithm(new RealtimeSearchTeam(new Vaw(true)));

		//addVawStrategies(experiment, selectedNormalizations, false);
		addIntervawStrategies(experiment, selectedNormalizations, false);
		
		Graph graph1 = GraphFileUtil.read("maps\\map_islands.xml", GraphFileFormat.SIMPATROL);
		Graph graph2 = GraphFileUtil.read("maps\\map_a.xml", GraphFileFormat.SIMPATROL);
		Graph graph3 = GraphFileUtil.read("maps\\complete50.xml", GraphFileFormat.SIMPATROL);
		int[] numbersOfAgents = {1, 5, 10, 15};
		
		//experiment.addMap("Islands", graph1, numbersOfAgents, 3);
		experiment.addMap("MapA", graph2, numbersOfAgents, 3);
		//experiment.addMap("Complete50", graph3, numbersOfAgents, 3);
		
		experiment.setRepetitions(1);		
		experiment.runAllSimulations(true, false, "results");
		
		System.out.printf("Total simulation time: %ds", experiment.getRunningTimeInSecs());
	}

	private static void addIntervawStrategies(ExperimentPerformer experiment, Normalization[] selectedNormalizations, boolean random) {
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new InterVawGeneral(random, normalizationEdge, false, normalizationCount)));
			}
		}
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new InterVawGeneral(random, normalizationEdge, true, normalizationCount)));
			}
		}
	}

//  OLDER VERSION
//	private static void addIntervawStrategies(ExperimentPerformer experiment, Normalization[] selectedNormalizations, boolean random) {
//		for (Normalization normalizationEdge : selectedNormalizations) {
//			for (Normalization normalizationCount : selectedNormalizations) {
//				experiment.addAlgorithm(
//						new RealtimeSearchTeam(new InterVawGeneral(random, normalizationEdge, '+', normalizationCount, false)));
//			}
//		}
//		for (Normalization normalizationEdge : selectedNormalizations) {
//			for (Normalization normalizationCount : selectedNormalizations) {
//				experiment.addAlgorithm(
//						new RealtimeSearchTeam(new InterVawGeneral(random, normalizationEdge, '+', normalizationCount, true)));
//			}
//		}
//		for (Normalization normalizationEdge : selectedNormalizations) {
//			for (Normalization normalizationCount : selectedNormalizations) {
//				experiment.addAlgorithm(
//						new RealtimeSearchTeam(new InterVawGeneral(random, normalizationEdge, '*', normalizationCount, false)));
//			}
//		}
//		for (Normalization normalizationEdge : selectedNormalizations) {
//			for (Normalization normalizationCount : selectedNormalizations) {
//				experiment.addAlgorithm(
//						new RealtimeSearchTeam(new InterVawGeneral(random, normalizationEdge, '*', normalizationCount, true)));
//			}
//		}
//	}

	private static void addVawStrategies(ExperimentPerformer experiment, Normalization[] selectedNormalizations, boolean random) {
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WVawSum(random, normalizationEdge, normalizationCount)));
			}
		}	
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				experiment.addAlgorithm(
						new RealtimeSearchTeam(new WVawMult(random, normalizationEdge, normalizationCount)));
			}
		}	
	}

}
