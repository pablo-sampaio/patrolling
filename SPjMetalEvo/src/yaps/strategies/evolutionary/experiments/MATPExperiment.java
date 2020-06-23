package yaps.strategies.evolutionary.experiments;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;

public abstract class MATPExperiment {

	public static final String SQLDDLFile = "data/db.sql";

	private Connection DBConnection;
	private String creationSQL;
	private String dbFile;
	
	private String experimentName;

	public MATPExperiment(String experimentName) {
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
	}
	
	public String getName() {
		return this.experimentName;
	}

	public abstract void execute();

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
					"POPSIZE, MUTATION, REBUILD_TYPE, CROSSOVER, SELECTION, CENTERING, " + 
					"PARTITION, PATHBUILDER, GRAPH_NAME, NUM_AGENTS, " + 
					"NUM_TURNS_ON_MEASURE, MAX_NUM_EVALUATIONS, METRIC, " + 
					"ELAPSED_TIME, BEST_METRIC_VALUE, EVAL_NUMBER_OF_BEST, BEST_STRING)" + 
					"VALUES (" + now() + 
					", \'" + instance.getAlgorithm() + "\'" +
					", " + instance.getMu() + 
					", " + instance.getLambda() + 
					", " + instance.getPopulationSize() + 
					", \'" + instance.getMutation() + "\'" +  
					", \'" + instance.getRebuild() + "\'" +  
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

}
