package yaps.experimentation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import yaps.graph.Graph;
import yaps.util.RandomUtil;
import yaps.util.lazylists.RangeLazyList;


/**
 * This class keeps settings related to the map that will be used in the simulations.
 * In essence, it keeps the graph and a set of arrays of initial positions where 
 * each array indicates where the agents will start in each simulation. Furthermore,
 * the size of each array implicitly determines the number of agents in each simulation. 
 *  
 * @author Pablo Sampaio
 * @author Rodrigo de Sousa
 *
 */
public class MapSettings {
	private String name;
	private Graph graph;
	private Map<Integer, List<int[]>> initialPositions; //maps #agents -> positions 

	/**
	 * With this constructor, you set specific predefined initial positions that will be used
	 * in the simulations in {@link ExperimentPerformer}. 
	 */
	public MapSettings(String name, Graph graph, List<int[]> initialPos) {
		this.name = name;
		this.graph = graph;
		this.initialPositions = new TreeMap<>();

		int numAgents;
		
		for (int i = 0; i < initialPos.size(); i++) {
			numAgents = initialPos.get(i).length;
			if (!initialPositions.containsKey(numAgents)) {
				initialPositions.put(numAgents, new LinkedList<>());
			}
			initialPositions.get(numAgents).add(initialPos.get(i));
		}
		
	}

	/**
	 * With this constructor, you only indicate the numbers of agents as an array of different values
	 * (you should not repeat a society size). Then, the positions are randomly generated once and 
	 * stored in this class to be used by {@link ExperimentPerformer}. You can also generate more than
	 * one positioning for each society size by setting a >= 1 value in the parameter 
	 * "positionConfigurationsPerTeamSize". The last parameter may be used to avoid generating agents 
	 * in the same start position. The position configurations, however, are never guaranteed to be 
	 * all different.
	 */
	public MapSettings(String name, Graph graph, int[] numbersOfAgents, int positionConfigurationsPerTeamSize, boolean agentsInDifferentPositions) {
		if (positionConfigurationsPerTeamSize < 1) {
			throw new Error("Parameter randomPosPerNumAgents should >= 1");
		}		
		this.name = name;
		this.graph = graph;
		this.initialPositions = new TreeMap<>();
		
		int agents;
		List<int[]> positions;
		
		RangeLazyList allNodes = new RangeLazyList(0, graph.getNumNodes()-1);
		
		if (agentsInDifferentPositions) {
			for (int i = 0; i < numbersOfAgents.length; i++) {
				agents = numbersOfAgents[i];
				positions = new LinkedList<>();
				initialPositions.put(agents, positions);
				
				for (int cnt = 0; cnt < positionConfigurationsPerTeamSize; cnt++) {
					int[] pos = toArray(RandomUtil.randomChoose(agents, allNodes));
					positions.add(pos);
				}
			}
			
		} else {
			for (int i = 0; i < numbersOfAgents.length; i++) {
				agents = numbersOfAgents[i];
				positions = new LinkedList<>();
				initialPositions.put(agents, positions);
				
				for (int cnt = 0; cnt < positionConfigurationsPerTeamSize; cnt++) {
					positions.add(generateRandomPositions(agents));				
				}
			}
		}
		
	}
	
	public MapSettings(String name, Graph graph, int[] numbersOfAgents, int positionConfigurationsPerTeamSize) {
		this(name, graph, numbersOfAgents, positionConfigurationsPerTeamSize, true);
	}

	private int[] toArray(List<Integer> list) {
		int[] result = new int[list.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	private int[] generateRandomPositions(int agents) {
		int[] positions = new int[agents];
		
		for(int j = 0; j < agents; j++){
			positions[j] = RandomUtil.chooseInteger(0, graph.getNumNodes() - 1);			
		}
		
		return positions;
	}

	
	public String getName() {
		return name;
	}

	public Graph getGraph() {
		return graph;
	}

	public SortedSet<Integer> getNumbersOfAgents() {
		return new TreeSet<Integer>(initialPositions.keySet());
	}

	public List<int[]> getInitialPositions(int numAgents) {
		return Collections.unmodifiableList(this.initialPositions.get(numAgents));
	}

	public List<int[]> getAllInitialPositions() {
		List<int[]> allInitialPos = new LinkedList<>();

		for (Integer agents : this.initialPositions.keySet()) {
			allInitialPos.addAll(this.initialPositions.get(agents));
		}
		
		return allInitialPos;
	}
	
}
