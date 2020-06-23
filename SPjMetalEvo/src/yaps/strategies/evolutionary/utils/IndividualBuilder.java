package yaps.strategies.evolutionary.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.strategies.evolutionary.variable.Agent;

public class IndividualBuilder {

	private Graph graph;
	private int numberOfAgents;

	private int centeringType;
	private int graphEquipartitionType;
	private int pathBuilderType;

	public IndividualBuilder(Graph graph, int numOfAgents, int centeringType,
			int graphEquipartitionType, int pathBuilderType) {
		this.graph = graph;
		this.numberOfAgents = numOfAgents;
		this.centeringType = centeringType;
		this.graphEquipartitionType = graphEquipartitionType;
		this.pathBuilderType = pathBuilderType;
	}
	
	private Centering setUpCentering() {
		Centering centering;
		switch(this.centeringType) {
		case Centering.RANDOM_CENTERING:
			centering = new RandomCentering(this.graph, this.numberOfAgents);
			break;
		case Centering.MAXIMUM_DISTANCE_CENTERING:
		default:
			centering = new MaximumDistanceCentering(this.graph, this.numberOfAgents);
		}
		return centering;
	}
	

	private GraphEquipartition setUpEquipartition(List<Integer> centers) {
		GraphEquipartition graphEquipartition;
		switch(this.graphEquipartitionType) {
		case GraphEquipartition.NAIVE_RANDOM_EQUIPARTITION:
			graphEquipartition = new NaiveRandomEquipartition(this.graph, centers);
			break;
		case GraphEquipartition.FUNGAL_COLONY_EQUIPARTITION:
		default:
			graphEquipartition = new FungalColonyEquipartition(this.graph, centers, true);	
		}
		return graphEquipartition;
	}
	
	private PathBuilder setUpPathBuilder(HashSet<Integer> nodeSubset) {
		PathBuilder pathBuilder;
		switch(this.pathBuilderType) {
		case PathBuilder.NEAREST_INSERTION_PATH_BUILDER:
			pathBuilder = new NearestInsertionPathBuilder(this.graph, new ArrayList<Integer>(nodeSubset));
			break;
		case PathBuilder.NEAREST_NEIGHBOR_PATH_BUILDER:
			pathBuilder = new NearestNeighborPathBuilder(this.graph, new ArrayList<Integer>(nodeSubset));
			break;
		case PathBuilder.RANDOM_PATH_BUILDER:
		default:
			pathBuilder = new RandomPathBuilder(this.graph, new ArrayList<Integer>(nodeSubset));
			break;
		}
		return pathBuilder;
	}

	public Individual build() {
		List<Agent> agents = new ArrayList<Agent>();
		Centering centering = this.setUpCentering();
		List<Integer> centers = centering.calculateCenters();
		GraphEquipartition graphEquipartition = this.setUpEquipartition(centers);
		HashMap<Integer, HashSet<Integer>> partitions = graphEquipartition.getPartitions();
		for (Integer center : centers) {
			PathBuilder pathBuilder = this.setUpPathBuilder(partitions.get(center));
			Path p = pathBuilder.build();
			Agent agent = new Agent(center, p, pathBuilder.getInducedSubGraph());
			agents.add(agent);
		}
		Individual indv = new Individual(this.graph, agents);
		return indv;
	}

}
