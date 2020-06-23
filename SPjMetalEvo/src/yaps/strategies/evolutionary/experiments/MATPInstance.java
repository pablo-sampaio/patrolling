package yaps.strategies.evolutionary.experiments;

import java.io.IOException;
import java.util.logging.Level;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.selection.Selection;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.metrics.Metric;
import yaps.strategies.evolutionary.operator.CrossOver;
import yaps.strategies.evolutionary.operator.HalfAddHalfSubRebuildImprove;
import yaps.strategies.evolutionary.operator.HalfAddHalfSubRebuildMutation;
import yaps.strategies.evolutionary.operator.MATPMutation;
import yaps.strategies.evolutionary.problem.SingleObjectiveMATP;
import yaps.strategies.evolutionary.utils.Centering;
import yaps.strategies.evolutionary.utils.FungalColonyEquipartition;
import yaps.strategies.evolutionary.utils.GraphEquipartition;
import yaps.strategies.evolutionary.utils.MaximumDistanceCentering;
import yaps.strategies.evolutionary.utils.NaiveRandomEquipartition;
import yaps.strategies.evolutionary.utils.NearestInsertionPathBuilder;
import yaps.strategies.evolutionary.utils.NearestNeighborPathBuilder;
import yaps.strategies.evolutionary.utils.PathBuilder;
import yaps.strategies.evolutionary.utils.RandomCentering;
import yaps.strategies.evolutionary.utils.RandomPathBuilder;
import yaps.strategies.evolutionary.variable.Agent;

public class MATPInstance {

	public static final String[] maps = { "map_cicles_corridor",
			"map_city_traffic", "map_grid", "map_islands",
			"map_random_directed_1", "map_random_directed_2" };

	private int numberOfAgents;
	private int numberOfTurnsOnEveryFitnessMeasure;
	private Integer mu;
	private Integer lambda;
	private int maximumNumberOfEvaluations;
	private int centering;
	private int graphEquipartition;
	private int pathBuilder;
	private Metric metric;

	private Graph graph;
	private String graphName;
	private Problem problem;

	private Algorithm algorithm;
	private MATPMutation mutation;
	private CrossOver crossOver;
	private Selection selection;
	private Integer populationSize;

	private Solution bestIndividual;
	private long elapsedTime;

	public MATPInstance(String map, int numberOfAgents,
			int numberOfTurnsOnEveryFitnessMeasure,
			int maximumNumberOfEvaluations, int centering,
			int graphEquipartition, int pathBuilder, Metric metric,
			Algorithm algorithm, Integer mu, Integer lambda,
			MATPMutation mutation, CrossOver crossover, Selection selection,
			Integer populationSize) {
		try {
			this.graphName = map;
			this.graph = GraphFileUtil.readSimpatrolFormat("maps/" + map
					+ ".xml");
			this.numberOfAgents = numberOfAgents;
			this.numberOfTurnsOnEveryFitnessMeasure = numberOfTurnsOnEveryFitnessMeasure;
			this.maximumNumberOfEvaluations = maximumNumberOfEvaluations;
			this.centering = centering;
			this.graphEquipartition = graphEquipartition;
			this.pathBuilder = pathBuilder;
			this.metric = metric;
			this.algorithm = algorithm;
			this.mu = mu;
			this.lambda = lambda;
			this.mutation = mutation;
			this.crossOver = crossover;
			this.selection = selection;
			this.problem = new SingleObjectiveMATP(this.graph, numberOfAgents,
					centering, graphEquipartition, pathBuilder, metric,
					numberOfTurnsOnEveryFitnessMeasure);
			this.populationSize = populationSize;

			this.bestIndividual = null;
			this.elapsedTime = 0;
		} catch (IOException e) {
			EvoMATPLogger.get().log(Level.SEVERE, e.getMessage());
		}
	}

