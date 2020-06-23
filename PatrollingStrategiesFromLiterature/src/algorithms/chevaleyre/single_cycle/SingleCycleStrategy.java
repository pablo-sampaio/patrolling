/**
 * 
 */
package algorithms.chevaleyre.single_cycle;

import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.graph.algorithms.AllShortestPaths;
import yaps.graph.algorithms.ChristofidesTspApproximation;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.core.AgentTeamInfo;
import yaps.simulator.core.Algorithm;
import yaps.util.Pair;

/**
 * Main solutions proposed by Chevaleyre. 
 *  
 * @author Pablo A. Sampaio
 */
public class SingleCycleStrategy implements Algorithm {
	private Path  theCycle;
	private int[] cycleIndexOf;
	
	public SingleCycleStrategy() {
		//futuro: permitir variante que use o ciclo �timo, a ser chamada "SC(opt)"
	}

	//attributes used to deploy agents along the cycle
	private int totalDistributionTime;
	private Path[] distributionPathOf;
	private int[] sleepTimeOf;
	
	@Override
	public String getName() {
		return "SC(chr)";
	}

	@Override
	public void onSimulationStart(Graph graph, AgentPosition[] initialInfo, int totalTime) {
		int numAgents = initialInfo.length;
		
		ChristofidesTspApproximation tspSolver = new ChristofidesTspApproximation(graph);
		tspSolver.compute();
		
		this.theCycle = tspSolver.getSolution();
		System.out.printf("TSP cycle found: %s (dist=%d) %n", theCycle, theCycle.getCost());
		assert(theCycle.getFirst() == theCycle.getLast());
		
		//choose equidistant positions along the cycle
		double deltaDistance = (double)this.theCycle.getCost() / (double)numAgents;
		AgentPosition[] equidistPositions = new AgentPosition[numAgents];
		Integer[] equidistPosCycleIndex = new Integer[numAgents];
		
		equidistPositions[0] = new AgentPosition(theCycle.get(0)); //the start of the cycle is taken as reference
		equidistPosCycleIndex[0] = 0;                              //the first equidistant position is exactly the first in the cycle
		System.out.printf("Equidistant positions: displacement %f %n", deltaDistance);
		
		System.out.printf(" - position: %s (index in cycle: %d) %n", equidistPositions[0], 0);
		Pair<AgentPosition,Integer> pair;
		for (int i = 1; i < numAgents; i++) {
			pair = advanceInPath(graph, this.theCycle, (int)(i*deltaDistance));
			equidistPositions[i] = pair.first;
			equidistPosCycleIndex[i] = pair.second;
			System.out.printf(" - position: %s (index in cycle: %d) %n", equidistPositions[i], equidistPosCycleIndex[i]);
		}
		
		AllShortestPaths shortestPaths = new AllShortestPaths(graph);
		shortestPaths.compute();
		
		//create the paths that the agents will follow initially, to distribute themselves equidistantly along the cycle
		computePathsToDistributeAgents(shortestPaths, initialInfo, equidistPositions, equidistPosCycleIndex);		
		
		for (int i = 0; i < initialInfo.length; i++) {
			int node = initialInfo[i].getCurrentNode();
			System.out.printf("Agent %d will go from %s to %s (sleep=%d, dist=%d) %n", i, 
					initialInfo[i], equidistPositions[i],
					sleepTimeOf[i], computeDistance(shortestPaths, node, equidistPositions[i]));
			System.out.printf(" - path: %s %n", this.distributionPathOf[i]);
			System.out.printf(" - index in cycle (after distribution): %d %n", this.cycleIndexOf[i]);
		}
		
		//remove repetition of the first node; it must be done here after distributing the agents
		this.theCycle.removeLast();
	}
	
	/**
	 * Returns two values: <br><br>
     * 1) The position after the agent has walked "distance" in the path; <br>
     * 2) The index in the path related to the returned position. (In P is in the middle of an edge, this index is 
     * the "target" node of the edge. <br>
	 */
	private static Pair<AgentPosition,Integer> advanceInPath(Graph graph, Path path, int distance) {
		int prevTotalDistance = 0;
		int currNodeIndex = 0;
		//TODO: melhorar: receber todos os displacements e retornar uma lista de todos os pontos, para evitar redund�ncias
		
		int nextTotalDistance = graph.getEdgeLength(path.get(currNodeIndex), path.get(currNodeIndex+1));
		while (nextTotalDistance < distance) {
			prevTotalDistance = nextTotalDistance;
			currNodeIndex ++;
			nextTotalDistance += graph.getEdgeLength(path.get(currNodeIndex), path.get(currNodeIndex+1));
		}
		
		if (nextTotalDistance == distance) {
			return new Pair<AgentPosition,Integer>(
							new AgentPosition(path.get(currNodeIndex)), currNodeIndex);
		} else {
			int edgeDistance = distance - prevTotalDistance;  // the same as lengthOfEdge - (nextTotalDistance - distance)
			return new Pair<AgentPosition,Integer>(
							new AgentPosition(path.get(currNodeIndex), path.get(currNodeIndex+1), edgeDistance),
							currNodeIndex+1);
		}
	}
	
