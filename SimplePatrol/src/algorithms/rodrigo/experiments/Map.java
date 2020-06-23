package algorithms.rodrigo.experiments;

import java.util.ArrayList;
import java.util.Random;

import yaps.graph.Graph;
import yaps.util.RandomUtil;

public class Map {

	private String name;
	private Graph graph;
	private ArrayList<int[]> initialPositions;
	private boolean randomInitialPositionsForEveryIteration;
	private int[] agentSets;
	private Random random = new Random();

	public Map(String name, Graph graph, ArrayList<int[]> initialPositions) {
		this.name = name;
		this.graph = graph;
		this.initialPositions = initialPositions;
		
		this.agentSets = new int[initialPositions.size()];
		for(int i = 0; i < initialPositions.size(); i++){
			agentSets[i] = initialPositions.get(i).length;
		}
		
		this.randomInitialPositionsForEveryIteration = false;
	}

	public Map(String name, Graph graph, boolean randomPositionsForEveryIteration, int[] agentSets) {
		this.name = name;
		this.graph = graph;
		this.randomInitialPositionsForEveryIteration = randomPositionsForEveryIteration;
		this.agentSets = agentSets;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	public int[] getAgentSets() {
		return agentSets;
	}

	public void setAgentSets(int[] agentSets) {
		this.agentSets = agentSets;
	}

	public ArrayList<int[]> getInitialPositions() {
		if(randomInitialPositionsForEveryIteration){
			initialPositions = getRandomPositions();
		}
		else if(initialPositions == null){
			initialPositions = getRandomPositions();
		}
		
		return initialPositions;
	}
	
	private ArrayList<int[]> getRandomPositions(){
		ArrayList<int[]> positionsList = new ArrayList<int[]>();
		
		for(int i : agentSets){
			int[] positions = new int[i];
			
			for(int j = 0; j < positions.length; j++){
				positions[j] = RandomUtil.chooseInteger(0, graph.getNumNodes() - 1);
				
			}
			
			positionsList.add(positions);
		}
		
		return positionsList;
	}

	public void setInitialPositions(ArrayList<int[]> initialPositions) {
		this.initialPositions = initialPositions;
	}
	
	public void addInitialPositions(int[] positions){
		this.initialPositions.add(positions);
	}

}
