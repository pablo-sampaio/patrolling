package yaps.experimentation;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import yaps.graph.Graph;
import yaps.metrics.CoverageRelatedMetrics;
import yaps.metrics.FrequencyMetricsReport;
import yaps.metrics.IdlenessMetricsReport;
import yaps.metrics.IntervalMetricsReport;
import yaps.simulator.core.Algorithm;
import yaps.simulator.core.Simulator;
import yaps.util.DoubleList;


/**
 * This class runs multiple simulations, with predefined settings, and outputs the results.
 * You can configure multiple algorithms, multiple maps with varied number of agents and initial positions 
 * and the simulations are run with all possible configurations. The results of three metrics are output: 
 * quadratic mean of the intervals, maximum interval (=worst idleness) and standard deviation of the frequencies.
 * <br><br>
 * The basic use consists in: creating an instance of this class, then adding the algorithms (that you want to 
 * test or compare), then adding the maps together with configurations of initial positions, then calling 
 * {@link #runAllSimulations(boolean, boolean)}.     
 *  
 * @author Rodrigo de Sousa
 * @author Pablo Sampaio
 *
 */
public class ExperimentPerformer {
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	private String experimentName;
	private List<Algorithm> algorithms;
	private List<MapSettings> maps;
	
	private int defaultRepetitions;
	private Map<Algorithm, Integer> repetitionsExceptions;

	private int simulationTimeInTurns;
	private int startTurnForEvaluation;
	
	private long runningTimeInSecs;

	public ExperimentPerformer(String experimentName, int simulationTime, int evalInitialTime, int repetitions) {
		if (simulationTime <= 1 || evalInitialTime < 0 || evalInitialTime >= simulationTime) {
			throw new Error("Invalid parameters");
		}
		this.experimentName = experimentName;
		this.algorithms = new LinkedList<>();
		this.maps = new LinkedList<>();
		this.simulationTimeInTurns = simulationTime;
		this.startTurnForEvaluation = evalInitialTime;
		this.defaultRepetitions = repetitions;
		this.repetitionsExceptions = new HashMap<>();
	}

	public ExperimentPerformer(String experimentName, int simulationTime, int evalInitialTime) {
		this(experimentName, simulationTime, evalInitialTime, 1);
	}
	
	public ExperimentPerformer(int simulationTime, int evalInitialTime) {
		this("Experiment-" + dateFormat.format(new Date()), simulationTime, evalInitialTime, 1);
	}
	
	public void runAllSimulations() {
		runAllSimulations(true, true);
	}
	
	/**
	 * Run all simulations, with all algorithms in all maps with all predefined settings.
	 * Compiles and saves the results in the output directory. Results that may be output
	 * (depending on the appropriate parameters) are: one ".csv" (with tables of results
	 * for all algorithms with all numbers of agents) and three/four graphs (one per metric,
	 * showing the metric x number of agents), all of these PER MAP.
	 */
	public void runAllSimulations(boolean saveCsvFiles, boolean saveCharts) {
		runAllSimulations(saveCsvFiles, saveCharts, "");
	}
	
