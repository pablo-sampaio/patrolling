package yaps.strategies.evolutionary.experiments;

import java.io.IOException;
import java.util.logging.Level;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.metaheuristic.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.metaheuristic.singleobjective.geneticalgorithm.SteadyStateGeneticAlgorithm;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.metrics.Metric;
import yaps.strategies.evolutionary.operator.CrossOver;
import yaps.strategies.evolutionary.operator.HalfAddHalfSubSmallChangesMutation;
import yaps.strategies.evolutionary.operator.MATPMutation;
import yaps.strategies.evolutionary.operator.SimpleRandomCrossOver;
import yaps.strategies.evolutionary.problem.SingleObjectiveMATP;
import yaps.strategies.evolutionary.selection.Tournament;
import yaps.strategies.evolutionary.utils.Centering;
import yaps.strategies.evolutionary.utils.GraphEquipartition;
import yaps.strategies.evolutionary.utils.PathBuilder;
import yaps.util.RandomUtil;

public class PopulationSizeExperiment extends MATPExperiment {

	private int start, end, increment, lambdaFactor;

	public PopulationSizeExperiment(String experimentName, int lambdaStart,
			int lambdaEnd, int lambdaFactor, int increment) {
		super(experimentName);
		this.start = lambdaStart;
		this.end = lambdaEnd;
		this.lambdaFactor = lambdaFactor;
		this.increment = increment;
	}

	@Override
	public void execute() {
		int numberOfAgents = 5;
		int numberOfTurnsOnEveryFitnessMeasure = 3000;
		int maximumNumberOfEvaluations = 100000;
		int centering = Centering.RANDOM_CENTERING;
		int graphEquipartition = GraphEquipartition.NAIVE_RANDOM_EQUIPARTITION;
		int pathBuilder = PathBuilder.RANDOM_PATH_BUILDER;
		Metric metric = Metric.QUADR_MEAN_OF_INTERVALS; // SingleObjectiveMATP.MAXIMUM_INTERVAL;

		String map = MATPInstance.maps[3];

		Graph graph;
		try {
			graph = GraphFileUtil.readSimpatrolFormat("maps/" + map + ".xml");
			for (int lambda = this.start; lambda <= this.end; lambda += this.increment) {
				RandomUtil.resetSeed();
				int mu = lambda / this.lambdaFactor;
				Problem problem = new SingleObjectiveMATP(graph,
						numberOfAgents, centering, graphEquipartition,
						pathBuilder, metric, numberOfTurnsOnEveryFitnessMeasure);
//				NonElitistEvolutionStrategy.Builder builder = new NonElitistEvolutionStrategy.Builder(
//						problem);
//				MATPMutation mutation = new HalfAddHalfSubSmallChangesMutation();
//				CrossOver crossOver = null;
//				Selection selection = null;
//				Integer popSize = null;
//				builder.setMutation(mutation);
//				builder.setMu(mu);
//				builder.setLambda(lambda);
//				builder.setMaxEvaluations(maximumNumberOfEvaluations);
//				Algorithm algorithm = builder.build();
				
				SteadyStateGeneticAlgorithm.Builder builder = new SteadyStateGeneticAlgorithm.Builder(problem);
				MATPMutation mutation = new HalfAddHalfSubSmallChangesMutation(); 
				builder.setMutation(mutation);
				CrossOver crossOver = new SimpleRandomCrossOver();
				builder.setCrossover(crossOver);
//				builder.setEvaluator(new SequentialSolutionSetEvaluator());
				builder.setPopulationSize(lambda);
				Tournament.Builder btBuilder = new Tournament.Builder();
				btBuilder.setComparator(new ObjectiveComparator(0));
				btBuilder.setNumberOfCandidates(5);
				Selection selection = btBuilder.build();
				builder.setSelection(selection);
				builder.setMaxEvaluations(maximumNumberOfEvaluations);
				Algorithm algorithm = builder.build();

				MATPInstance mi = new MATPInstance(map, numberOfAgents,
						numberOfTurnsOnEveryFitnessMeasure,
						maximumNumberOfEvaluations, centering,
						graphEquipartition, pathBuilder, metric, algorithm, mu,
						lambda, mutation, crossOver, selection, lambda);
				mi.execute();
				writeToDatabase(mi);
			}
		} catch (IOException e) {
			EvoMATPLogger.get().log(Level.SEVERE, e.getMessage());
		}
	}

	public static void main(String[] args) {
		PopulationSizeExperiment pse = new PopulationSizeExperiment(
				"PopulationSizeExperiment4", 80, 180, 5,10);
		PopulationSizeExperiment pse2 = new PopulationSizeExperiment(
				"PopulationSizeExperiment4", 72, 180, 6, 12);
//		PopulationSizeExperiment pse3 = new PopulationSizeExperiment(
//				"PopulationSizeExperiment4", 72, 180, 4, 12);
		pse.execute();
		pse2.execute();
//		pse3.execute();
	}

}
