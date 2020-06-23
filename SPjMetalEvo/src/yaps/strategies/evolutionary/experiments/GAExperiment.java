package yaps.strategies.evolutionary.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import yaps.strategies.evolutionary.operator.HalfAddHalfSubRebuildImprove;
import yaps.strategies.evolutionary.operator.HalfAddHalfSubSmallChangesImprove;
import yaps.strategies.evolutionary.operator.MATPMutation;
import yaps.strategies.evolutionary.operator.SimpleRandomCrossOver;
import yaps.strategies.evolutionary.problem.SingleObjectiveMATP;
import yaps.strategies.evolutionary.selection.Tournament;
import yaps.strategies.evolutionary.utils.Centering;
import yaps.strategies.evolutionary.utils.GraphEquipartition;
import yaps.strategies.evolutionary.utils.PathBuilder;
import yaps.util.RandomUtil;

public class GAExperiment extends MATPExperiment {

	private List<MATPInstance> listOfTasks;

	public GAExperiment(String experimentName, int numberOfAgents, String map) throws IOException {
		super(experimentName);
		listOfTasks = new ArrayList<MATPInstance>();
		MATPMutation[] mutations = {
				(new HalfAddHalfSubRebuildImprove(5, PathBuilder.NEAREST_INSERTION_PATH_BUILDER)),
				(new HalfAddHalfSubRebuildImprove(5, PathBuilder.NEAREST_NEIGHBOR_PATH_BUILDER)),
				(new HalfAddHalfSubSmallChangesImprove(5))};
		Metric metric = Metric.QUADR_MEAN_OF_INTERVALS;
		int[] centerings = {Centering.MAXIMUM_DISTANCE_CENTERING, Centering.RANDOM_CENTERING};
		int[] partitions = {GraphEquipartition.FUNGAL_COLONY_EQUIPARTITION, GraphEquipartition.NAIVE_RANDOM_EQUIPARTITION};
		int[] pathBuilders = {PathBuilder.NEAREST_INSERTION_PATH_BUILDER, PathBuilder.NEAREST_NEIGHBOR_PATH_BUILDER, PathBuilder.RANDOM_PATH_BUILDER};
		//String[] maps = {"map_cicles_corridor", "map_grid", "map_islands"};
		int mu = 18;
		int lambda = 90;
		int popsize = 96;
		int popsize2 = 110;
		int numberOfTurnsOnEveryFitnessMeasure = 2000;
		int maximumNumberOfEvaluations = 30000;
		for (MATPMutation mutation : mutations) {
			for (int centering : centerings) {
				for (int graphEquipartition : partitions) {
					for (int pathBuilder : pathBuilders) {
						Graph graph = GraphFileUtil.readSimpatrolFormat("maps/" + map + ".xml");
						
						Problem problem = new SingleObjectiveMATP(graph,
								numberOfAgents, centering, graphEquipartition,
								pathBuilder, metric, numberOfTurnsOnEveryFitnessMeasure);
						CrossOver crossOver = new SimpleRandomCrossOver();
						GenerationalGeneticAlgorithm.Builder builder = new GenerationalGeneticAlgorithm.Builder(problem);
						builder.setMutation(mutation);
						builder.setCrossover(crossOver);
						builder.setEvaluator(new SequentialSolutionSetEvaluator());
						builder.setPopulationSize(popsize);
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
								lambda, mutation, crossOver, selection, popsize);
						listOfTasks.add(mi);

						
						Problem problem2 = new SingleObjectiveMATP(graph,
								numberOfAgents, centering, graphEquipartition,
								pathBuilder, metric, numberOfTurnsOnEveryFitnessMeasure);
						CrossOver crossOver2 = new SimpleRandomCrossOver();
						SteadyStateGeneticAlgorithm.Builder builder2 = new SteadyStateGeneticAlgorithm.Builder(problem2);
						builder2.setMutation(mutation);
						builder2.setCrossover(crossOver2);
						builder2.setPopulationSize(lambda);
						Tournament.Builder btBuilder2 = new Tournament.Builder();
						btBuilder2.setComparator(new ObjectiveComparator(0));
						btBuilder2.setNumberOfCandidates(5);
						Selection selection2 = btBuilder2.build();
						builder2.setSelection(selection2);
						builder2.setMaxEvaluations(maximumNumberOfEvaluations);
						Algorithm algorithm2 = builder2.build();

						MATPInstance mi2 = new MATPInstance(map, numberOfAgents,
								numberOfTurnsOnEveryFitnessMeasure,
								maximumNumberOfEvaluations, centering,
								graphEquipartition, pathBuilder, metric, algorithm2, mu,
								lambda, mutation, crossOver2, selection2, popsize2);
						listOfTasks.add(mi2);
					}
				}
			}
		}
	}

	@Override
	public void execute() {
		for (MATPInstance task : listOfTasks) {
			RandomUtil.resetSeed();
			task.execute();
			writeToDatabase(task);
		}
	}

	public static void main(String[] args) {
		GAExperiment gae;
		//args = new String[3]; args[0] = "GAExperiment"; args[1] = "1"; args[2] = "map_islands";
		try {
			gae = new GAExperiment(args[0], Integer.parseInt(args[1]), args[2]);
			gae.execute();
			//System.out.println(gae.listOfTasks.size());
		} catch (Exception e) {
			e.printStackTrace();
			String stacktrace = "";
			for (StackTraceElement ste : e.getStackTrace()) {
				stacktrace += ste.toString() + "\n";
			}
			EvoMATPLogger.get().log(Level.SEVERE, stacktrace);
		}
	}

}
