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
 * Experiments with alternative normalizations (not used in the main experiments). 
 * 
 * Some of the new normalizations improved performance (comparing strategies with similar parameters,
 * e.g. using Pmin instead of Pmin+), however this did not occur in the best strategies. So, the
 * relative improvement is not important.
 * 
 * Dmin0 was compared to Dmin in a separate set of experiments where good strategies using
 * Dmin were tested against their counterpart with Dmin0. Although, in general, strategies with 
 * Dmin and Dmin0 performed very similar, in some cases, Dmin0 improved. Especially with 
 * multiplication operator on Map A and Islands, measured by QMI and IMAX.
 * 
 * @author Pablo Sampaio
 */
public class ExperAlternativeNormalizations {

	public static void main(String[] args) throws IOException {
		ExperimentPerformer experiment = new ExperimentPerformer("Exper-new-normalizations-dmin", 150000, 50001);
		
		// Original Strategies Node Counting, VAW, LRTAStar for unit edges
		experiment.addAlgorithm(new RealtimeSearchTeam(new NodeCounting(true)));
		experiment.addAlgorithm(new RealtimeSearchTeam(new Vaw(true)));

		//addSelectedNewStrategies(experiment);
		addTopDminStrategies(experiment);
		
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
		Normalization[] selectedNormalizations = new Normalization[] {
				Normalization.NONE, 
				Normalization.PROPORTIONAL_1_2, 
				Normalization.PROPORTIONAL_TO_MIN_X,
				Normalization.DIFFERENCE_TO_MIN
		};
		
		// Variation of all normalizations in each class
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				if (normalizationEdge == Normalization.PROPORTIONAL_1_2 || normalizationEdge == Normalization.PROPORTIONAL_TO_MIN_X 
						|| normalizationCount == Normalization.PROPORTIONAL_1_2 || normalizationCount == Normalization.PROPORTIONAL_TO_MIN_X) {
					experiment.addAlgorithm(
							new RealtimeSearchTeam(new WNodeCountingSum(false, normalizationEdge, normalizationCount)));
				}
			}
		}
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				if (normalizationEdge == Normalization.PROPORTIONAL_1_2 || normalizationEdge == Normalization.PROPORTIONAL_TO_MIN_X 
						|| normalizationCount == Normalization.PROPORTIONAL_1_2 || normalizationCount == Normalization.PROPORTIONAL_TO_MIN_X) {
					experiment.addAlgorithm(
							new RealtimeSearchTeam(new WNodeCountingMult(false, normalizationEdge, normalizationCount)));
				}
			}
		}
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				if (normalizationEdge == Normalization.PROPORTIONAL_1_2 || normalizationEdge == Normalization.PROPORTIONAL_TO_MIN_X 
						|| normalizationCount == Normalization.PROPORTIONAL_1_2 || normalizationCount == Normalization.PROPORTIONAL_TO_MIN_X) {
					experiment.addAlgorithm(
							new RealtimeSearchTeam(new WVawSum(false, normalizationEdge, normalizationCount)));
				}
			}
		}
		for (Normalization normalizationEdge : selectedNormalizations) {
			for (Normalization normalizationCount : selectedNormalizations) {
				if (normalizationEdge == Normalization.PROPORTIONAL_1_2 || normalizationEdge == Normalization.PROPORTIONAL_TO_MIN_X 
						|| normalizationCount == Normalization.PROPORTIONAL_1_2 || normalizationCount == Normalization.PROPORTIONAL_TO_MIN_X) {
					experiment.addAlgorithm(
							new RealtimeSearchTeam(new WVawMult(false, normalizationEdge, normalizationCount)));
				}
			}
		}
				
	}

	private static void addTopDminStrategies(ExperimentPerformer experiment) {
		Normalization[][] normPairs = {
				{Normalization.NONE, Normalization.DIFFERENCE_TO_MIN},
				{Normalization.PROPORTIONAL_TO_MIN, Normalization.DIFFERENCE_TO_MIN},
				{Normalization.PROPORTIONAL_0_1, Normalization.DIFFERENCE_TO_MIN},
				{Normalization.DIFFERENCE_TO_MIN, Normalization.NONE},
				{Normalization.DIFFERENCE_TO_MIN, Normalization.DIFFERENCE_TO_MIN},
		};
		
		for (Normalization[] pair : normPairs) {
			experiment.addAlgorithm(new RealtimeSearchTeam(new WVawSum(false, pair[0], pair[1])));
			experiment.addAlgorithm(new RealtimeSearchTeam(new WVawSum(false, toDmin0(pair[0]), toDmin0(pair[1]))));

			experiment.addAlgorithm(new RealtimeSearchTeam(new WVawMult(false, pair[0], pair[1])));
			experiment.addAlgorithm(new RealtimeSearchTeam(new WVawMult(false, toDmin0(pair[0]), toDmin0(pair[1]))));
			
			experiment.addAlgorithm(new RealtimeSearchTeam(new WNodeCountingSum(false, pair[0], pair[1])));
			experiment.addAlgorithm(new RealtimeSearchTeam(new WNodeCountingSum(false, toDmin0(pair[0]), toDmin0(pair[1]))));

			experiment.addAlgorithm(new RealtimeSearchTeam(new WNodeCountingMult(false, pair[0], pair[1])));
			experiment.addAlgorithm(new RealtimeSearchTeam(new WNodeCountingMult(false, toDmin0(pair[0]), toDmin0(pair[1]))));
		}
	}

	private static Normalization toDmin0(Normalization normalization) {
		if (normalization == Normalization.DIFFERENCE_TO_MIN) {
			return Normalization.DIFFERENCE_TO_MIN_0;
		}
		return normalization;
	}


}
