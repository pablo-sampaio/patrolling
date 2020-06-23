package yaps.strategies.evolutionary.experiments;

import java.io.IOException;
import java.util.logging.Level;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.operator.selection.Selection;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.metrics.Metric;
import yaps.strategies.evolutionary.algorithms.ElitistEvolutionStrategy;
import yaps.strategies.evolutionary.operator.CrossOver;
import yaps.strategies.evolutionary.operator.HalfAddHalfSubSmallChangesMutation;
import yaps.strategies.evolutionary.operator.MATPMutation;
import yaps.strategies.evolutionary.problem.SingleObjectiveMATP;
import yaps.strategies.evolutionary.utils.Centering;
import yaps.strategies.evolutionary.utils.GraphEquipartition;
import yaps.strategies.evolutionary.utils.PathBuilder;

public class MaximumEvaluationsExperiment extends MATPExperiment {
	
	private int start, end, increment;

	public MaximumEvaluationsExperiment(String experimentName, int start,
			int end, int increment) {
		super(experimentName);
		this.start = start;
		this.end = end;
		this.increment = increment;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getIncrement() {
		return increment;
	}

	@Override
	public void execute() {
		int numberOfAgents = 5;
		int numberOfTurnsOnEveryFitnessMeasure = 3000;
		int mu = 30;
		int lamda = 180;
		int centering = Centering.RANDOM_CENTERING;
		int graphEquipartition = GraphEquipartition.NAIVE_RANDOM_EQUIPARTITION;
		int pathBuilder = PathBuilder.RANDOM_PATH_BUILDER;
		Metric metric = Metric.QUADR_MEAN_OF_INTERVALS; // SingleObjectiveMATP.MAXIMUM_INTERVAL;

		String map = MATPInstance.maps[3];

		Graph graph;
		try {
			graph = GraphFileUtil.readSimpatrolFormat("maps/" + map + ".xml");
			for (int maximumNumberOfEvaluations = this.start; 
					maximumNumberOfEvaluations <= this.end; 
					maximumNumberOfEvaluations += this.increment) {
				Problem problem = new SingleObjectiveMATP(graph, numberOfAgents,
						centering, graphEquipartition, pathBuilder, metric,
						numberOfTurnsOnEveryFitnessMeasure);
				ElitistEvolutionStrategy.Builder builder = new ElitistEvolutionStrategy.Builder(
						problem);
				MATPMutation mutation = new HalfAddHalfSubSmallChangesMutation();
				CrossOver crossOver = null;
				Selection selection = null;
				Integer popSize = null;
				builder.setMutation(mutation);
				builder.setMu(mu);
				builder.setLambda(lamda);
				builder.setMaxEvaluations(maximumNumberOfEvaluations);
				Algorithm algorithm = builder.build();

				MATPInstance mi = new MATPInstance(map, numberOfAgents,
						numberOfTurnsOnEveryFitnessMeasure,
						maximumNumberOfEvaluations, centering, graphEquipartition,
						pathBuilder, metric, algorithm, mu,
						lamda, mutation, crossOver, selection, popSize);
				mi.execute();
				writeToDatabase(mi);
			}
		} catch (IOException e) {
			EvoMATPLogger.get().log(Level.SEVERE, e.getMessage());
		}
	}

	public static void main(String[] args) {
		MaximumEvaluationsExperiment mee = new MaximumEvaluationsExperiment(
				"MaximumEvaluationExperiment", 125000, 150000, 1000);
		mee.execute();
	}

}
