package yaps.strategies.evolutionary.variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.uma.jmetal.core.Variable;

import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.graph.algorithms.AllShortestPaths;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.strategies.evolutionary.utils.GraphUtils;
import yaps.strategies.evolutionary.utils.InducedSubGraph;
import yaps.strategies.evolutionary.utils.NearestInsertionPathBuilder;
import yaps.strategies.evolutionary.utils.NearestNeighborPathBuilder;
import yaps.strategies.evolutionary.utils.PathBuilder;
import yaps.util.RandomUtil;
import yaps.util.Range;
import yaps.util.lazylists.LazyList;

public class Agent extends SimpleAgent implements Variable {

	private Integer center;
	private Path path;
	private InducedSubGraph inducedSubGraph;

	private int currentNode;
	private int currentNodeIndex;
	private HashSet<Integer> coveredNodesSet;

	public Agent(Integer c, Path p, InducedSubGraph isg) {
		super(System.out);
		this.center = c;
		this.path = p;
		this.coveredNodesSet = new HashSet<Integer>(this.path);
		this.inducedSubGraph = new InducedSubGraph(new ArrayList<Integer>(
				this.coveredNodesSet), isg.getOriginalGraph());
		this.currentNodeIndex = 0;
		this.currentNode = this.path.get(this.currentNodeIndex);
		this.coveredNodesSet = new HashSet<Integer>(path);
	}

	public Integer getCenter() {
		return center;
	}

	public Path getPath() {
		return path;
	}

	public InducedSubGraph getInducedSubGraph() {
		return inducedSubGraph;
	}

