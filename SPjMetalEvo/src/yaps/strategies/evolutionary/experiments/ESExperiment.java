package yaps.strategies.evolutionary.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.metaheuristic.singleobjective.evolutionstrategy.ElitistEvolutionStrategy;
import org.uma.jmetal.metaheuristic.singleobjective.evolutionstrategy.NonElitistEvolutionStrategy;
import org.uma.jmetal.operator.selection.Selection;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.metrics.Metric;
import yaps.strategies.evolutionary.operator.CrossOver;
import yaps.strategies.evolutionary.operator.HalfAddHalfSubRebuildImprove;
import yaps.strategies.evolutionary.operator.HalfAddHalfSubSmallChangesImprove;
import yaps.strategies.evolutionary.operator.MATPMutation;
import yaps.strategies.evolutionary.problem.SingleObjectiveMATP;
import yaps.strategies.evolutionary.utils.Centering;
import yaps.strategies.evolutionary.utils.GraphEquipartition;
import yaps.strategies.evolutionary.utils.PathBuilder;
import yaps.util.RandomUtil;

public class ESExperiment extends MATPExperiment {

	private List<MATPInstance> listOfTasks;

	public ESExperiment(String experimentName, int numberOfAgents) throws IOException {
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
		String[] maps = {"map_cicles_corridor", "map_grid", "map_islands"};
		int mu = 18;
		int lambda = 90;
		int numberOfTurnsOnEveryFitnessMeasure = 2000;
		int maximumNumberOfEvaluations = 30000;
		for (MATPMutation mutation : mutations) {
			for (int centering : centerings) {
				for (int graphEquipartition : partitions) {
					for (int pathBuilder : pathBuilders) {
						for (String map : maps) {
							Graph graph = GraphFileUtil.readSimpatrolFormat("maps/" + map + ".xml");
							
//							mu = 18;
//							lambda = 90;
//							
//							Problem problem = new SingleObjectiveMATP(graph,
//									numberOfAgents, centering, graphEquipartition,
//									pathBuilder, metric, numberOfTurnsOnEveryFitnessMeasure);
//							ElitistEvolutionStrategy.Builder builder = new ElitistEvolutionStrategy.Builder(
//									problem);
//							CrossOver crossOver = null;
//							Selection selection = null;
//							Integer popSize = null;
//							builder.setMutation(mutation);
//							builder.setMu(mu);
//							builder.setLambda(lambda);
//							builder.setMaxEvaluations(maximumNumberOfEvaluations);
//							Algorithm algorithm = builder.build();
//
//							MATPInstance mi = new MATPInstance(map, numberOfAgents,
//									numberOfTurnsOnEveryFitnessMeasure,
//									maximumNumberOfEvaluations, centering,
//									graphEquipartition, pathBuilder, metric, algorithm, mu,
//									lambda, mutation, crossOver, selection, popSize);
//							listOfTasks.add(mi);
							
							mu = 26;
							lambda = 156;
							
							Problem problem2 = new SingleObjectiveMATP(graph,
									numberOfAgents, centering, graphEquipartition,
									pathBuilder, metric, numberOfTurnsOnEveryFitnessMeasure);
							NonElitistEvolutionStrategy.Builder builder2 = new NonElitistEvolutionStrategy.Builder(
									problem2);
							CrossOver crossOver2 = null;
							Selection selection2 = null;
							Integer popSize2 = null;
							builder2.setMutation(mutation);
							builder2.setMu(mu);
							builder2.setLambda(lambda);
							builder2.setMaxEvaluations(maximumNumberOfEvaluations);
							Algorithm algorithm2 = builder2.build();

							MATPInstance mi2 = new MATPInstance(map, numberOfAgents,
									numberOfTurnsOnEveryFitnessMeasure,
									maximumNumberOfEvaluations, centering,
									graphEquipartition, pathBuilder, metric, algorithm2, mu,
									lambda, mutation, crossOver2, selection2, popSize2);
							listOfTasks.add(mi2);
						}
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
		ESExperiment ese;
		//args = new String[2]; args[0] = "ESExperiment"; args[1] = "1";
		try {
			ese = new ESExperiment(args[0], Integer.parseInt(args[1]));
			ese.execute();
			//System.out.println(ese.listOfTasks.size());
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