	private void computePathsToDistributeAgents(AllShortestPaths shortestPaths, AgentPosition[] initialPos, 
							AgentPosition[] equidistPos, Integer[] equidistPosCycleIndex) {
		int numAgents = initialPos.length;
		int maxDistance = 0;
		
		this.distributionPathOf = new Path[numAgents];
		this.cycleIndexOf = new int[numAgents];
		
		//TODO: usar bipatite matching (de nos extras representando as posicoes dos agentes para as posicoes do ciclo desejadas)
		
		int[] distAgent2EquidistPos = new int[numAgents];
		
		//the positions are set for each agent in a greedy fashion: for each agent, 
		//chooses the "equidistant" position of the cycle which is closest to the agent
		for (int ag = 0; ag < numAgents; ag++) {
			int agInitialNode = initialPos[ag].getCurrentNode();
			
			int bestPositionIndex = -1;
			int bestPositionDistance = Integer.MAX_VALUE; 

			int distance;
			for (int i = ag; i < equidistPos.length; i++) {
				distance = computeDistance(shortestPaths, agInitialNode, equidistPos[i]);
				if (distance < bestPositionDistance) {
					bestPositionIndex = i;
					bestPositionDistance = distance;					
				}
			}			
			assert(bestPositionIndex != -1);
			
			exchange(equidistPos, ag, bestPositionIndex);            //put the best (for this agent) in the same index of the agent
			exchange(equidistPosCycleIndex, ag, bestPositionIndex); //and also exchange the related indexes in the tsp cycle

			distAgent2EquidistPos[ag] = bestPositionDistance;
			if (equidistPos[ag].inNode()) {
				this.distributionPathOf[ag] = shortestPaths.getPath(initialPos[ag].getCurrentNode(), 
						equidistPos[ag].getCurrentNode());
				this.cycleIndexOf[ag] = equidistPosCycleIndex[ag];
			} else {
				this.distributionPathOf[ag] = shortestPaths.getPath(initialPos[ag].getCurrentNode(), 
						equidistPos[ag].getOrigin());
				this.distributionPathOf[ag].add(equidistPos[ag].getDestination());
				this.cycleIndexOf[ag] = equidistPosCycleIndex[ag];
			}
			
			if (bestPositionDistance > maxDistance) {
				maxDistance = bestPositionDistance;
			}
		}

		//set the sleep times
		this.sleepTimeOf = new int[numAgents];
		for (int a = 0; a < numAgents; a++) {
			this.sleepTimeOf[a] = maxDistance - distAgent2EquidistPos[a];
		}
		this.totalDistributionTime = maxDistance;
	}
	
	private static int computeDistance(AllShortestPaths shortestPaths, int startNode, AgentPosition end) {
		int destinyNode, extraDistance;
		if (end.inNode()) {
			destinyNode = end.getCurrentNode();
			extraDistance = 0;
		} else {
			destinyNode = end.getOrigin();
			extraDistance = end.getDistance();
		}
		return shortestPaths.getDistance(startNode, destinyNode) + extraDistance;
	}

	private static<T> void exchange(T[] array, int posX, int posY) {
		if (posX == posY) return;
		T temp = array[posX];
		array[posX] = array[posY];
		array[posY] = temp;
	}
	
	@Override
	public void onTurn(int nextTurn, AgentTeamInfo team) {
		AgentPosition agPosition;
		int agNextNode;
		
		if (totalDistributionTime == 0) {
			for (int agent = 0; agent < team.getTeamSize(); agent ++) {
				agPosition = team.getPosition(agent);
				
				if (agPosition.inNode()) {
					agNextNode = selectGoalNode(agent, agPosition.getCurrentNode());
					team.actGoto(agent, agNextNode);
					//System.out.printf("Agent %d going from node(%d) to node(%d), in turn %d%n", agent, agPosition.getCurrentNode(), agNextNode, nextTurn);
				}
			}
			
		} else {
			for (int agent = 0; agent < team.getTeamSize(); agent ++) {
				agPosition = team.getPosition(agent);
				System.out.printf("Agent %d in %s %n", agent, agPosition);

				if (this.sleepTimeOf[agent] > 0) {
					System.out.printf(" - still sleeping (%d more)%n", this.sleepTimeOf[agent]-1);
					this.sleepTimeOf[agent] --;				
				} else if (agPosition.inNode()) {
					this.distributionPathOf[agent].removeFirst();
					agNextNode = this.distributionPathOf[agent].get(0);
					System.out.printf(" - going to %s %n", agNextNode);
					team.actGoto(agent, agNextNode);				
				}
			}			
			
			this.totalDistributionTime --;
			if (totalDistributionTime == 0) {
				System.out.println("Finished distributing agents!");
			}
		}
	}

	private int selectGoalNode(int agent, int currentNode) {
		assert(currentNode == this.theCycle.get(this.cycleIndexOf[agent] % this.theCycle.size()));
		this.cycleIndexOf[agent] = (this.cycleIndexOf[agent] + 1) % this.theCycle.size();
		return this.theCycle.get(this.cycleIndexOf[agent]);
	}

	@Override
	public void onSimulationEnd() {
		// faz nada		
	}

}
