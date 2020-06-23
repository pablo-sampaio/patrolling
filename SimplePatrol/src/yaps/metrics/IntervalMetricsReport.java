package yaps.metrics;

import yaps.util.DoubleList;



/**
 * Class that calculates metrics based on the intervals between consecutive visits.
 * 
 * @author Pablo A. Sampaio
 */
public class IntervalMetricsReport {
	private int numNodes;
	private int startTime, endTime;  // closed interval

	private VisitsList visits;
	
	private DoubleList[] intervalsByNode; // each list has the intervals of a node
	private DoubleList   allIntervals;

	public IntervalMetricsReport(int nodes, int initialTime, int finalTime, VisitsList list) {
		init(nodes, initialTime, finalTime, list);
	}
	
	private void init(int nodes, int initialTime, int finalTime, VisitsList list) {
		numNodes = nodes;
		startTime = initialTime;
		endTime = finalTime;
		
		visits = list.filterByTime(startTime, endTime);
		
		intervalsByNode = new DoubleList[nodes];
		
		int sumSize = 0;
		
		for (int v = 0; v < numNodes; v++) {
			intervalsByNode[v] = calculateIntervals(v);
			sumSize += intervalsByNode[v].size();
		}

		allIntervals = new DoubleList(sumSize);
	
		for (int v = 0; v < numNodes; v++) {
			allIntervals.insertAll(intervalsByNode[v]);
		}
	}
	
	private DoubleList calculateIntervals(int node) {
		DoubleList intervals = new DoubleList();
		
		VisitsList nodeVisits = visits.filterByVertex(node);
		
		long lastVisitTime = startTime;
		double interval;
		
		Visit v;
		
		for (int i = 0; i < nodeVisits.getNumVisits(); i ++) {
			v = nodeVisits.getVisit(i);
			
			interval = (v.time - lastVisitTime);
			intervals.add(interval);			
			lastVisitTime = v.time;
		}
		
		interval = (endTime + 1 - lastVisitTime);
		intervals.add(interval);
		
		return intervals;
	}
	
	public double getLargestMeanTime(){
		
		double largest = intervalsByNode[0].mean(); 
		
		for(int i = 1; i < intervalsByNode.length; i++){
			if(intervalsByNode[i].mean() > largest){
				largest = intervalsByNode[i].mean();
			}
		}
		
		return largest;
	}
	
	public double getSmallestMeanTime(){
		
		double smallest = intervalsByNode[0].mean(); 
		
		for(int i = 1; i < intervalsByNode.length; i++){
			if(intervalsByNode[i].mean() < smallest){
				smallest = intervalsByNode[i].mean();
			}
		}
		
		return smallest;
	}

	/**
	 * Maximum interval between consecutive visits, considering all 
	 * intervals from all nodes.
	 */
	public double getMaximumInterval() {
		return allIntervals.max();
	}
	
	/**
	 * Average interval between consecutive visits, considering all 
	 * intervals from all nodes.
	 */
	public double getAverageInterval() {
		return allIntervals.mean();
	}
	
	/**
	 * Standard deviation of the intervals between consecutive visits, 
	 * considering all intervals from all nodes.
	 */
	public double getStdDevOfIntervals() {
		return allIntervals.standardDeviation();
	}
	
	/**
	 * Quadratic mean of the intervals between consecutive visits,
	 * considering all intervals from all nodes.
	 */
	public double getQuadraticMeanOfIntervals() {
		return allIntervals.generalizedMean(2.0d);
	}
	
	/**
	 * Generalized mean of the intervals between consecutive visits,
	 * considering all intervals from all nodes.
	 * The priorities of the nodes are used as weights for each interval.
	 */
	public double getGeneralizedMeanOfIntervals(double p) {
		return allIntervals.generalizedMean(p);
	}

	/**
	 * Returns the absolute frequencies of each (size of) interval. 
	 */
	public int[] getIntervalsHistogram() {
		int[] histogram = new int[(int)this.allIntervals.max() + 1];
		int interval;
		
		for (int i = 0; i < this.allIntervals.size(); i ++) {
			interval = (int)this.allIntervals.get(i);
			histogram[interval] ++;
		}
		
		return histogram;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int nodeX = 0; nodeX < this.numNodes; nodeX++) {
			builder.append(this.intervalsByNode[nodeX].toString());
			builder.append('\n');
		}
		return builder.toString();
	}
	
}
