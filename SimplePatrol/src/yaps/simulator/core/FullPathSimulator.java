package yaps.simulator.core;

import java.util.List;

import javax.swing.SwingUtilities;

import simulator.control.TMAPGraphDrawUtil;
import yaps.graph.Graph;
import yaps.metrics.VisitsList;

/**
 * This simulator aims at being a more efficient simulator of algorithms that plans
 * the whole path of each agent at once. 
 * <br><br>
 * It does not simulate the passage of time. Instead, it predicts the visits
 * that will be done by each agent based on the given path, then merges the
 * visits of all agents.
 * 
 * @author Pablo A. Sampaio
 */
public class FullPathSimulator implements Runnable {
	//inputs
	private Graph graph;
	private AgentPosition[] initialPositions;
	private int totalTime;
	
	//an algorithm that plans the path of each agent previously
	private FullPathAlgorithm algorithm;

	//outputs
	private long algorithmSetupTimeMs;
	private long simulationTimeMs;
	private VisitsList visits; //salvar aqui ou em arquivo de log (oferecer escolha)
	
	//GUI object
	private TMAPGraphDrawUtil gui;
	
	public FullPathSimulator() {
	}
	
	public void setGUI(TMAPGraphDrawUtil gui) {
		this.gui = gui;
	}
	
	public void setAlgorithm(FullPathAlgorithm alg) {
		this.algorithm = alg;
	}
	
	public void setGraph(Graph map) {
		this.graph = map;
	}
	
	public void setTotalTime(int turns) {
		this.totalTime = turns;
	}
	
	public VisitsList getVisitsList() {
		return visits;
	}

	public FullPathAlgorithm getAlgorithm() {
		return this.algorithm;
	}

	public Graph getGraph() {
		return this.graph;
	}
	
	public long getAlgorithmSetupTimeMillis() {
		return this.algorithmSetupTimeMs;
	}
	
	public long getSimulationTimeMillis() {
		return this.simulationTimeMs;
	}

	
	/**
	 * This method must be called before starting simulation to set the nodes where the
	 * agents will start. 
	 * <br><br>
	 * Attention: The index of each value in "agentsNodes" is used to identify the agent
	 * in the whole simulation. 
	 */
	public void setAgentsInitialNodes(List<Integer> agentsNodes) {
		this.initialPositions = new AgentPosition[agentsNodes.size()];
		
		for (int i = 0; i < initialPositions.length; i ++) {
			//all agents start in nodes (not in edges)
			this.initialPositions[i] = new AgentPosition(agentsNodes.get(i));
		}
	}
	

	/**
	 * This method must be called before starting simulation to set the nodes where the
	 * agents will start. 
	 * <br><br>
	 * Attention: The index of each value in "agentsNodes" is used to identify the agent
	 * in the whole simulation. 
	 */
	public void setAgentsInitialNodes(int[] agentsNodes) {
		this.initialPositions = new AgentPosition[agentsNodes.length];
		
		for (int i = 0; i < initialPositions.length; i ++) {
			//all agents start in nodes (not in edges)
			this.initialPositions[i] = new AgentPosition(agentsNodes[i]);
		}
	}

	@Override
	public void run() {
		//TODO: checar initial positions and totalTime!
		AgentPosition[] agentsInfoAlg = new AgentPosition[this.initialPositions.length];
		
		int[] initial = new int[this.initialPositions.length];
		
		for (int ag = 0; ag < initialPositions.length; ag++) {
			agentsInfoAlg[ag] = this.initialPositions[ag].clone();
		}
		
		//If GUI is setted prepare agents to be painted on gui.
		if (gui != null) {
			gui.prepareAgents(initial);
		}
		
		this.visits = new VisitsList();
		
		//System.out.print("Starting simulation. Setting up algorithm... ");

		long startTimeMs = System.currentTimeMillis();
		algorithm.onSimulationStart(graph, agentsInfoAlg, totalTime);
		algorithmSetupTimeMs = System.currentTimeMillis() - startTimeMs;
		
		//System.out.printf("finished in %d ms.\n", algorithmSetupTimeMs);

		List<Integer> agentPath;
		int currNode, nextNode;
		int currTime;
		int pathIndex;
		VisitsList agentVisits;
		
		startTimeMs = System.currentTimeMillis();
		for (int agentId = 0; agentId < agentsInfoAlg.length; agentId++) {
			//System.out.printf("Computing visits of agent %d\n", agentId);
			
			agentPath = algorithm.getAgentTrajectory(agentId);
			agentVisits = new VisitsList();
			
			currNode = this.initialPositions[agentId].getCurrentNode();
			if (currNode != agentPath.get(0)) {
				throw new Error("Trajectory starts with invalid node: " + agentPath.get(0));
			}
			
			pathIndex = 1;
			currTime = 0;
			
			while (pathIndex < agentPath.size()) {
				nextNode = agentPath.get(pathIndex);
				pathIndex ++;
				
				currTime += graph.getEdge(currNode, nextNode).getLength();
				
				if (gui != null && !SwingUtilities.isEventDispatchThread()) 
					   // Current Thread is Main Thread.
				 {
					//Move agents on gui.
					gui.getAgentUtil().moveAgent(agentId, currNode, nextNode);

					//Wait agents movement.
					while (gui.getAgentUtil().getAgents().get(agentId)
							.isAgentMoving()) {
						//Wait agent movement.
					}
				}
				
				if (currTime <= this.totalTime) {
					currNode = nextNode;
					agentVisits.addVisit(currTime, currNode, agentId);
					//System.out.printf(" > time %d, node %s\n", currTime, currNode);
				} else {
					break;
				}
			}
			
			//the path of the agent is to short -- stand still in the current node
			while (currTime < this.totalTime) {
				currTime ++;
				agentVisits.addVisit(currTime, currNode, agentId);
				//System.out.printf(" > time %d, node %s\n", currTime, currNode);
			}
			
			//this may be too much time-consuming...
			this.visits.merge(agentVisits);
		}
		this.simulationTimeMs = System.currentTimeMillis() - startTimeMs;
		
		System.out.println("End of simulation!");
		System.out.println("Time elapsed (ms): " + simulationTimeMs);
	}

}
