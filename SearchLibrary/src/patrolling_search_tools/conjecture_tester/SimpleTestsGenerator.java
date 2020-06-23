package patrolling_search_tools.conjecture_tester;

import yaps.graph.Graph;
import yaps.graph.generators.GraphGenerator;
import yaps.graph.generators.MaxDegreeGraphGenerator;
import yaps.util.RandomUtil;


/**
 * This test generator creates tests (TMAP instances) varying the size <b>N</b> of the graph (number 
 * of nodes) from 3 to the maximum number of nodes given in the constructor, and varying the number of 
 * agents <b>K</b> from 1 to <i>N</i>/3. 
 * <p>
 * For each pair <i>(N,K)</i>, more than one test may be done. In each test, a new graph is generated 
 * (with <i>N</i> nodes) and new (<i>K</i>) initial positions are randomly chosen for the agents.  
 * The number of repetitions depends on the pair <i>(N,K)</i>, as defined by {@link #chooseRepetitions(int, int)} ,
 * which may be overridden to change the number of repetitions. 
 * <p>
 * The graph is generated with an instance of <b>GraphGenerator</b>. If none is provided, this class 
 * uses a default graph generator.
 * 
 * @see GraphGenerator
 * @author Pablo A. Sampaio
 */
public class SimpleTestsGenerator implements TestsGenerator {
	private GraphGenerator generator;
	private int graphMaxSize;

	private int repetitions;  //repetitions with the same sizes of society and of the graph (but changing initial positions and graph topology)
	
	private Graph generatedGraph;
	private int[] generatedPositions;
	
	/**
	 * Creates a test generator with the random graph generator. 
	 */
	public SimpleTestsGenerator(int graphMaxNodes) {
		this(new MaxDegreeGraphGenerator(5), graphMaxNodes);
	}
	
	/**
	 * Creates a test generator with the given graph generator. 
	 */
	public SimpleTestsGenerator(GraphGenerator generator, int graphMaxNodes) {
		this.generator = generator;
		this.graphMaxSize = graphMaxNodes;
		
		this.generator.setNumberOfNodes(3); //starts with three nodes...
		this.generatedGraph = generator.generate();
		this.generatedPositions = new int[]{ RandomUtil.chooseInteger(0, 2) }; //... and one agent
		
		this.repetitions = chooseRepetitions(3, 1) - 1;
	}

	@Override
	public boolean hasNextTest() {
		return (this.generatedGraph != null && this.generatedPositions != null);
	}
	
	@Override
	public TmapInstance nextTest() {
		if (this.generatedGraph == null || this.generatedPositions == null) {
			return null;
		}
		TmapInstance tmap = new TmapInstance(generatedGraph, generatedPositions);
		
		prepareNewTest();
		
		return tmap;
	}
	
	private void prepareNewTest() {
		int teamSize = generatedPositions.length;
		int numNodes = generatedGraph.getNumNodes();
		
		if (this.repetitions > 0) {
			//the team size and graph size remains the same (but the graph and the positions will be generated again)
			this.repetitions --;
			
		} else if ((teamSize + 1) <= numNodes / 3) {
			//increases the team; the graph size doesn't change
			teamSize ++;
			this.repetitions = chooseRepetitions(numNodes, teamSize) - 1; 
			
		} else {

			if (numNodes >= graphMaxSize) {
				//the last graph reached the maximum size, no more tests need to be done
				this.generatedGraph = null;
				this.generatedPositions = null;
				return;
			} 
			
			//changes the graph, increasing the number of nodes
			numNodes ++;
			this.generator.setNumberOfNodes(numNodes);	
			teamSize = 1;
			this.repetitions = chooseRepetitions(numNodes, teamSize) - 1;
		}
		
		this.generatedGraph = generator.generate();		
		this.generatedPositions = RandomUtil.generateArrayOfInts(teamSize, 0, numNodes-1);
	}

	protected int chooseRepetitions(int numNodes, int numAgents) {
		int totalCombinationsOfStartPositions = 1;  
		
		//these two loops set the total combinations to the binomial coefficient "C(numNodes,numAgents)"
		for (int f = numNodes; f > (numNodes - numAgents); f--) {
			totalCombinationsOfStartPositions *= f;
		}		
		for (int f = numAgents; f >= 1; f--) {
			totalCombinationsOfStartPositions /= f;
		}
		
		int startPositionsFactor = totalCombinationsOfStartPositions / 5; //20%
		if (startPositionsFactor == 0) startPositionsFactor = 1;
		
		//approximate number of unrooted unlabeled trees, used here as an estimate (a lower bound, indeed) for the number of connected graphs with "n" nodes (Otter, 1948)
		//double totalGraphs = (0.534949606d / Math.pow(numNodes, 2.5d)) * Math.pow(2.95576528565d, numNodes);  
		
		//a lower bound for the number of labeled trees with "n" nodes -- the correct number would be n^(n-2)
		int totalGraphs = 1 << (numNodes-2); //2^(numNodes-2)
		int graphFactor = totalGraphs / 5;
		if (graphFactor == 0) graphFactor = 1;
		
		System.out.printf("CR(%dnodes, %dagents) = %d*%d = %d %n", numNodes, numAgents, startPositionsFactor, graphFactor, startPositionsFactor*graphFactor);
		return startPositionsFactor*graphFactor;
	}
	
}