	/**
	 * Run all simulations, with all algorithms in all maps with all predefined settings.
	 * Compiles and saves the results in the output directory. Results that may be output
	 * (depending on the appropriate parameters) are: one ".csv" (with tables of results
	 * for all algorithms with all numbers of agents) and three/four graphs (one per metric,
	 * showing the metric x number of agents), all of these PER MAP.  
	 */
	public void runAllSimulations(boolean saveCsvFiles, boolean saveCharts, String outputDirectory) {
		long startTime = System.currentTimeMillis();
		ExperimentReport expReport;
		
		outputDirectory = outputDirectory.trim();
		if (! outputDirectory.endsWith(File.separator) && ! outputDirectory.equals("")) {
			outputDirectory = outputDirectory + File.separator;
		}
		File directory = new File(outputDirectory);
		if (! directory.exists()) {
			directory.mkdirs();
		}
		
		for (MapSettings map : this.maps) {
			System.out.printf("%n ==============");
			System.out.printf("%n SIMULATIONS ON %s", map.getName().toUpperCase());
			System.out.printf("%n ============== %n");
			
			expReport = runSimulationsOnMap(map);
			
			if (saveCsvFiles) {
				try {
					expReport.saveCsvFile(outputDirectory + experimentName + "-" + map.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (saveCharts) {
				try {
					expReport.saveChartFiles(outputDirectory + experimentName + "-" + map.getName(), "Agents", "Values");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		this.runningTimeInSecs = (System.currentTimeMillis() - startTime) / 1000;
	}

	public ExperimentReport runSimulationsOnMap(MapSettings mapSettings) {
		long startTime = System.currentTimeMillis();
		ExperimentReport report = runOnMapInternal(mapSettings);
		this.runningTimeInSecs = (System.currentTimeMillis() - startTime) / 1000;
		return report;
	}
	
	private ExperimentReport runOnMapInternal(MapSettings mapSettings) {
		int sims = 0;
		Graph graph = mapSettings.getGraph();
		int numNodes = graph.getNumNodes();

		Simulator simulator = new Simulator();
		ExperimentReport experimentReport = new ExperimentReport(experimentName, mapSettings);

		for (Algorithm algorithm : algorithms) {
			AlgorithmReport algorithmReport = new AlgorithmReport(algorithm.getName());
			
			MetricReport maxIntervalReport = new MetricReport("Max Interval");  // each of these metrics report is one line in the results' table
			MetricReport quadraticMeanIntervalReport = new MetricReport("QMI");
			MetricReport frequencyStandardDeviationReport = new MetricReport("SDF");
			//MetricReport coverTimeReport = new MetricReport("Cover Time");
			MetricReport averageIdlenessReport = new MetricReport("Oavg (average idleness)");

			algorithmReport.addMetricReport(maxIntervalReport);
			algorithmReport.addMetricReport(quadraticMeanIntervalReport);
			algorithmReport.addMetricReport(frequencyStandardDeviationReport);
			//algorithmReport.addMetricReport(coverTimeReport);
			//algorithmReport.addMetricReport(averageIdlenessReport);

			experimentReport.addAlgorithmReport(algorithmReport);
			
			int repetitions = this.defaultRepetitions;
			if (this.repetitionsExceptions.containsKey(algorithm)) {
				repetitions = this.repetitionsExceptions.get(algorithm);
			}
			
			for (int agents : mapSettings.getNumbersOfAgents()) {
				
				DoubleList maxIntervalValues = new DoubleList();       //each of these lists holds the metrics for repeated simulations with the same configuration
				DoubleList quadrMeanIntervalValues = new DoubleList(); //each will generate one cell for "mean" and one cell for "standard deviation" of the metric value
				DoubleList frequencyStdDevValues = new DoubleList();
				//DoubleList coverageTimeValues = new DoubleList();
				//DoubleList averageIdlenessValues = new DoubleList();

				maxIntervalReport.addValues(maxIntervalValues);
				quadraticMeanIntervalReport.addValues(quadrMeanIntervalValues);
				frequencyStandardDeviationReport.addValues(frequencyStdDevValues);
				//coverTimeReport.addValues(coverageTimeValues);
				//averageIdlenessReport.addValues(averageIdlenessValues);

				for (int[] initialPositions : mapSettings.getInitialPositions(agents)) {

					for (int i = 0; i < repetitions; i++) {
						sims++;
						System.out.printf("SIMULATION %d on %s - %s - positions %s - exec #%d %n", sims, mapSettings.getName(), algorithm.getName(), Arrays.toString(initialPositions), (i+1));

						simulator.setGraph(graph);
						simulator.setAgentsInitialNodes(initialPositions);
						simulator.setTotalTime(simulationTimeInTurns);
						simulator.setAlgorithm(algorithm);

						simulator.run();
						
						IntervalMetricsReport intervalReport = new IntervalMetricsReport(numNodes, startTurnForEvaluation, simulationTimeInTurns, simulator.getVisitsList());
						FrequencyMetricsReport frequencyReport = new FrequencyMetricsReport(numNodes, startTurnForEvaluation, simulationTimeInTurns, simulator.getVisitsList());
						//CoverageRelatedMetrics coverageReport = new CoverageRelatedMetrics(numNodes, agents, startTurnForEvaluation, simulationTimeInTurns, simulator.getVisitsList());
						//IdlenessMetricsReport idlenessReport = new IdlenessMetricsReport(numNodes, startTurnForEvaluation, simulationTimeInTurns, simulator.getVisitsList());

						double imax = intervalReport.getMaximumInterval();
						double qmi = intervalReport.getQuadraticMeanOfIntervals();
						double sdf = frequencyReport.getStdDevOfFrequencies();
						//int et = coverageReport.getExplorationTimeUntilStop();
						//int ct = coverageReport.getCoverageTime();
						//double avgIdl = idlenessReport.getAverageIdleness();
						
						maxIntervalValues.add(imax);
						quadrMeanIntervalValues.add(qmi);
						frequencyStdDevValues.add(sdf);
						//coverageTimeValues.add(ct); //or .add(et);
						//averageIdlenessValues.add(avgIdl);
						
						System.out.println(" - Max Inteval: " + imax);
						System.out.println(" - QMI: " + qmi);
						System.out.println(" - SDF: " + sdf);
						//System.out.println(" - Cov.Time: " + ct);
						//System.out.println(" - Expl.Time: " + et);
						//System.out.println(" - Avg Idl: " + avgIdl);
					}
				}
				
			}
			
		}

		return experimentReport;
	}

	/**
	 * Add a patrolling algorithm that will be simulated varying all the settings of the experiment 
	 * (maps, repetitions, etc.) 
	 */
	public void addAlgorithm(Algorithm algorithm) {
		this.algorithms.add(algorithm);
	}

	public List<Algorithm> getAlgorithms() {
		return algorithms;
	}

	public List<MapSettings> getMaps() {
		return this.maps;
	}

	/**
	 * Add new settings of graph and initial positions of agents. All algorithms will be simulated with
	 * the given settings. 
	 */
	public void addMap(MapSettings mapSettings) {
		this.maps.add(mapSettings);
	}
	
	/**
	 * Add new settings of graph and initial positions of agents. All algorithms will be simulated with
	 * the given settings. See {@link MapSettings#MapSettings(String, Graph, List)}.
	 */
	public void addMap(String mapName, Graph graph, List<int[]> initialPositions) {
		this.maps.add(new MapSettings(mapName, graph, initialPositions));
	}
	
	/**
	 * Add new settings of graph and initial positions of agents. All algorithms will be simulated with
	 * the given settings. See {@link MapSettings#MapSettings(String, Graph, int[], int)}.
	 */
	public void addMap(String mapName, Graph graph, int[] numbersOfAgents, int positionsToGenerate, boolean generateDifferentPositions) {
		this.maps.add(new MapSettings(mapName, graph, numbersOfAgents, positionsToGenerate, generateDifferentPositions));
	}
	public void addMap(String mapName, Graph graph, int[] numbersOfAgents, int positionsToGenerate) {
		this.maps.add(new MapSettings(mapName, graph, numbersOfAgents, positionsToGenerate, true));
	}

	/**
	 * Default number of times each simulation will be repeated with the same settings (algorithm, graph and positions).
	 * Some algorithms may have a different number of repetitions, set by {@link #setRepetitionsException(Algorithm, int)}. 
	 */
	public int getRepetitions() {
		return defaultRepetitions;
	}

	/**
	 * To set the number of times a simulation will be run with exactly the same configuration
	 * (map, algorithm and positions). This is useful for algorithms that make random decisions.
	 * See {@link ExperimentPerformer#setRepetitionsException(Algorithm, int)}
	 */
	public void setRepetitions(int executions) {
		this.defaultRepetitions = executions;
	}
	
	/**
	 * To set a different number of repetitions of simulations for a specific algorithm.
	 * See {@link ExperimentPerformer#setRepetitions(int)}.
	 */
	public void setRepetitionsException(Algorithm algorithm, int repetition) {
		this.repetitionsExceptions.put(algorithm, repetition);
	}

	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	/**
	 * Total time in turns that will be used in all simulations.  
	 */
	public int getTotalSimulationTime() {
		return simulationTimeInTurns;
	}

	public void setTotalSimulationTime(int turns) {
		if (turns <= 1 || startTurnForEvaluation >= turns) {
			throw new Error("Invalid parameters");
		}
		this.simulationTimeInTurns = turns;
	}

	/**
	 * Specific turn of simulation from where the metrics will be applied to evaluate the algorithm.
	 * The inclusive period of evaluation is <b>[</b>getEvaluationInitialTime()<b>;</b> getTotalSimulationTime()<b>]</b>.
	 * All turns before this one will be discarded for evaluation purposes. (They are useful only as
	 * stabilization period for the agents).   
	 */
	public int getEvaluationInitialTime() {
		return startTurnForEvaluation;
	}

	public void setEvaluationInitialTime(int initialTurn) {
		if (initialTurn < 1 || initialTurn >= simulationTimeInTurns) {
			throw new Error("Invalid parameters");
		}
		this.startTurnForEvaluation = initialTurn;
	}
	
	public long getRunningTimeInSecs() {
		return this.runningTimeInSecs;
	}
	
}