	public Solution execute() {
		long start = System.currentTimeMillis();
		SolutionSet solutions;
		try {
			solutions = algorithm.execute();
			this.elapsedTime = System.currentTimeMillis() - start;
			for (int index = 0; index < solutions.size(); index++) {
				if (this.bestIndividual == null) {
					this.bestIndividual = solutions.get(index);
				} else if (this.bestIndividual.getObjective(0) > solutions.get(
						index).getObjective(0)) {
					this.bestIndividual = solutions.get(index);
				} else if (this.bestIndividual.getObjective(0) == solutions
						.get(index).getObjective(0)
						&& this.bestIndividual.getObjective(1) > solutions.get(
								index).getObjective(1)) {
					this.bestIndividual = solutions.get(index);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			EvoMATPLogger.get().log(Level.SEVERE, e.getMessage());
		}
		EvoMATPLogger.get().log(Level.INFO,
				"Best individual = " + getBestIndividualString());
		return this.bestIndividual;
	}

	public String getGraphName() {
		return this.graphName;
	}

	public Solution getBestIndividual() {
		return bestIndividual;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public Integer getPopulationSize() {
		return populationSize;
	}

	public int getNumberOfAgents() {
		return numberOfAgents;
	}

	public int getNumberOfTurnsOnEveryFitnessMeasure() {
		return numberOfTurnsOnEveryFitnessMeasure;
	}

	public Integer getMu() {
		return mu;
	}

	public Integer getLambda() {
		return lambda;
	}

	public int getMaximumNumberOfEvaluations() {
		return maximumNumberOfEvaluations;
	}

	public String getCentering() {
		switch (this.centering) {
		case Centering.MAXIMUM_DISTANCE_CENTERING:
			return MaximumDistanceCentering.class.getSimpleName();
		case Centering.RANDOM_CENTERING:
		default:
			return RandomCentering.class.getSimpleName();
		}
	}

	public String getGraphEquipartition() {
		switch (this.graphEquipartition) {
		case GraphEquipartition.FUNGAL_COLONY_EQUIPARTITION:
			return FungalColonyEquipartition.class.getSimpleName();
		case GraphEquipartition.NAIVE_RANDOM_EQUIPARTITION:
		default:
			return NaiveRandomEquipartition.class.getSimpleName();
		}
	}

	public String getPathBuilder() {
		switch (this.pathBuilder) {
		case PathBuilder.NEAREST_INSERTION_PATH_BUILDER:
			return NearestInsertionPathBuilder.class.getSimpleName();
		case PathBuilder.NEAREST_NEIGHBOR_PATH_BUILDER:
			return NearestNeighborPathBuilder.class.getSimpleName();
		case PathBuilder.RANDOM_PATH_BUILDER:
		default:
			return RandomPathBuilder.class.getSimpleName();
		}
	}

	public String getMetric() {
		if (metric == null) {
			return "null";
		} else {
			return metric.name();
		}
	}

	public Graph getGraph() {
		return graph;
	}

	public Problem getProblem() {
		return problem;
	}

	public String getAlgorithm() {
		return algorithm.getClass().getSimpleName();
	}

	public String getMutation() {
		if (mutation == null) {
			return "null";
		} else {
			return mutation.getClass().getSimpleName();
		}
	}
	
	public String getRebuild() {
		if (this.mutation instanceof HalfAddHalfSubRebuildMutation) {
			HalfAddHalfSubRebuildMutation hahsr =
					(HalfAddHalfSubRebuildMutation) this.mutation;
			switch(hahsr.getPathRebuilderType()) {
			case PathBuilder.NEAREST_INSERTION_PATH_BUILDER:
				return "NEAREST_INSERTION_PATH_BUILDER";
			case PathBuilder.NEAREST_NEIGHBOR_PATH_BUILDER:
				return "NEAREST_NEIGHBOR_PATH_BUILDER";
			case PathBuilder.RANDOM_PATH_BUILDER:
			default:
				return "RANDOM_PATH_BUILDER";
			}
		} else if (this.mutation instanceof HalfAddHalfSubRebuildImprove) {
			HalfAddHalfSubRebuildImprove hahsri = 
					(HalfAddHalfSubRebuildImprove) this.mutation;
			switch(hahsri.getPathRebuilderType()) {
			case PathBuilder.NEAREST_INSERTION_PATH_BUILDER:
				return "NEAREST_INSERTION_PATH_BUILDER";
			case PathBuilder.NEAREST_NEIGHBOR_PATH_BUILDER:
				return "NEAREST_NEIGHBOR_PATH_BUILDER";
			case PathBuilder.RANDOM_PATH_BUILDER:
			default:
				return "RANDOM_PATH_BUILDER";
			}
		} else {
			return "null";
		}
	}

	public String getCrossOver() {
		if (crossOver == null) {
			return "null";
		} else {
			return crossOver.getClass().getSimpleName();
		}
	}

	public String getSelection() {
		if (selection == null) {
			return "null";
		} else {
			return selection.getClass().getSimpleName();
		}
	}

	public String getBestIndividualString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		for (int i = 0; i < numberOfAgents; i++) {
			sb.append(((Agent) this.bestIndividual.getDecisionVariables()[i])
					.toString());
			sb.append("\n");
		}
		sb.append("} with fitness " + this.bestIndividual.getObjective(0));
		return sb.toString();
	}
}
