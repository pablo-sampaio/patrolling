package yaps.graph.algorithms;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import yaps.graph.Edge;
import yaps.graph.Graph;
import yaps.graph.Path;


/**
 * Algorithm that computes Eulerian Circuit. It works with
 * mixed graphs (with directed and undirected edges). 
 *  
 * @author Pablo A. Sampaio
 */
public class EulerianCircuit extends GraphAlgorithm {
	private Path circuit;
	
	public EulerianCircuit(Graph g) {
		super(g);
		this.circuit = null;
	}
	
	public Path compute() {
		return compute(0);
	}
	
	public Path compute(int startNode) {
		if (graph == null) {
			throw new Error("No graph was given");
		} 
		this.circuit = null;
		
		int numNodes = graph.getNumNodes();
		int numEdges = graph.getNumEdges();

		// iterator for the adjacency lists of each node
		Iterator<Edge>[] edges = new Iterator[numNodes];
		for (int i = 0; i < numNodes; i++) {
			edges[i] = graph.getOutEdges(i).iterator();
		}
		
		//boolean[] closedNodes = new boolean[numNodes]; //flag to be set per node id
		boolean[] closedEdges = new boolean[numEdges]; //flag to be set per edge id

		// insertionPoint[x] indicates the last position in the circuit where node "x" appears
		// so, this position (that may be in the middle of the list) is used to insert successors of "x"    
	   	ListNode[] insertionPoint = new ListNode[numNodes];
	   	
	   	// starts only with the start node
	   	ListNode circuitList = insertionPoint[startNode] = new ListNode(startNode);

	   	// this is the main part, that sets the (supposed) answer in the "circuit" variable
		findCircuit(startNode, edges, closedEdges, insertionPoint);

		// final processing
		boolean foundUnvisitedEdge = false;
		for (int e = 0; e < numEdges; e++) {
			if (!closedEdges[e]) {
				foundUnvisitedEdge = true;
				break;
			}
		}
		
		if (foundUnvisitedEdge) {
			this.circuit = null;
		} else {
			this.circuit = new Path(this.graph);
			circuitList.copyAll(this.circuit);
		}

		return this.circuit;
	}
	
	private void findCircuit(int s, Iterator<Edge>[] edges, boolean[] closedEdges, ListNode[] insertionPoint) {
	    LinkedList<Integer> stack = new LinkedList<Integer>(); //used to do an iterative DFS
	    stack.push(s);
	    
	    int curr, next;
	    Edge currEdge;
	    boolean edgeFound;
	    
	    while (! stack.isEmpty()) {
	    	curr = stack.peek(); //current node (vertex)
	    	edgeFound = false;
	    	
	    	while (edges[curr].hasNext()) {
	    		currEdge = edges[curr].next();
	    		
	    		assert(curr == currEdge.getSource());	    		
	        	
	    		next = currEdge.getTarget();  //successor of "curr"

				if (! closedEdges[currEdge.getId()]) {
	    	        edgeFound = true;
		    		insertionPoint[next] = insertionPoint[curr].add(next);
		    		closedEdges[currEdge.getId()] = true;
					stack.push(next);
	    	        break;
	        	}
	    	}
	    	
	    	if (!edgeFound) {
	    		assert(curr == stack.peek());
	    		stack.pop(); //removes "curr" node
	    	}
	    }
	    
	}
	
	public Path getCircuit() {
		return circuit;		
	}
	
}

class ListNode {
	final int content;
	private ListNode next;
	
	ListNode(int c) {
		content = c;
		next = null;
	}
	
	ListNode add(int nextContent) {
		ListNode newNode = new ListNode(nextContent);
		newNode.next = this.next; 
		this.next = newNode;
		return newNode;
	}
	
	List<Integer> toList() {
		LinkedList<Integer> output = new LinkedList<Integer>();
		copyAll(output);
		return output;
	}
	
	void copyAll(List<Integer> outputList) {
		outputList.add(content);
		if (next != null) {
			next.copyAll(outputList);
		}
	}
	
}
