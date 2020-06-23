package yaps.strategies.evolutionary.experiments;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.operator.selection.Selection;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.metrics.Metric;
import yaps.strategies.evolutionary.algorithms.ElitistEvolutionStrategy;
import yaps.strategies.evolutionary.operator.CrossOver;
import yaps.strategies.evolutionary.operator.HalfAddHalfSubSmallChangesImprove;
import yaps.strategies.evolutionary.operator.MATPMutation;
import yaps.strategies.evolutionary.problem.SingleObjectiveMATP;
import yaps.strategies.evolutionary.utils.Centering;
import yaps.strategies.evolutionary.utils.GraphEquipartition;
import yaps.strategies.evolutionary.utils.PathBuilder;
import yaps.util.RandomUtil;

public class ImproveTriesExperiment {
	
	private int start, end, increment, currentNumberTries;

	public static final String SQLDDLFile = "data/triesdb.sql";

	private Connection DBConnection;
	private String creationSQL;
	private String dbFile;
	
	private String experimentName;

	public ImproveTriesExperiment(String experimentName, int start, int end, int increment) {
		this.experimentName = experimentName;
		try {
			this.dbFile = "data/" + experimentName + ".db";
			Class.forName("org.sqlite.JDBC");
			this.DBConnection = DriverManager.getConnection("jdbc:sqlite:"
					+ this.dbFile);
			EvoMATPLogger.get().log(Level.INFO,
					"Opened Database Successfully in file " + this.dbFile);
		} catch (SQLException | ClassNotFoundException e) {
			EvoMATPLogger.get().log(Level.SEVERE, e.getMessage());
		}
		try {
			this.creationSQL = new Scanner(new File(SQLDDLFile)).useDelimiter(
					"\\Z").next();
		} catch (Exception e) {
			EvoMATPLogger.get().log(Level.SEVERE, e.getMessage());
		}
		this.createDatabase();
		this.start = start;
		this.end = end;
		this.increment = increment;
	}
	
	public String getName() {
		return this.experimentName;
	}

	public void createDatabase() {
		try {
			Statement stmt = this.DBConnection.createStatement();
			stmt.executeUpdate(this.creationSQL);
			stmt.close();
		} catch (Exception e) {
			EvoMATPLogger.get().log(Level.SEVERE, e.getMessage());
		}
		EvoMATPLogger.get().log(Level.INFO,
				"Tables where created successfully!");
	}
	
	public synchronized void writeToDatabase(MATPInstance instance) {
		String sql = "";
		try {
//			File file = new File("agents/Agent_" + EvoMATPLogger.now() + ".jobj");
//			FileOutputStream fout = new FileOutputStream(file);
//			ObjectOutputStream oos = new ObjectOutputStream(fout);
//			oos.writeObject(instance.getBestIndividual());
//			oos.close();
			EvoMATPLogger.get().log(Level.INFO, "Object sucessfully written to file!");
			Statement stmt = this.DBConnection.createStatement();
			sql = "INSERT INTO instance (ID, ALGORITHM_NAME, MU, LAMBDA, " +  
					"POPSIZE, MUTATION, NUMBER_IMPROVE_TRIES, CROSSOVER, SELECTION, CENTERING, " + 
					"PARTITION, PATHBUILDER, GRAPH_NAME, NUM_AGENTS, " + 
					"NUM_TURNS_ON_MEASURE, MAX_NUM_EVALUATIONS, METRIC, " + 
					"ELAPSED_TIME, BEST_METRIC_VALUE, EVAL_NUMBER_OF_BEST, BEST_STRING)" + 
					"VALUES (" + now() + 
					", \'" + instance.getAlgorithm() + "\'" +
					", " + instance.getMu() + 
					", " + instance.getLambda() + 
					", " + instance.getPopulationSize() + 
					", \'" + instance.getMutation() + "\'" + 
					", " + this.currentNumberTries + 
					", \'" + instance.getCrossOver() + "\'" + 
					", \'" + instance.getSelection() + "\'" +  
					", \'" + instance.getCentering() + "\'" +  
					", \'" + instance.getGraphEquipartition() + "\'" + 
					", \'" + instance.getPathBuilder() + "\'" + 
					", \'" + instance.getGraphName() + "\'" + 
					", " + instance.getNumberOfAgents() + 
					", " + instance.getNumberOfTurnsOnEveryFitnessMeasure() +   
					", " + instance.getMaximumNumberOfEvaluations() +   
					", \'" + instance.getMetric() + "\'" +   
					", " + instance.getElapsedTime() +   
					", " + instance.getBestIndividual().getObjective(0) + 
					", " + Math.round(instance.getBestIndividual().getObjective(1)) + 
					", \'" + instance.getBestIndividualString() + "\');";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.out.println(sql);
			e.printStackTrace();
			EvoMATPLogger.get().log(Level.SEVERE, e.getMessage() + "\n" + e.getCause());
			System.exit(1);
		}
	}
	
	private long now() {
		DateFormat dateformat = new SimpleDateFormat("ddMMyyyyHHmmss");
		Date date = new Date();
		return Long.parseLong(dateformat.format(date));
	}


	public void execute() {
		int numberOfAgents = 5;
		int mu = 18;
		int lambda = 90;
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
			for (this.currentNumberTries = this.start; this.currentNumberTries <= this.end; this.currentNumberTries += this.increment) {
				RandomUtil.resetSeed();
				Problem problem = new SingleObjectiveMATP(graph,
						numberOfAgents, centering, graphEquipartition,
						pathBuilder, metric, numberOfTurnsOnEveryFitnessMeasure);
				ElitistEvolutionStrategy.Builder builder = new ElitistEvolutionStrategy.Builder(
						problem);
				MATPMutation mutation = new HalfAddHalfSubSmallChangesImprove(this.currentNumberTries);
				CrossOver crossOver = null;
				Selection selection = null;
				Integer popSize = null;
				builder.setMutation(mutation);
				builder.setMu(mu);
				builder.setLambda(lambda);
				builder.setMaxEvaluations(maximumNumberOfEvaluations);
				Algorithm algorithm = builder.build();

				MATPInstance mi = new MATPInstance(map, numberOfAgents,
						numberOfTurnsOnEveryFitnessMeasure,
						maximumNumberOfEvaluations, centering,
						graphEquipartition, pathBuilder, metric, algorithm, mu,
						lambda, mutation, crossOver, selection, popSize);
				mi.execute();
				writeToDatabase(mi);
			}
		} catch (IOException e) {
			EvoMATPLogger.get().log(Level.SEVERE, e.getMessage());
		}
	}

	public static void main(String[] args) {
		ImproveTriesExperiment ite2 = new ImproveTriesExperiment("ImproveTriesExperiment", 0, 10, 5);
		ImproveTriesExperiment ite = new ImproveTriesExperiment("ImproveTriesExperiment", 20, 100, 10);
		ite2.execute();
		ite.execute();
	}

}
