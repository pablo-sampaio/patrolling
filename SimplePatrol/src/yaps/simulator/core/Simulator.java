package yaps.simulator.core;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import simulator.control.TMAPGraphDrawUtil;
import simulator.control.TurnAgentUtil;
import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.metrics.VisitsList;

/**
 * This class simulates agents walking on a graph. It receives an {@link Algorithm} that sends
 * the next positions of the agents. The simulator will do the simulation turn by turn, 
 * dispatching events (on every turn, on the beginning and at the end of a simulation).
 * 
 * @author Pablo Sampaio
 */
public class Simulator implements Runnable {
	// inputs
	private Graph graph;
	private AgentPosition[] initialPositions;
	private int totalTime;

	// a real-time algorithm (i.e., that decides during execution)
	private Algorithm algorithm;

	// outputs
	private long algorithmSetupTimeMs;
	private long simulationTimeMs;
	private VisitsList visits; // salvar aqui ou em arquivo de log (oferecer escolha)

	// simulation info
	private int currentTurn;
	private AgentTeamInfo teamInfo;

	// object responsible for the GUI
	private TMAPGraphDrawUtil gui;

	// agents' turn information.
	private ArrayList<TurnAgentUtil> agUtil = new ArrayList<TurnAgentUtil>();

	// time that a agent will stay on a node.
	private int tempoNoMili = 700;

	public Simulator() {

	}

	public int getTempoNoMili() {
		return tempoNoMili;
	}

	public void setTempoNoMili(int tempoNoMili) {
		this.tempoNoMili = tempoNoMili;
	}

	public void setGUI(TMAPGraphDrawUtil gui) {
		this.gui = gui;
	}

	public void setAlgorithm(Algorithm alg) {
		this.algorithm = alg;
	}

