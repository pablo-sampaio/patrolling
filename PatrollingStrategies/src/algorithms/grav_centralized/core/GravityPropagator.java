package algorithms.grav_centralized.core;


import algorithms.grav_centralized.core.propagators.GravityPropagatorEdgeMax;
import algorithms.grav_centralized.core.propagators.GravityPropagatorEdgeSum;
import algorithms.grav_centralized.core.propagators.GravityPropagatorMixedMax;
import algorithms.grav_centralized.core.propagators.GravityPropagatorMixedSum;
import algorithms.grav_centralized.core.propagators.GravityPropagatorNodeMax;
import algorithms.grav_centralized.core.propagators.GravityPropagatorNodeSum;
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
				return new GravityPropagatorEdgeMax(g, exp);
			} else if (comb == ForceCombination.SUM) {
				return new GravityPropagatorEdgeSum(g, exp);
			}
			
		} else if (prop == ForcePropagation.NODE_NO_DISTANCE) {
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
			
		}
		
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
	
	/**
	 * Retorna a gravidade (combinada) que atua na aresta saindo do v�rtice 
	 * "attractedNodeId" e chegando no seu v�rtice vizinho "neighborNodeId".   
	 */
//TODO: remove
//	public double getGravity(String attractedNodeId, String neighborNodeId) {
//		int attracted = graph.getNode(attractedNodeId).getIndex();
//		int neighbor = graph.getNode(neighborNodeId).getIndex();
//		return getGravity(attracted, neighbor);
//	}

	/**
	 * Retorna a gravidade (combinada) que atua na aresta saindo do
	 * v�rtice "startNode" e chegando no v�rtice "nighborNode".   
	 */
	public abstract double getGravity(int attractedNode, int neighborNode);

	/**
	 * Aplica as for�as de gravidade exercidas pelo n� "attractorId" sobre todos 
	 * os demais n�s do grafo. As gravidades de um mesmo n� n�o podem ser aplicadas 
	 * uma segunda vez sem que tenham sido desfeitas antes.
	 */
//	public void applyGravities(String attractorId, double attractorMass) {
//		applyGravities(graph.getNode(attractorId).getIndex(), attractorMass);
//	}

	/**
	 * Aplica as for�as de gravidade exercidas por "attractor" sobre todos 
	 * os demais v�rtices do grafo. As gravidades de um mesmo n� n�o podem
	 * ser aplicadas uma segunda vez, a menos que sejam desfeitas antes.
	 */
	public abstract void applyGravities(int attractor, double attractorMass);
	
	/**
	 * Desfaz todas for�as de gravidade exercidas por "attractorId" sobre  
	 * os demais v�rtices do grafo.
	 */
//	public void undoGravities(String attractorId) {
//		undoGravities(graph.getNode(attractorId).getIndex());
//	}

	/**
	 * Desfaz todas for�as de gravidade exercidas por "attractor" sobre  
	 * os demais v�rtices do grafo.
	 */
	public abstract void undoGravities(int attractor);
	

	/**
	 * Desfaz todas as gravidades exercidas por todos os n�s.
	 */
	public void undoAllGravities() {
		for (int origin = 0; origin < masses.length; origin++) {
			if (masses[origin] != -1) {
				undoGravities(origin);				
			}
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
