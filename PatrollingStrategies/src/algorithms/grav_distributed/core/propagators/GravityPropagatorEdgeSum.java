package algorithms.grav_distributed.core.propagators;

import algorithms.grav_distributed.core.GravityPropagator;
import yaps.graph.Graph;


/**
 * In this implementation, the resulting force in a specific edge x->y (in this specific
 * direction) is calculated as the SUM of all forces produced in x->y.
 * 
 * @see GravityPropagatorEdge
 * @author Pablo A. Sampaio
 */
public class GravityPropagatorEdgeSum extends GravityPropagator {
	
	protected double[][] gravities;
	
	public GravityPropagatorEdgeSum(Graph graph, double exponent) {
		super(graph, exponent);
		
		int numVertices = graph.getNumNodes();
		gravities = new double[numVertices][numVertices];
	}

	@Override
	public void applyGravities(int attracted) {
		//assert (attractorMass >= 0.0d);
		//assert (masses[attractor] == -1.0d);

		applyGravitiesInternal(attracted);
		//masses[attracted] = attractorMass;
	}
	
	@Override
	public void updateNodeMass(int node, double mass) {
		masses[node] = mass;
	}
	
	private void applyGravitiesInternal(int attracted) {
		int numVertices = gravities.length;

		int nextFromAttracted;
		
		for (int attractor = 0; attractor < numVertices; attractor++) {
			if (attractor != attracted) {
				double attractorMass = masses[attractor];
				nextFromAttracted = shortestPaths.getSourceSuccessor(attracted, attractor);
				gravities[attracted][nextFromAttracted] += attractorMass * propagationFactor[attracted][attractor];
			}
		}
	}

	@Override
	public double getGravity(int attractedNode, int neighborNode) {
		return gravities[attractedNode][neighborNode];
	}

}