	public void setAlgorithm(FullPathAlgorithm alg) {
		this.algorithm = new FullPathAlgorithmConverter(alg);
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

	public Algorithm getAlgorithm() {
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

	// TODO: faltam outros gets, faltam os comentarios dos set ?

	/**
	 * This method must be called before starting simulation to set the nodes
	 * where the agents will start. <br>
	 * <br>
	 * Attention: The index of each value in "agentsNodes" is used to identify
	 * the agent in the whole simulation.
	 */
	public void setAgentsInitialNodes(List<Integer> agentsNodes) {
		this.initialPositions = new AgentPosition[agentsNodes.size()];

		for (int i = 0; i < initialPositions.length; i++) {
			// all agents start in nodes (not in edges)
			this.initialPositions[i] = new AgentPosition(agentsNodes.get(i));

		}

	}

	/**
	 * This method must be called before starting simulation to set the nodes
	 * where the agents will start. <br>
	 * <br>
	 * Attention: The index of each value in "agentsNodes" is used to identify
	 * the agent in the whole simulation.
	 */
	public void setAgentsInitialNodes(int[] agentsNodes) {
		this.initialPositions = new AgentPosition[agentsNodes.length];

		for (int i = 0; i < initialPositions.length; i++) {
			// all agents start in nodes (not in edges)
			this.initialPositions[i] = new AgentPosition(agentsNodes[i]);
		}

	}

	@Override
	public void run() {
		// TODO: checar initial positions, totalTime, etc

		int numAgents = initialPositions.length;

		AgentPosition[] initialPosCopy = new AgentPosition[this.initialPositions.length];

		int[] initial = new int[this.initialPositions.length];

		visits = new VisitsList(); //TODO: criar um objeto que encapsula isso, junto com numero de agentes, de nós, etc
		for (int ag = 0; ag < numAgents; ag++) {
			initialPosCopy[ag] = this.initialPositions[ag].clone();
			initial[ag] = this.initialPositions[ag].origin;
			visits.addVisit(0, initial[ag], ag);
		}

		/*
		 * If GUI object is set, this step prepare the whole paint configuration in gui.
		 */
		if (gui != null) {
			gui.prepareAgents(initial);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// System.out.print("Starting simulation. Setting up algorithm... ");
		long startTimeMs = System.currentTimeMillis();

		// 1. CALCULOS INICIAIS
		algorithm.onSimulationStart(graph, initialPosCopy, totalTime);

		algorithmSetupTimeMs = System.currentTimeMillis() - startTimeMs;
		// System.out.printf("finished in %d ms.\n", algorithmSetupTimeMs);

		teamInfo = new AgentTeamInfo(initialPosCopy);

		currentTurn = 0;
		startTimeMs = System.currentTimeMillis();

		// 2. LOOP PRINCIPAL
		while (currentTurn < this.totalTime) {

			// 2.1. INVOCA O ALGORITMO
			// => Se fosse tempo continuo, aqui seria o instante *currTime*
			// => Como o tempo e discreta, para o algoritmo, ele sera o turno *currTime+1*
			// => A ideia e que o simulador esta pedindo as acoes que serao realizadas no intervalo [currTime; currTime+1)

			// System.out.printf("DECIDING NEXT NODES TO GO:\n");
			algorithm.onTurn(currentTurn + 1, teamInfo);
			
			/*for (int id = 0; id < numAgents; id++) {
				if (teamInfo.nextActions[id] >= 0) {
					System.out.printf(" > Agent %d: goto %d\n", id, teamInfo.nextActions[id]);
				} else {
					System.out.printf(" > Agent %d: stop\n", id);
				}
			}*/

			// 2.2. MOVIMENTA OS AGENTES
			// => Efetua as a��es indicadas pelo algoritmo para cada agente
			// => Se fosse cont�nuo, corresponderia ao desenrolar do per�odo
			// [currTime;currTime+1)
			currentTurn++;

			// System.out.printf("TURN %d (results): \n", currentTurn);

			agUtil.clear();

			for (int id = 0; id < numAgents; id++) {

				moveAgentToNext(id);

			}

			if (!agUtil.isEmpty()) {
				//Print current turn on gui.
				gui.setTextTurn("TURN " + currentTurn + " ----------> " + teamInfo);

				//Move agents on gui.
				for (int i = 0; i < agUtil.size(); i++) {
					gui.getAgentUtil().moveAgent(agUtil.get(i).getAgentId(), agUtil.get(i).getCurrent(), agUtil.get(i).getNext());
				}

				//Loop to wait agents move on gui.
				while (gui.getAgentUtil().getAgents().get(agUtil.get(agUtil.size() - 1).getAgentId()).isAgentMoving()) {
					// Wait agent movement.
				}

				try {
					//Wait time agent in node.
					Thread.sleep(tempoNoMili);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		this.simulationTimeMs = System.currentTimeMillis() - startTimeMs;

		// 3. NOTIFICA FIM
		algorithm.onSimulationEnd();

		// System.out.println("End of simulation!");
		// System.out.println("Time elapsed (ms): " + simulationTimeMs);
	}

	/**
	 * Change the object "agent" to simulate the movement of the patrolling
	 * agent towards "next".
	 */
	private void moveAgentToNext(int agentId) {
		AgentPosition agent = teamInfo.agents[agentId];
		int next = teamInfo.nextActions[agentId];

		boolean checkArrival = false;
		int current = agent.origin;
		Edge edge = null;

		//System.out.printf(" > Agent %d:\n", agentId);
		
		//If gui is set, this step creates an object with agents movement information
		if (gui != null && !SwingUtilities.isEventDispatchThread()) {

			TurnAgentUtil ag = new TurnAgentUtil();
			ag.setAgentId(agentId);
			ag.setCurrent(current);
			ag.setNext(next);

			agUtil.add(ag);
		}

		if (agent.inNode()) {
			if (next >= 0 && next != current) {
				if (graph.existsEdge(current, next)) {
					// System.out.printf("   Starting to traverse edge %s->%s (d=1)\n", current, next);
					agent.setEdgePosition(current, next, 1); // dist=1, assumindo o resultado ap�s 1 ciclo

					edge = graph.getEdge(current, next);
					checkArrival = true;

				} else {
					throw new Error("Invalid edge: " + current + "->" + next
							+ "(agent " + agentId + ")");
				}

			} else if (next == -1) {
				// System.out.printf("   Stopped in node %s\n", current);
				visits.addVisit(currentTurn, current, agentId); // agente revisita o mesmo no

				checkArrival = false;

			} else {
				throw new Error("Unexpected action: " + next);

			}

		} else { // agent in edge

			if (next == agent.destination) {
				edge = graph.getEdge(current, next);
				checkArrival = true;

				// System.out.printf("   Traversing edge %s->%s (d=%d)\n",
				// current, next, agent.distance+1);
				agent.distance++;

			} else if (next == agent.origin) { // changing direction

				edge = graph.getEdge(agent.destination, agent.origin);
				if (edge != null && !edge.isDirected()) {
					checkArrival = true;

					agent.origin = agent.destination;
					agent.destination = next;
					agent.distance = edge.getLength() - agent.distance + 1;
					// System.out.printf("   Changed direction, and is now going %s->%s (d=%d)\n", agent.origin, next, agent.distance);
				} else {
					throw new Error("Agents can only change direction in undirected edges");

				}

			} else if (next == -1) {
				// agente parado na mesma posi��o
				// System.out.printf("   Stopped in edge %s->%s (d=%d)\n", current, next, agent.distance+1);
				checkArrival = false;

			} else {
				throw new Error("Invalid next node: " + next);

			}
		}

		if (checkArrival) {
			if (agent.distance == edge.getLength()) {
				// System.out.printf("   Arrived in node %s and visited it!\n", agent.destination);
				agent.setNodePosition(agent.destination);
				visits.addVisit(currentTurn, agent.destination, agentId);
				teamInfo.nextActions[agentId] = -1; // if no other action is set by the algorithm, the
													// agent will stop until next turn

			}
		}

	}

}

class FullPathAlgorithmConverter implements Algorithm {
	private FullPathAlgorithm fullPathAlgorithm;
	private List<Integer>[] agentPath;
	private int[] agentPathIndex;

	FullPathAlgorithmConverter(FullPathAlgorithm alg) {
		this.fullPathAlgorithm = alg;
	}

	@Override
	public String getName() {
		return fullPathAlgorithm.getName();
	}

	@Override
	public void onSimulationStart(Graph graph, AgentPosition[] initialInfo, int totalTime) {
		int numAgents = initialInfo.length;

		this.fullPathAlgorithm.onSimulationStart(graph, initialInfo, totalTime);

		this.agentPath = new List[numAgents];
		this.agentPathIndex = new int[numAgents]; // starts all 0's (ok)

		for (int ag = 0; ag < numAgents; ag++) {
			this.agentPath[ag] = this.fullPathAlgorithm.getAgentTrajectory(ag);
			if (this.agentPath[ag].get(0) != initialInfo[ag].getCurrentNode()) {
				throw new Error("Path of agent " + ag + " starts with wrong start node: " + this.agentPath[ag].get(0));
			}
		}
	}

	@Override
	public void onTurn(int nextTurn, AgentTeamInfo team) {
		int nextNode;
		int numAgents = team.getTeamSize();
		AgentPosition agentPosition;

		for (int id = 0; id < numAgents; id++) {
			agentPosition = team.getPosition(id);

			if (agentPosition.inNode()) {
				this.agentPathIndex[id]++;
				int agIndex = this.agentPathIndex[id];

				if (agIndex < this.agentPath[id].size()) {
					nextNode = this.agentPath[id].get(agIndex);
				} else {
					nextNode = agentPosition.getCurrentNode(); // stand still (it is not necessary to set
															   // the "next node" in this case, indeed)
				}

				// System.out.printf("Agent %d going to node %d\n", id, nextNode);
				team.actGoto(id, nextNode);
			}
		}
	}

	@Override
	public void onSimulationEnd() {
		// nop
	}

}
