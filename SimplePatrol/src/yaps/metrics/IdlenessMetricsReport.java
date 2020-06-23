package yaps.metrics;

import yaps.util.DoubleList;



/**
 * Calculate metrics based on the instantaneous idleness of all nodes (in all turns).
 * 
 * @author Pablo A. Sampaio
 */
public class IdlenessMetricsReport {
	private int numNodes;
	private int startTime, endTime;  // closed interval

	private VisitsList visits;
	
	private DoubleList[] instIdlenessesByNode; // instantaneous idleness per node
	
	public IdlenessMetricsReport(int nodes, int initialTime, int finalTime, VisitsList list) {
		init(nodes, initialTime, finalTime, list);
	}
	
	private void init(int nodes, int initialTime, int finalTime, VisitsList list) {
		numNodes = nodes;
		startTime = initialTime;
		endTime = finalTime;
		
		visits = list.filterByTime(startTime, endTime);

		instIdlenessesByNode = new DoubleList[nodes];
		for (int i = 0; i < nodes; i++) {
			instIdlenessesByNode[i] = new DoubleList(endTime-startTime+1);
		}
		
		int[] currIdlenesses = new int[numNodes];
		int currVisitIndex = 0;
		Visit currVisit = visits.getVisit(currVisitIndex);
		
		for (int timeT = startTime; timeT<= endTime; timeT++){
			// increment the idlenesses of all nodes 
			for (int nodeX = 0; nodeX < numNodes; nodeX++) {
				currIdlenesses[nodeX]++;
			}
			// identify nodes currently visited
			while (currVisit.time == timeT){
				currIdlenesses[currVisit.node] = 1;
				currVisitIndex++;
				if (currVisitIndex < visits.getNumVisits()) {
					currVisit = visits.getVisit(currVisitIndex);
				} else {
					break;
				}
			}
			// set the calculated instantaneous idlenesses
			for (int nodeX = 0; nodeX < numNodes; nodeX++) {
				instIdlenessesByNode[nodeX].set(timeT-startTime, currIdlenesses[nodeX]);
			}

		}
	}
	
	/**
	 * Maximum instantaneous idleness (in all nodes in all turns/timestamps).
	 */
	public double getMaxIdleness() {
		double maxMaxIdl = -1.0d;
	
		for (int nodeX = 0; nodeX < this.numNodes; nodeX++) {
			double nodeMax = this.instIdlenessesByNode[nodeX].max();
			if (nodeMax > maxMaxIdl) {
				maxMaxIdl = nodeMax;
			}
		}
		
		return maxMaxIdl;
	}
	
	/**
	 * Average idleness along the simulation, averaged by the number of nodes
	 * (average of nodes of average in time or vice-versa).<br><br>
	 * Also called "global idleness"  
	 */
	public double getAverageIdleness() {
		double sumNodeAvg = 0.0d;
		
		for (int nodeX = 0; nodeX < this.numNodes; nodeX++) {
			sumNodeAvg += this.instIdlenessesByNode[nodeX].average();
		}
		
		return sumNodeAvg / this.numNodes;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int nodeX = 0; nodeX < this.numNodes; nodeX++) {
			builder.append(this.instIdlenessesByNode[nodeX].toString());
			builder.append('\n');
		}
		return builder.toString();
	}
	
}
