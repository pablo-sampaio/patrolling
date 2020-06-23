package algorithms.grav_centralized.core.propagators;

import yaps.graph.Graph;
import yaps.util.priority_queue.BinHeapPQueue;
import yaps.util.priority_queue.PQueue;
import yaps.util.priority_queue.PQueueElement;


/**
 * In this implementation, the resulting force in a specific edge x->y (in this specific
 * direction) is calculated as the MAXIMUM of all forces produced in x->y.
 * 
 * @see GravityPropagatorEdge
 * @author Pablo A. Sampaio
 */
public class GravityPropagatorEdgeMax extends GravityPropagatorEdge {
	private PQueue<GravInfo>[][] gravLists;
	

	@SuppressWarnings("unchecked")
	public GravityPropagatorEdgeMax(Graph graph, double exponent) {
		super(graph, exponent);		
		gravLists = new PQueue[graph.getNumNodes()][graph.getNumNodes()];
	}


	@Override
	public void applyGravities(int attractor, double attractorMass) {
		assert (attractorMass >= 0.0d);
		assert (masses[attractor] == -1.0d);
		
		int numVertices = gravities.length;

		int nextFromAttracted;
		GravInfo gravInfo;

		for (int attracted = 0; attracted < numVertices; attracted++) {
			if (attracted != attractor) {
				gravInfo = new GravInfo(attractor, attractorMass * propagationFactor[attracted][attractor]); 

				nextFromAttracted = shortestPaths.getSourceSuccessor(attracted, attractor);
				gravities[attracted][nextFromAttracted] = addToGravList(attracted, nextFromAttracted, gravInfo); 
			}
		}
		
		masses[attractor] = attractorMass;
	}
	
	
	// adiciona na lista/heap e retorna a gravidade m�xima
	private double addToGravList(int attractedNode, int neighbor, GravInfo gravInfo) {
		if (gravLists[attractedNode][neighbor] == null) {
			gravLists[attractedNode][neighbor] = new BinHeapPQueue<GravInfo>(super.graph.getNumVertices());
		}
		
		gravLists[attractedNode][neighbor].add(gravInfo);
		
		return gravLists[attractedNode][neighbor].getMinimum().gravity; //obs: minimum key has maximum gravity
	}

	
	@Override
	public void undoGravities(int attractorNode) {
		assert (masses[attractorNode] >= 0.0d);
		
		int numVertices = gravities.length;
		int nextFromAttracted;

		//System.out.println("... desfazendo gravidades partindo de " + attractorNode);
		for (int attracted = 0; attracted < numVertices; attracted++) {
			if (attracted != attractorNode) {
				nextFromAttracted = shortestPaths.getSourceSuccessor(attracted, attractorNode);
				
				gravities[attracted][nextFromAttracted] = removeFromGravList(attracted, nextFromAttracted, attractorNode); 
			}
		}

		masses[attractorNode] = -1.0d;
	}
	
	private double removeFromGravList(int attracted, int neighbor, int attractor) {
		assert (gravLists[attracted][neighbor] != null);
		
		PQueue<GravInfo> gravList = gravLists[attracted][neighbor];
		
		//System.out.print("..... gravidade de " + attracted + " para " + neighbor + " mudou de " + gravList.getMinimum().gravity); 
		gravList.remove(new GravInfo(attractor, -1.0));
		//System.out.println(" para " + (gravList.isEmpty() ? 0.0d : gravList.getMinimum().gravity));
		
		return gravList.isEmpty()? 0.0d : gravList.getMinimum().gravity;
	}
	
	
	@Override
	public void undoAllGravities() {
		// specialized implementation
		for (int i = 0; i < gravLists.length; i++) {
			for (int j = 0; j < gravLists.length; j++) {
				gravLists[i][j] = null;
				gravities[i][j] = 0.0d;
			}
			masses[i] = -1.0d;
		}
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		long FACTOR = 10000L;
		long aprox;
		
		builder.append("[ ");
		for (int i = 0; i < masses.length; i++) {
			builder.append("v" + i);
			builder.append(": ");
			builder.append(masses[i]);
			builder.append(", ");
		}
		builder.append("]\n");
		
		int attractor;
		
		for (int i = 0; i < masses.length; i++) {
			for (int j = 0; j < masses.length; j++) {
				aprox = (long)(FACTOR * getGravity(i,j));
				if (aprox != 0) {
					builder.append("\t| from v" + i + " to v" + j + ": ");
					builder.append(aprox);
					
					attractor = gravLists[i][j].getMinimum().attractorNode;
					
					builder.append(" (v");
					builder.append(attractor);
					builder.append(")");
					builder.append('\n');
				}
			}
		}
		
		return builder.toString();
	}

	// classe auxiliar
	private class GravInfo extends PQueueElement {
		int attractorNode;
		double gravity;
		
		GravInfo(int or, double gr) {
			attractorNode = or;
			gravity = gr;
		}
		
		@Override
		public double getKey() {
			return -gravity;
		}
		
		public boolean equals(Object o) {
			return o instanceof GravInfo && ((GravInfo)o).attractorNode == attractorNode;
		}
	}

}
