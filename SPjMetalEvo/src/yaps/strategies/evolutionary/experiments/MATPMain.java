package yaps.strategies.evolutionary.experiments;

import java.io.IOException;
import java.util.logging.Level;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.singleobjective.evolutionstrategy.ElitistEvolutionStrategy;
import org.uma.jmetal.metaheuristic.singleobjective.evolutionstrategy.NonElitistEvolutionStrategy;
import org.uma.jmetal.metaheuristic.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.metaheuristic.singleobjective.geneticalgorithm.SteadyStateGeneticAlgorithm;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.metrics.Metric;
import yaps.strategies.evolutionary.operator.*;
import yaps.strategies.evolutionary.problem.SingleObjectiveMATP;
import yaps.strategies.evolutionary.selection.Tournament;
import yaps.strategies.evolutionary.utils.Centering;
import yaps.strategies.evolutionary.utils.GraphEquipartition;
import yaps.strategies.evolutionary.utils.PathBuilder;
import yaps.strategies.evolutionary.variable.Agent;

public class MATPMain {

	public static void main(String[] args) throws IOException {
		//Experimenting with the Elitist Evolutionary Strategy (mu+lambda).
		
		args = new String[2];
		args[0] = "map_islands"; args[1] = "5";
		
		int numberOfAgents = Integer.parseInt(args[1]);
		int numberOfTurnsOnEveryFitnessMeasure = 2000;
		int lamda = 96;
		int maximumNumberOfEvaluations = 30000;
		int centering = Centering.MAXIMUM_DISTANCE_CENTERING;
		int graphEquipartition = GraphEquipartition.FUNGAL_COLONY_EQUIPARTITION;
		int pathBuilder = PathBuilder.NEAREST_NEIGHBOR_PATH_BUILDER;
		Metric metric = Metric.QUADR_MEAN_OF_INTERVALS; //SingleObjectiveMATP.MAXIMUM_INTERVAL;

		Graph graph = GraphFileUtil.readSimpatrolFormat("maps/"+args[0]+".xml");
		
		System.out.println(graph);

		Problem problem;
		Algorithm algorithm;

		//Define problem
		problem = new SingleObjectiveMATP(graph, numberOfAgents,
				centering,
				graphEquipartition,
				pathBuilder,
				metric, numberOfTurnsOnEveryFitnessMeasure);
		//Define algorithm
		GenerationalGeneticAlgorithm.Builder builder = new GenerationalGeneticAlgorithm.Builder(problem);
		builder.setMutation(new HalfAddHalfSubSmallChangesImprove(5));
		builder.setCrossover(new SimpleRandomCrossOver());
		builder.setEvaluator(new SequentialSolutionSetEvaluator());
		builder.setPopulationSize(lamda);
		Tournament.Builder btBuilder = new Tournament.Builder();
		btBuilder.setComparator(new ObjectiveComparator(0));
		btBuilder.setNumberOfCandidates(5);
		builder.setSelection(btBuilder.build());
		builder.setMaxEvaluations(maximumNumberOfEvaluations);
		algorithm = builder.build();
		try {
			long start = System.currentTimeMillis();
			SolutionSet solutions = algorithm.execute();
			long delta = System.currentTimeMillis() - start;
			Solution solution = solutions.get(0);
			EvoMATPLogger.get().log(Level.INFO, "Evolution finished after " + delta + " miliseconds.");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < numberOfAgents; i++) {
				sb.append(((Agent) solution.getDecisionVariables()[i]).toString() );
				sb.append("\n");
			}
			EvoMATPLogger.get().log(Level.INFO, "Best individual = {\n" + sb.toString() +"}" + "with fitness " + solution.getObjective(0));
			
			Solution bestIndividual = null;
			
			for (int index = 0; index < solutions.size(); index++) {
				if (bestIndividual == null) {
					bestIndividual = solutions.get(index);
				} else if (bestIndividual.getObjective(0) > solutions.get(
						index).getObjective(0)) {
					bestIndividual = solutions.get(index);
				} else if (bestIndividual.getObjective(0) == solutions
						.get(index).getObjective(0)
						&& bestIndividual.getObjective(1) > solutions.get(
								index).getObjective(1)) {
					bestIndividual = solutions.get(index);
				}
			}
			
			problem = new SingleObjectiveMATP(graph, numberOfAgents,
					centering,
					graphEquipartition,
					pathBuilder,
					metric, 5000);
			
			problem.evaluate(bestIndividual);
			
			System.out.println("Metricas:\n - intervalo quadratico medio: " + bestIndividual.getObjective(0));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
