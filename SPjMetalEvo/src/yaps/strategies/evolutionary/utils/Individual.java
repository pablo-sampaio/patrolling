package yaps.strategies.evolutionary.utils;

import java.util.Arrays;
import java.util.List;

import yaps.graph.Graph;
import yaps.strategies.evolutionary.variable.Agent;

public class Individual {
	private List<Agent> agents;
	private Graph graph;
	
	public Individual(Graph graph, Agent ... agents) {
		this(graph, Arrays.asList(agents));
	}
	
	public Individual(Graph graph, List<Agent> agents) {
		this.graph = graph;
		this.agents = agents;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public Graph getGraph() {
		return graph;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n\t\"number_agents\" : " + this.agents.size());
		sb.append("\n\t\"agents\" : [\n");
		for (int i = 0;;) {
			Agent agent = this.agents.get(i);
			if (++i < this.agents.size()) {
				sb.append("\t\t" + agent.toString() + ",\n");
				continue;
			} else {
				sb.append("\t\t" + agent.toString() + "\n");
				break;
			}
		}
		sb.append("\t]\n}");
		return sb.toString();
	}
	
}
