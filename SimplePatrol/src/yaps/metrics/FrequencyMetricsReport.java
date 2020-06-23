package yaps.metrics;

import yaps.util.DoubleList;


/**
 * Calculate metrics based on the (absolute or relative) frequencies 
 * of visits of the nodes.
 * 
 * @author Pablo A. Sampaio
 */
public class FrequencyMetricsReport {
	private int startTime, endTime;  // closed interval
	private VisitsList visits;
	
	private DoubleList nodesFequencies;
	private DoubleList nodesVisits;

	public FrequencyMetricsReport(int nodes, int initialTime, int finalTime, VisitsList list) {
		init(nodes, initialTime, finalTime, list);
	}
	
	private void init(int numNodes, int initialTime, int finalTime, VisitsList list) {
		this.startTime = initialTime;
		this.endTime = finalTime;
		
		this.visits = list.filterByTime(startTime, endTime);
		int period = finalTime - initialTime + 1;
		
		this.nodesVisits = new DoubleList(numNodes);
		this.nodesFequencies = new DoubleList(numNodes);
		
		for (int n = 0; n < numNodes; n++) {
			int nodeVisits = visits.filterByVertex(n).getNumVisits();
			this.nodesVisits.set(n, nodeVisits);
			this.nodesFequencies.set(n, (double)nodeVisits / (double)period);
		}
	}
	
	/**
	 * Total number of visits, considering all nodes.
	 */
	public int getTotalVisits() {
		return (int)this.nodesVisits.sum();
	}
	
	/**
	 * Average number of visits per node.
	 */
	public double getAverageVisits() {
		return this.nodesVisits.mean();
	}
	
	/**
	 * Standard deviation of the numbers of visits per node. 
	 */
	public double getStdDevOfVisits() {
		return this.nodesVisits.standardDeviation();
	}
	
	/**
	 * The minimum number of visits among all nodes.
	 */
	public int getMinimumVisits() {
		return (int)this.nodesVisits.min(); 
	}
	
	/**
	 * Average frequency of all nodes.
	 */
	public double getAverageFrequency() {
		return this.nodesFequencies.average(); 
	}

	/**
	 * The minimum frequency among all nodes.
	 */
	public double getMinimumFrequency() {
		return this.nodesFequencies.min(); 
	}

	/**
	 * The standard deviation of the frequencies of all nodes.
	 */
	public double getStdDevOfFrequencies() {
		return this.nodesFequencies.standardDeviation(); 
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.nodesVisits);
		builder.append('\n');
		builder.append(this.nodesFequencies);
		builder.append('\n');
		return builder.toString();
	}
	
}
