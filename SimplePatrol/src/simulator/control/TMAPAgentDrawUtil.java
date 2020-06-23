package simulator.control;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

/**
 * Represents all agents of the graph in a global context.
 * 
 * @author Alison Carrera
 *
 */
public class TMAPAgentDrawUtil {

	private Graph graph;
	private int[] agentsInitialPath;
	private LinkedList<AgentUtil> agents;
	private int velocity = 40;

	/**
	 * Constructor of the agents on gui.
	 * @param graph Graph that the agents will be drawn.
	 * @param agentsInitialPath Agents initials nodes.
	 * @param velocity Agents velocity.
	 */
	public TMAPAgentDrawUtil(Graph graph, int[] agentsInitialPath, int velocity) {
		this.graph = graph;
		this.agentsInitialPath = agentsInitialPath;
		this.agents = new LinkedList<AgentUtil>();
		this.velocity = velocity;

		this.drawInitialPositionAgents();

	}
	
	public LinkedList<AgentUtil> getAgents()
	{
		return agents;
	}
	
	/**
	 * Draw the agents in yours initial position.
	 * Agent's color are chosen randomly.
	 */
	private void drawInitialPositionAgents() {
		for (int i = 0; i < agentsInitialPath.length; i++) {

			SpriteManager sman = new SpriteManager(graph);

			int r = ThreadLocalRandom.current().nextInt(0, 255);
			int g = ThreadLocalRandom.current().nextInt(0, 255);
			int b = ThreadLocalRandom.current().nextInt(0, 255);

			StringBuilder build = new StringBuilder();
			build.append("rgb(").append(r).append(",").append(g).append(",")
					.append(b).append(");");

			Sprite s = sman.addSprite("A" + (i + 1));
			s.addAttribute("ui.style", "fill-color: " + build.toString());
			s.addAttribute("ui.style", "shape: box;");
			s.addAttribute("ui.label", "Agent " + (i + 1));
			s.addAttribute("ui.style", "text-alignment: above;");
			
			s.attachToNode(String.valueOf(agentsInitialPath[i]));

			List<String> edges = new LinkedList<String>();

			for (Edge edge : graph.getEachEdge()) {

				edges.add(edge.getId());

			}
			
			AgentUtil u = new AgentUtil(s, edges, velocity);			
			agents.add(u);

		}
	}

	/**
	 * This method moves an agent on gui.
	 * @param agent Agent id that will be moved.
	 * @param position Actual position of the agent.
	 * @param nextNode Next position of the agent.
	 */
	public void moveAgent(int agent, int position, int nextNode) {

		String orderedEdges = String.valueOf(position) + String.valueOf(nextNode);

		agents.get(agent).updateNextNode(orderedEdges);

	}

}