	public HashSet<Integer> getCoveredNodeSet() {
		return coveredNodesSet;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n\t\"center\" : " + this.center + ",\n");
		sb.append("\t\"nodes_to_visit\" : "
				+ this.inducedSubGraph.getNodesSubSet() + ",\n");
		sb.append("\t\"path\" : " + this.path + ",\n");
		sb.append("}");
		return sb.toString();
	}

	@Override
	public Variable copy() {
		Path p = new Path(inducedSubGraph.getOriginalGraph());
		p.addAll(this.path);
		Agent agent = new Agent(this.center, p, this.inducedSubGraph);
		return agent;
	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		this.currentNodeIndex = (this.currentNodeIndex + 1)
				% (this.path.size() - 1);
		/*
		 * Minus one because the path is a cycle, so the last and first elements
		 * are the same
		 */
		this.currentNode = this.path.get(this.currentNodeIndex);
		return this.currentNode;
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onTurn(int arg0) {
	}

	public void addRandomNodeWithSmallChanges() {
		Graph fatherGraph = this.inducedSubGraph.getOriginalGraph();
		if (this.getCoveredNodeSet().size() == fatherGraph.getNumNodes()) {
			// If the agent is already covering the entire graph, can't add a
			// new node
			return;
		}
		// Pick a random node to add
		Integer node = null;
		List<Integer> shuffeledNodeList = RandomUtil.shuffle(GraphUtils
				.getNodeList(fatherGraph));
		for (Integer n : shuffeledNodeList) {
			if (!this.coveredNodesSet.contains(n)) {
				node = n;
				this.coveredNodesSet.add(n);
				break;
			}
		}
		if (node == null) {
			return;
		}
		// Find the node i, in the agent's path, with the smallest distance to
		// the chosen node
		int i = 0;
		AllShortestPaths asp = new AllShortestPaths(fatherGraph);
		asp.compute();
		double d = asp.getDistance(this.path.getFirst(), node);
		double dTry = d;
		for (int k = 0; k < this.path.size(); k++) {
			Integer nTry = this.path.get(k);
			dTry = asp.getDistance(nTry, node);
			if (dTry < d) {
				d = dTry;
				i = k;
			}
		}
		// Removes the node i and also gets the path from i do the chosen node
		Path p = asp.getPath(this.path.remove(i), node);
		// We are removing the node 'i' from the agent's path because this new
		// path we are creating, 'p', has 'i' as its first element
		// Add all nodes in the path from i to the chosen node into the list of
		// covered nodes and update the agent's induced subgraph
		this.coveredNodesSet.addAll(p);
		this.inducedSubGraph = new InducedSubGraph(new ArrayList<Integer>(
				this.coveredNodesSet), this.inducedSubGraph.getOriginalGraph());
		// Adds the path from i to the chosen node into the agent's path
		for (int k = 0; k < p.size(); k++) {
			if (k == p.size() - 1) {
				this.path.add(i + k, p.get(k));
				break;
			}
			this.path.add(i + k, p.get(k));
			this.path.add(i + k + 1, p.get(k));

		}
	}

	public void removeRandomNodeWithSmallChanges() {
		// If there are only two nodes, don't do it
		if (this.path.size() <= 3) {
			return;
			// Example: Path = {1, 3, 1}
		}
		// Choose randomly a node from path that is not the center
		int nodeIndex;
		List<Integer> indexList = new Range(0, this.path.size() - 1);
		do {
			nodeIndex = RandomUtil.chooseAtRandom(indexList);
		} while (path.get(nodeIndex).equals(this.center));

		AllShortestPaths asp = new AllShortestPaths(
				this.inducedSubGraph.getOriginalGraph());
		asp.compute();

		if (nodeIndex == 0 || nodeIndex == (this.path.size() - 1)) {
			// If the chosen node is the first or the last
			// In our case path is a cycle, so the first and last are the same
			this.path.removeFirst();
			this.path.removeLast();
			// So, the above lines remove the chosen node from the agent's path

			// Create a new path between the new last and the new first in the
			// agent's path
			Path p = asp.getPath(this.path.peekLast(), this.path.peekFirst());
			// Remove the first node from this new path, since it is already in
			// the agent's path
			p.removeFirst();
			// Example of these operations:
			/*
			 * original path = {1, 3, 4, 5, 1}, now remove 1 {3, 4, 5} calculate
			 * path between 5 and 3 = {5, 6, 3} remove first node from this path
			 * = {6, 3}
			 */

			// Append the new Path into the agent's path
			for (Integer np : p) {
				// do field medal
				this.path.addLast(np);
			}
			// In our earlier example, {3, 4, 5} + {6, 3}
			// = {3, 4, 5, 6, 3} is the agent's new path

			// Update the agent's covered node set and induced subgraph
			this.coveredNodesSet = new HashSet<Integer>(this.path);
			this.inducedSubGraph = new InducedSubGraph(new ArrayList<Integer>(
					this.coveredNodesSet),
					this.inducedSubGraph.getOriginalGraph());
			return;
		}

		if (this.path.get(nodeIndex - 1) == this.path.get(nodeIndex + 1)) {
			this.path.remove(nodeIndex);
			this.path.remove(nodeIndex);
			this.coveredNodesSet = new HashSet<Integer>(this.path);
			this.inducedSubGraph = new InducedSubGraph(new ArrayList<Integer>(
					this.coveredNodesSet),
					this.inducedSubGraph.getOriginalGraph());
			return;
		}

		// In "normal" cases: suppose the path is {1, 3, 4, 5, 1} remove 3
		// Calculate a path between 1 and 4: e.g. {1, 2, 7, 4}
		Path p = asp.getPath(this.path.get(nodeIndex - 1),
				this.path.get(nodeIndex + 1));
		// Remove the 1 and 4 from this new path, because we already have them
		// in agent's path
		p.removeFirst();
		p.removeLast();
		// So now, in the example, we have {2, 7}
		// Remove the chosen node from the path
		// In the example, we would be left with {1, 4, 5, 1}
		this.path.remove(nodeIndex);
		// Add every node from the new Path to the agent's path
		int I = p.size();
		for (int i = 0; i < I; i++) {
			// do field medal
			this.path.add(nodeIndex, p.removeLast());
			// In our example, this would do:
			// Iteration 1: Agent's Path = {1, 7, 4, 5, 1}
			// Iteration 2: Agent's Path = {1, 2, 7, 4, 5, 1}
		}
		// Now, update the agent's covered node set and induced subgraph
		this.coveredNodesSet = new HashSet<Integer>(this.path);
		this.inducedSubGraph = new InducedSubGraph(new ArrayList<Integer>(
				this.coveredNodesSet), this.inducedSubGraph.getOriginalGraph());
	}

	public void addRandomNodeAndRebuildPath(int pathRebuilderType) {
		final Graph originalGraph = this.inducedSubGraph.getOriginalGraph();
		if (this.coveredNodesSet.size() == originalGraph.getNumNodes()) {
			// If this agent already covers all nodes in the graph
			// there is no way to add another one
			return;
		}
		// Find a random node that is not already covered by this agent
		List<Integer> shuffledNodeList = RandomUtil.shuffle(GraphUtils
				.getNodeList(originalGraph));
		Integer node = null;
		for (Integer n : shuffledNodeList) {
			if (this.coveredNodesSet.contains(n)) {
				node = n;
				this.coveredNodesSet.add(n);
				break;
			}
		}
		// Just to be safe:
		if (node == null) {
			return;
		}
		// Now build a new path covering all the nodes assigned to this agent
		AllShortestPaths asp = new AllShortestPaths(originalGraph);
		asp.compute();
		// In case the new node is not accessible through the original nodes,
		// we have to add more nodes, so we chose to add a path from the
		// agent's center node to the new node.
		this.coveredNodesSet.addAll(asp.getPath(this.center, node));
		this.inducedSubGraph = new InducedSubGraph(new ArrayList<Integer>(
				this.coveredNodesSet), originalGraph);
		this.buildNewPath(pathRebuilderType);
	}

	public void removeRandomNodeAndRebuildPath(int pathRebuilderType) {
		if (this.coveredNodesSet.size() < 3) {
			return;
		} // We shall not remove nodes from agents with less than 3 nodes
		LinkedList<Integer> nodes = new LinkedList<Integer>(
				this.coveredNodesSet);

		Integer electedNode;
		InducedSubGraph isg;
		List<Integer> shuffledNodeList = RandomUtil.shuffle(LazyList
				.createRangeList(0, nodes.size() - 1, 1));
		for (int i : shuffledNodeList) {
			// Randomly removes a node from the list
			electedNode = nodes.remove(i);
			if (electedNode.equals(this.center)) {
				nodes.add(i, electedNode);
				continue;
				// If the elected node is the center, try again
			}
			// Create a new InducedSubGraph
			isg = new InducedSubGraph(nodes,
					this.inducedSubGraph.getOriginalGraph());
			// If we didn't disconnect the induced subgraph by removing the
			// elected node
			if (isg.isConnected()) {
				this.inducedSubGraph = isg;
				this.coveredNodesSet.remove(electedNode);
				this.buildNewPath(pathRebuilderType);
				return;
			}
			// If not, try again
			nodes.add(i, electedNode);
		}
	}

	private void buildNewPath(int pathBuilderType) {
		PathBuilder pb = null;
		switch (pathBuilderType) {
		case PathBuilder.NEAREST_INSERTION_PATH_BUILDER:
			pb = new NearestInsertionPathBuilder(
					this.inducedSubGraph.getOriginalGraph(),
					new ArrayList<Integer>(this.coveredNodesSet));
			break;
		case PathBuilder.NEAREST_NEIGHBOR_PATH_BUILDER:
		default:
			pb = new NearestNeighborPathBuilder(
					this.inducedSubGraph.getOriginalGraph(),
					new ArrayList<Integer>(this.coveredNodesSet));
			break;
		}
		this.path = pb.build();
	}
	
	/**
	 * This method provides our adaptation of the 2-change algorithm.
	 * It will apply the 2-change at two random edges in the Agent's path.
	 * If the new path is shorter than the former, it will substitute
	 * the agent's path. Otherwise, nothing happens.
	 */
	public void twoChangeAndImprove() {
		Path newPath = applyRandomTwoChange();
		if(newPath.getCost() < this.path.getCost()){
			this.path = newPath;
		}
	}

	/**
	 * Applies random two change on the agent. This method does NOT change the
	 * Agent. It just returns a candidate new Path. Another method in this class
	 * must call applyRandomTwoChange() and then decide whether to apply this
	 * new Path or not.
	 * 
	 * @return a Path for the Agent where the two change has happened.
	 */
	private Path applyRandomTwoChange() {
		// We will not be doing random 2-change on a path with less than 5 nodes
		if (this.path.size() < 5) {
			return (Path) this.path.clone();
		}
		int e1 = 0;
		int e2 = 0;
		int aux;
		while ((e1 == e2) || (e1 + 1 == e2)) {
			// Minus 2 because the last element of the path is the same as the
			// first. Also, this chooseInteger method is inclusive.
			e1 = RandomUtil.chooseInteger(0, this.path.size() - 2);
			e2 = RandomUtil.chooseInteger(0, this.path.size() - 2);
			// We want the first argument, e1, to the twoChange method to be the
			// edge at the left in the Path list.
			if (e1 > e2) {
				aux = e1;
				e1 = e2;
				e2 = aux;
			}
		}
		return twoChange(e1, e2);
	}

	/**
	 * Cuts two edges: between edge1 and edge1+1; between edge2 and edge2+1.
	 * Then, reconnects the path by connecting edge1 to edge2 and edge1+1 to
	 * edge2+2. Where these edge1, edge2, edge1+1, edge2+1 are indexes in the
	 * Path list.
	 * 
	 * @param edge1 The index, in the Path, of the first chosen Edge's source
	 * @param edge2 The index, in the Path, of the second chosen Edge's source
	 * @return A Path resulting from applying the 2-change to the Agent's path
	 */
	private Path twoChange(int edge1, int edge2) {
		if (edge1 == edge2 + 1) {
			return new Path(this.inducedSubGraph, this.path);
		}
		if (edge1 == edge2) {
			return new Path(this.inducedSubGraph, this.path);
		}
		AllShortestPaths asp = new AllShortestPaths(this.inducedSubGraph);
		asp.compute();
		Path newPath = new Path(this.inducedSubGraph);
		Integer now;
		for (int k = 0; k < this.path.size(); k++) {
			// Imagine the Agent's original path is as follows:
			// 0...(edge1-1) edge1 (edge1+1)...(edge2-1) edge2 (edge2+1)...n
			// -------------       ---------------------       -------------
			//       A                       B                       C
			// We want it to change into:
			//          some path                some path
			// A edge1 ----------- edge2 B(^-1) ----------- C
			if (k < edge1) {
				// Adds block A
				now = this.path.get(k);
				if (newPath.size() == 0) {
					newPath.add(now);
				} else if (!newPath.peekLast().equals(now)) {
					newPath.add(now);
				}
			} else if (k == edge1) {
				// Adds a path between edge1 and edge2
				Path p1 = asp.getPath(this.path.get(edge1),
						this.path.get(edge2));
				now = p1.removeFirst();

				if (newPath.size() == 0) {
					newPath.add(now);
				} else if (!newPath.peekLast().equals(now)) {
					newPath.add(now);
				}
				newPath.addAll(p1);
			} else if (k < edge2) {
				// Adds B reversed
				now = this.path.get(edge1 + edge2 - k);
				if (newPath.size() == 0) {
					newPath.add(now);
				} else if (!newPath.peekLast().equals(now)) {
					newPath.add(now);
				}
			} else if (k == edge2) {
				// Adds a path between edge1+1 and edge2+1
				Path p1 = asp.getPath(this.path.get(edge1 + 1),
						this.path.get(edge2 + 1));
				// edge1+1 was added when we added b reversed
				// edge2+1 will be added soon when we add C
				// So, if there are no nodes between them, do nothing
				if (p1.size() > 2) {
					p1.removeFirst();
					p1.removeLast();
					newPath.addAll(p1);
				}
			} else {
				// Adds C
				now = this.path.get(k);
				if (newPath.size() == 0) {
					newPath.add(now);
				} else if (!newPath.peekLast().equals(now)) {
					newPath.add(now);
				}
			}
		}
		return newPath;
	}

}
