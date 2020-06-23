package yaps.strategies.evolutionary.problem;

import java.util.List;
import java.util.logging.Level;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.util.JMetalException;

import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.metrics.Metric;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.FullPathAlgorithm;
import yaps.simulator.core.FullPathSimulator;
import yaps.strategies.evolutionary.experiments.EvoMATPLogger;
import yaps.strategies.evolutionary.solutiontype.MATPSolutionType;
import yaps.strategies.evolutionary.variable.Agent;
import yaps.util.lazylists.LazyList;


public class SingleObjectiveMATP extends Problem {
	private static final long serialVersionUID = -1362759109082247101L;
	
	/*public static final int MAXIMUM_INTERVAL = 0;
	public static final int AVERAGE_INTERVAL = 1;
	public static final int STANDARD_DEVIATION_OF_INTERVALS = 2;
	public static final int QUADRATIC_MEAN_OF_INTERVALS = 3;
	public static final int GENERALIZED_MEAN_OF_INTERVALS = 4;*/

	private Graph graph;
	private int numberOfTurns;

	private long evalCounter;
	
	private Metric metric;

	public SingleObjectiveMATP(Graph graph, int numberOfAgents,
			int centeringType, int graphEquipartitionType, int pathBuilderType,
			Metric metricType, int numberOfTurns) {
		this.graph = graph;
		this.numberOfVariables = numberOfAgents;
		this.numberOfConstraints = 0;
		this.numberOfObjectives = 2;
		this.numberOfTurns = numberOfTurns;
		this.solutionType = new MATPSolutionType(this, this.graph,
				centeringType, pathBuilderType, graphEquipartitionType);
		this.metric = metricType;
		
		evalCounter = 0;
	}

	public Graph getGraph() {
		return graph;
	}

	public int getNumberOfTurns() {
		return numberOfTurns;
	}

	public void setNumberOfTurns(int numberOfTurns) {
		this.numberOfTurns = numberOfTurns;
	}

	@Override
	public void evaluate(final Solution solution) throws JMetalException {
		int[] agentInitialNodes = new int[this.numberOfVariables];
		for (int i = 0; i < this.numberOfVariables; i++) {
			Agent agent = ((Agent) solution.getDecisionVariables()[i]);
			agentInitialNodes[i] = agent.getPath().get(0);
		}
		
//		Simulator simulator = new Simulator();
//		SimpleMultiagentAlgorithm alg = new SimpleMultiagentAlgorithm("evo-alg") {
//
//			@Override
//			public void onSimulationEnd() {
//			}
//
//			@Override
//			public SimpleAgent[] createTeam(AgentPosition[] initialInfo,
//					Graph g, int time) {
//				int numAgents = initialInfo.length;
//				SimpleAgent[] agents = new SimpleAgent[numAgents];
//				for (int i = 0; i < numAgents; i++) {
//					agents[i] = (Agent) solution.getDecisionVariables()[i];
//				}
//				return agents;
//			}
//		};

		//Alternativa - apenas um pouco mais rapido
		FullPathSimulator simulator = new FullPathSimulator();
		FullPathAlgorithm alg = new FullPathAlgorithm() {
			@Override
			public void onSimulationStart(Graph graph, AgentPosition[] initialInfo,
					int totalTime) {
				//faz nada
			}			
			
			@Override
			public String getName() {
				return "evo-alg-alternative";
			}
			
			@Override
			public List<Integer> getAgentTrajectory(int agent) {
				Path p = ((Agent)solution.getDecisionVariables()[agent]).getPath();
				return LazyList.toLazyList(p)
						.sublist(0, p.size()-2)          //remove o ultimo no (igual ao primeiro)
						.repeatUntilSize(numberOfTurns); //cria uma "lista" com repeticoes ciclicas da at� preenhcer o dado tamanho 
				                                         //(que poderia ser menor, mas nao faz diferenca para uma LazyList)
			}
		};
		
		simulator.setGraph(this.graph);
		simulator.setAgentsInitialNodes(agentInitialNodes);
		simulator.setTotalTime(this.numberOfTurns);
		simulator.setAlgorithm(alg);
		simulator.run();

		double objective = this.metric.calculate(simulator.getVisitsList(), this.graph.getNumNodes(), 1, this.numberOfTurns);

//		IntervalMetricsReport intervalMetricsReport = new IntervalMetricsReport(
//				this.graph.getNumNodes(), 1, this.numberOfTurns,
//				simulator.getVisitsList());
//		switch(this.metricType) {
//		case QUADRATIC_MEAN_OF_INTERVALS:
//			objective = intervalMetricsReport.getQuadraticMeanOfIntervals();
//			break;
//		case STANDARD_DEVIATION_OF_INTERVALS:
//			objective = intervalMetricsReport.getStdDevOfIntervals();
//			break;
//		case AVERAGE_INTERVAL:
//			objective = intervalMetricsReport.getAverageInterval();
//			break;
//		case MAXIMUM_INTERVAL:
//		default:
//			objective = intervalMetricsReport.getMaximumInterval();
//		}
		solution.setObjective(0, objective);
		this.evalCounter++;
		solution.setObjective(1, this.evalCounter);
		EvoMATPLogger.get().log(Level.INFO, "Evaluation number " + this.evalCounter + ": " + objective);
	}

}
