package algorithms.grav_distributed.core;

import algorithms.grav_distributed.core.propagators.GravityPropagatorEdgeSum;
import yaps.graph.Graph;
import yaps.graph.algorithms.AllShortestPaths;


/**
 * Implementations of this classes calculate the gravities exerted by all nodes in all nodes. 
 * In common, all of them assume that nodes have masses which produce forces in all the other 
 * nodes. This force is inversely proportional to the the minimum distance raised to the 
 * given exponent.  
 * <br><br>
 * The creation of instances of concrete subclasses can be done through the static method create(). 
 * <br><br>
 * To use concrete instances of this class, first, set the masses of all nodes with 
 * applyGravities(). Then, the (combined) gravity in a (directed) edge from "a" to "b" can be 
 * queried with a call to getGravity(a,b).  
 * 
 * @author Pablo A. Sampaio
 */
public abstract class GravityPropagator {
	protected Graph graph;
	protected AllShortestPaths shortestPaths;
	
	protected double distanceExponent;
	protected double[][] propagationFactor;
	
	protected double[] masses;
	
	
	public static GravityPropagator create(ForcePropagation prop, double exp, ForceCombination comb, Graph g) {
		if (prop == ForcePropagation.EDGE) {
			if (comb == ForceCombination.MAX) {
				return null;// new GravityPropagatorEdgeMax(g, exp);
			} else if (comb == ForceCombination.SUM) {
				return new GravityPropagatorEdgeSum(g, exp);
			}
			
		} /*else if (prop == ForcePropagation.NODE_NO_DISTANCE) {
			if (comb == ForceCombination.MAX) {
				return new GravityPropagatorNodeMax(g, exp, false);
			} else if (comb == ForceCombination.SUM) {
				return new GravityPropagatorNodeSum(g, exp, false);
			}
			
		} else if (prop == ForcePropagation.NODE) {
			if (comb == ForceCombination.MAX) {
				return new GravityPropagatorNodeMax(g, exp, true);
			} else if (comb == ForceCombination.SUM) {
				return new GravityPropagatorNodeSum(g, exp, true);
			}
			
		} else if (prop == ForcePropagation.MIXED) {
			if (comb == ForceCombination.MAX) {
				return new GravityPropagatorMixedMax(g, exp);
			} else if (comb == ForceCombination.SUM) {
				return new GravityPropagatorMixedSum(g, exp);
			}
			
		}*/
		
		return null;
	}
	
	
	protected GravityPropagator(Graph g, double exponent) {
		int numVertices = g.getNumVertices();

		this.graph = g;
		
		this.shortestPaths = new AllShortestPaths(g);
		this.shortestPaths.compute();

		this.distanceExponent = exponent;
		this.propagationFactor = new double[numVertices][numVertices];
		this.masses = new double[g.getNumVertices()];
		
		for (int attracted = 0; attracted < numVertices; attracted++) {
			this.masses[attracted] = -1.0d; //mass is not set
			
			for (int attractor = 0; attractor < numVertices; attractor++) {
				if (attracted != attractor) {
					this.propagationFactor[attracted][attractor] = 1.0d / 
							Math.pow(this.shortestPaths.getDistance(attracted,attractor), exponent);
				} else {
					this.propagationFactor[attracted][attractor] = 0.0d;
				}
			}
		}		

	}
	
	public abstract void updateNodeMass(int node, double mass);

	/**
	 * Retorna a gravidade (combinada) que atua na aresta saindo do
	 * v�rtice "startNode" e chegando no v�rtice "nighborNode".   
	 */
	public abstract double getGravity(int attractedNode, int neighborNode);

	/**
	 * Aplica as for�as de gravidade exercidas por todos os v�rtices do grafo 
	 * sobre o v�rtice "attracted".
	 */
	public abstract void applyGravities(int attracted);

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
		
		for (int i = 0; i < masses.length; i++) {
			for (int j = 0; j < masses.length; j++) {
				aprox = (long)(FACTOR * getGravity(i,j));
				if (aprox != 0) {
					builder.append("\t| from v" + i + " to v" + j + ": ");
					builder.append(aprox);
					builder.append('\n');
				}
			}
		}
		
		return builder.toString();
	}
	

}
